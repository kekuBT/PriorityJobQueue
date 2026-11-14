import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import components.priorityjobqueue.JobEntry;
import components.priorityjobqueue.PriorityJobQueue;
import components.priorityjobqueue.PriorityJobQueue1L;

/** Tests interface and the Object common methods. */
public class PriorityJobQueueSecondaryTest {

    private PriorityJobQueue<String> make(String... jobs) {
        PriorityJobQueue<String> q = new PriorityJobQueue1L<>();
        for (String job : jobs) {
            q.enqueue(job, 3);
        }
        return q;
    }

    private static List<JobEntry<String>> drain(PriorityJobQueue<String> q) {
        List<JobEntry<String>> results = new ArrayList<>();
        while (q.length() > 0) {
            results.add(q.dequeue());
        }
        return results;
    }

    @Test
    public void isEmptyTrueForNewQueue() {
        assertTrue(this.make().isEmpty());
    }

    @Test
    public void isEmptyFalseForPopulatedQueue() {
        PriorityJobQueue<String> q = this.make("A");
        assertFalse(q.isEmpty());
        assertEquals(new JobEntry<>("A", 3), q.dequeue());
    }

    @Test
    public void isEmptyBecomesTrueAfterDequeue() {
        PriorityJobQueue<String> q = this.make("A");
        q.dequeue();
        assertTrue(q.isEmpty());
    }

    @Test
    public void peekOnOneJobRestoresQueue() {
        PriorityJobQueue<String> q = this.make("A");
        assertEquals(new JobEntry<>("A", 3), q.peek());
        assertEquals(List.of(new JobEntry<>("A", 3)), drain(q));
    }

    @Test
    public void peekWithEqualPrioritiesPreservesFifo() {
        PriorityJobQueue<String> q = this.make("A", "B", "C");
        assertEquals(new JobEntry<>("A", 3), q.peek());
        assertEquals(List.of(new JobEntry<>("A", 3), new JobEntry<>("B", 3),
                new JobEntry<>("C", 3)), drain(q));
    }

    @Test
    public void countPriorityZeroOnEmptyQueue() {
        PriorityJobQueue<String> q = this.make();
        assertEquals(0, q.countPriority(5));
        assertTrue(q.isEmpty());
    }

    @Test
    public void countPriorityIncludesDuplicates() {
        PriorityJobQueue<String> q = this.make("same", "same", "same");
        assertEquals(3, q.countPriority(3));
        assertEquals(3, q.length());
    }

    @Test
    public void equalsEmptyQueues() {
        PriorityJobQueue<String> a = this.make();
        PriorityJobQueue<String> b = this.make();
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(0, a.length());
        assertEquals(0, b.length());
    }

    @Test
    public void equalsDifferentLengths() {
        assertNotEquals(this.make("A"), this.make("A", "B"));
    }

    @Test
    public void equalsSameJobDifferentPriority() {
        PriorityJobQueue<String> a = this.make();
        PriorityJobQueue<String> b = this.make();
        a.enqueue("A", 3);
        b.enqueue("A", 5);
        assertNotEquals(a, b);
    }

    @Test
    public void equalsSamePriorityDifferentFifoOrder() {
        assertNotEquals(this.make("A", "B"), this.make("B", "A"));
    }

    @Test
    public void equalsPreservesBothArgumentsEvenWhenDifferent() {
        PriorityJobQueue<String> a = this.make("A", "B");
        PriorityJobQueue<String> b = this.make("A", "C");
        assertFalse(a.equals(b));
        assertEquals(List.of(new JobEntry<>("A", 3), new JobEntry<>("B", 3)),
                drain(a));
        assertEquals(List.of(new JobEntry<>("A", 3), new JobEntry<>("C", 3)),
                drain(b));
    }

    @Test
    public void toStringEmptyAndNonemptyDoesNotChangeQueue() {
        PriorityJobQueue<String> empty = this.make();
        assertEquals("[]", empty.toString());
        PriorityJobQueue<String> q = this.make("A", "B");
        String printed = q.toString();
        assertTrue(printed.contains("A"));
        assertTrue(printed.contains("B"));
        assertTrue(printed.indexOf("A") < printed.indexOf("B"));
        assertEquals(List.of(new JobEntry<>("A", 3), new JobEntry<>("B", 3)),
                drain(q));
    }

    @Test
    public void jobEntryValueSemantics() {
        JobEntry<String> a = new JobEntry<>("A", 5);
        JobEntry<String> b = new JobEntry<>("A", 5);
        JobEntry<String> c = new JobEntry<>("A", 4);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertEquals("A", a.job());
        assertEquals(5, a.priority());
    }

    @Test(expected = NullPointerException.class)
    public void jobEntryRejectsNullJob() {
        new JobEntry<>(null, 1);
    }
}
