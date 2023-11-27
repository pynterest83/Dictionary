package base;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class CompleteSentence {
    public static ArrayList<CompleteSentence> completeSentences = new ArrayList<CompleteSentence>();
    private final String question;
    private final String[] choices;
    private final String answer;
    public static void LoadQuestionsAndAnswers() throws IOException {
        File questionFile= new File("src/resources/CompleteSentenceQuestions.txt");
        FileReader questionReader = new FileReader(questionFile);
        BufferedReader bufferQuestion = new BufferedReader(questionReader);
        File answerFile = new File("src/resources/CompleteSentenceAnswers.txt");
        FileReader answerReader = new FileReader(answerFile);
        BufferedReader bufferAnswer = new BufferedReader(answerReader);
        String line = null;
        String question = null;
        ArrayList<String> choices = new ArrayList<String>();
        String answer = null;
        int count = 0;
        while ((line = bufferQuestion.readLine()) != null) {
            if (count%6==0) {
                if (count>0) {
                    completeSentences.add(new CompleteSentence(question,choices,answer));
                    choices.clear();
                }
                int beginIndex = String.valueOf(count / 6 + 1).length() + 2;
                question = line.substring(beginIndex);
                answer = bufferAnswer.readLine().substring(beginIndex);
            }
            else {
                choices.add(line);
            }
            count++;
        }
        questionReader.close();
        answerReader.close();
    }
    public CompleteSentence (String question,ArrayList<String> choices,String answer) {
        this.question = question;
        String[] temp = new String[choices.size()];
        temp = choices.toArray(temp);
        this.choices = temp;
        this.answer = answer;
    }
    public static String askQuestion(int position) {
        return "<p>"
                + completeSentences.get(position).question
                + "</p><p style=\"position:fixed;top:20px\";><br>&emsp;&emsp;" + completeSentences.get(position).choices[0]
                + "</p><p style=\"position:fixed;top:50px\";><br>&emsp;&emsp;" + completeSentences.get(position).choices[1]
                + "</p><p style=\"position:fixed;top:80px\";><br>&emsp;&emsp;" + completeSentences.get(position).choices[2]
                + "</p><p style=\"position:fixed;top:110px\";><br>&emsp;&emsp;" + completeSentences.get(position).choices[3]
                + "</p><p style=\"position:fixed;top:140px\";><br>&emsp;&emsp;" + completeSentences.get(position).choices[4]+"</p>";
    }
    public static String getQuestionOnly(int position) {
        return completeSentences.get(position).question;
    }
    public static String showAnswer(int position,String answer) {
        if (Objects.equals(answer, completeSentences.get(position).answer.substring(0, 1))) {
            return askQuestion(position) + "<p style=\"color:MediumSeaGreen;position:fixed;top:165px;\"><br>"
                    + completeSentences.get(position).answer + "</p>";
        }
        else return askQuestion(position) + "<p style=\"color:Crimson;position:fixed;top:165px;\"><br>"
                + completeSentences.get(position).answer + "</p>";
    }
}
