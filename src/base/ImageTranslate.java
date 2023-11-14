package base;


import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Word;
import com.google.protobuf.ByteString;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageTranslate {
    private static ImageAnnotatorClient client;
    public static void prepare() throws IOException {
        client = ImageAnnotatorClient.create();
    }
    public static List<Pair<String, BoundingPoly>> textAnnotations = new ArrayList<>();
    public static void detectText(String filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
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
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page : annotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    for (Paragraph para : block.getParagraphsList()) {
                        String paraText = "";
                        for (Word word : para.getWordsList()) {
                            StringBuilder wordText = new StringBuilder();
                            for (Symbol symbol : word.getSymbolsList()) {
                                wordText.append(symbol.getText());
                            }
                            paraText = String.format("%s %s", paraText, wordText);
                        }
                        textAnnotations.add(new Pair<>(paraText,para.getBoundingBox()));
                    }
                }
            }
        }
    }
}
