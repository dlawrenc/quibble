/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Public class for representing a transaction. Encapsulates a transaction code and an event associated with the
 * transaction.
 *
 * Contains only one method, toString, which is used mainly to print the transaction to the current transaction file.
 */
public class Transaction {
    private int t_code;
    private Event event;

    /**
     * One parameter constructor for the Transaction class. Sets the transaction code and creates an empty event.
     * Used primarily for the "logout" transaction, which does not require specific event information.
     * @param t_code_ - code for transaction
     */
    public Transaction(int t_code_) {
        t_code = t_code_;
        event = new Event();
    }

    /**
     * Two parameter constructor for the Transaction class. Sets the transaction code and clones the specified event.
     * Used for transactions other than logout, since these require event info.
     * @param t_code_ - the transaction code
     * @param event_ - the associated event
     */
    public Transaction(int t_code_, Event event_) {
        t_code = t_code_;
        event = event_.clone();
    }

    /**
     * Displays the transaction as a 36 character string, with each field padded properly.
     * @return
     */
    @Override public String toString() {
        String line_to_add = String.format("%2s" ,t_code).replace(" ", "0") // pad left side with up to 2 zeros
                             + " " + String.format("%-20s", event.get_event_name()) // pad right side with up to 20 spaces
                             + " " + String.format("%6s", event.get_event_date()) .replace(" ", "0") // pad right side with up to 6 zeros
                             + " " + String.format("%5s", event.get_num_tickets()).replace(" ", "0"); // pad left side with up to 5 zeros
        return line_to_add;
    }



}
