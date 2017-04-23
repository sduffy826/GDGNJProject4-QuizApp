package com.example.android.gdgnjproject4_quizapp;
import java.util.ArrayList;
import java.util.List;
/**
 * Simple java object to store question attributes.
 * Created by seanduffy on 4/21/17.
 */

public class Question {
    int questionId;                  // Identifier (the nnn value from strings.xml
    private String question;         // Question text
    private String answer;           // The answer
    private List<String> responses;  // List of possible responses (this is the list of radio opts)

    public Question(int questNum) {  // Constructor only takes the identifier
        questionId = questNum;
        question = null;
        answer = null;
        responses = new ArrayList<String>();
    }

    // Simple getters/setters
    public int getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
