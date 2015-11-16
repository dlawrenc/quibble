/**
 * Created by dlawrence on 10/11/15.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class QuibbleBE {
    private String current_date;
    private ArrayList<Event> events;
    private QuibbleIO qio;

    public QuibbleBE() {
        qio = new QuibbleIO();
        current_date = qio.get_current_date();
        events = new ArrayList<>();
    }

    public void start() {
        read_master();
        read_merged();
        Collections.sort(events);
        create_master();
        create_events();
    }

    private void read_master() {
        try (BufferedReader br = new BufferedReader(new FileReader("master-events"))) {
            String m;
            while ((m = br.readLine()) != null) {
                add_from_master(m);
            }
        }
        // If we haven't created a master events file, then we don't need to do anything.
        catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    private void read_merged() {
        try (BufferedReader br = new BufferedReader(new FileReader("merged-transactions"))) {
            String t;
            while ((t = br.readLine()) != null) {
                handle_transaction(t);
            }
        }
        catch (FileNotFoundException e) {
            System.exit(1);
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    private void create_master() {
        File master = new File("master-events");
        if (master.exists()) {
            master.delete();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(master))) {
            for (Event event : events) {
                bw.write(event.to_master_event());
                bw.write("\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.exit(1);
        }

    }

    private void create_events() {
        File current_events = new File("current-events");
        if (current_events.exists()) {
            current_events.delete();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(current_events))) {
            for (Event event : events) {
                bw.write(event.to_current_event());
                bw.write("\n");
            }
            // write the END transaction to the file
            bw.write(String.format("%-20s", "END") + " " + String.format("%5s", "0").replace(" ", "0"));
            bw.close();
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    public void add_from_master(String m) {
        String event_date = m.substring(0, 6).trim();
        int tickets = Integer.parseInt(m.substring(7, 12));
        String event_name = m.substring(13).trim();

        if (Integer.parseInt(event_date) < Integer.parseInt(current_date)) {
            return;
        }

        events.add(new Event(event_name, event_date, tickets));
    }

    public void handle_transaction(String t) {
        int t_code = Integer.parseInt(t.substring(0, 2));
        String event_name = t.substring(3,23).trim();
        String event_date = t.substring(24, 30).trim();
        int tickets = Integer.parseInt(t.substring(31));

        // command is logout
        if (t_code == 0) {
            return;
        }

        Event event = new Event(event_name, event_date, tickets);
        Event found_event;

        // command is create
        if (t_code == 3) {
            events.add(event);
        }

        found_event = events.get(events.indexOf(event));

        // command is sell
        if (t_code == 1) {
            try {
                found_event.sell_tickets(tickets);
            }
            catch (EventException e) {
                return;
            }
        }

        // command is return
        if (t_code == 2) {
            try {
                found_event.return_tickets(tickets);
            }
            catch (EventException e) {
                return;
            }
        }

        // command is add
        if (t_code == 4) {
            try {
                found_event.add_tickets(tickets);
            }
            catch (EventException e) {
                return;
            }
        }
        // command is delete
        if (t_code == 5) {
            events.remove(found_event);
        }
    }

}
