package com.example.sportradar.internal;

import com.example.sportradar.api.MatchScore;
import com.example.sportradar.api.Scoreboard;
import com.example.sportradar.api.exceptions.DuplicateTeamNamesException;
import com.example.sportradar.api.exceptions.MatchAlreadyExistsException;
import com.example.sportradar.api.exceptions.MatchNotFoundException;
import com.example.sportradar.api.exceptions.TeamAlreadyInMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class InMemoryScoreboardTest {
    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = new InMemoryScoreboard();
    }

    @DisplayName("startMatch: should add match when two correct names are given")
    @Test
    void startMatch_shouldAddMatch_whenTwoCorrectNamesAreGiven() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly(homeTeam, awayTeam);
    }

    @DisplayName("startMatch: should add multiple matches when different teams are given")
    @Test
    void startMatch_shouldAddMultipleMatches_whenDifferentTeamsAreGiven() {
        String homeTeam1 = "Spain";
        String awayTeam1 = "Brazil";
        String homeTeam2 = "Argentina";
        String awayTeam2 = "Germany";

        scoreboard.startMatch(homeTeam1, awayTeam1);
        scoreboard.startMatch(homeTeam2, awayTeam2);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(2)
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactlyInAnyOrder(
                        tuple(homeTeam1, awayTeam1),
                        tuple(homeTeam2, awayTeam2)
                );
    }

    @DisplayName("startMatch: should initialize match with both teams having initial score")
    @Test
    void startMatch_shouldInitializeMatchWithBothTeamsHavingInitialScore() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeScore, MatchScore::awayScore)
                .containsExactly(Scoreboard.INITIAL_SCORE, Scoreboard.INITIAL_SCORE);
    }

    @DisplayName("startMatch: should throw exception when match already exists")
    @Test
    void startMatch_shouldThrowException_whenMatchAlreadyExists() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                .isInstanceOf(MatchAlreadyExistsException.class);
        assertThatThrownBy(() -> scoreboard.startMatch(awayTeam, homeTeam))
                .isInstanceOf(MatchAlreadyExistsException.class);
    }

    @DisplayName("startMatch: should throw exception when team or both teams are empty")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNames")
    void startMatch_shouldThrowException_whenTeamOrBothTeamsAreEmpty(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideEmptyTeamNames() {
        return Stream.of(
                Arguments.of("", "Brazil"),
                Arguments.of("Spain", ""),
                Arguments.of("", "")
        );
    }

    @DisplayName("startMatch: should throw exception when team or both teams are null")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideNullTeamNames")
    void startMatch_shouldThrowException_whenTeamOrBothTeamsAreNull(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideNullTeamNames() {
        return Stream.of(
                Arguments.of(null, "Brazil"),
                Arguments.of("Spain", null),
                Arguments.of(null, null)
        );
    }

    @DisplayName("startMatch: should throw exception when both teams are the same")
    @Test
    void startMatch_shouldThrowException_whenBothTeamsAreTheSame() {
        String team = "Spain";

        assertThatThrownBy(() -> scoreboard.startMatch(team, team))
                .isInstanceOf(DuplicateTeamNamesException.class);
    }

    @DisplayName("startMatch: should trim whitespaces from team names")
    @Test
    void startMatch_shouldTrimWhitespacesFromTeamNames() {
        String homeTeam = "   Spain   ";
        String awayTeam = "   Brazil   ";

        scoreboard.startMatch(homeTeam, awayTeam);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly("Spain", "Brazil");
    }

    @DisplayName("startMatch: should throw exception when team or teams are empty after trimming")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNamesWithWhitespace")
    void startMatch_shouldThrowException_whenHomeTeamIsEmptyAfterTrimming(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideEmptyTeamNamesWithWhitespace() {
        return Stream.of(
                Arguments.of("   ", "Brazil"),
                Arguments.of("Spain", "   "),
                Arguments.of("   ", "   ")
        );
    }

    @DisplayName("startMatch: should throw exception when team is already in a match")
    @Test
    void startMatch_shouldThrowException_whenTeamIsAlreadyInAMatch() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, "Argentina"))
                .isInstanceOf(TeamAlreadyInMatchException.class);
        assertThatThrownBy(() -> scoreboard.startMatch("Argentina", awayTeam))
                .isInstanceOf(TeamAlreadyInMatchException.class);
    }

    @DisplayName("updateScore: should update score when match is ongoing")
    @Test
    void updateScore_shouldUpdateScore_whenMatchIsOngoing() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.updateScore(homeTeam, awayTeam, 1, 2);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeScore, MatchScore::awayScore)
                .containsExactly(1, 2);
    }

    @DisplayName("updateScore: should update scores for multiple ongoing matches")
    @Test
    void updateScore_shouldUpdateScoresForMultipleOngoingMatches() {
        String homeTeam1 = "Spain";
        String awayTeam1 = "Brazil";
        String homeTeam2 = "Argentina";
        String awayTeam2 = "Germany";

        scoreboard.startMatch(homeTeam1, awayTeam1);
        scoreboard.startMatch(homeTeam2, awayTeam2);

        scoreboard.updateScore(homeTeam1, awayTeam1, 1, 2);
        scoreboard.updateScore(homeTeam2, awayTeam2, 3, 4);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(2)
                .extracting(MatchScore::homeScore, MatchScore::awayScore)
                .containsExactlyInAnyOrder(
                        tuple(1, 2),
                        tuple(3, 4)
                );
    }

    @DisplayName("updateScore: should trim whitespaces from team names")
    @Test
    void updateScore_shouldTrimWhitespacesFromTeamNames() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";
        String homeTeamWithWhitespace = "   Spain   ";
        String awayTeamWithWhitespace = "   Brazil   ";

        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.updateScore(homeTeamWithWhitespace, awayTeamWithWhitespace, 1, 2);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly("Spain", "Brazil");
    }

    @DisplayName("updateScore: should throw exception when match is not started")
    @Test
    void updateScore_shouldThrowException_whenMatchIsNotStarted() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, 1, 2))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @DisplayName("updateScore: should throw exception when team names are empty")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNames")
    void updateScore_shouldThrowException_whenTeamNamesAreEmpty(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, 1, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("updateScore: should throw exception when team names are null")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideNullTeamNames")
    void updateScore_shouldThrowException_whenTeamNamesAreNull(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, 1, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("updateScore: should throw exception when team or teams are empty after trimming")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNamesWithWhitespace")
    void updateScore_shouldThrowException_whenTeamNamesAreEmptyAfterTrimming(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, 1, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("updateScore: should throw exception when match is already ended")
    @Test
    void updateScore_shouldThrowException_whenMatchIsAlreadyEnded() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.endMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, 1, 2))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @DisplayName("updateScore: should throw exception when score or scores are negative")
    @ParameterizedTest(name = "homeScore: {0}, awayScore: {1}")
    @MethodSource("provideNegativeScores")
    void updateScore_shouldThrowException_whenScoreOrScoresAreNegative(int homeScore, int awayScore) {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.updateScore(homeTeam, awayTeam, homeScore, awayScore))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideNegativeScores() {
        return Stream.of(
                Arguments.of(-1, 0),
                Arguments.of(0, -1),
                Arguments.of(-1, -1)
        );
    }

    @DisplayName("endMatch: should end match when match is ongoing")
    @Test
    void endMatch_shouldEndMatch_whenMatchIsOngoing() {
        String homeTeam1 = "Spain";
        String awayTeam1 = "Brazil";
        String homeTeam2 = "Argentina";
        String awayTeam2 = "Germany";

        scoreboard.startMatch(homeTeam1, awayTeam1);
        scoreboard.startMatch(homeTeam2, awayTeam2);

        scoreboard.endMatch(homeTeam1, awayTeam1);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly(homeTeam2, awayTeam2);
    }

    @DisplayName("endMatch: should trim whitespaces from team names")
    @Test
    void endMatch_shouldTrimWhitespacesFromTeamNames() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";
        String homeTeamWithWhitespace = "   Spain   ";
        String awayTeamWithWhitespace = "   Brazil   ";

        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.endMatch(homeTeamWithWhitespace, awayTeamWithWhitespace);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .isEmpty();
    }

    @DisplayName("endMatch: should throw exception when team names are empty")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNames")
    void endMatch_shouldThrowException_whenTeamNamesAreEmpty(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.endMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("endMatch: should throw exception when team names are null")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideNullTeamNames")
    void endMatch_shouldThrowException_whenTeamNamesAreNull(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.endMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("endMatch: should throw exception when team names are empty after trimming")
    @ParameterizedTest(name = "homeTeam: \"{0}\", awayTeam: \"{1}\"")
    @MethodSource("provideEmptyTeamNamesWithWhitespace")
    void endMatch_shouldThrowException_whenTeamNamesAreEmptyAfterTrimming(String homeTeam, String awayTeam) {
        assertThatThrownBy(() -> scoreboard.endMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("endMatch: should throw exception when match is not started")
    @Test
    void endMatch_shouldThrowException_whenMatchIsNotStarted() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        assertThatThrownBy(() -> scoreboard.endMatch(homeTeam, awayTeam))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @DisplayName("endMatch: should throw exception when match is already ended")
    @Test
    void endMatch_shouldThrowException_whenMatchIsAlreadyEnded() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.endMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.endMatch(homeTeam, awayTeam))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @DisplayName("getMatchSummary: should return an empty list when no matches are in progress")
    @Test
    void getMatchSummary_shouldReturnEmptyList_whenNoMatchesInProgress() {
        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary).isEmpty();
    }

    @DisplayName("getMatchSummary: should return a single match when only one match is in progress")
    @Test
    void getMatchSummary_shouldReturnSingleMatch_whenOnlyOneMatchInProgress() {
        String homeTeam = "Spain";
        String awayTeam = "Brazil";

        scoreboard.startMatch(homeTeam, awayTeam);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .hasSize(1)
                .first()
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly(homeTeam, awayTeam);
    }

    @DisplayName("getMatchSummary: should return matches sorted by total score in descending order")
    @Test
    void getMatchSummary_shouldReturnMatchesSortedByTotalScoreInDescendingOrder() {
        scoreboard.startMatch("Argentina", "Germany");
        scoreboard.startMatch("Poland", "France");
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore("Spain", "Brazil", 3, 2);
        scoreboard.updateScore("Argentina", "Germany", 1, 1);
        scoreboard.updateScore("Poland", "France", 8, 0);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam, MatchScore::homeScore, MatchScore::awayScore)
                .containsExactly(
                        tuple("Poland", "France", 8, 0),
                        tuple("Spain", "Brazil", 3, 2),
                        tuple("Argentina", "Germany", 1, 1)
                );
    }

    @DisplayName("getMatchSummary: should return matches with the same total score ordered by most recently started match")
    @Test
    void getMatchSummary_shouldReturnMatchesWithSameTotalScoreOrderedByMostRecentlyStartedMatch() {
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.startMatch("Argentina", "Germany");
        scoreboard.updateScore("Spain", "Brazil", 2, 2);
        scoreboard.updateScore("Argentina", "Germany", 2, 2);

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam)
                .containsExactly(
                        tuple("Argentina", "Germany"),
                        tuple("Spain", "Brazil")
                );
    }

    @DisplayName("getMatchSummary: should handle matches with zero scores")
    @Test
    void getMatchSummary_shouldHandleMatchesWithZeroScores() {
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.startMatch("Argentina", "Germany");

        List<MatchScore> summary = scoreboard.getMatchSummary();
        assertThat(summary)
                .extracting(MatchScore::homeTeam, MatchScore::awayTeam, MatchScore::homeScore, MatchScore::awayScore)
                .containsExactly(
                        tuple("Argentina", "Germany", 0, 0),
                        tuple("Spain", "Brazil", 0, 0)
                );
    }
}