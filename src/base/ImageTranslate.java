package base;


import com.google.cloud.vision.v1.Word;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.Symbol;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Page;
import com.google.protobuf.ByteString;
import javafx.util.Pair;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageTranslate {
    private static ImageAnnotatorClient client;
    public static void prepare() throws IOException {
        client = ImageAnnotatorClient.create();
    }
    public static List<Pair<String, BoundingPoly>> textAnnotations = new ArrayList<>();
    public static HashMap<Pair<String, BoundingPoly>,Double> symbolSize = new HashMap<>();
    public static void detectText(byte[] image) throws IOException, URISyntaxException {
        new URI("https://speech.googleapis.com/").toURL().openConnection().connect();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.copyFrom(image);
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
                return;
            }
            textAnnotations.clear();
            symbolSize.clear();
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page : annotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    for (Paragraph para : block.getParagraphsList()) {
                        String paraText = "";
                        int size = 0;
                        double sum = 0;
                        for (Word word : para.getWordsList()) {
                            StringBuilder wordText = new StringBuilder();
                            for (Symbol symbol : word.getSymbolsList()) {
                                wordText.append(symbol.getText());
                                size++;
                                sum+= symbol.getBoundingBox().getVertices(2).getY() - symbol.getBoundingBox().getVertices(1).getY();
                            }
                            paraText = String.format("%s %s", paraText, wordText);
                        }
                        Pair<String,BoundingPoly> paragraph = new Pair<>(paraText,para.getBoundingBox());
                        symbolSize.put(paragraph,sum/size);
                        textAnnotations.add(paragraph);
                    }
                }
            }
        }
    }
}
