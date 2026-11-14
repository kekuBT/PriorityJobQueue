package components.priorityjobqueue;

/**
 * stable priority job queue, secondary operation are defined in terms of the
 * kernel and {@code Standard}.
 *
 * @param <T>
 *            type of stored job
 */
public interface PriorityJobQueue<T> extends PriorityJobQueueKernel<T> {

    /**
     * Reports the next job without removing it.
     *
     * @return highest-priority waiting job
     * @requires this.length() > 0
     * @ensures peek = first(this) and this = #this
     */
    JobEntry<T> peek();

    /**
     * Reports if queue is empty.
     *
     * @return whether no jobs are waiting
     * @ensures isEmpty = (|this| = 0) and this = #this
     */
    boolean isEmpty();

    /**
     * Counts waiting jobs having exactly the given priority.
     *
     * @param priority
     *            priority to count
     * @return number of jobs with that priority
     * @ensures countPriority = number of entries e in this such that
     *          e.priority() = priority, and this = #this
     */
    int countPriority(int priority);
}
