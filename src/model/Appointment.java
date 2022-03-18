package model;

import java.time.LocalDateTime;

/**
 * Class representing an Appointment. An appointment is held by a User, and
 * attended by a Customer.
 *
 * @author Jonathan Hawranko
 */
public class Appointment {
    private int apptId;
    private String title;
    private String desc;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private int customerId;
    private int userId;
    private int contactId;
    private String contact;

    /**
     * Constructor for an empty appointment.  Used when creating a new appointment.
     */
    public Appointment() {
        super();
    }

    /**
     * Constructor for an appointment.  Used when loading appointments from the database.
     * @param id The Appointment ID
     */
    public Appointment(int id) {
        super();
        this.apptId = id;
    }

    /**
     * Returns the appointment ID used by the database.  Automatically generated.
     *
     * @return The Appointment ID
     */
    public int getApptId() {
        return apptId;
    }

    /**
     * Sets the appointment ID. <b>NOTE:</b> appointment ID is automatically
     * generated upon database insertion.  Only use this method when loading
     * from the database.
     *
     * @param apptId The Appointment ID
     */
    public void setApptId(int apptId) {
        this.apptId = apptId;
    }

    /**
     * The title of the appointment.
     *
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the appointment's title.
     *
     * @param title The title of the appointment
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The description of the appointment.
     *
     * @return Description
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the appointment's description.
     *
     * @param desc The description of the appointment
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * The location of the appointment.
     *
     * @return Location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the appointment's location.
     *
     * @param location The location of the appointment
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The type of appointment.
     *
     * @return Type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the appointment's type.
     *
     * @param type The type of appointment
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * The <b>ZonedDateTime</b> start of the appointment.  This time is kept in
     * local time while in memory, and converted to UTC when written to the database.
     *
     * @return The start time of the appointment, in local time
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Sets the appointments start time, in local time.
     *
     * @param start The start time of the appointment, in local time
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * The <b>ZonedDateTime</b> end of the appointment.  This time is kept in
     * local time while in memory, and converted to UTC when written to the database.
     *
     * @return The end time of the appointment, in local time
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Sets the appointments end time, in local time.
     *
     * @param end The end time of the appointment, in local time
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * The date and time when the appointment was created.  Kept in local time
     * while in memory, and converted to UTC when written to the database.
     *
     * @return The date and time appointment was created
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets the date and time the appointment was created.
     *
     * @param createDate The date and time appointment was created, in local time
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * The user who created this appointment.
     *
     * @return User who created appointment
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the appointment.
     *
     * @param createdBy The user who created the appointment
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * The date and time the appointment was last updated.  Kept in local time while
     * in memory, and converted to UTC when written to the database.
     *
     * @return The date and time appointment was last updated
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the date and time the appointment was last updated.
     *
     * @param lastUpdate The date and time appointment was last updated
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * The user who last updated the appointment.
     *
     * @return Name of user
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the name of the user who last updated the appointment.
     *
     * @param lastUpdatedBy Name of user
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * The Customer ID of the customer for whom the appointment is for.
     *
     * @return ID of appointment's Customer
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the Customer ID of the customer for whom the appointment is for.
     *
     * @param customerId ID of appointment's Customer
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * The User ID of the user who will hold the appointment.
     *
     * @return ID of appointment's User
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the User ID of the user who will hold the appointment.
     *
     * @param userId ID of appointment's user
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * The ID of the appointment's contact.
     *
     * @return ID of appointment's contact
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets the contact ID for the appointment.
     *
     * @param contactId ID of appointment's contact
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * The name of the contact for the appointment.
     *
     * @return name of contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the name of the contact for the appointment.
     *
     * @param contact name of contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }
}
