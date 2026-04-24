# Quiz Leaderboard System

## Overview
This project implements a backend-style solution for processing quiz data received from an external API. The goal was to correctly aggregate participant scores across multiple rounds while handling duplicate responses, and then generate a final leaderboard.

---

## Problem Understanding
The API provides quiz events across 10 polls. However, due to system behavior, the same event data can appear multiple times across different API calls.

If duplicates are not handled properly, the final score calculation becomes incorrect.

---

## Approach

### 1. API Polling
- Called the API 10 times using `poll=0` to `poll=9`
- Maintained a 5-second delay between each request as required

### 2. Deduplication Strategy
- Each event is uniquely identified using:
  
  `roundId + participant`

- Used a `HashSet` to track already processed events
- Ignored any duplicate entries across polls

### 3. Score Aggregation
- Used a `HashMap<String, Integer>` to store total scores per participant
- Updated scores only for unique events

### 4. Leaderboard Generation
- Converted the map into a list
- Sorted participants in descending order of total score

### 5. Final Submission
- Constructed the required JSON payload manually
- Submitted the leaderboard using POST API

---

## Key Challenges

- Handling duplicate API responses correctly
- Ensuring consistent data aggregation across multiple polls
- Building JSON without external libraries (to avoid dependency issues during execution)

---

## Tech Stack

- Java (JDK 11+)
- Built-in `HttpClient` for API communication
- Core Java data structures (HashMap, HashSet, ArrayList)

---

## How to Run

```bash
javac QuizApp.java
java QuizApp
