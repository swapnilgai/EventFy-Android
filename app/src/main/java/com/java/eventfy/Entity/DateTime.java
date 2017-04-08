package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by swapnil on 2/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateTime  implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private int id;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String dateTimeFrom;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String dateTimeTo;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String timeZone;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }


    private Events event;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getDateTimeFrom() {
        return dateTimeFrom;
    }
    public void setDateTimeFrom(String dateTimeFrom) {
        this.dateTimeFrom = dateTimeFrom;
    }
    public String getDateTimeTo() {
        return dateTimeTo;
    }
    public void setDateTimeTo(String dateTimeTo) {
        this.dateTimeTo = dateTimeTo;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


}
