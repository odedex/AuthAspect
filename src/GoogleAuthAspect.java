import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nadav on 31/05/2016.
 */
@Aspect
public class GoogleAuthAspect{
    private static ProceedingJoinPoint staticPoint;

    private static JFrame frame;

    String clientId = "";
    String clientSecret = "";
    static String secretState;
    static OAuth20Service service;


    public static void setKey(String clinetId, String clientSecret){

    }

    public GoogleAuthAspect() throws ClassNotFoundException, Exception{

        Class mainclass = Class.forName("Main"); //TODO: GENEREALIZE 'Main' TO SOME DEVELOPER DEFINED VALUE.
        Annotation[] annotations = mainclass.getAnnotations();
        boolean googleCreds = false;

        for(Annotation annotation : annotations){
            if(annotation instanceof GoogleCreds){
                GoogleCreds myAnnotation = (GoogleCreds) annotation;
                clientId = myAnnotation.clientId();
                clientSecret = myAnnotation.secret();
                googleCreds= true;
            }
        }

        if (!googleCreds) {
            throw new Exception("Google credentials were not supplied\nPlease add " +
                    "@FGoogleCreds(clientId = <clientID>, secret = <secret> before the program's main class");
        }

        secretState = "secret" + new Random().nextInt(999_999);
        service = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .scope("profile") // replace with desired scope
                .state(secretState)
                .callback("http://www.rotenberg.co.il/oauth_callback/")
                .build(GoogleApi20.instance());

    }

    private static OAuth2AccessToken _userToken;
    private static boolean _tokenHeld = false;



    @Pointcut("execution(@GoogleAuth * *(..))")
    public void basicAuthAnnot() {}


    @Around("basicAuthAnnot()")
    public void aroundBasicAuthAnnot(ProceedingJoinPoint point) {

        if (_tokenHeld) {
            try {
                System.out.println("already logged in");
                ArrayList<Object> container = new ArrayList<Object>();
                container.add(_userToken);
                AspectUtils.loggedIn(container); //TODO: this may be the wrong place
                point.proceed();
            } catch (Throwable t) {
                System.out.println("caught throwable, refer to FacebookAuthAspect.");
                System.out.println(t);
            }
        } else {
            staticPoint = point;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    initAndShowGUI();
                }
            });
        }
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
        Browser browser = new Browser();
        browser.AddListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED && !_tokenHeld) {
                    System.out.println("URL: " + browser.getLocation());
                    if (browser.getLocation().startsWith("http://www.rotenberg.co.il")) {
                        String url = browser.getLocation();
                        Pattern p = Pattern.compile(".+state=(.+)&code=(.+)#.*");
                        Matcher m = p.matcher(url);
                        if (m.find()) {
                            String value = m.group(1);
                            String code = m.group(2);

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
                            _userToken = service.getAccessToken(code);
                            System.out.println("Got the Access Token!");
                            System.out.println("(if your curious it looks like this: " + _userToken
                                    + ", 'rawResponse'='" + _userToken.getRawResponse() + "')");

//                            System.out.println("Refreshing the Access Token...");
//                            accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
//                            System.out.println("Refreshed the Access Token!");
//                            System.out.println("(if your curious it looks like this: " + accessToken
//                                    + ", 'rawResponse'='" + accessToken.getRawResponse() + "')");
//                            System.out.println();
                        }

                    }
                }
            }
        });


        Scene scene = new Scene(browser,750,500, Color.web("#666970"));
        final Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        //force to reget refresh token (if usera are asked not the first time)
        additionalParams.put("prompt", "consent");
        final String authorizationUrl = service.getAuthorizationUrl(additionalParams);
        browser.loadUrl(authorizationUrl);

        return (scene);
    }
}