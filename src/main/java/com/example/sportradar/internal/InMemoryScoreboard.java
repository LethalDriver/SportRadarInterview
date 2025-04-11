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
        throwIfNamesNullOrBlank(homeTeam, awayTeam);

        homeTeam = homeTeam.trim();
        awayTeam = awayTeam.trim();

        throwIfNamesAreEqual(homeTeam, awayTeam);

        for (Match match : matches) {
            throwIfMatchEqual(match, homeTeam, awayTeam);
            throwIfTeamInAMatch(match, homeTeam);
            throwIfTeamInAMatch(match, awayTeam);
        }

        matches.add(new Match(homeTeam, awayTeam, INITIAL_SCORE, INITIAL_SCORE));
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

    private void throwIfNamesNullOrBlank(String homeTeam, String awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Team names cannot be null");
        }

        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Team names cannot be empty");
        }
    }

    private void throwIfNamesAreEqual(String homeTeam, String awayTeam) {
        if (homeTeam.equals(awayTeam)) {
            throw new DuplicateTeamNamesException("Home and away team names cannot be the same");
        }
    }

    private boolean isMatchEqual(Match existingMatch, String homeTeam, String awayTeam) {
        return existingMatch.getHomeTeam().equals(homeTeam) && existingMatch.getAwayTeam().equals(awayTeam) ||
                existingMatch.getHomeTeam().equals(awayTeam) && existingMatch.getAwayTeam().equals(homeTeam);
    }

    private void throwIfMatchEqual(Match existingMatch, String homeTeam, String awayTeam) {
        if (isMatchEqual(existingMatch, homeTeam, awayTeam)) {
            throw new MatchAlreadyExistsException(
                    String.format("Match already exists for teams: %s vs %s", homeTeam, awayTeam)
            );
        }
    }

    private void throwIfTeamInAMatch(Match existingMatch, String team) {
        if (existingMatch.getHomeTeam().equals(team)) {
            throw new TeamAlreadyInMatchException(
                    String.format("Team %s is already in a match", team)
            );
        }

        if (existingMatch.getAwayTeam().equals(team)) {
            throw new TeamAlreadyInMatchException(
                    String.format("Team %s is already in a match", team)
            );
        }
    }
}
