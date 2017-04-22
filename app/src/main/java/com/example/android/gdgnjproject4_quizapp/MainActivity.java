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
     * onClick for the 'next' button, we store the response in questionResponses map
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

        if (questionOn < (questionNumbers.size()-1)) {
            // Got to the next question
            processQuestion(++questionOn);
        }
        else {
            done();
        }
    }

    /**
     * onClick for the 'previous' button
     *
     * @param view
     */
    public void buttonPrevious(View view) {
        processQuestion(--questionOn);
    }

    /**
     * Done with the survey
     */
    private void done() {
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("results",getResults());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private String getResults() {
        int numberCorrect = 0;
        int totalQuests = 0;
        String rtnString = "";
        // Remember questionsNumbers is an array that contains the questionId's
        for (int i = 0; i < questionNumbers.size(); i++) {
            totalQuests++;
            int questionId = questionNumbers.get(i);
            Question quest = quiz.get(questionId);

            String question = quest.getQuestion();
            String answer = quest.getAnswer();
            String userResponse = questionResponses.get(questionId);

            rtnString += question + "\n\t" + userResponse + "\n";
            if (answer.equalsIgnoreCase(userResponse)) numberCorrect++;
        }
        rtnString += "\n";
        if (numberCorrect == 0) {
            rtnString += "Really... my dish towel could do better";
        }
        else if (numberCorrect == totalQuests) {
            rtnString += "Impressive, you are a genius";
        }
        else {
            rtnString += String.format("You got %d out of %d correct, that's an %d%%",
                    numberCorrect,totalQuests,Math.round((numberCorrect*100.0)/totalQuests));
        }
        return rtnString;
    }

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
     * Return an unused resource id (so that we might use it)
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
     * all the questions/answers/responses we should use in the app.
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

                if (pos != 0) {
                    // Get the resource id
                    int strId = getResources().getIdentifier(fieldName, "string", getPackageName());

                    // initialize the question number (based on name) and get question text
                    int questionNumber = Integer.parseInt(fieldName.substring(pos, pos + 3));
                    String theText = getString(strId);

                    Question quest = null;
                    try {
                        quest = quiz.get(questionNumber);
                    } catch (Exception e) {
                        // Nothing to do;
                    }
                    if (quest == null) {
                        quest = new Question(questionNumber);
                        quiz.put(questionNumber, quest);
                        questionNumbers.add(questionNumber);
                        questionOn = 0; // We'll start with first question
                    }
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

                    // Log message
                    Log.d("MainActivity", "Got resource: " + questionNumber +
                            " type: " + type + " " +
                            getString(strId).toString());
                }

            }
        }
        Collections.sort(questionNumbers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();
        dumpQuestions();
        setContentView(R.layout.activity_main);
        processQuestion(questionOn);
        setButtons();
    }

    private void processQuestion(int questionArrayPosition) {
        responseId = 0;
        if (questionArrayPosition >= 0 && questionArrayPosition < questionNumbers.size()) {
            int questionId = questionNumbers.get(questionArrayPosition);
            Question quest = quiz.get(questionId);
            if (quest != null) {
                // Set the question text
                TextView questionTextView = (TextView) findViewById(R.id.question);
                questionTextView.setText(quest.getQuestion());

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.responses);
                // If has any children then remove them
                if (linearLayout.getChildCount() > 0) {
                    linearLayout.removeAllViews();
                }

                int numButtons = quest.getResponses().size();
                RadioButton[] radioButtons = new RadioButton[numButtons];
                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);

                // set listener to set the responseId the user selected
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        responseId = checkedId;
                        setButtons();
                    }
                });

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
