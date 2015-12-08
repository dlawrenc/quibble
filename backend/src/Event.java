import java.lang.Comparable;
/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Public class that contains relevant event information for the back end,
 * including:
 *     event name
 *     event date
 *     number of tickets for an event
 *
 * This class contains methods for getting and setting attributes, as well as adding, selling, and returning
 * tickets. Input is assumed to be valid.
 */
public class Event implements Comparable {
    private final int MAX_TICKETS = 99999;
    private final int MIN_TICKETS = 0;
    private String event_name;
    private String event_date;
    private int num_tickets;

    /**
     * Three parameter constructor for the event class. Sets the name of the event, the date of the event,
     * and the number of tickets for the event
     *
     * @param event_name_ - the name of the event
     * @param event_date_ - the date of the event
     * @param num_tickets_ - the number of tickets for the event
     */
    public Event(String event_name_, String event_date_, int num_tickets_) {
        event_name = event_name_;
        event_date = event_date_;
        num_tickets = num_tickets_;
    }

    public String get_event_date() {
        return event_date;
    }

    public String get_event_name() {
        return event_name;
    }

    /**
     * Adds tickets to an event for the back end.
     *
     * @param tickets - the number of tickets to be added
     */
    public void add_tickets(int tickets) {
        // case where too many tickets have been returned from separate terminals
        if (num_tickets + tickets > MAX_TICKETS) {
            num_tickets = MAX_TICKETS;
        }
        else {
            num_tickets += tickets;
        }
    }

    /**
     * Sells tickets for an event for the backend.
     * @param tickets
     */
    public void sell_tickets(int tickets) {
        // case where too many tickets have been sold from separate terminals
        if (num_tickets - tickets < MIN_TICKETS) {
            num_tickets = MIN_TICKETS;
        }
        else {
            num_tickets -= tickets;
        }
    }

    /**
     * Public override for equals method. Equality is determined by event name only.
     * @param other - another object
     * @return - true or false depending on whether the object has the same event name
     */
    public boolean equals(Object other) {
        if((other == null) || (getClass() != other.getClass())){
            return false;
        }
        else {
            Event other_event = (Event) other;
            return event_name.equals(other_event.get_event_name());
        }
    }

    /**
     * Compares one event object to another event object. Events are compared based on date.
     * @param other - the other event
     * @return -1 if the event comes before another event, 0 if they are on the same day, 1 if the event comes after.
     */
    @Override public int compareTo(Object other) {
        Event other_event = (Event) other;
        int d1 = Integer.parseInt(event_date);
        int d2 = Integer.parseInt(other_event.get_event_date());
        if(d1 < d2) {
            return -1;
        }
        if (d1 == d2) {
            return 0;
        }
        return 1;
    }

    /**
     * Formats the event for the master events file as a string.
     * @return string representing the event in the master events file
     */
    public String to_master_event() {
        return String.format("%6s", event_date).replace(" ", "0")
                + " " + String.format("%5s", num_tickets).replace(" ", "0")
                + " " + String.format("%-20s", event_name);
    }

    /**
     * Formats the event for the current events file as a string
     * @return string representing the event in the current events file
     */
    public String to_current_event() {
        return String.format("%-20s", event_name)
                + " " + String.format("%5s", num_tickets).replace(" ", "0");
    }
}
