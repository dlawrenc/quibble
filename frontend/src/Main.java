/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Starts and runs the Quibble front end. Accepts the current events file as a string argument. If this file is not
 * specified, the Quibble front end will start without parsing the events file.
 */
public class Main {
    public static void main(String[] args) {
        QuibbleFE fe = null;
        if (args.length > 1) {
            System.err.println("Usage: Quibble [events-file]");
            return;
        }
        // start the front end without parsing the events file
        if (args.length == 0) {
            fe = new QuibbleFE();
        }
        // start the front end and parse the events file
        if (args.length == 1) {
            fe = new QuibbleFE(args[0]);
        }
        fe.start();
    }
}
