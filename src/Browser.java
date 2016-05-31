import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();



    public Browser() {

        //apply the styles
        getStyleClass().add("browser");
        // load the web page

        // Obtain the Authorization URL


        getChildren().add(browser);
    }

    public void AddListener(ChangeListener<Worker.State> stateChangedListener ){
        webEngine.getLoadWorker().stateProperty().addListener(stateChangedListener);
    }

    public String getLocation(){
        return webEngine.getLocation();
    }

    public void loadUrl(String url){
        webEngine.load(url);
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