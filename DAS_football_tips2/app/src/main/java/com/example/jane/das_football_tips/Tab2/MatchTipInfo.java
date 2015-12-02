package com.example.jane.das_football_tips.Tab2;

/**
 * Created by Jane on 10/20/2015.
 */
public class MatchTipInfo {

    String home_team;
    String away_team;
    String goal_bet;
    String full_time_bet;

    public MatchTipInfo(String home_team, String away_team, String goal_bet, String full_time_bet) {
        this.home_team = home_team;
        this.away_team = away_team;
        this.goal_bet = goal_bet;
        this.full_time_bet = full_time_bet;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }

    public String getGoal_bet() {
        return goal_bet;
    }

    public void setGoal_bet(String goal_bet) {
        this.goal_bet = goal_bet;
    }

    public String getFull_time_bet() {
        return full_time_bet;
    }

    public void setFull_time_bet(String full_time_bet) {
        this.full_time_bet = full_time_bet;
    }
}
