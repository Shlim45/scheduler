package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Class representing a Customer.  A customer can attend
 * appointments.
 *
 * @author Jonathan Hawranko
 */
public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private Division division;

    /**
     * Constructor for an empty customer.  Used when creating a new customer.
     */
    public Customer() {
        super();
    }

    /**
     * Constructor for a customer.  Used when loading customers from the database.
     *
     * @param custId the Customer ID
     */
    public Customer(int custId) {
        super();
        this.customerId = custId;
    }

    /**
     * Returns the customer's unique ID used by the database.
     *
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID. <b>NOTE:</b> customer ID is automatically
     * generated upon database insertion.  Only use this method when loading
     * from the database.
     *
     * @param customerId the customer ID
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the name of the customer.
     *
     * @return name of customer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the customer.
     *
     * @param name name of customer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the address of the customer.
     *
     * @return address of customer
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the customer.
     *
     * @param address address of customer
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the postal code for the customer.
     *
     * @return postal code for customer
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code for the customer.
     * @param postalCode postal code for customer
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns the phone number of the customer.
     *
     * @return phone number of customer
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the customer.
     * @param phone phone number of customer
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * The date and time when the customer was created.  Kept in local time
     * while in memory, and converted to UTC when written to the database.
     *
     * @return The date and time customer was created
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets the date and time the customer was created.
     *
     * @param createDate The date and time customer was created, in local time
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }


    /**
     * The user who created this customer.
     *
     * @return User who created customer
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the customer.
     *
     * @param createdBy The user who created the customer
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * The date and time the customer was last updated.  Kept in local time while
     * in memory, and converted to UTC when written to the database.
     *
     * @return The date and time customer was last updated
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the date and time the customer was last updated.
     *
     * @param lastUpdate The date and time customer was last updated
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * The user who last updated the customer.
     *
     * @return Name of user
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the name of the user who last updated the customer.
     *
     * @param lastUpdatedBy Name of user
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
