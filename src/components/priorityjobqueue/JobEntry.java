package components.priorityjobqueue;

import java.util.Objects;

/**
 * An immutable job/priority pair returned by a priority job queue.
 *
 * @param <T>
 *            type of the job
 * @param job
 *            the job, which must not be null
 * @param priority
 *            its priority
 */
public record JobEntry<T>(T job, int priority) {

    /**
     * Creates a job entry, rejecting a null job.
     *
     * @param job
     *            the non-null job
     * @param priority
     *            priority associated with this job
     * @requires job != null
     * @ensures this.job() = job and this.priority() = priority
     */
    public JobEntry {
        Objects.requireNonNull(job, "job");
    }
}
