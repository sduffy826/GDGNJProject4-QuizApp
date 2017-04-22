package com.example.android.gdgnjproject4_quizapp;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<Integer, Question> quiz;
    private Map<Integer, String> questionResponses;
    private ArrayList<Integer> questionNumbers;
    private int questionOn;
    private int responseId;

    /**
     * onClick for the 'next' button, we store the users response; if we're on the last question
     * then we'll call the done method otherwise we'll call the processQuestion with the next
     * question index to be processed.
     *
     * @param view
     */
    public void buttonNext(View view) {
        // Get the question id of the one we are on
        int questionId = questionNumbers.get(questionOn);

        // Get the radio button the user selected
        RadioButton rb = (RadioButton) findViewById(responseId);
        if (rb != null) {
            questionResponses.put(questionId, rb.getText().toString());
        }

        if (questionOn < (questionNumbers.size() - 1)) {
            // Got to the next question
            processQuestion(++questionOn);
        } else {
            done();
        }
    }

    /**
     * onClick for the 'previous' button, call processQuestion method passing in the prior questions
     * index position
     *
     * @param view
     */
    public void buttonPrevious(View view) {
        processQuestion(--questionOn);
    }

    /**
     * Done with the survey, we'll show the results activity
     */
    private void done() {
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("results", getResults());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * For debugging only, it dumps out the contents of the quiz
     */
    private void dumpQuestions() {
        Iterator<Integer> questionIterator = quiz.keySet().iterator();
        while (questionIterator.hasNext()) {
            Question quest = quiz.get(questionIterator.next());
            Log.d("Questions", String.format("%3d", quest.getQuestionId()) + quest.getQuestion());
            for (String resp : quest.getResponses()) {
                String prefix = (resp.equalsIgnoreCase(quest.getAnswer()) ? "Ans ->" : " ");
                Log.d("Questions", String.format("%8s %s", prefix, resp));
            }
        }
    }

    /**
     * This method builds a string with the results of the quiz
     * @return String with the questions/responses and a overall summary line... this is a pretty
     * rudimentary result... could prettify down the road
     */
    private String getResults() {
        int numberCorrect = 0;
        int totalQuests = 0;
        String rtnString = "";

        // Remember questionsNumbers is an array that contains the questionId's
        for (int i = 0; i < questionNumbers.size(); i++) {
            totalQuests++;
            int questionId = questionNumbers.get(i);                  // Get question id
            Question quest = quiz.get(questionId);                    // Get the question object

            String question = quest.getQuestion();                    // The question text
            String answer = quest.getAnswer();                        // The correct answer
            String userResponse = questionResponses.get(questionId);  // The users answer

            rtnString += question + "\n\t" + userResponse + "\n";
            if (answer.equalsIgnoreCase(userResponse)) numberCorrect++;
        }
        rtnString += "\n";

        // Last line is a summary of how the user did
        if (numberCorrect == 0) {
            rtnString += "Really... my dish towel could do better";
        } else if (numberCorrect == totalQuests) {
            rtnString += "Impressive, everything right, you're a jeanyus"; // intentional spelling:)
        } else {
            rtnString += String.format("You got %d out of %d correct, that's a %d%%",
                    numberCorrect, totalQuests, Math.round((numberCorrect * 100.0) / totalQuests));
        }
        return rtnString;
    }

    /**
     * Return an unused resource id, done so we don't generate resource id's that collide with
     * other artifacts
     *
     * @param startNum the starting int to search for
     * @return an unused resource id
     */
    private int getUniqueViewId(int startNum) {
        View vw = findViewById(startNum);
        if (vw != null) {
            return getUniqueViewId(startNum + 1);
        } else {
            return startNum;  // Got available number to use
        }
    }

    /**
     * Initialize app attributes, primary job is to loop thru the string resources and identify
     * all the questions/answers/responses we should use in the app.  When done with this the
     * quiz (map) has the attributes for the quiz (the questions, available responses and the
     * correct answer).
     */
    private void initApp() {
        questionOn = -1;
        quiz = new HashMap<Integer, Question>();
        questionResponses = new HashMap<Integer, String>();
        questionNumbers = new ArrayList<Integer>();

        // Loop thru all the string resources that we care about (question, answer, response)
        Field[] idFields = R.string.class.getFields();
        for (int i = 0; i < idFields.length; i++) {
            String fieldName = idFields[i].getName();
            if (fieldName.length() > 9) {
                char type = ' ';
                int pos = 0;
                if (fieldName.substring(0, 7).equalsIgnoreCase("answer_")) {
                    type = 'A';
                    pos = 7;
                } else if (fieldName.substring(0, 9).equalsIgnoreCase("response_")) {
                    type = 'R';
                    pos = 9;
                } else if (fieldName.substring(0, 9).equalsIgnoreCase("question_")) {
                    type = 'Q';
                    pos = 9;
                }

                if (pos != 0) {  // Got a resource I care about
                    // Get the resource id
                    int strId = getResources().getIdentifier(fieldName, "string", getPackageName());

                    // initialize the question number (based on name) and get question text
                    int questionNumber = Integer.parseInt(fieldName.substring(pos, pos + 3));
                    String theText = getString(strId);

                    Question quest = quiz.get(questionNumber);
                    if (quest == null) {
                        quest = new Question(questionNumber);
                        quiz.put(questionNumber, quest);
                        questionNumbers.add(questionNumber);
                        questionOn = 0; // We'll start with first question
                    }
                    // Add text to correct object attribute ('Q'uestion, 'R'esponse, 'A'nswer)
                    switch (type) {
                        case 'Q':
                            quest.setQuestion(theText);
                            break;
                        case 'R':
                            quest.getResponses().add(theText);
                            break;
                        case 'A':
                            quest.setAnswer(theText);
                            break;
                    }

                    // Log debugging message
                    Log.d("MainActivity", "Got resource: " + questionNumber +
                            " type: " + type + " " +
                            getString(strId).toString());
                }

            }
        }
        // Have all the questions, sort it so that we process in order... we did this so that
        // app isn't dependent on attributes in the xml file (i.e. supports question id skipping
        // numbers)
        Collections.sort(questionNumbers);
    }

    /**
     * Calls initApp (initialise app attributes), dumpQuestions (for debugging), processQuestion
     * that puts up the question/responses on the screen, and setButtons to set whether the
     * buttons are available or not (i.e. can't click next until user enters a response)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();
        dumpQuestions();
        setContentView(R.layout.activity_main);
        processQuestion(questionOn);
        setButtons();
    }

    /**
     * This has the logic to handle the question index passed in; it sets the appropriate text
     * on the screen and creates the group of radio buttons for the user to choose their response
     * from (it adds them as children to the 'responses' linear layout)
     * @param questionArrayPosition
     */
    private void processQuestion(int questionArrayPosition) {
        responseId = 0;
        if (questionArrayPosition >= 0 && questionArrayPosition < questionNumbers.size()) {
            int questionId = questionNumbers.get(questionArrayPosition);
            Question quest = quiz.get(questionId);
            if (quest != null) {
                // Set the question text
                TextView questionTextView = (TextView) findViewById(R.id.question);
                questionTextView.setText(quest.getQuestion());

                // Identify the linearLayout that we'll add the radio buttons to
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.responses);

                // If layout has any children then remove them
                if (linearLayout.getChildCount() > 0) {
                    linearLayout.removeAllViews();
                }

                // Create array to hold the buttons (coulda used single obj but did this way in
                // case need references later); we also create the radio group that has the buttons
                int numButtons = quest.getResponses().size();
                RadioButton[] radioButtons = new RadioButton[numButtons];
                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);

                // set listener for radioGroup, it just stores the responseId the user checked, we
                // also call setButtons so that the button will become enabled
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        responseId = checkedId;
                        setButtons();
                    }
                });

                // This loop creates a button for each of the questions responses that are available
                for (int i = 0; i < numButtons; i++) {
                    String aResponse = quest.getResponses().get(i);

                    // Need a unique resource id for this response, android has
                    // View.generateViewId() but that's for newer versions of sdk so
                    // we rolled our own.
                    int responseId2Use = getUniqueViewId(i * 100 + 1);
                    radioButtons[i] = new RadioButton(this);
                    radioButtons[i].setText(aResponse);
                    radioButtons[i].setId(responseId2Use);
                    radioGroup.addView(radioButtons[i]);
                }
                // Add the radio group to the linear layout
                linearLayout.addView(radioGroup);
            }
        }
        setButtons();
    }

    /**
     * Set the visibility of the buttons (previous/next)
     */
    private void setButtons() {
        boolean buttonNext, buttonPrev;

        // I'm sure we'll change rules on when buttons allowed, but for not it's simple
        buttonPrev = (questionOn > 0);
        buttonNext = (responseId != 0);

        // Get object references for the buttons
        Button buttPrev = (Button) findViewById(R.id.buttonPrevious);
        Button buttNext = (Button) findViewById(R.id.buttonNext);

        // See if the button text for next is correct (it should be Submit when on last question)
        String buttonText = buttNext.getText().toString();
        String textShouldBe = "Next";
        if (questionOn == (questionNumbers.size() - 1)) {
            textShouldBe = "Submit";
        }
        // If the text on the button is wrong set it
        if (buttonText.equalsIgnoreCase(textShouldBe) == false) {
            buttNext.setText(textShouldBe);
        }

        buttNext.setEnabled(buttonNext);
        buttPrev.setEnabled(buttonPrev);
    }
}
