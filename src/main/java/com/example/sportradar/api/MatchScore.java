package com.example.sportradar.api;

public record MatchScore(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore
) {
}
