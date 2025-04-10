package com.example.sportradar.internal;

import com.example.sportradar.api.MatchScore;
import com.example.sportradar.api.Scoreboard;

import java.util.List;

public class InMemoryScoreboard implements Scoreboard {
    @Override
    public void startMatch(String homeTeam, String awayTeam) {

    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {

    }

    @Override
    public void endMatch(String homeTeam, String awayTeam) {

    }

    @Override
    public List<MatchScore> getMatchSummary() {
        return null;
    }
}
