# GDGNJ Project 4 - Quiz App
Project 4 required us to develop a little quiz application, I decided to create a simple trivia app.  

The application is driven by the data in the strings.xml file.  The code loops thru all the the resources and looks for strings named: 'question_nnn', 'answer_nnn' and 'response_nnn_y'. nnn should be an integer (i.e. 001 or 103 etc..); it is used to relate questions to answers; and to the responses that should be presented to the user.  The '_y' for responses is irrelivant, it's needed so that the 
resource is unique, the important part is the nnn value.  To give an example, say you had the following resources:

| Resource | Value  |
| -------- | -------|
| question_001 | How many nibbles do you take for 2 bytes |
| response_001_1 | 2 |
| response_001_2 | 3 |
| response_001_3 | 4 |
| response_001_4 | 5 |
| response_001_5 | 6 |
| response_001_6 | None of the above |
| answer_001 | 4 |
| question_002 | Having a pet lowers your blood pressure |
| answer_002 | true |
| response_002_1 | True |
| response_002_2 | False |

The code will read this and create a quiz with two questions.  The first question gives the user 5 choices to choose from (choices are radio buttons), 4 is the correct answer for question 1.  For question 2 there are two choices, True and False; the correct answer is true (note I intentionally changed the case... case doesn't matter when matching a response to an answer).

The code is commented so I won't reiterate here, but worth noting:
- the initApp method is responsible for reading the resources and initializing the properties needed to run the app
- the processQuestion method is passed index position of the question we want to show; it updates the view elements so the information is correctly displayed to the user
- the buttons are only available when it make sense (i.e. can't click next until you respond)
- responses are only saved when user clicks the next (or submit) button (method buttonNext).
- When the user submits the last response I use another activity to display results.  I use intents and a 'Bundle' object to pass the results to the other activity; the other activity is very basic... it shows the question, the users response on the next line (indented by tab).  At the end is has a summary line.  If this was an app to be distributed I'd make that much better looking :)

*Note:* I don't use the nnn value as the array index position since it'd be messy to manage questions... you can have nnn values of: 200, 105, 250; the code sorts the nnn value and maps it to the associated index position.  Index 0 would get 105, 1 would get 200, and 2 would get 250; that'd be the order the questions are presented.  Only mention in case you wondering why it's coded as it is :)  Also, if you really wanted to use this I'd increment by a value other than 1, i.e. number questions as 100, 120, 140 (then you could easily insert new questions between existing ones).

Future enhancements
- Make results pretty
- Doesn't re-display prior response when the 'Previous' button is pressed, may want to do that
- Support having image with the question
- Support other response types (other than radio buttons)
- May want to support comments
