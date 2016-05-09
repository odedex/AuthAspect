import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.stage.Window;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.swing.*;
import java.awt.*;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.Desktop;
import java.net.URI;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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

import javax.swing.tree.ExpandVetoException;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Created by OdedA on 04-May-16.
 */
@Aspect
public class BasicAuthAspect {
    private static String htmlString;
    private static ProceedingJoinPoint staticPoint;
    public static class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
//        private Stage stage;

        public Browser(/*Stage stageC*/) {
//            stage = stageC;

            //apply the styles
            getStyleClass().add("browser");
            // load the web page
            webEngine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<State>() {
                        public void changed(ObservableValue ov, State oldState, State newState) {
                            if (newState == State.SUCCEEDED) {
//                                stage.setTitle(webEngine.getLocation());
                                Document doc = webEngine.getDocument();
                                try {
                                    DOMSource domSource = new DOMSource(doc);
                                    StringWriter writer = new StringWriter();
                                    StreamResult result = new StreamResult(writer);
                                    TransformerFactory tf = TransformerFactory.newInstance();
                                    Transformer transformer = tf.newTransformer();
                                    transformer.transform(domSource, result);
//                                    System.out.println("XML IN String format is: \n" + writer.toString());
                                    htmlString = writer.toString();
                                    if (htmlString.length() > 0) {
                                        System.out.println("html length longer than 0. proceeding");
                                        try {
                                            staticPoint.proceed();
                                        } catch (Throwable t) {
                                            System.out.println("caught throwable, refer to BasicAuthAspect");
                                        }
                                    }
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


    public static class WebViewSample extends Application {
        private Scene scene;
        @Override public void start(Stage stage) {
            // create the scene
            stage.setTitle("Web View");
            scene = new Scene(new Browser(/*stage*/),750,500, Color.web("#666970"));
            stage.setScene(scene);
            scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
            stage.show();
        }
    }

    @Pointcut("execution(@BasicAuth * *(..))")
    public void basicAuthAnnot() {}

//    @Pointcut("execution(public * *(..))")
//    public void publicMethod() {}

//    @Pointcut("basicAuthAnnot()")
//    public void publicMethodInsideAClassMarkedWithBasicAuth() {}

//    @Before("publicMethodInsideAClassMarkedWithBasicAuth()")
//    public void beforeMonitored(JoinPoint joinPoint) {
//        System.out.println("Clicked basicAuthAnnot function.");
//    }

    boolean isValid = false;
    @Around("basicAuthAnnot()")
    public void aroundBasicAuthAnnot(ProceedingJoinPoint point) {
        staticPoint = point;

//        System.out.println(point.toString());
//
//        JFrame frame = new JFrame("FrameDemo");
//
////2. Optional: What happens when the frame closes?
////        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        JTextField passJText = new JTextField();
//        passJText.setPreferredSize(new Dimension(160, 20));
//        passJText.setEnabled(true);
//        passJText.setHorizontalAlignment(4);
//
//        JButton sendButton = new JButton("send");
//        sendButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (passJText.getText().equals("admin")) {
////                if (true) {
//                    try {
//                        frame.setVisible(false);
//                        frame.dispose();
//                        point.proceed();
//                    } catch (Throwable t) {
//                        System.out.println("caught throwable, refer to BasicAuthAspect");
//                    }
//                } else {
//                    System.out.println("\"" + passJText.getText() + "\"" + " is wrong input");
//                }
//            }
//        });
//
//        JPanel myPanel = new JPanel();
//        myPanel.add(new JLabel("Pass:"));
//        myPanel.add(passJText);
//        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
//        myPanel.add(new JLabel());
//        myPanel.add(sendButton);
//
//        frame.add(myPanel);
//        frame.pack();
//        frame.setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI();
            }
        });
    }

        /*##########################################################################*/

    private static void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Swing and JavaFX");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
//        Group root  =  new  Group();
//        Scene scene  =  new  Scene(root, Color.ALICEBLUE);
//        Text text  =  new  Text();
//
//        text.setX(40);
//        text.setY(100);
//        text.setFont(new javafx.scene.text.Font(25));
//        text.setText("Welcome JavaFX!");
//
//        root.getChildren().add(text);

        Scene scene = new Scene(new Browser(/*stage*/),750,500, Color.web("#666970"));
//        scene.getStylesheets().add("webviewsample/BrowserToolbar.css");

        return (scene);
    }








        /*##########################################################################*/




//        JPanel motherPanel = new JPanel();
//        motherPanel.setLayout(new BoxLayout(motherPanel, BoxLayout.Y_AXIS));
//
//        JPanel textPanel = new JPanel();
//        textPanel.setPreferredSize(new Dimension(160, 20));
//        textPanel.add(resultJText);
//
//
//        motherPanel.add(textPanel);
//        motherPanel.add(numberButtonsPanel);
//        motherPanel.add(functionButtonPanel);
//        add(motherPanel);
//
//        setTitle("ButtonTest");
//        setSize(180, 290);
//        setLocationByPlatform(true);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setVisible(true);








//        System.out.println("please enter 'y' to continue");
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String s = br.readLine();
//            if (s.equals("y")) {
//                try {
//                    point.proceed();
//                } catch (Throwable t) {
//                    System.out.println("caught throwable, refer to BasicAuthAspect");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("error");
//        }

}
