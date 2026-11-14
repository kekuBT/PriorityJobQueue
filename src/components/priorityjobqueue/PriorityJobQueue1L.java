package components.priorityjobqueue;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Stable priority job queue represented as a sorted Java linked list.
 *
 * <p>
 * <strong>Convention:</strong> {@code entries != null}; no entry or job is
 * null; priorities are in non-increasing order. When two entries have equal
 * priority, the entry enqueued earlier comes before the later one.
 * </p>
 *
 * <p>
 * <strong>Correspondence:</strong> {@code this} is the sequence of
 * {@code JobEntry} values in {@code entries}, from head to tail.
 * </p>
 *
 * <p>
 * Enqueue is O(n), dequeue and length are O(1). The queue is not thread-safe,
 * and it does not automatically run jobs.
 * </p>
 *
 * @param <T>
 *            type of job
 */
public final class PriorityJobQueue1L<T> extends PriorityJobQueueSecondary<T> {

    /** Internal representation, always in processing order. */
    private LinkedList<JobEntry<T>> entries;

    /** Replaces the representation with a fresh, empty value. */
    private void createNewRep() {
        this.entries = new LinkedList<>();
    }

    /**
     * Creates an empty priority job queue.
     *
     * @ensures this = empty sequence
     */
    public PriorityJobQueue1L() {
        this.createNewRep();
    }

    @Override
    public void enqueue(T job, int priority) {
        Objects.requireNonNull(job, "job");
        JobEntry<T> entry = new JobEntry<>(job, priority);
        ListIterator<JobEntry<T>> cursor = this.entries.listIterator();
        while (cursor.hasNext()) {
            JobEntry<T> current = cursor.next();
            if (current.priority() < priority) {
                cursor.previous();
                cursor.add(entry);
                return;
            }
        }
        this.entries.addLast(entry);
    }

    @Override
    public JobEntry<T> dequeue() {
        assert !this.entries.isEmpty() : "Violation of: length() > 0";
        return this.entries.removeFirst();
    }

    @Override
    public int length() {
        return this.entries.size();
    }

    @Override
    public PriorityJobQueue<T> newInstance() {
        return new PriorityJobQueue1L<>();
    }

    @Override
    public void clear() {
        this.createNewRep();
    }

    @Override
    public void transferFrom(PriorityJobQueue<T> source) {
        assert source != null : "Violation of: source != null";
        assert source != this : "Violation of: source != this";
        assert source.getClass() == this
                .getClass() : "Violation of: dynamic type(source) = dynamic type(this)";
        PriorityJobQueue1L<T> local = (PriorityJobQueue1L<T>) source;
        this.entries = local.entries;
        local.createNewRep();
    }
}
