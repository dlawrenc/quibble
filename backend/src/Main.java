/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Starts and runs the Quibble front end. Accepts the current events file as a string argument. If this file is not
 * specified, the Quibble front end will start without parsing the events file.
 */
public class Main {
    public static void main(String[] args) {
        QuibbleBE be = new QuibbleBE();
        be.start();
    }

}
