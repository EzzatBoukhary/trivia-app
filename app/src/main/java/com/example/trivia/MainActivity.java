package com.example.trivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.util.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Constants
    public static final String MULTIPLE = "multiple";
    private static final String PREFS_ID = "data_prefs";
    private static final String QUESTION_INDEX_ID = "questionIndex";
    private final int ONE_POINT = 1;

    List<Question> questionsList;
    private int currentQuestionIndex = 0, currentScore = 0;

    SharedPreferences sharedPreferences;

    private ActivityMainBinding binding;
    private MediaPlayer mediaPlayer;
    private Button correctButton, incorrectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Restore saved data if any:
        //sharedPreferences = getSharedPreferences(PREFS_ID, MODE_PRIVATE);
        //currentHighScore = sharedPreferences.getInt(HIGH_SCORE_ID, 0);

        questionsList = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                // if list is empty or less than the number of questions required return to welcome page
                if (questionArrayList.size() == 0 || questionArrayList.size() < Utils.NUMBER_OF_QUESTIONS) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra(Utils.ERROR_ID, "Sorry! No results were found or an error occurred.");

                    startActivity(intent);
                    finish();
                }

                Question question = questionsList.get(currentQuestionIndex);
                binding.questionTextView.setText(question.getQuestion());
                binding.questionCategoryTextview.setText(question.getCategory());
                binding.questionDifficultyTextview.setText(question.getDifficulty());

                if (question.getQuestionType().equals(MULTIPLE)) {

                    String firstMsg = question.getAllAnswers().get(0);
                    binding.firstAnswerButton.setText(firstMsg);
                    checkAndSetCorrectButton(question, firstMsg, binding.firstAnswerButton);


                    String secondMsg = question.getAllAnswers().get(1);
                    binding.secondAnswerButton.setText(secondMsg);
                    checkAndSetCorrectButton(question, secondMsg, binding.secondAnswerButton);


                    String thirdMsg = question.getAllAnswers().get(2);
                    binding.thirdAnswerButton.setText(thirdMsg);
                    binding.thirdAnswerButton.setVisibility(View.VISIBLE);
                    checkAndSetCorrectButton(question, thirdMsg, binding.thirdAnswerButton);

                    String fourthMsg = question.getAllAnswers().get(3);
                    binding.fourthAnswerButton.setText(fourthMsg);
                    binding.fourthAnswerButton.setVisibility(View.VISIBLE);
                    checkAndSetCorrectButton(question, fourthMsg, binding.fourthAnswerButton);

                } else {
                    checkAndSetCorrectButton(question, binding.firstAnswerButton.getText().toString(), binding.firstAnswerButton);
                    checkAndSetCorrectButton(question, binding.secondAnswerButton.getText().toString(), binding.secondAnswerButton);
                }

                updateCounter(questionArrayList);
            }

            @Override
            public void processFailed(String error) {
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.putExtra(Utils.ERROR_ID, error);
                startActivity(intent);
                finish();
            }
        });

        // True Button or First button
        binding.firstAnswerButton.setOnClickListener(view -> {
            checkAnswer(binding.firstAnswerButton.getText().toString().trim(), view);
        });

        // False Button or Second button
        binding.secondAnswerButton.setOnClickListener(view -> {
            checkAnswer(binding.secondAnswerButton.getText().toString().trim(), view);
        });
        // Third button
        binding.thirdAnswerButton.setOnClickListener(view -> {
            checkAnswer(binding.thirdAnswerButton.getText().toString().trim(), view);
        });

        // Fourth button
        binding.fourthAnswerButton.setOnClickListener(view -> {
            checkAnswer(binding.fourthAnswerButton.getText().toString().trim(), view);

        });

        // Home Chip button
        binding.chip.setOnClickListener(view -> {
            endGame();
        });

        // Skip Button
        binding.buttonNext.setOnClickListener(view -> {
            goToNextQuestion();
        });

        // Share Button
        binding.buttonShare.setOnClickListener(view -> {
            shareTrivia();
        });
    }

    private void shareTrivia() {
        Question question = questionsList.get(currentQuestionIndex);
        String answers;

        // if multiple choice question
        if (question.getAllAnswers().size() == 4) {
            answers = question.getAllAnswers().toString().replace("[", "").replace("]", "");
        } else {
            answers = "True, false";
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "I'm playing Trivia!");
        intent.putExtra(Intent.EXTRA_TEXT, String.format("I'm currently on Question #%d:\nQuestion: %s\n\nPossible Answers: %s\n\nMy current score is: %d/%d",
                currentQuestionIndex+1, question.getQuestion(), answers, currentScore, Utils.NUMBER_OF_QUESTIONS));
        startActivity(intent);
    }

    private void checkAndSetCorrectButton(Question question, String message, Button button) {
        if (Objects.equals(message.toLowerCase(), question.getCorrectAnswer().toLowerCase())) {
            correctButton = button;
        }
    }

    private void checkAnswer(String userChosenAnswer, View view) {
        incorrectButton = null;

        String answer = questionsList.get(currentQuestionIndex).getCorrectAnswer();
        int snackbarText;

        // Correct Answer
        if (Objects.equals(userChosenAnswer.toLowerCase(), answer.toLowerCase())) {
            snackbarText = R.string.correct_answer;

            // Increase score
            currentScore += ONE_POINT;
            startAnimations();

        } else { // Incorrect Answer
            snackbarText = R.string.incorrect;
            incorrectButton = (Button) view;
            startAnimations();
        }
        Snackbar.make(binding.cardView, snackbarText, Snackbar.LENGTH_SHORT).show();
        binding.currentScoreText.setText(String.format("Current Score: %d", currentScore)); // update score text

    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionArrayList.size()));
    }

    private void goToNextQuestion() {
        // If index isn't the last item, skip to next
        if (currentQuestionIndex < questionsList.size() - 1) {
            currentQuestionIndex++;
            updateQuestion();

        } else { // else, end game (reached end of list)
            endGame();
        }
    }

    private void updateQuestion() {
        Question question = questionsList.get(currentQuestionIndex);
        binding.questionTextView.setText(question.getQuestion());
        binding.questionCategoryTextview.setText(question.getCategory());
        binding.questionDifficultyTextview.setText(question.getDifficulty());

        if (question.getQuestionType().equals(MULTIPLE)) {

            String firstMsg = question.getAllAnswers().get(0);
            binding.firstAnswerButton.setText(firstMsg);
            checkAndSetCorrectButton(question, firstMsg, binding.firstAnswerButton);


            String secondMsg = question.getAllAnswers().get(1);
            binding.secondAnswerButton.setText(secondMsg);
            checkAndSetCorrectButton(question, secondMsg, binding.secondAnswerButton);


            String thirdMsg = question.getAllAnswers().get(2);
            binding.thirdAnswerButton.setText(thirdMsg);
            binding.thirdAnswerButton.setVisibility(View.VISIBLE);
            checkAndSetCorrectButton(question, thirdMsg, binding.thirdAnswerButton);

            String fourthMsg = question.getAllAnswers().get(3);
            binding.fourthAnswerButton.setText(fourthMsg);
            binding.fourthAnswerButton.setVisibility(View.VISIBLE);
            checkAndSetCorrectButton(question, fourthMsg, binding.fourthAnswerButton);

        } else {
            checkAndSetCorrectButton(question, binding.firstAnswerButton.getText().toString(), binding.firstAnswerButton);
            checkAndSetCorrectButton(question, binding.secondAnswerButton.getText().toString(), binding.secondAnswerButton);
        }

        updateCounter((ArrayList<Question>) questionsList);
    }

    private void startAnimations() {

        // block button clicking at this point
        binding.firstAnswerButton.setClickable(false);
        binding.secondAnswerButton.setClickable(false);
        binding.thirdAnswerButton.setClickable(false);
        binding.fourthAnswerButton.setClickable(false);

        // Correct answer button -> green
        ViewCompat.setBackgroundTintList(
                correctButton,
                ColorStateList.valueOf(Color.rgb(0,128,0))); // dark green

        // Incorrect answer button -> red
        if (incorrectButton != null) {
            ViewCompat.setBackgroundTintList(
                    incorrectButton,
                    ColorStateList.valueOf(Color.rgb(139,0,0))); // dark red
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            // change button colors back to OG color after set milliseconds
            ViewCompat.setBackgroundTintList(
                    correctButton,
                    ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.buttons_color)));

            if (incorrectButton != null) {
                ViewCompat.setBackgroundTintList(
                        incorrectButton,
                        ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.buttons_color)));
            }
            goToNextQuestion();

            // remove click block
            binding.firstAnswerButton.setClickable(true);
            binding.secondAnswerButton.setClickable(true);
            binding.thirdAnswerButton.setClickable(true);
            binding.fourthAnswerButton.setClickable(true);
        }, 800);

    }

    private void endGame() {
        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        Toast.makeText(this, "Your score was: " + currentScore + "/" + Utils.NUMBER_OF_QUESTIONS, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Play background music
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
        mediaPlayer = null;

        // Save Data
        //sharedPreferences = getSharedPreferences(PREFS_ID, MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt(HIGH_SCORE_ID, currentHighScore);
        //editor.putInt(QUESTION_INDEX_ID, currentQuestionIndex);
        //editor.apply();
    }

}