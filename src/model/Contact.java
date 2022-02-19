package model;

/**
 * Class representing a Contact.  Every appointment has a Contact.
 *
 * @author Jonathan Hawranko
 */
public class Contact {
    private int contactId;
    private String name;
    private String email;

    /**
     * Constructor for a contact.  Used when loading contacts from the database.
     *
     * @param contactId The Contact's ID
     */
    public Contact(int contactId) {
        super();
        this.contactId = contactId;
    }

    /**
     * Returns the contact's ID used by the database.
     *
     * @return The Contact's ID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets the Contact's ID.
     * @param contactId the contact's ID
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Returns the Contact's name.
     *
     * @return Name of the contact
     */
    public String getName() {
        return name;
    }

    /**
     * Set's the contact's name.
     * @param name Name of the contact
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the Contact's email address.
     *
     * @return Email for the contact
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set's the contact's email address.
     *
     * @param email Email for the contact
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the contact's name as a String representation of this Contact.
     *
     * @see #getName()
     * @return name as String representation of contact
     */
    @Override
    public String toString() {
        return getName();
    }
}
