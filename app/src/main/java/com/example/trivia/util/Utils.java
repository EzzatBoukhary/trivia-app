package com.example.trivia.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static final String ERROR_ID = "Error";
    public static final int NUMBER_OF_QUESTIONS = 10;

    private static String Url = "https://opentdb.com/api.php?amount="+NUMBER_OF_QUESTIONS+"&type=multiple"; // default Url to avoid issues

    public static String getUrl() {
        return Url;
    }

    // Different cases depending on what customization the user makes
    public static void setUrl(String type) {
        Url = "https://opentdb.com/api.php?amount="+NUMBER_OF_QUESTIONS+"&type="+type;
    }

    public static void setUrl(int category, String type) {
        Url = "https://opentdb.com/api.php?amount="+NUMBER_OF_QUESTIONS+"&category=" + category + "&type="+type;
    }

    public static void setUrl(String difficulty, String type) {
        Url = "https://opentdb.com/api.php?amount="+NUMBER_OF_QUESTIONS+"&difficulty="+difficulty+"&type="+type;
    }

    public static void setUrl(int category, String difficulty, String type) {
        Url = "https://opentdb.com/api.php?amount="+NUMBER_OF_QUESTIONS+"&category=" + category + "&difficulty="+difficulty+"&type="+type;
    }

//    public static int getRandomNumber(int min, int max) {
//        Random random = new Random();
//        return random.nextInt(max - min) + min;
//    }


}
