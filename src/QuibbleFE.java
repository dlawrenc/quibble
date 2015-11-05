/**
 * @author Dan Lawrence, Jerry Mak
 */
import java.util.ArrayList;


/**
 * Bread and butter class of the Quibble front end. Contains methods for running the main loop of the FE, which
 * waits for a user to enter a command and modifies the state of the FE object based on the user input.
 *
 * Important attributes:
 *     current_user    - Account object representing the current user of the system (used to determine privileges)
 *     current_command - Current command running by the system
 *     session_number  - The current session (used to name transaction files after login-logout sequences)
 *     current_events  - A list of event objects representing each event known to Quibble. These are first loaded
 *                       upon startup after reading the current events file, and then modified after subsequent commands
 *     transactions    - A list of transactions in the current session. This list is written to a file and reset after
 *                       a logout command is parsed
 *
 * Important methods:
 *     start              - starts the main loop of the front end and runs a command based on user input
 *     execute_login      - runs the login command
 *     execute_logout     - runs the logout command
 *     execute_return     - runs the return command
 *     execute_sell       - runs the sell command
 *     execute_create     - runs the create command
 *     execute_delete     - runs the delete command
 *     execute_return     - runs the return command
 *     end_session        - dumps all transactions to a file and clears the transactions list
 *     find_current_event - searches for an event from current_events and returns it
 *
 *     Note: this class will not throw exceptions, and in general attempts to re-prompt the user for input instead of
 *     crashing horribly.
 */
public class QuibbleFE {
    private Account current_user;
    private String current_command;
    private int session_num;
    private ArrayList<Event> current_events;
    private ArrayList<Transaction> transactions;
    private QuibbleIO qio;

    // List of valid FE commands
    private String[] commands = {"logout", "sell", "return", "create", "add", "delete", "login"};

    /**
     * Default constructor for the front end. Used for testing when no current events file is supplied.
     */
    public QuibbleFE() {
        qio = new QuibbleIO();
        current_user = new Account();
        current_events = new ArrayList<>();
        transactions = new ArrayList<>();
        current_command = "";
        session_num = 1;
    }

    /**
     * One parameter constructor for the front end. Initializes all attributes to default and loads in events from
     * the current events file.
     * @param events_file
     */
    public QuibbleFE(String events_file) {
        qio = new QuibbleIO();
        current_user = new Account();
        current_events = qio.read_events_file(events_file);
        transactions = new ArrayList<>();
        current_command = "";
        session_num = 1;
    }

    /**
     * Main loop for front end. Waits for a user to enter a command and modifies the front end state based on the
     * command. Exits upon reading EOF (ctrl+D).
     */
    public void start() {

        while (true) {
            String command = qio.get_user_input("Enter command:");
            current_command = command;

            if (!is_valid_command(command)) {
                System.err.println(QuibbleFEError.invalid_command(command));
            }
            else if (!current_user.is_logged_in() && !command.equals("login")) {
                // already logged out
                if (command.equals("logout")) {
                    System.err.println("You have already logged out.");
                }
                else {
                    System.err.println(QuibbleFEError.command_without_login_error(command));
                }
            }
            else if (command.equals("login")) {
                execute_login();
            }
            else if (command.equals("logout")) {
                execute_logout();
            }
            else if (command.equals("create")) {
                execute_create();
            }
            else if (command.equals("add")) {
                execute_add();
            }
            else if (command.equals("delete")) {
                execute_delete();
            }
            else if (command.equals("sell")) {
                execute_sell();
            }
            else if (command.equals("return")) {
                execute_return();
            }
        }
    }

    /**
     * Method to execute the login command. Modifies the current_user object if user input is valid, and does nothing
     * otherwise.
     */
    public void execute_login() {
        // are we already logged in?
        if (current_user.is_logged_in()) {
            System.err.println(QuibbleFEError.already_logged_in_error(current_user.toString()));
            return;
        }

        String user = qio.get_user_input("Username:");
        try {
            current_user.login(user);
        }
        catch (AccountException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method to execute the logout command. Modifies the current_user object and ends the current session if the
     * front end is in a valid login.
     */
    public void execute_logout() {
        // are we already logged out?
        try {
            current_user.logout();
        }
        catch (AccountException e) {
            System.err.println(e.getMessage());
            return;
        }

        // add the logout transaction to the list and dump the list to the transaction file
        transactions.add(new Transaction(get_current_command_id()));
        end_session();
    }

    /**
     * Method to execute the create command. Adds the user event to the list of events if the event is valid and
     * does not already exist. Also adds a transaction to the transaction list upon success.
     */
    public void execute_create() {
        // command requires elevated privileges
        if (!current_user.is_admin()) {
            System.err.println(QuibbleFEError.insufficient_privileges_error(current_command));
            return;
        }

        String event_name = qio.get_user_event_name("Event name:");
        Event event = new Event(event_name);

        // does the event already exist in the system?
        if (current_events.contains(event)) {
            Event found = find_current_event(event);
            if (found.is_deleted()) {
                // Event has been deleted, report this
                System.err.println(QuibbleFEError.event_deleted(event_name));
            }
            else {
                // Event already exists, report this
                System.err.println(QuibbleFEError.event_already_exists(event_name, current_command));
            }
            return;
        }

        String event_date = qio.get_user_event_date("Event date:");
        event.set_event_date(event_date);
        int event_tickets = qio.get_user_event_tickets("Number of tickets:");
        event.set_ticket_number(event_tickets);

        // add the event and transaction to each list
        current_events.add(event);
        transactions.add(new Transaction(get_current_command_id(), event));
    }

    /**
     * Method to execute the delete command. Modifies an existing event object in current_events
     * (if the event is found) and marks it as deleted.
     */
    public void execute_delete() {
        // command requires elevated privileges
        if (!current_user.is_admin()) {
            System.err.println(QuibbleFEError.insufficient_privileges_error(current_command));
            return;
        }

        String event_name = qio.get_user_event_name("Event name:");
        Event event = new Event(event_name);

        if (!current_events.contains(event)) {
            System.err.println(QuibbleFEError.event_not_found(event_name, current_command));
            return;
        }

        Event found = find_current_event(event);
        try {
            found.mark_deleted();
        }
        catch (EventException e) {
            System.err.println(e.getMessage());
            return;
        }

        transactions.add(new Transaction(get_current_command_id(), found));
    }

    /**
     * Method to execute the sell command. Modifies an existing event object in current_events
     * (if the event is found) and removes a user specified number of tickets from it.
     */
    public void execute_sell() {
        String event_name = qio.get_user_event_name("Event name:");
        Event event = new Event(event_name);

        if (!current_events.contains(event)) {
            System.err.println(QuibbleFEError.event_not_found(event_name, current_command));
            return;
        }

        Event found = find_current_event(event);
        int tickets = qio.get_user_event_tickets("Number of tickets:");

        try {
            found.sell_tickets(tickets, current_user);
        }
        catch (EventException e) {
            System.err.println(e.getMessage());
            return;
        }

        transactions.add(new Transaction(get_current_command_id(), found));
    }

    /**
     * Method to execute the add command. Modifies an existing event object in current_events (if it exists) and
     * adds a user specified number of tickets to it.
     */
    public void execute_add() {
        // command requires elevated privileges
        if (!current_user.is_admin()) {
            System.err.println(QuibbleFEError.insufficient_privileges_error(current_command));
            return;
        }

        String event_name = qio.get_user_event_name("Event name:");
        Event event = new Event(event_name);

        if (!current_events.contains(event)) {
            System.err.println(QuibbleFEError.event_not_found(event_name, current_command));
            return;
        }

        Event found = find_current_event(event);
        int tickets = qio.get_user_event_tickets("Number of tickets:");

        try {
            found.add_tickets(tickets);
        }
        catch (EventException e) {
            System.err.println(e.getMessage());
            return;
        }

        transactions.add(new Transaction(get_current_command_id(), found));
    }

    /**
     * Method to execute the return command. Modifies an existing event object in current_events (if it exists) and
     * returns a user specified number of tickets to it.
     */
    public void execute_return() {
        String event_name = qio.get_user_event_name("Event name:");
        Event event = new Event(event_name);

        if (!current_events.contains(event)) {
            System.err.println(QuibbleFEError.event_not_found(event_name, current_command));
            return;
        }

        Event found = find_current_event(event);
        int tickets = qio.get_user_event_tickets("Number of tickets:");

        try {
            found.return_tickets(tickets, current_user);
        }
        catch (EventException e) {
            System.err.println(e.getMessage());
            return;
        }

        transactions.add(new Transaction(get_current_command_id(), found));
    }

    /**
     * Returns true if a command is a valid Quibble command, false otherwise.
     * @param command - the name of a command
     * @return - true if a command is valid
     */
    public boolean is_valid_command(String command) {
        for (String i : commands) {
            if (command.equals(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a reference to the specified event in the current_events list. Assumes that the event is in the list.
     * @param to_find - the event to search for in the list
     * @return a reference to the event in the current events list
     */
    public Event find_current_event(Event to_find) {
        int i = current_events.indexOf(to_find);
        return current_events.get(i);
    }

    /**
     * Gets the command id of a Quibble command for use with transaction files.
     * @return command id of a command
     */
    public int get_current_command_id() {
        for (int i = 0; i < commands.length; ++i) {
            if (current_command.equals(commands[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Ends a front end session, writing all transactions to a file and clearing the current list of transactions.
     */
    public void end_session() {
        String t_file = qio.create_transaction_file(session_num);
        qio.write_transactions(t_file, transactions);
        ++session_num;
        transactions.clear();
    }
}