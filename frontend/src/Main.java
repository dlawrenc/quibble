/**
 * @author Dan Lawrence, Jerry Mak
 */

import java.io.File;

/**
 * Starts and runs the Quibble front end. Accepts the current events file as a string argument. If this file is not
 * specified, the Quibble front end will start without parsing the events file.
 */
public class Main {
    public static void main(String[] args) {
        // start the front end without parsing the events file
        QuibbleFE fe = null;
        File current_events = new File("current-events");
        if (current_events.exists()) {
            fe = new QuibbleFE("current-events");
        }
        else {
            fe = new QuibbleFE();
        }
        fe.start();
    }
}
