/**
 * @author Dan Lawrence, Jerry Mak
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class for the quibble backend. Contains one public method, start, which runs the backend.
 * The backend will attempt to read in the master events file (with the name master-events in the current directory)
 * if one exists, and will then prune events that have already passed. The backend also expects a merged transaction
 * file, with the name "merged-transactions" to be present in the current directory.
 */
public class QuibbleBE {
    private String current_date;
    private ArrayList<Event> events; // list of events from the master events file

    /**
     * Default constructor for the quibble backend. Instantiates all attributes and gets the current date.
     */
    public QuibbleBE() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date now = new Date();
        current_date = sdf.format(now);
        events = new ArrayList<>();
    }


    /**
     * Runs the quibble backend, which will read in the master events file, modify each event with the contents
     * of the merged transaction file, and generate both the new master events file and current events file.
     */
    public void start() {
        read_master();
        read_merged();
        Collections.sort(events);
        create_master();
        create_events();
    }

    /**
     * Reads in the master events file (if one is present) and populates the list of known events.
     */
    private void read_master() {
        try (BufferedReader br = new BufferedReader(new FileReader("master-events"))) {
            String m;
            while ((m = br.readLine()) != null) {
                // parse each line from the master file and populate the events
                add_from_master(m);
            }
        }
        // If we haven't created a master events file, then we don't need to do anything.
        catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e) {
            System.err.println("[BACKEND ERROR] Unable to read master-events file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Reads in the merged transaction file and modifies each event the backend knows about.
     */
     private void read_merged() {
        try (BufferedReader br = new BufferedReader(new FileReader("merged-transactions"))) {
            String t;
            while ((t = br.readLine()) != null) {
                // modify an event based on the transaction code
                handle_transaction(t);
            }
        }
        catch (IOException e) {
            System.err.println("[BACKEND ERROR] Unable to read merged-transactions file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Creates the master events file based on the current state of the events in the backend. Overwrites the
     * previous master events file.
     */
    private void create_master() {
        File master = new File("master-events");
        if (master.exists()) {
            master.delete();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(master))) {
            for (Event event : events) {
                // write the event as a master event
                bw.write(event.to_master_event());
                bw.write("\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.err.println("[BACKEND ERROR] Unable to create master-events file: " + e.getMessage());
            System.exit(1);
        }

    }

    /**
     * Creates the current events file based on the current state of the events in the backend. Overwrites the
     * previous current events file.
     */
    private void create_events() {
        File current_events = new File("current-events");
        if (current_events.exists()) {
            current_events.delete();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(current_events))) {
            for (Event event : events) {
                // write the event as a current event
                bw.write(event.to_current_event());
                bw.write("\n");
            }
            // write the END transaction to the file
            bw.write(String.format("%-20s", "END") + " " + String.format("%5s", "0").replace(" ", "0"));
            bw.close();
        }
        catch (IOException e) {
            System.err.println("[BACKEND ERROR] Unable to create current-events file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Adds an event to the events list based on a single line in the master events file. The method parses the
     * line adds the event based on the event name and number of tickets remaining. If the event date has already
     * passed, the event is not added to the event list.
     * @param m - the line from the master events file to be parsed
     */
    private void add_from_master(String m) {
        String event_date = m.substring(0, 6).trim();
        int tickets = Integer.parseInt(m.substring(7, 12));
        String event_name = m.substring(13).trim();

        // don't add the event if the day has already passed
        if (Integer.parseInt(event_date) < Integer.parseInt(current_date)) {
            return;
        }

        events.add(new Event(event_name, event_date, tickets));
    }

    /**
     * Modifies an event in the events list based on a single line in the merged transaction file. The method parses the
     * line and modifies an existing event based on the transaction code. The method assumes that the event being
     * modified already exists (i.e the merged transaction file must be properly formed)
     *
     * @param t - the line from the merged transaction file to be parsed
     */
    public void handle_transaction(String t) {
        int t_code = Integer.parseInt(t.substring(0, 2));
        String event_name = t.substring(3,23).trim();
        String event_date = t.substring(24, 30).trim();
        int tickets = Integer.parseInt(t.substring(31));

        // command is logout, nothing to do!
        if (t_code == 0) {
            return;
        }

        Event event = new Event(event_name, event_date, tickets);

        // command is create
        if (t_code == 3) {
            events.add(event);
        }

        Event found_event = events.get(events.indexOf(event));

        // command is sell
        if (t_code == 1) {
            found_event.sell_tickets(tickets);
        }
        // command is return or add
        if (t_code == 2 || t_code == 4) {
            found_event.add_tickets(tickets);
        }
        // command is delete
        if (t_code == 5) {
            events.remove(found_event);
        }
    }
}
