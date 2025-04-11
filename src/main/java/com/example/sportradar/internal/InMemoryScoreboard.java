package com.example.sportradar.internal;

import com.example.sportradar.api.MatchScore;
import com.example.sportradar.api.Scoreboard;
import com.example.sportradar.api.exceptions.DuplicateTeamNamesException;
import com.example.sportradar.api.exceptions.MatchAlreadyExistsException;
import com.example.sportradar.api.exceptions.MatchNotFoundException;
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

        homeTeam = cleanTeamName(homeTeam);
        awayTeam = cleanTeamName(awayTeam);

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
        throwIfScoreIsNegative(homeScore, awayScore);
        throwIfNamesNullOrBlank(homeTeam, awayTeam);

        homeTeam = cleanTeamName(homeTeam);
        awayTeam = cleanTeamName(awayTeam);

        for (Match match : matches) {
            if (isMatchEqual(match, homeTeam, awayTeam)) {
                match.updateScore(homeScore, awayScore);
                return;
            }
        }

        throw new MatchNotFoundException(
                String.format("Match does not exist for teams: %s vs %s", homeTeam, awayTeam)
        );
    }


    @Override
    public void endMatch(String homeTeam, String awayTeam) {
        throwIfNamesNullOrBlank(homeTeam, awayTeam);

        homeTeam = cleanTeamName(homeTeam);
        awayTeam = cleanTeamName(awayTeam);

        for (Match match : matches) {
            if (isMatchEqual(match, homeTeam, awayTeam)) {
                matches.remove(match);
                return;
            }
        }

        throw new MatchNotFoundException(
                String.format("Match does not exist for teams: %s vs %s", homeTeam, awayTeam)
        );
    }

    @Override
    public List<MatchScore> getMatchSummary() {
        return matches.stream()
                .sorted(this::compareMatches)
                .map(this::mapToMatchScore)
                .toList();
    }

    private int compareMatches(Match match1, Match match2) {
        // Compare by total score first
        int scoreComparison = compareByTotalScore(match1, match2);

        // If scores not equal, use the total score for sorting
        if (scoreComparison != 0) {
            return scoreComparison;
        }

        // If scores are equal, sort by inverse of order of insertion
        return Integer.compare(matches.indexOf(match2), matches.indexOf(match1));
    }

    private int compareByTotalScore(Match match1, Match match2) {
        return Integer.compare(
                (match2.getHomeScore() + match2.getAwayScore()),
                (match1.getHomeScore() + match1.getAwayScore())
        );
    }

    private MatchScore mapToMatchScore(Match match) {
        return new MatchScore(match.getHomeTeam(), match.getAwayTeam(), match.getHomeScore(), match.getAwayScore());
    }

    private String cleanTeamName(String teamName) {
        return teamName.trim();
    }

    private void throwIfScoreIsNegative(int homeScore, int awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
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

    // Treat the match as equal if the teams are the same, regardless of order
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
