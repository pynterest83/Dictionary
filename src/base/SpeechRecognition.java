package base;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SpeechRecognition {
    public static boolean isListening = false;
    public static ArrayList<String> alternatives = new ArrayList<>();
    static SpeechClient client;
    static RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode("en").setSampleRateHertz(16000).build();
    static ClientStream<StreamingRecognizeRequest> clientStream;
    static StreamingRecognizeRequest request;
    static ResponseObserver<StreamingRecognizeResponse> responseObserver;
    static AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
    static DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    static TargetDataLine targetDataLine;
    static ByteArrayOutputStream output;

    public static void prepare() throws IOException, LineUnavailableException {
        client = SpeechClient.create();
        if (!AudioSystem.isLineSupported(targetInfo)) {
            System.out.println("Microphone not supported");
            System.exit(0);
        }
        targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetDataLine.open(audioFormat);
    }
    public static void recordFor(int length) throws LineUnavailableException, IOException {
        alternatives.clear();
        targetDataLine.flush();
        targetDataLine.start();
        long startTime = System.currentTimeMillis();
        AudioInputStream audioIn = new AudioInputStream(targetDataLine);
        output = new ByteArrayOutputStream();
        isListening = true;
        while (isListening && System.currentTimeMillis() - startTime <= length) {
            byte[] data = new byte[320];
            int count = audioIn.read(data);
            output.write(data,0,count);
        }
        targetDataLine.stop();
    }
    public static void sendRequest() {
        byte[] out = output.toByteArray();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(out)).build();
        RecognizeResponse response = client.recognize(recognitionConfig,audio);
        java.util.List<SpeechRecognitionResult> results = response.getResultsList();
        for (SpeechRecognitionResult result : results) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            alternatives.add(alternative.getTranscript());
        }
    }

    public static void changeLanguage(String language) {
        recognitionConfig = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode(language).setSampleRateHertz(16000).build();
    }
}