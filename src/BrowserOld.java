import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.concurrent.Worker.State;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/*
TODO: this class is redundant and only here for reference information
 */
class BrowserOld extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    private Stage stage;

    public BrowserOld(Stage stageC) {
        stage = stageC;

        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            stage.setTitle(webEngine.getLocation());
                            Document doc = webEngine.getDocument();
                            try {
                                DOMSource domSource = new DOMSource(doc);
                                StringWriter writer = new StringWriter();
                                StreamResult result = new StreamResult(writer);
                                TransformerFactory tf = TransformerFactory.newInstance();
                                Transformer transformer = tf.newTransformer();
                                transformer.transform(domSource, result);
                                System.out.println("XML IN String format is: \n" + writer.toString());
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                });
        webEngine.load("https://www.google.com");
        //add the web view to the scene
        getChildren().add(browser);


    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 750;
    }

    @Override protected double computePrefHeight(double width) {
        return 500;
    }
}