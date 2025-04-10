package com.example.sportradar.internal;

import com.example.sportradar.api.MatchScore;
import com.example.sportradar.api.Scoreboard;
import com.example.sportradar.api.exceptions.DuplicateTeamNamesException;
import com.example.sportradar.api.exceptions.MatchAlreadyExistsException;
import com.example.sportradar.api.exceptions.TeamAlreadyInMatchException;

import java.util.ArrayList;
import java.util.List;

public class InMemoryScoreboard implements Scoreboard {
    private final List<Match> matches;

    public InMemoryScoreboard() {
        this.matches = new ArrayList<>();
    }

    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Team names cannot be null");
        }

        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Team names cannot be empty");
        }

        homeTeam = homeTeam.trim();
        awayTeam = awayTeam.trim();

        if (homeTeam.equals(awayTeam)) {
            throw new DuplicateTeamNamesException("Home and away teams cannot be the same");
        }

        for (Match match : matches) {
            if (match.getHomeTeam().equals(homeTeam) && match.getAwayTeam().equals(awayTeam)) {
                throw new MatchAlreadyExistsException(
                        String.format("Match already exists for teams: %s vs %s", homeTeam, awayTeam)
                );
            }

            if (match.getHomeTeam().equals(homeTeam)) {
                throw new TeamAlreadyInMatchException(
                        String.format("Team %s is already in a match", homeTeam)
                );
            }

            if (match.getAwayTeam().equals(awayTeam)) {
                throw new TeamAlreadyInMatchException(
                        String.format("Team %s is already in a match", awayTeam)
                );
            }
        }

        matches.add(new Match(homeTeam, awayTeam));
    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {

    }

    @Override
    public void endMatch(String homeTeam, String awayTeam) {

    }

    @Override
    public List<MatchScore> getMatchSummary() {
        return matches.stream()
                .map(match -> new MatchScore(match.getHomeTeam(), match.getAwayTeam(), match.getHomeScore(), match.getAwayScore()))
                .toList();
    }
}
