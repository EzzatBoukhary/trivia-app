package com.example.trivia.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Question {
    private String question;
    private String correctAnswer;
    private String category, difficulty, questionType;
    private List<String> allAnswers;

    public Question() {
    }

    public Question(String question, String correctAnswer, String category, String difficulty, String questionType, List<String> allAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.allAnswers = allAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getAllAnswers() {
        return allAnswers;
    }

    public void setAllAnswers(List<String> allAnswers) {
        this.allAnswers = allAnswers;
    }

    @NonNull
    @Override
    public String toString() {
        return "Question{" +
                "Question='" + question + '\'' +
                ", answer=" + correctAnswer +
                '}';
    }
}
