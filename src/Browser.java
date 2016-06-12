import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Class used to open a web browser in java environment
 */
public class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    /**
     * Constructor
     */
    public Browser() {
        getStyleClass().add("browser");
        getChildren().add(browser);
    }

    /**
     * Create a listener for the web content inside the browser.
     * @param stateChangedListener
     */
    public void AddListener(ChangeListener<Worker.State> stateChangedListener ){
        webEngine.getLoadWorker().stateProperty().addListener(stateChangedListener);
    }

    /**
     * Return the current url of the web content
     * @return url as string
     */
    public String getLocation(){
        return webEngine.getLocation();
    }

    /**
     * Upade the web content to a given url
     * @param url url to load
     */
    public void loadUrl(String url){
        webEngine.load(url);
    }

    /**
     * Updated the dimensions of the browser frame
     */
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