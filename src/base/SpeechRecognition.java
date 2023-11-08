package base;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;

public class SpeechRecognition {
    public static boolean isListening = false;
    public static ArrayList<String> alternatives = new ArrayList<>();
    static SpeechClient client;
    static RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode("en-US").setSampleRateHertz(16000).build();
    static ClientStream<StreamingRecognizeRequest> clientStream;
    static StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();
    static StreamingRecognizeRequest request;
    static ResponseObserver<StreamingRecognizeResponse> responseObserver;
    static AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
    static DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

    public static void prepare() throws IOException, LineUnavailableException {
        client = SpeechClient.create();
        if (!AudioSystem.isLineSupported(targetInfo)) {
            System.out.println("Microphone not supported");
            System.exit(0);
        }
    }

    static byte[] data;

    public static void recordFor(int length) throws LineUnavailableException, IOException {
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetDataLine.open(audioFormat);
        targetDataLine.start();
        long startTime = System.currentTimeMillis();
        // Audio Input Stream
        AudioInputStream audio = new AudioInputStream(targetDataLine);
        while (true) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            byte[] data = new byte[320];
            audio.read(data);
            if (estimatedTime > length) {
                targetDataLine.close();
                targetDataLine.stop();
                break;
            }
            request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
            clientStream.send(request);
        }
        responseObserver.onComplete();
    }
    public static void recordIndefinite() throws IOException, LineUnavailableException {
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetDataLine.open(audioFormat);
        targetDataLine.start();
        isListening = true;
        long startTime = System.currentTimeMillis();
        // Audio Input Stream
        AudioInputStream audio = new AudioInputStream(targetDataLine);
        while (isListening && System.currentTimeMillis() - startTime <= 10000) {
            byte[] data = new byte[320];
            audio.read(data);
            request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
            clientStream.send(request);
        }
        targetDataLine.stop();
        targetDataLine.close();
        responseObserver.onComplete();
    }
    public static void streamingMicRecognize() {
        alternatives.clear();
        try {
            request = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingRecognitionConfig).build(); // The first request in a streaming call has to be a config
            responseObserver = new ResponseObserver<>() {
                final ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();
                public void onStart(StreamController controller) {
                }
                public void onResponse(StreamingRecognizeResponse response) {
                    responses.add(response);
                }
                public void onComplete() {
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        for (int i = 0; i < result.getAlternativesList().size(); i++) {
                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(i);
                            alternatives.add(alternative.getTranscript());
                        }
                    }
                }
                public void onError(Throwable t) {
                }
            };
            clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);
            clientStream.send(request);
        } catch (Exception ignored) {}
    }
}