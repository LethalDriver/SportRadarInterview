package com.example.sportradar.api;

import java.util.List;
import com.example.sportradar.api.exceptions.MatchNotFoundException;
import com.example.sportradar.api.exceptions.TeamAlreadyInMatchException;
import com.example.sportradar.api.exceptions.DuplicateTeamNamesException;
import com.example.sportradar.api.exceptions.MatchAlreadyExistsException;

/**
 * The {@code Scoreboard} interface defines the contract for managing and retrieving information
 * about sports matches. It provides methods to start a match, update scores, end a match, and
 * retrieve a summary of matches in progress.
 */
public interface Scoreboard {

    /**
     * The initial score for both teams when a match starts.
     */
    int INITIAL_SCORE = 0;

    /**
     * Starts a new match between the specified home and away teams.
     *
     * @param homeTeam the name of the home team, whitespaces are removed before processing
     * @param awayTeam the name of the away team, whitespaces are removed before processing
     * @throws IllegalArgumentException if the team names are null, blank or contain only whitespace
     * @throws DuplicateTeamNamesException if the team names are the same
     * @throws MatchAlreadyExistsException if a match already exists for the specified teams
     * @throws TeamAlreadyInMatchException if either team is already in a match
     */
    void startMatch(String homeTeam, String awayTeam);

    /**
     * Updates the score for an ongoing match between the specified home and away teams.
     *
     * @param homeTeam the name of the home team, whitespaces are removed before processing
     * @param awayTeam the name of the away team, whitespaces are removed before processing
     * @param homeScore the new score for the home team
     * @param awayScore the new score for the away team
     * @throws IllegalArgumentException if the team names are null, blank, or the scores are negative
     * @throws MatchNotFoundException if the match does not exist
     */
    void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore);

    /**
     * Ends an ongoing match between the specified home and away teams.
     *
     * @param homeTeam the name of the home team, whitespaces are removed before processing
     * @param awayTeam the name of the away team, whitespaces are removed before processing
     * @throws IllegalArgumentException if the team names are null or blank
     * @throws MatchNotFoundException if the match does not exist
     */
    void endMatch(String homeTeam, String awayTeam);

    /**
     * Retrieves a summary of matches currently in progress. The matches are ordered by their
     * total score in descending order. Matches with the same total score are ordered by the
     * most recently started match.
     *
     * @return a list of {@code MatchScore} objects representing the summary of matches
     */
    List<MatchScore> getMatchSummary();
}