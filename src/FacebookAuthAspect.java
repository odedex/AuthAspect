import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.concurrent.Worker.State;

import java.lang.annotation.Annotation;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Created by OdedA on 04-May-16.
 */
@Aspect
public class FacebookAuthAspect {
    private static ProceedingJoinPoint staticPoint;

    private static JFrame frame;

    String clientId = "";
    String clientSecret = "";
    static String secretState;
    static OAuth20Service service;

    public static void setKey(String clinetId, String clientSecret){

    }

    public FacebookAuthAspect() throws ClassNotFoundException, Exception{

        Class mainclass = Class.forName("Main"); //TODO: GENEREALIZE 'Main' TO SOME DEVELOPER DEFINED VALUE.
        Annotation[] annotations = mainclass.getAnnotations();
        boolean facebookCreds = false;

        for(Annotation annotation : annotations){
            if(annotation instanceof FacebookCreds){
                FacebookCreds myAnnotation = (FacebookCreds) annotation;
                clientId = myAnnotation.clientId();
                clientSecret = myAnnotation.secret();
                facebookCreds = true;
            }
        }

        if (!facebookCreds) {
            throw new Exception("Facebook credentials were not supplied\nPlease add " +
                    "@FacebookCreds(clientId = <clientID>, secret = <secret> before the program's main class");
        }

        secretState = "secret" + new Random().nextInt(999_999);
        service = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .state(secretState)
                .callback("http://www.rotenberg.co.il/oauth_callback/")
                .build(FacebookApi.instance());


    }

    private static class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();





        public Browser() {

            //apply the styles
            getStyleClass().add("browser");
            // load the web page
            webEngine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<State>() {
                        public void changed(ObservableValue ov, State oldState, State newState) {
                            if (newState == State.SUCCEEDED) {
                                System.out.println("URL: " + webEngine.getLocation());
                                if (webEngine.getLocation().startsWith("http://www.rotenberg.co.il")){
                                    String url = webEngine.getLocation();
                                    Pattern p = Pattern.compile(".+code=(.+)&state=(.+)#.*");
                                    Matcher m = p.matcher(url);
                                    if (m.find()){
                                        System.out.println("in if");
                                        String code = m.group(1);
                                        String value = m.group(2);

                                        if (secretState.equals(value)) {
                                            System.out.println("State value does match!");
                                        } else {
                                            System.out.println("Ooops, state value does not match!");
                                            System.out.println("Expected = " + secretState);
                                            System.out.println("Got      = " + value);
                                            System.out.println();
                                        }

                                        // Trade the Request Token and Verfier for the Access Token
                                        System.out.println("Trading the Request Token for an Access Token...");
                                        final OAuth2AccessToken accessToken = service.getAccessToken(code);

//                                        private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.5/me";

//                                        // Now let's go and ask for a protected resource!
//                                        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
//                                        service.signRequest(accessToken, request);
//                                        final Response response = request.send();
//                                        System.out.println("Got it! Lets see what we found...");
//                                        System.out.println();
//                                        System.out.println(response.getCode());
//                                        System.out.println(response.getBody());


                                        try {
                                            frame.setVisible(false);
                                            staticPoint.proceed();
                                        } catch (Throwable t) {
                                            System.out.println("caught throwable, refer to FacebookAuthAspect");
                                            System.out.println(t);
                                        }
                                    }

                                }
                            }
                        }
                    });




            // Obtain the Authorization URL

            final String authorizationUrl = service.getAuthorizationUrl();

            webEngine.load(authorizationUrl);
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


    private static class WebViewSample extends Application {
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

    @Pointcut("execution(@FacebookAuth * *(..))")
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
        frame = new JFrame("Login Using Aspect");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


//        frame.addWindowListener(new WindowAdapter()
//        {
//            public void windowClosing(WindowEvent e)
//            {
//                System.out.println("Browser window closed");
//                frame.dispose();
//            }
//        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                System.out.println("Browser window closed");
                frame.setVisible(false);
            }
        });

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
        Scene scene = new Scene(new Browser(/*stage*/),750,500, Color.web("#666970"));

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

}
