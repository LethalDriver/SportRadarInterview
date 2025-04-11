### README Documentation

# SportRadar Scoreboard

## Overview
This project implements an in-memory scoreboard system for tracking sports matches. It provides functionality to start matches, update scores, end matches, and retrieve a summary of ongoing matches. The scoreboard ensures proper validation and ordering of matches based on specific rules.

## Features
- Start a new match with initial scores.
- Update the scores of an ongoing match.
- End a match and remove it from the scoreboard.
- Retrieve a summary of ongoing matches, sorted by:
    1. Total score in descending order.
    2. Most recently started match if scores are tied.

## Technologies Used
- **Programming Language**: Java 21
- **Libraries**: JUnit, JUnitParams, AssertJ
- **Build Tool**: Maven
- **IDE**: IntelliJ IDEA

## Project Structure
- `com.example.sportradar.api`: Contains the public interfaces and data models.
    - `Scoreboard`: Interface defining the contract for the scoreboard.
    - `MatchScore`: Immutable record representing match details.
- `com.example.sportradar.api.exceptions`: Custom exceptions for validation and error handling.
    - `DuplicateTeamNamesException`: Thrown when trying to start a match with duplicate team names.
    - `MatchAlreadyExistsException`: Thrown when trying to start a match that was already started.
    - `MatchNotFoundException`: Thrown when trying to update or end a match that does not exist.
    - `TeamAlreadyInMatchException`: Thrown when trying to start a match with a team that is already in another match.
- `com.example.sportradar.internal`: Contains the internal implementation of the scoreboard.
    - `InMemoryScoreboard`: Implements the `Scoreboard` interface using an in-memory list.
    - `Match`: Represents an internal match object with mutable scores and immutable team names, can be modified only by 
    the Scoreboard

## How to Run
1. Clone the repository.
2. Open the project in IntelliJ IDEA or other IDE.
3. Build the project using Maven.
4. Run the tests to verify functionality.

## Validation Rules
- Team names cannot be null, blank, or identical.
- Scores cannot be negative.
- A team cannot participate in multiple matches simultaneously.
- Duplicate matches are not allowed.

## Assumptions
- Each match is uniquely identified by the combination of team names.
- The order of teams does not matter when starting a match (i.e., "Team A vs Team B" is the same as "Team B vs Team A").
- Whitespaces in team names are trimmed before processing.
- One team cannot be in multiple matches at the same time.

## Design Decisions
- The scoreboard is implemented as an in-memory list for simplicity, I considered hashmap for faster lookups by using
  concatenated team names as keys, but finally decided to use a list because it naturally maintains the order of insertion,
  so I don't need to include this information in match model.
- A dedicated record class `MatchScore` is used to represent match details, ensuring immutability and separation of
  concerns (presentation of match summary is decoupled from the internal representation). This design adheres to the 
  Single Responsibility Principle.
- Public components of the library are kept in the `api` package, while internal implementations are in the `internal` 
  package to enforce encapsulation and clearly define the API surface.
- The scoreboard implementation implements the `Scoreboard` interface, allowing for easy extension or replacement with 
  different implementations in the future. This design adheres to the Dependency Inversion Principle.
- There are many edge cases when working with string keys (special characters, different encodings and so on...),
  I assumed that handling and then testing all such cases isn't the point of the exercise, so I decided to include 
  handling of only the most common cases (whitespaces, nulls, empty strings) and exhaustively test them.
- The initial score (0) is defined as a constant on the Scoreboard interface, ensuring consistency across the codebase
  and adhere to clean code principles (avoiding magic numbers).

## Example Usage
### Starting a Match
```java
Scoreboard scoreboard = new InMemoryScoreboard();
scoreboard.startMatch("Team A", "Team B");
```

### Updating Scores
```java
scoreboard.updateScore("Team A", "Team B", 2, 1);
```

### Ending a Match
```java
scoreboard.endMatch("Team A", "Team B");
```

### Retrieving Match Summary
```java
List<MatchScore> summary = scoreboard.getMatchSummary();
summary.forEach(System.out::println);
```

## Testing
Unit tests are provided in the `InMemoryScoreboardTest` class to ensure the correctness of the implementation. The tests cover:
- Starting matches.
- Updating scores.
- Ending matches.
- Retrieving match summaries with edge cases.
- Validation of parameters for all the functionalities mentioned above.