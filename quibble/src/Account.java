/**
 * @author Dan Lawrence, Jerry Mak
 */

/**
 * Public class that contains Account information for the front end, including:
 *     the current user
 *
 * Contains methods for logging in, logging out, and displaying the current user as a string.
 */
public class Account {
    private String username;
    private String[] valid_users = {"admin", "sales"};

    /**
     * Default constructor for Account class. Sets the username to empty, representing a "logged out" state.
     */
    public Account() {
        username = "";
    }

    /**
     * Method for logging into an account. Before setting the user, the method checks if the user is in the list of
     * known users. If not, it will throw an AccountException.
     * @param user - the user to login
     * @throws AccountException - if the username is invalid
     */
    public void login(String user) throws AccountException {
        if (!is_valid_user(user)) {
            throw new AccountException("Invalid username.");
        }
        username = user;
    }

    /**
     * Method for logging out of an account. Resets the username field to empty. If the account is already logged out,
     * the method will throw an AccountException.
     * @throws AccountException - when the account is already logged out
     */
    public void logout() throws AccountException {
        if (!is_logged_in()) {
            throw new AccountException("You have already logged out.");
        }
        username = "";
    }

    /**
     * Returns true if the username is recognized by the system, false otherwise
     * @param user - the username to be checked
     * @return true if the username is valid
     */
    public boolean is_valid_user(String user) {
        for (String i : valid_users) {
            if (user.equals(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the user is logged in, false otherwise
     * @return true if the user is logged in
     */
    public boolean is_logged_in() {
        // an empty username represents a logged out state in the account
        return !username.isEmpty();
    }

    /**
     * Returns true if the current user is the admin, false otherwise
     * @return true if the user is admin
     */
    public boolean is_admin() {
        return username.equals("admin");
    }

    /**
     * Public override for toString. Returns the name of the current logged in user.
     * @return name of the logged in user
     */
    @Override public String toString() {
        return username;
    }
}
