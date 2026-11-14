package components.examples;

import components.priorityjobqueue.JobEntry;
import components.priorityjobqueue.PriorityJobQueue;
import components.priorityjobqueue.PriorityJobQueue1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Use case 2: Simulate a CPU scheduler with one time slice per dispatch.
 */
public final class TimeSliceScheduler {

    /** Immutable simulation process. */
    private record Process(String name, int remainingSlices) {
    }

    /** Utility class. */
    private TimeSliceScheduler() {
    }

    /**
     * Runs a deterministic time-slice simulation.
     *
     * @param args
     *            unused command-line arguments
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        PriorityJobQueue<Process> ready = new PriorityJobQueue1L<>();
        ready.enqueue(new Process("Editor", 3), 2);
        ready.enqueue(new Process("Compiler", 2), 2);
        ready.enqueue(new Process("Backup", 2), 1);

        int tick = 0;
        while (!ready.isEmpty()) {
            tick++;
            JobEntry<Process> dispatched = ready.dequeue();
            Process current = dispatched.job();
            out.println("Tick " + tick + ": " + current.name() + " (priority "
                    + dispatched.priority() + ")");
            if (current.remainingSlices() > 1) {
                ready.enqueue(
                        new Process(current.name(),
                                current.remainingSlices() - 1),
                        dispatched.priority());
            }
            if (tick == 2) {
                out.println("Urgent interrupt arrived");
                ready.enqueue(new Process("Interrupt handler", 1), 5);
            }
        }
        out.println("All processes completed after " + tick + " time slices.");
        out.close();
    }
}
