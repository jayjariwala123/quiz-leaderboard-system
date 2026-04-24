# Quiz Leaderboard System

## 📌 Problem
This project consumes quiz API data, removes duplicate entries, aggregates scores, and generates a leaderboard.

## 🚀 Approach
- Polled API 10 times (poll=0 to 9)
- Maintained 5-second delay between requests
- Deduplicated data using (roundId + participant)
- Aggregated scores using HashMap
- Sorted leaderboard in descending order
- Submitted final result via POST API

## 🧠 Key Logic
Deduplication key:
roundId + "_" + participant

## ⚙️ Tech Used
- Java (no external libraries)
- HttpClient (Java 11+)

## 📊 Output
- Correct leaderboard generated
- Total score computed accurately

## 📁 Files
- QuizApp.java → Main implementation

## ✅ Status
Working and tested successfully