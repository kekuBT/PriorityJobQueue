package components.priorityjobqueue;

import components.standard.Standard;

/**
 * Kernel interface for a stable priority queue of jobs.
 *
 * <p>
 * <strong>Mathematical model:</strong> {@code PriorityJobQueue<T>} is a finite
 * sequence of {@link JobEntry} values. The sequence is ordered by
 * non-increasing integer priority. Among jobs with equal priority, their
 * positions reflect the order in which they were enqueued. The first entry is
 * always the next job to process.
 * </p>
 *
 * <p>
 * Jobs are stored by reference. The queue does not execute jobs; clients
 * explicitly remove and process them.
 * </p>
 *
 * @param <T>
 *            the type of job stored in this queue
 */
public interface PriorityJobQueueKernel<T>
        extends Standard<PriorityJobQueue<T>> {

    /**
     * Enqueues a job based on its priority. If equal priority then this job
     * appears after all of them.
     *
     * @param job
     *            job to enqueue
     * @param priority
     *            integer priority;
     * @aliases reference {@code job}
     * @updates this
     * @requires job != null
     * @ensures this = stable priority insertion of (job, priority) in #this
     */
    void enqueue(T job, int priority);

    /**
     * Removes and returns the earliest of the highest-priority jobs.
     *
     * @return the removed job and its original priority
     * @updates this
     * @requires this.length() > 0
     * @ensures dequeue = first(#this) and this = rest(#this)
     */
    JobEntry<T> dequeue();

    /**
     * Reports the number of jobs in the queue. no changes.
     *
     * @return number of waiting jobs
     * @ensures length = |this| and this = #this
     */
    int length();
}
