package com.example.sportradar.api;

/**
 * Represents the score of a sports match, including the teams involved and their respective scores.
 * This class is implemented as a record, providing an immutable data structure.
 *
 * @param homeTeam the name of the home team
 * @param awayTeam the name of the away team
 * @param homeScore the score of the home team
 * @param awayScore the score of the away team
 */
public record MatchScore(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore
) {
}