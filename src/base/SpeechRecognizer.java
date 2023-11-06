package base;

//Imports
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import java.io.IOException;


public class SpeechRecognizer {
    public static Boolean start = false;
    public static void main(String[] args) throws IOException {
        //Configuration Object
        Configuration configuration = new Configuration();

        // Set path to the acoustic model.
        configuration.setAcousticModelPath("src/resources/en-us");
        // Set path to the dictionary.
        configuration.setDictionaryPath("src/resources/cmudict-en-us.dict");
        // Set path to the language model.
        configuration.setLanguageModelPath("src/resources/en-us.lm.bin");

        //Recognizer object, Pass the Configuration object
        LiveSpeechRecognizer recognize = new LiveSpeechRecognizer(configuration);

        //Start Recognition Process (The bool parameter clears the previous cache if true)
        recognize.startRecognition(true);

        //Creating SpeechResult object
        SpeechResult result;

        //Check if recognizer recognized the speech
        while (start) {
            result = recognize.getResult();
            System.out.println("You said: " + result.getHypothesis() + "\n");
        }

    }

}