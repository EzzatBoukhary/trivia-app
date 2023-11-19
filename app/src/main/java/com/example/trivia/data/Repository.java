package com.example.trivia.data;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.trivia.HtmlManipulator;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;
import com.example.trivia.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repository {

    // Constants for API
    public static final String RESPONSE_CODE = "response_code";
    public static final String RESULTS = "results";
    public static final String QUESTION_FIELD = "question";
    public static final String CORRECT_ANSWER_FIELD = "correct_answer";
    public static final String CATEGORY_FIELD = "category";
    public static final String DIFFICULTY_FIELD = "difficulty";
    public static final String TYPE_FIELD = "type";
    public static final String INCORRECT_ANSWERS_FIELD = "incorrect_answers";
    public static final String MULTIPLE = "multiple";

    String url = Utils.getUrl();
    ArrayList<Question> questionArrayList = new ArrayList<>();

    public List<Question> getQuestions(final AnswerListAsyncResponse callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    try {
                        // Check Result Codes
                        if (response.getInt(RESPONSE_CODE) == 1) {
                            if (null != callback) callback.processFailed("No results!");
                            return;

                        } else if (response.getInt(RESPONSE_CODE) == 2) {
                            if (null != callback) callback.processFailed("Invalid Parameter!");
                            return;

                        } else if (response.getInt(RESPONSE_CODE) == 3) {
                            if (null != callback) callback.processFailed("Token Not Found!");
                            return;

                        } else if (response.getInt(RESPONSE_CODE) == 4) {
                            if (null != callback) callback.processFailed("Token Empty!");
                            return;

                        } else { // result code 0 -> good
                            JSONArray results = response.getJSONArray(RESULTS);

                            for (int i = 0; i < results.length(); i++) { // loop through the Results array

                                JSONObject resultsJSONObject = results.getJSONObject(i); // get one JSON object holding one question data
                                Question question = new Question();

                                //------- Populate Question Object -------//

                                // Question: Text
                                String questionText = resultsJSONObject.getString(QUESTION_FIELD).replace("&#039;", "'"); // get the question from JSON and fix HTML codes
                                questionText = HtmlManipulator.replaceHtmlEntities(questionText);
                                question.setQuestion(questionText);

                                // Question: Correct Answer
                                String answer = resultsJSONObject.getString(CORRECT_ANSWER_FIELD); // get correct answer
                                answer = answer.replace("&#039;", "'"); //  fix HTML codes
                                answer = HtmlManipulator.replaceHtmlEntities(answer);
                                question.setCorrectAnswer(answer);

                                // Question: Category
                                String category = resultsJSONObject.getString(CATEGORY_FIELD);
                                question.setCategory(category);

                                // Question: Difficulty
                                String difficulty = resultsJSONObject.getString(DIFFICULTY_FIELD);
                                question.setDifficulty(difficulty);

                                // Question: Type
                                String type = resultsJSONObject.getString(TYPE_FIELD);
                                question.setQuestionType(type);

                                //------- Question: All Answers (Shuffled) -------//
                                String tempText = "";
                                List<String> allAnswers = new ArrayList<>();
                                allAnswers.add(answer); // add correct answer to list

                                // Add all incorrect answers to list
                                tempText = resultsJSONObject.getJSONArray(INCORRECT_ANSWERS_FIELD).getString(0).replace("&#039;", "'"); //fix HTML codes
                                tempText = HtmlManipulator.replaceHtmlEntities(tempText);
                                allAnswers.add(tempText);

                                if (type.equals(MULTIPLE)) {
                                    tempText = resultsJSONObject.getJSONArray(INCORRECT_ANSWERS_FIELD).getString(1).replace("&#039;", "'"); //fix HTML codes
                                    tempText = HtmlManipulator.replaceHtmlEntities(tempText);
                                    allAnswers.add(tempText);

                                    tempText = resultsJSONObject.getJSONArray(INCORRECT_ANSWERS_FIELD).getString(2).replace("&#039;", "'"); //fix HTML codes
                                    tempText = HtmlManipulator.replaceHtmlEntities(tempText);
                                    allAnswers.add(tempText);
                                    Collections.shuffle(allAnswers);
                                }

                                question.setAllAnswers(allAnswers);

                                // Add questions to array list:
                                questionArrayList.add(question);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (null != callback) callback.processFinished(questionArrayList);
                },
                error -> {
                    if (null != callback) callback.processFailed(error.getMessage());
                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        return questionArrayList;
    }
}
