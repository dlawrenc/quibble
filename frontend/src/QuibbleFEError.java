package src;

/**
 * @author Dan Lawrence, Jerry Mak
 *
 * Consists of various error messages for the Quibble front end and related classes. Errors are
 * constructed based on the current Quibble command being run and the name of the event being modified.
 */

public class QuibbleFEError {

    /**
     * Generates an error message telling the user that the Quibble command was not able to run.
     *
     * @param command_name - the name of the command running
     * @return a generic error message telling the user that the command could not be run
     */
    public static String base_error(String command_name) {
        return "Unable to execute command '" + command_name + "'.";
    }

    /**
     * Generates an error message telling the user that the Quibble command was invalid.
     *
     * @param command_name - the name of the command running
     * @return a message telling the user that the command was invalid
     */
    public static String invalid_command(String command_name) {
        return "Command '" + command_name + "' is not a valid command.";
    }

    /**
     * Generates an error message telling the user that the Quibble command cannot be run without a valid login.
     *
     * @param command_name - The name of the command running
     * @return a message telling the user that a valid login is required
     */
    public static String command_without_login_error(String command_name) {
        return "Command '" + command_name + "' requires a valid login. You must login before you " +
                           "can create or modify an event.";
    }

    /**
     * Generates an error message telling the user that the command requires elevated privileges.
     *
     * @param command_name - The name of the command running
     * @return A message telling the user that an admin login is required
     */
    public static String insufficient_privileges_error(String command_name) {
        return "Insufficient privileges for command '" + command_name + "'. You must " +
                           "login to the admin account to execute this command.";
    }

    /**
     * Generates an error message telling the user that they have already logged into the system.
     *
     * @param account_name - the name of the current Quibble user
     * @return a message telling the user that a valid login is required
     */
    public static String already_logged_in_error(String account_name) {
        return "Already logged into account '" + account_name + "'.";
    }

    /**
     * Generates an error message telling the user that specified event was not found in the system.
     *
     * @param event_name - the name of the specified event
     * @param command_name - the name of the command running
     * @return a message telling the user that the event specified was not found in the system
     */
    public static String event_not_found(String event_name, String command_name) {
        return base_error(command_name) + " src.Event '" + event_name + "' does not exist.";
    }

    /**
     * Generates an error message informing the user that an event already exists in the system.
     *
     * @param event_name - the name of the specified event
     * @param command_name - the name of the command running
     * @return a message informing the user that the event already exists in the system.
     */

    public static String event_already_exists(String event_name, String command_name) {
        return base_error(command_name) + " src.Event '" + event_name + "' already exists.";
    }

    /**
     * Generates an error message informing the user that an event has already been deleted.
     *
     * @param event_name - the name of the specified event
     * @return a message informing the user that an event has already been deleted
     */
    public static String event_deleted(String event_name) {
        return "src.Event '" + event_name + "' has been deleted and cannot be modified.";
    }

    /**
     * Generates an error message informing the user that the sales account cannot return/sell
     * more than 8 tickets in a single transaction.
     *
     * @param event_name - the name of the specified event
     * @param command_name - the name of the command running
     * @param current_user - the name of the current user
     * @return a message informing the user that an admin login is required to return/sell more than 8 tickets
     */
    public static String unprivileged_tickets(String event_name, String command_name, Account current_user) {
        return  "Unable to " + command_name + " tickets for event '" + event_name + "'. " +
                "src.Account '" + current_user.toString() + "' cannot " + command_name +
                " more than 8 tickets in a " + "single transaction.";

    }

    /**
     * Generates an error message informing the user that the number of tickets to sell exceeds the number of tickets
     * available. Also displays the number of tickets remaining for an event.
     *
     * @param event_name - the name of the specified event
     * @param num_tickets - the number of tickets to be sold
     * @return a message informing the user that there are not enough tickets available to sell
     */
    public static String sell_tickets_error(String event_name, int num_tickets) {
        return "Unable to sell tickets for event '" + event_name + "'. " +
                "The number of tickets to be sold exceeds the number of tickets available. " +
                "Tickets left: " + num_tickets;
    }

    /**
     * Generates an error message informing the user that the number of tickets to add exceeds the maximum number
     * of tickets. Also displays the number of tickets remaining for an event.
     *
     * @param event_name - the name of the specified event
     * @param num_tickets - the number of tickets to be added
     * @return a message informing the user that the ticket limit has been exceeded
     */
    public static String add_tickets_error(String event_name, int num_tickets) {
        return "Unable to add tickets for event '" + event_name + "'. " +
                "The number of tickets for this event cannot exceed "  + Event.MAX_TICKETS +
                ". Current number of tickets for event '" + event_name + "': " + num_tickets;
    }

    /**
     * Generates an error message informing the user that the number of tickets to return exceeds the maximum number
     * of tickets. Also displays the number of tickets currently remaining for an event.
     *
     * @param event_name - the name of the specified event
     * @param num_tickets - the number of tickets to be returned
     * @return a message informing the user that the ticket limit has been exceeded
     */
    public static String return_tickets_error(String event_name, int num_tickets) {
        return "Unable to return tickets for event '" + event_name + "'. " +
                "The number of tickets for this event cannot exceed "  + Event.MAX_TICKETS +
                ". Current number of tickets for event '" + event_name + "': " + num_tickets;

    }

}
