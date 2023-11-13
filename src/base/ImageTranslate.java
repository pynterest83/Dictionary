package base;


import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import javafx.util.Pair;
import org.apache.commons.collections4.map.LinkedMap;

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
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("en").build();
        ImageContext imageContext2 = ImageContext.newBuilder().addLanguageHints("vi").build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).setImageContext(imageContext).setImageContext(imageContext2).build();
        requests.add(request);
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
                return;
            }
            textAnnotations.clear();
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                textAnnotations.add(new Pair<>(annotation.getDescription(), annotation.getBoundingPoly()));
            }
        }
    }
}
