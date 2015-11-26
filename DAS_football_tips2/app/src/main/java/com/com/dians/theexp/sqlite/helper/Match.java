package com.com.dians.theexp.sqlite.helper;

/**
 * Created by k1ko on 11/24/15.
 */
public class Match {

    private long id;
    private String homeTeam;
    private String awayTeam;
    private Integer prediction;


    public Match() {

    }

    public Match(String homeTeam, String awayTeam, Integer prediction) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.prediction = prediction;
    }

    public Match(long id, String homeTeam, String awayTeam, Integer prediction) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.prediction = prediction;
    }

    public Integer getPrediction() {
        return prediction;
    }

    public void setPrediction(Integer prediction) {
        this.prediction = prediction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAwayTeam() {

        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeam() {

        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    @Override
    public String toString() {
        return homeTeam + " " + prediction + " " + awayTeam;
    }
}
