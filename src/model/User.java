package model;

/**
 * Class representing a User.  Users log in with a username
 * and password, and can create, update, and delete customer's
 * and appointments.
 *
 * @author Jonathan Hawranko
 */
public class User {
    private int userId;
    private String userName;
    private String password;

    /**
     * Constructor for a User.
     *
     * @param userId unique ID used by database
     * @param userName case-sensitive username
     * @param password case-sensitive plaintext password
     */
    public User(int userId, String userName, String password) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Returns the user's unique ID used by the database.
     *
     * @return user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user's ID.  Used when loading a user from
     * the database.
     *
     * @param userId the user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the User's username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's username.
     *
     * @param userName the username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the user's password in plaintext.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password in plaintext.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
