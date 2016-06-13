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

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Aspect to manage the user authentication of the users, managed by google.
 * Requires the addition of a @GoogleCreds annotation before the main class of the program.
 * Will validate authentication for every function having @GoogleAuth before its' declaration.
 * Saves an existing token during the sessions
 */
@Aspect
public class GoogleAuthAspect{
    private static ProceedingJoinPoint staticPoint;

    private static JFrame frame;

    String clientId = "";
    String clientSecret = "";
    String scope = "";
    static String secretState;
    static OAuth20Service service;

    private static OAuth2AccessToken _userToken;
    private static boolean _tokenHeld = false;

    /**
     * Constructor.
     * Validates the existence of a @GoogleCreds annotation.
     * @throws Exception if @GoogleCreds was not found
     */
    public GoogleAuthAspect() throws Exception{

        boolean googleCreds = false;

        for(Annotation annotation : CredentialsAspect.annotations){
            if(annotation instanceof GoogleCreds){
                GoogleCreds myAnnotation = (GoogleCreds) annotation;
                clientId = myAnnotation.clientId();
                clientSecret = myAnnotation.secret();
                scope = myAnnotation.scope();
                googleCreds= true;
            }
        }

        if (!googleCreds) {
            throw new Exception("Google credentials were not supplied\nPlease add " +
                    "@GoogleCreds(clientId = <clientID>, secret = <secret> before the program's main class");
        }

        secretState = "secret" + new Random().nextInt(999_999);
        service = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .scope(scope) // replace with desired scope
                .state(secretState)
                .callback("http://www.rotenberg.co.il/oauth_callback/")
                .build(GoogleApi20.instance());

    }

    /**
     * Around an annonymous pointcut containing the execution of any function with the annotation @GoogleAuth
     * validates the existence of a token. If no token is currently held, open a browser with google for the
     * user to log into with the google accout.
     * @param point ProceedingJoinPoint
     */
    @Around("@annotation(GoogleAuth) && execution(* *(..))")
    public void ArounGoogleAuthAnnotation(ProceedingJoinPoint point) {
        OAuth2AccessToken authToken;
        // If no token is currently available, try reading one from the disk.
        if (!_tokenHeld) {
            authToken = AspectUtils.attemptingLogIn(AuthType.GOOGLE);
            if (authToken != null) {
                _userToken = authToken;
                _tokenHeld = true;
            }
        }

        // If a token was successfully read from the disk, let the advised function proceed uninterrupted.
        if (_tokenHeld) {
            AspectUtils.finishedLogIn();
            try {
//                System.out.println("already logged in");
                point.proceed();
            } catch (Throwable t) {
                System.err.println(t);
            }
        }
        // If no token was found, open a browser to let the user log in.
        else {
            staticPoint = point;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    initAndShowGUI();
                }
            });
        }
    }

    /**
     * Open a JFrame window containing the browser to let the user log in.
     */
    private static void initAndShowGUI() {
        // This method is invoked on the EDT thread
        frame = new JFrame("Login Using Aspect");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Add a custom window closing listener to tell the time-window aspect that the logging in process is done.
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                System.out.println("Browser window closed.");
                frame.setVisible(false);
                AspectUtils.finishedLogIn();
            }
        });

        // Add the browser to the JFrame.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    /**
     * Create the browser scene and set it as the fxpanel's scene
     * @param fxPanel
     */
    private static void initFX(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    /**
     * Create a browser object and add a listener that parses a token out of the url.
     * @return the created Scene
     */
    private static Scene createScene() {
        Browser browser = new Browser();
        browser.AddListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED && !_tokenHeld) {
                    if (browser.getLocation().startsWith("http://www.rotenberg.co.il")) { // make sure we are in the right page
                        String url = browser.getLocation();
                        Pattern p = Pattern.compile(".+state=(.+)&code=(.+)#.*");
                        Matcher m = p.matcher(url);
                        if (m.find()) {
                            String value = m.group(1);
                            String code = m.group(2);
                            // Trade the Request Token and Verfier for the Access Token
                            _userToken = service.getAccessToken(code);
                            _tokenHeld = true;

                            // When the token is found, hide the browser frame, and call the utils function to let the
                            // time-window aspects that the log in has finished. proceed the advised function.
                            try {
                                frame.setVisible(false);
                                AspectUtils.loggedIn(_userToken, AuthType.GOOGLE);
                                AspectUtils.finishedLogIn();
                                staticPoint.proceed();
                            } catch (Throwable t) {
                                System.err.println(t);
                            }
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