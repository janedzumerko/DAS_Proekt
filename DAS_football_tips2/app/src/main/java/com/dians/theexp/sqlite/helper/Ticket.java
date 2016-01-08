package com.dians.theexp.sqlite.helper;

/**
 * Created by k1ko on 11/24/15.
 */
public class Ticket {

    private Long id;
    private String datetime;

    public Ticket() {
    }

    public Ticket(Long id, String datetime) {
        this.id = id;
        this.datetime = datetime;
    }

    public Ticket(String datetime) {
        this.datetime = datetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return datetime;
    }
}
