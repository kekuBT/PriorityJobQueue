package components.priorityjobqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Shared implementations of secondary operations and the common Object methods.
 *
 * @param <T>
 *            type of stored job
 */
public abstract class PriorityJobQueueSecondary<T>
        implements PriorityJobQueue<T> {

    /**
     * Obtains an ordered snapshot using only kernel and Standard operations.
     *
     * @param <E>
     *            type of job
     * @param queue
     *            queue to examine and restore
     * @return ordered list of the queues entries
     */
    private static <E> List<JobEntry<E>> snapshot(PriorityJobQueue<E> queue) {
        PriorityJobQueue<E> temporary = queue.newInstance();
        List<JobEntry<E>> result = new ArrayList<>();
        while (queue.length() > 0) {
            JobEntry<E> entry = queue.dequeue();
            result.add(entry);
            temporary.enqueue(entry.job(), entry.priority());
        }
        queue.transferFrom(temporary);
        return result;
    }

    @Override
    public final JobEntry<T> peek() {
        assert this.length() > 0 : "Violation of: length() > 0";
        return snapshot(this).get(0);
    }

    @Override
    public final boolean isEmpty() {
        return this.length() == 0;
    }

    @Override
    public final int countPriority(int priority) {
        int count = 0;
        for (JobEntry<T> entry : snapshot(this)) {
            if (entry.priority() == priority) {
                count++;
            }
        }
        return count;
    }

    @Override
    public final String toString() {
        return snapshot(this).toString();
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PriorityJobQueue<?>)) {
            return false;
        }
        PriorityJobQueue<?> that = (PriorityJobQueue<?>) other;
        if (this.length() != that.length()) {
            return false;
        }
        return Objects.equals(snapshot(this), snapshot(that));
    }

    @Override
    public final int hashCode() {
        return snapshot(this).hashCode();
    }
}
