package com.example.sportradar.api;

import java.util.List;

public interface Scoreboard {
    void startMatch(String homeTeam, String awayTeam);
    void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore);
    void endMatch(String homeTeam, String awayTeam);
    List<MatchScore> getMatchSummary();
}
