/**
 * @author Dan Lawrence, Jerry Mak
 */
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Public class containing only static members for IO. This IO class ensures that user input will be valid for the
 * front end, validating dates, ticket numbers, and event names.
 *
 */
public class QuibbleIO {

    // unicode sequence for EOF
    public static String EOF = "\u001a";
    private Scanner in;

    public QuibbleIO() {
        in = new Scanner(System.in);
    }

    /**
     * Returns the user input as a string
     *
     * @param prompt - a prompt to be displayed in the terminal
     * @return the user input as a string
     */
    public String get_user_input(String prompt) {
        System.out.println(prompt);
        String input = "";
        try {
            input = in.nextLine();
        }
        // Input is EOF
        catch (java.util.NoSuchElementException e) {
            System.exit(0);
        }
        return input;
    }

    /**
     * Returns the user date, which must be specified in YYMMDD format. Loops until the user enters a valid date.
     * @param prompt - a terminal prompt
     * @return user date
     */
    public String get_user_event_date(String prompt) {
        boolean input_OK = false;
        int current_date = Integer.parseInt(get_current_date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        sdf.setLenient(false);
        Date date = null;

        while (!input_OK) {
            String user_date = get_user_input(prompt);
            try {
                date = sdf.parse(user_date);
                if (check_valid_date(current_date, Integer.parseInt(sdf.format(date)))) {
                    System.err.println("The date of an event must be within two years of the current date.");
                }
                else {
                    input_OK = true;
                }
            }
            catch (ParseException e) {
                System.err.println("Invalid event date. The event date must be in the form of YYMMDD, where:\n" +
                        "YY is a number from 00-99, representing the year of an event.\n" +
                        "MM is a number from 01-12, representing the month of the event.\n" +
                        "DD is a number from 01-31, representing the day of the event.");
            }
        }
        return sdf.format(date);
    }

    public boolean check_valid_date(int current_date, int user_date) {
        if (current_date > user_date) {
            return false;
        }
        if (Math.abs(current_date - user_date) > 20000) {
            return true;
        }
        return false;
    }

    /**
     * Returns the number of tickets specified by the user. The number of tickets must be between MIN_TICKETS and
     * MAX_TICKETS (as defined in Event). Loops until the user enters a valid number of tickets
     *
     * @param prompt - a terminal prompt
     * @return the number of tickets
     */
    public int get_user_event_tickets(String prompt) {
        boolean input_OK = false;
        int user_int = 0;
        while (!input_OK) {
            try {
                user_int = Integer.parseInt(get_user_input(prompt));
                if (user_int < Event.MIN_TICKETS) {
                    System.err.println("The number of tickets entered cannot be less than " + Event.MIN_TICKETS + ".");
                }
                else if (user_int > Event.MAX_TICKETS) {
                    System.err.println("The number of tickets entered cannot be greater than " + Event.MAX_TICKETS + ".");
                }
                else {
                    input_OK = true;
                }
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid number of tickets entered.");
            }
        }
        return user_int;
    }

    /**
     * Gets the event name from the user. The event name must be less than or equal to 20 characters. The function will
     * loop until the user provides valid input.
     * @param prompt - a terminal prompt
     * @return the event name
     */
    public String get_user_event_name(String prompt) {
        boolean input_OK = false;
        String user_event = "";
        while (!input_OK) {
            user_event = get_user_input(prompt);
            if (user_event.length() > Event.MAX_EVENT_NAME) {
                System.err.println("The name of an event cannot exceed " + Event.MAX_EVENT_NAME + " characters.");
            }
            else {
                input_OK = true;
            }
        }
        return user_event;
    }

    /**
     * Returns the current date in YYMMDD format as a string
     * @return the current date
     */
    public String get_current_date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date now = new Date();
        return sdf.format(now);
    }

    /**
     * Parses the events file into a list of Event objects. Assumes that the file is properly formatted.
     * @param events_file - the events file to read
     * @return the list of events
     */
    public ArrayList<Event> read_events_file(String events_file) {
        ArrayList<Event> events = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(events_file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Event names should be 20 characters long
                String event_name = line.substring(0, 20).trim();
                // ticket numbers should be 5 characters long
                int ticket_num = Integer.parseInt(line.substring(21));
                Event event = new Event(event_name, ticket_num);
                events.add(event);
            }
        }
        catch (IOException e) {
            System.err.println("[INTERNAL ERROR] Unable to read events file '" + events_file + "': " + e.getMessage());
            System.exit(1);
        }

        return events;
    }

    public String create_transaction_file(int session_number) {
        String filename = "transaction-" + get_current_date() + "-" + session_number;
        File file = new File(filename);

        if (file.exists()) {
            return filename;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("[INTERNAL ERROR] Unable to create transaction file '" + filename + "': " + e.getMessage());
            System.exit(1);
        }

        return filename;
    }

    /**
     * Writes a list of transactions to a specified transaction file.
     * @param t_file - the transaction file
     * @param transactions - lost of transactions
     */
    public void write_transactions(String t_file, ArrayList<Transaction> transactions) {
        File file = new File(t_file);
        try {
            FileWriter fw = new FileWriter(file);
            for (Transaction t : transactions) {
                fw.write(t.toString() + '\n');
            }
            fw.close();
        }
        catch (IOException e) {
            System.err.println("[INTERNAL ERROR] Unable to write transactions to file '" + t_file + "': " + e.getMessage());
            System.exit(1);
        }
    }
}
