import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import components.priorityjobqueue.JobEntry;
import components.priorityjobqueue.PriorityJobQueue;
import components.priorityjobqueue.PriorityJobQueue1L;

/**
 * Systematic tests of kernel operations and Standard methods.
 */
public class PriorityJobQueueKernelTest {

    private PriorityJobQueue<String> create() {
        return new PriorityJobQueue1L<>();
    }

    private static List<JobEntry<String>> drain(PriorityJobQueue<String> q) {
        List<JobEntry<String>> entries = new ArrayList<>();
        while (q.length() > 0) {
            entries.add(q.dequeue());
        }
        return entries;
    }

    @Test
    public void constructorCreatesEmptyQueue() {
        PriorityJobQueue<String> q = this.create();
        assertEquals(0, q.length());
        assertTrue(q.isEmpty());
    }

    @Test
    public void enqueueIntoEmptyQueue() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("A", 4);
        assertEquals(1, q.length());
        assertEquals(new JobEntry<>("A", 4), q.dequeue());
        assertEquals(0, q.length());
    }

    @Test
    public void enqueueIncreasingPrioritiesSortsDescending() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("low", 1);
        q.enqueue("mid", 3);
        q.enqueue("high", 5);
        assertEquals(List.of(new JobEntry<>("high", 5),
                new JobEntry<>("mid", 3), new JobEntry<>("low", 1)), drain(q));
    }

    @Test
    public void enqueueDecreasingPrioritiesPreservesOrder() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("high", 5);
        q.enqueue("mid", 3);
        q.enqueue("low", 1);
        assertEquals(List.of(new JobEntry<>("high", 5),
                new JobEntry<>("mid", 3), new JobEntry<>("low", 1)), drain(q));
    }

    @Test
    public void enqueueMixedPrioritiesInsertsInMiddle() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("first", 5);
        q.enqueue("last", 1);
        q.enqueue("middle", 3);
        assertEquals(List.of(new JobEntry<>("first", 5),
                new JobEntry<>("middle", 3), new JobEntry<>("last", 1)),
                drain(q));
    }

    @Test
    public void enqueueSamePriorityMaintainsFifo() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("A", 2);
        q.enqueue("B", 2);
        q.enqueue("C", 2);
        assertEquals(List.of(new JobEntry<>("A", 2), new JobEntry<>("B", 2),
                new JobEntry<>("C", 2)), drain(q));
    }

    @Test
    public void enqueueSameJobTwiceDoesNotDeduplicate() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("same", 5);
        q.enqueue("same", 1);
        assertEquals(2, q.length());
        assertEquals(
                List.of(new JobEntry<>("same", 5), new JobEntry<>("same", 1)),
                drain(q));
    }

    @Test(expected = NullPointerException.class)
    public void enqueueRejectsNullJob() {
        this.create().enqueue(null, 3);
    }

    @Test
    public void newInstanceIsEmptySameDynamicType() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("source", 5);
        PriorityJobQueue<String> fresh = q.newInstance();
        assertNotSame(q, fresh);
        assertEquals(q.getClass(), fresh.getClass());
        assertEquals(0, fresh.length());
        fresh.enqueue("fresh", 1);
        assertEquals(List.of(new JobEntry<>("source", 5)), drain(q));
        assertEquals(List.of(new JobEntry<>("fresh", 1)), drain(fresh));
    }

    @Test
    public void clearEmptyQueueRemainsEmpty() {
        PriorityJobQueue<String> q = this.create();
        q.clear();
        assertEquals(0, q.length());
    }

    @Test
    public void clearNonemptyQueueRemovesEverything() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("A", 4);
        q.enqueue("B", 2);
        q.clear();
        assertEquals(0, q.length());
        q.enqueue("C", 3);
        assertEquals(new JobEntry<>("C", 3), q.dequeue());
    }

    @Test
    public void transferFromEmptyClearsNonemptyDestination() {
        PriorityJobQueue<String> target = this.create();
        PriorityJobQueue<String> source = this.create();
        target.enqueue("old", 5);
        target.transferFrom(source);
        assertEquals(0, target.length());
        assertEquals(0, source.length());
    }

    @Test
    public void transferIntoEmptyDestinationTakesValue() {
        PriorityJobQueue<String> target = this.create();
        PriorityJobQueue<String> source = this.create();
        source.enqueue("A", 2);
        target.transferFrom(source);
        assertEquals(new JobEntry<>("A", 2), target.dequeue());
        assertTrue(source.isEmpty());
    }

    @Test
    public void lengthIsNonDestructive() {
        PriorityJobQueue<String> q = this.create();
        q.enqueue("A", 2);
        q.enqueue("B", 5);
        assertEquals(2, q.length());
        assertEquals(2, q.length());
        assertFalse(q.isEmpty());
        assertEquals(List.of(new JobEntry<>("B", 5), new JobEntry<>("A", 2)),
                drain(q));
    }
}
