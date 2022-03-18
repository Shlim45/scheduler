package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Class representing a Country.  Countries are used in conjunction
 * with Top-Level Divisions.
 *
 * @author Jonathan Hawranko
 */
public class Country {
    private int countryId;
    private String country;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructor for a country.  Used when loading countries from
     * the database.
     *
     * @param countryId the Country's ID
     */
    public Country(int countryId) {
        super();
        this.countryId = countryId;
    }

    /**
     * Returns the countries unique ID used by the database.
     *
     * @return country ID
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Sets the countries ID.
     *
     * @param countryId the country ID
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * Returns the name of this Country.
     *
     * @return name of the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the name of this Country.
     *
     * @param country name of the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * The date and time when the Country was created.  Kept in local time
     * while in memory, and converted to UTC when written to the database.
     *
     * @return The date and time country was created
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets the date and time the country was created.
     *
     * @param createDate The date and time country was created, in local time
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * The user who created this country.
     *
     * @return User who created the country
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the country.
     *
     * @param createdBy The user who created the country
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * The date and time the country was last updated.  Kept in local time while
     * in memory, and converted to UTC when written to the database.
     *
     * @return The date and time country was last updated
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the date and time the country was last updated.
     *
     * @param lastUpdate The date and time country was last updated
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * The user who last updated the country.
     *
     * @return Name of user
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the name of the user who last updated the country.
     *
     * @param lastUpdatedBy Name of user
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
