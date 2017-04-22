package com.example.android.gdgnjproject4_quizapp;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by seanduffy on 4/21/17.
 */

public class Question {
    int questionId;
    private String question;
    private String answer;
    private List<String> responses;

    public Question(int questNum) {
        questionId = questNum;
        question = null;
        answer = null;
        responses = new ArrayList<String>();
    }

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
