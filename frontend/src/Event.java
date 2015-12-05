/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Public class that contains relevant event information for the front end,
 * including:
 *     event name
 *     event date
 *     number of tickets for an event
 *     flag to indicate deletion
 *
 * This class contains methods for getting and setting attributes, as well as adding, selling, and returning
 * tickets. Methods that relate to add, sell, and return command will throw EventExceptions upon trying to
 * modify an event with an illegal number of tickets.
 *
 * The constructors of this class do not throw EventExceptions. It is assumed that the QuibbleIO class will
 * get valid input before creating an instance of this class.
 */
public class Event {
    public static final int MIN_TICKETS = 1;        // minimum number of tickets for any transaction
    public static final int MAX_TICKETS = 99999;    // maximum number of tickets for any transaction
    public static final int MAX_EVENT_NAME = 20;    // maximum number of characters for an event

    private String event_name;
    private String event_date;
    private int num_tickets;
    private int session_num; // created in session number
    private boolean deleted;


    /**
     * Default constructor for the Event class. Initializes all attributes to empty/zero values.
     */
    public Event() {
        event_name = "";
        event_date = "";
        num_tickets = 0;
        session_num = 0;
        deleted = false;
    }

    /**
     * One parameter constructor for the event class. Sets only the name of the event to a non-default value.
     * This is useful to create when comparing two event objects, since equality is determined only by event name.
     *
     * @param event_name_ - the name of the event
     */
    public Event(String event_name_) {
        event_name = event_name_;
        event_date = "";
        num_tickets = 0;
        session_num = 0;
        deleted = false;
    }

    /**
     * Two parameter constructor for the event class. Sets the name of the event and the number of
     * tickets for the event to non-default values.
     *
     * @param event_name_ - the name of the event
     */
    public Event(String event_name_, int num_tickets_) {
        event_name = event_name_;
        event_date = "";
        num_tickets = num_tickets_;
        session_num = 0;
        deleted = false;
    }

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
        session_num = 0;
        deleted = false;
    }

    /**
     * Sets the event name.
     *
     * @param event_name_ - the name of the event
     */
    public void set_event_name(String event_name_) {
        event_name = event_name_;
    }

    /**
     * Sets the event date.
     *
     * @param event_date_ - the date of the event.
     */
    public void set_event_date(String event_date_) {
        event_date = event_date_;
    }

    /**
     * Sets the number of tickets for an event.
     *
     * @param num_tickets_ - the number of tickets for an event
     */
    public void set_ticket_number(int num_tickets_) {
        num_tickets = num_tickets_;
    }

    /**
     * Gets the event name.
     *
     * @return the event name
     */
    public String get_event_name() {
        return event_name;
    }

    /**
     * Gets the event date.
     *
     * @return the event date
     */
    public String get_event_date() {
        return event_date;
    }

    /**
     * Gets the number of tickets for an event.
     *
     * @return the number of tickets
     */
    public int get_num_tickets() {
        return num_tickets;
    }

    public int get_session_num() {
        return session_num;
    }

    void set_session_num(int num) {
        session_num = num;
    }

    /**
     * Adds tickets to an event. The event must be active and the number of tickets to add must not exceed
     * the maximum number of tickets. Otherwise, this method will throw an EventException.
     *
     * @param tickets - the number of tickets to be added
     * @throws EventException - when the event is deleted or in an illegal state
     */
    public void add_tickets(int tickets) throws EventException {
        if (deleted) {
            throw new EventException(QuibbleFEError.event_deleted(event_name));
        }
        if (tickets + num_tickets > MAX_TICKETS) {
            throw new EventException(QuibbleFEError.add_tickets_error(event_name, num_tickets));
        }
        num_tickets += tickets;
    }

    /**
     * Sells tickets for an event. The method will check the user account to determine how many tickets are allowed to
     * be sold at a time.
     *
     * The method will throw event exceptions when:
     *     The event is deleted
     *     The number of tickets to be sold exceeds the number of tickets available
     *     The sales account tries to sell more than 8 tickets
     *
     * @param tickets - the number of tickets to sell
     * @param current_user - the current user as an Account object
     * @throws EventException - when an illegal state is encountered
     */
    public void sell_tickets(int tickets, Account current_user) throws EventException {
        if (deleted) {
            throw new EventException(QuibbleFEError.event_deleted(event_name));
        }
        if ((num_tickets - tickets) < 0) {
            throw new EventException(QuibbleFEError.sell_tickets_error(event_name, num_tickets));
        }
        if (!current_user.is_admin() && tickets > 8) {
            throw new EventException(QuibbleFEError.unprivileged_tickets(event_name, "sell", current_user));
        }
        num_tickets -= tickets;
    }

    /**
     * Returns tickets for an event. The method will check the user account to determine how many tickets are allowed to
     * be returned at a time.
     *
     * The method will throw event exceptions when:
     *     The event is deleted
     *     The number of tickets to be returned exceeds the maximum number of tickets
     *     The sales account tries to return more than 8 tickets
     *
     * @param tickets - the number of tickets to return
     * @param current_user - the current user as an Account object
     * @throws EventException - when an illegal state is encountered
     */
    public void return_tickets(int tickets, Account current_user) throws EventException {
        if (deleted) {
            throw new EventException(QuibbleFEError.event_deleted(event_name));
        }
        if (tickets + num_tickets > MAX_TICKETS) {
            throw new EventException(QuibbleFEError.return_tickets_error(event_name, num_tickets));
        }
        if (!current_user.is_admin() && tickets > 8) {
            throw new EventException(QuibbleFEError.unprivileged_tickets(event_name, "return", current_user));
        }
        num_tickets += tickets;
    }

    /**
     * Marks an event as deleted. If an event is already deleted, this method will throw an EventException.
     *
     * @throws EventException - when an event has already been deleted
     */
    public void mark_deleted() throws EventException {
        if (deleted) {
            throw new EventException(QuibbleFEError.event_deleted(event_name));
        }
        deleted = true;
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
     * Returns true if the event has been deleted.
     * @return boolean value indicating event deletion.
     */
    public boolean is_deleted() {
        return deleted;
    }

    /**
     * Clones an Event object.
     * @return a cloned event object.
     */
    public Event clone() {
        return new Event(event_name, event_date, num_tickets);
    }
}
