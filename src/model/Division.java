package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Class representing a Top-Level Division.  Divisions are used
 * in conjuction with Countries.
 *
 * @author Jonathan Hawranko
 */
public class Division {
    private int divisionId;
    private String division;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private int countryId;

    /**
     * Constructor for a division.  Used when loading divisions
     * from the database.
     *
     * @param divId the Division's ID
     */
    public Division(int divId) {
        super();
        this.divisionId = divId;
    }

    /**
     * Returns the division's unique ID used by the database.
     *
     * @return division ID
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets the division's ID.
     *
     * @param divisionId the division ID
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Returns the name of this Division.
     *
     * @return name of the division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the name of this Division.
     *
     * @param division name of the division
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * The date and time when the Division was created.  Kept in local time
     * while in memory, and converted to UTC when written to the database.
     *
     * @return The date and time division was created
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets the date and time the Division was created.
     *
     * @param createDate The date and time division was created, in local time
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * The user who created the Division.
     *
     * @return User who created the division
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the Division.
     *
     * @param createdBy The user who created the division
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * The date and time the Division was last updated.  Kept in local time while
     * in memory, and converted to UTC when written to the database.
     *
     * @return The date and time division was last updated
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the date and time the Division was last updated.
     *
     * @param lastUpdate The date and time division was last updated
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * The user who last updated the Division.
     *
     * @return Name of user
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the name of the user who last updated the Division.
     *
     * @param lastUpdatedBy Name of user
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns the ID of the country to which this division belongs.
     *
     * @return ID of country
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Sets the country ID for the division.
     *
     * @param countryId ID of country
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * Returns the division's name as a String representation of this Division.
     *
     * @see #getDivision()
     * @return name as String representation of division
     */
    @Override
    public String toString() {
        return division;
    }
}
