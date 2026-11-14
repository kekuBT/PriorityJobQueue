package components.examples;

import components.priorityjobqueue.JobEntry;
import components.priorityjobqueue.PriorityJobQueue;
import components.priorityjobqueue.PriorityJobQueue1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Use case 1: Route data center maintenance tickets by operational urgency.
 * This is just an example I am using from my work related experiences.
 */
public final class DataCenterIncident {

    /** A ticket whose identifier and description are fixed at creation. */
    private record Ticket(String id, String description) {
    }

    /** Utility class. */
    private DataCenterIncident() {
    }

    /**
     * Demonstrates ticket insertion.
     *
     * @param args
     *            unused command-line arguments
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        PriorityJobQueue<Ticket> queue = new PriorityJobQueue1L<>();
        queue.enqueue(new Ticket("DC101", "DIMM not detected"), 1);
        queue.enqueue(new Ticket("DC102", "Host unavailable"), 5);
        queue.enqueue(new Ticket("DC103", "Drive replacement"), 3);
        queue.enqueue(new Ticket("DC104", "Network link down"), 5);
        queue.enqueue(new Ticket("DC105", "Cable not plugged in"), 1);

        out.println("Critical incidents waiting: " + queue.countPriority(5));
        out.println("Next dispatch: " + queue.peek().job().id());
        out.println("Dispatch order:");
        while (!queue.isEmpty()) {
            JobEntry<Ticket> next = queue.dequeue();
            out.println("  P" + next.priority() + " | " + next.job().id()
                    + " | " + next.job().description());
        }
        out.close();
    }
}
