package com.example.trivia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trivia.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Question Type Spinner fields
    public static final String MULTIPLE_CHOICE = "Multiple Choice";
    public static final String TRUE_FALSE = "True / False";

    // Difficulty Spinner fields
    public static final String ANY_DIFFICULTY = "Any Difficulty";
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";

    // Categories spinner fields
    public static final String ANY_CATEGORY = "Any Category";
    public static final String GENERAL_KNOWLEDGE = "General Knowledge";
    public static final String ENTERTAINMENT_VIDEO_GAMES = "Entertainment: Video Games";
    public static final String ENTERTAINMENT_BOOKS = "Entertainment: Books";
    public static final String ENTERTAINMENT_FILM = "Entertainment: Film";
    public static final String ENTERTAINMENT_MUSIC = "Entertainment: Music";
    public static final String ENTERTAINMENT_MUSICALS_AND_THEATRES = "Entertainment Musicals and Theatres";
    public static final String ENTERTAINMENT_TV = "Entertainment: TV";
    public static final String BOARD_GAMES = "Board Games";
    public static final String SCIENCE_NATURE = "Science & Nature";
    public static final String SCIENCE_COMPUTERS = "Science: Computers";
    public static final String SCIENCE_MATHEMATICS = "Science: Mathematics";
    public static final String MYTHOLOGY = "Mythology";
    public static final String SPORTS = "Sports";
    public static final String GEOGRAPHY = "Geography";
    public static final String HISTORY = "History";
    public static final String ART = "Art";
    public static final String CELEBRITIES = "Celebrities";
    public static final String ANIMALS = "Animals";
    public static final String POLITICS = "Politics";
    public static final String VEHICLES = "Vehicles";
    public static final String ENTERTAINMENT_COMICS = "Entertainment: Comics";
    public static final String SCIENCE_GADGETS = "Science: Gadgets";
    public static final String ENTERTAINMENT_JAPANESE_ANIME_MANGA = "Entertainment: Japanese Anime & Manga";
    public static final String ENTERTAINMENT_CARTOON_ANIMATIONS = "Entertainment: Cartoon & Animations";

    // Widgets
    private Spinner categorySpinner, difficultySpinner, questionTypeSpinner;
    private Button playButton;
    private ProgressBar progressBar;

    private int categoryNumber;
    private String difficultyString;
    private String questionTypeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //------- Transparent Status Bar -------//
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //------- Error intent -------//
        Intent intent = getIntent();
        if (intent.hasExtra(Utils.ERROR_ID)) {
            Toast.makeText(this, "Sorry! Something went wrong. Try a different customization.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Error Message: " + intent.getStringExtra(Utils.ERROR_ID), Toast.LENGTH_LONG).show();
        }

        //------- Widgets -------//
        categorySpinner = findViewById(R.id.category_spinner);
        difficultySpinner = findViewById(R.id.difficulty_spinner);
        questionTypeSpinner = findViewById(R.id.question_type_spinner);
        playButton = findViewById(R.id.welcome_play_button);
        progressBar = findViewById(R.id.progressBar);

        // Spinner click listener
        categorySpinner.setOnItemSelectedListener(this);
        difficultySpinner.setOnItemSelectedListener(this);
        questionTypeSpinner.setOnItemSelectedListener(this);

        //------- Spinner Drop down elements -------//

        // Categories
        List<String> categories = new ArrayList<String>();
        categories.add(ANY_CATEGORY);
        categories.add(GENERAL_KNOWLEDGE);
        categories.add(ENTERTAINMENT_BOOKS);
        categories.add(ENTERTAINMENT_FILM);
        categories.add(ENTERTAINMENT_MUSIC);
        categories.add(ENTERTAINMENT_MUSICALS_AND_THEATRES);
        categories.add(ENTERTAINMENT_TV);
        categories.add(ENTERTAINMENT_VIDEO_GAMES);
        categories.add(BOARD_GAMES);
        categories.add(SCIENCE_NATURE);
        categories.add(SCIENCE_COMPUTERS);
        categories.add(SCIENCE_MATHEMATICS);
        categories.add(MYTHOLOGY);
        categories.add(SPORTS);
        categories.add(GEOGRAPHY);
        categories.add(HISTORY);
        categories.add(POLITICS);
        categories.add(ART);
        categories.add(CELEBRITIES);
        categories.add(ANIMALS);
        categories.add(VEHICLES);
        categories.add(ENTERTAINMENT_COMICS);
        categories.add(SCIENCE_GADGETS);
        categories.add(ENTERTAINMENT_JAPANESE_ANIME_MANGA);
        categories.add(ENTERTAINMENT_CARTOON_ANIMATIONS);


        // Difficulties
        List<String> difficulties = new ArrayList<String>();
        difficulties.add(ANY_DIFFICULTY);
        difficulties.add(EASY);
        difficulties.add(MEDIUM);
        difficulties.add(HARD);

        // Question Types
        List<String> questionTypes = new ArrayList<String>();
        questionTypes.add(MULTIPLE_CHOICE);
        questionTypes.add(TRUE_FALSE);

        //------- Creating adapter for spinners -------//
        ArrayAdapter<String> categorySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<String> difficultySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, difficulties);
        ArrayAdapter<String> questionTypeSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questionTypes);

        //------- Drop down layout style - list view with radio button -------//
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //------- attaching data adapter to spinners -------//
        categorySpinner.setAdapter(categorySpinnerAdapter);
        difficultySpinner.setAdapter(difficultySpinnerAdapter);
        questionTypeSpinner.setAdapter(questionTypeSpinnerAdapter);

        //------- Play Button, Pass data to next activity -------//
        playButton.setOnClickListener(view -> {
            // todo: how do we deal with parameters that have less than 10 questions? do we look up all questions and search for those matching our parameters to get the actual count?
            progressBar.setVisibility(View.VISIBLE);

            // If "Any Category" AND "Any Difficulty" were chosen. (stayed at default value)
            if (categoryNumber == 8 && Objects.equals(difficultyString, "")) {
                Utils.setUrl(questionTypeString);
            }

            // If "Any Category" ONLY. (difficulty is not default)
            else if (categoryNumber == 8) {
                Utils.setUrl(difficultyString, questionTypeString);
            }

            // If "Any Difficulty" ONLY. (category is not default)
            else if (difficultyString.equals("")) {
                Utils.setUrl(categoryNumber, questionTypeString);
            }

            // If everything was customized and no longer default value
            else {
                Utils.setUrl(categoryNumber, difficultyString, questionTypeString);
            }

            // go to main activity
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

        // if Category was changed
        if (adapterView == categorySpinner) {
            categoryNumber = 8; // set number to 8 each time because we will be adding. Category numbers start from 9 and go to 32
            categoryNumber += pos; // add position of clicked item to get category number

            // if Difficulty was changed
        } else if (adapterView == difficultySpinner) {
            difficultyString = "";

            // If User chose something other than Any Difficulty, change the string to lowercased version of item selected
            if (!adapterView.getSelectedItem().toString().equals(ANY_DIFFICULTY))
                difficultyString = adapterView.getSelectedItem().toString().toLowerCase();


        } else if (adapterView == questionTypeSpinner) { // If Question Type was changed

            // Chose Multiple Choice
            if (adapterView.getSelectedItem().toString().equals(MULTIPLE_CHOICE))
                questionTypeString = getString(R.string.multiple_choice_url_text);

            else if (adapterView.getSelectedItem().toString().equals(TRUE_FALSE)) // Chose True/False
                questionTypeString = getString(R.string.true_false_url_text);

        }

        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.YELLOW);
//        ((TextView) adapterView.getChildAt(i)).setTextSize(18);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset visibility back to gone
        progressBar.setVisibility(View.GONE);

    }
}