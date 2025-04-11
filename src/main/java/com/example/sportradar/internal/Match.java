package com.example.sportradar.internal;

class Match {
    private final String homeTeam;
    private final String awayTeam;
    private int homeScore;
    private int awayScore;

    Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    String getHomeTeam() {
        return homeTeam;
    }

    String getAwayTeam() {
        return awayTeam;
    }

    int getHomeScore() {
        return homeScore;
    }

    int getAwayScore() {
        return awayScore;
    }

    void updateScore(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}