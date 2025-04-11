package com.example.sportradar.internal;

import com.example.sportradar.api.MatchScore;
import com.example.sportradar.api.Scoreboard;
import com.example.sportradar.api.exceptions.DuplicateTeamNamesException;
import com.example.sportradar.api.exceptions.MatchAlreadyExistsException;
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

public class InMemoryScoreboardTest {
    private Scoreboard scoreboard;
    @BeforeEach
    void setUp() {
        scoreboard = new InMemoryScoreboard();
    }

    @DisplayName("should add match when two correct names are given")
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

    @DisplayName("should initialize match with both teams having initial score")
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

    @DisplayName("should throw exception when match already exists")
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

    @DisplayName("should throw exception when team or both teams are empty")
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

    @DisplayName("should throw exception when team or both teams are null")
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

    @DisplayName("should throw exception when both teams are the same")
    @Test
    void startMatch_shouldThrowException_whenBothTeamsAreTheSame() {
        String team = "Spain";

        assertThatThrownBy(() -> scoreboard.startMatch(team, team))
                .isInstanceOf(DuplicateTeamNamesException.class);
    }

    @DisplayName("should trim whitespaces from team names")
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

    @DisplayName("should throw exception when home team is empty after trimming")
    @Test
    void startMatch_shouldThrowException_whenHomeTeamIsEmptyAfterTrimming() {
        String homeTeam = "   ";
        String awayTeam = "Brazil";

        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("should throw exception when team is already in a match")
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
}
