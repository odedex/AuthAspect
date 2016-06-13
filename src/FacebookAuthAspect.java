import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.annotation.Annotation;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Aspect to manage the user authentication of the users, managed by facebook.
 * Requires the addition of a @FacebookCreds annotation before the main class of the program.
 * Will validate authentication for every function having @FacebookAuth before its' declaration.
 * Saves an existing token during the session.
 * The aspect exposes an option to get a privte url given any resource url of Facebook Graph.
 */
@Aspect
public class FacebookAuthAspect {

    private static ProceedingJoinPoint staticPoint;

    private static JFrame frame;

    String clientId = "";
    String clientSecret = "";
    static String secretState;
    static OAuth20Service service;

    private static OAuth2AccessToken _userToken;
    private static boolean _tokenHeld;

    /**
     * Constructor.
     * Validates the existence of a @FacebookCreds annotation.
     * @throws Exception if @FacebookCreds was not found
     */
    public FacebookAuthAspect() throws Exception{

        boolean facebookCreds = false;
        String scope = "";
        _tokenHeld = false;

        for(Annotation annotation : CredentialsAspect.annotations){
            if(annotation instanceof FacebookCreds){
                FacebookCreds myAnnotation = (FacebookCreds) annotation;
                clientId = myAnnotation.clientId();
                clientSecret = myAnnotation.secret();
                scope = myAnnotation.scope();
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
                .scope(scope)
                .callback("http://www.rotenberg.co.il/oauth_callback/") // callback of authorized website
                .build(FacebookApi.instance());

    }

    /**
     * Around an annonymous pointcut containing the execution of any function with the annotation @FacebookAuth
     * validates the existence of a token. If no token is currently held, open a browser with facebook for the
     * user to log into with the facebook accout.
     * @param point ProceedingJoinPoint
     */
    @Around("@annotation(FacebookAuth) && execution(* *(..))")
    public void AroundFacebookAuthAnnotation(ProceedingJoinPoint point) {
        OAuth2AccessToken authToken;
        // If no token is currently available, try reading one from the disk.
        if (!_tokenHeld) {
            authToken = AspectUtils.attemptingLogIn(AuthType.FACEBOOK);
            if (authToken != null) {
                _userToken = authToken;
                _tokenHeld = true;
            }
        }

        // If a token was successfully read from the disk, let the advised function proceed uninterrupted.
        if (_tokenHeld) {
            AspectUtils.finishedLogIn();
            try {
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
     * Used to wrap execution of methods who returns a resource from facebook
     * @param FacebookPrivateResource the facebook graph url to acess
     */
    @Pointcut(value="@annotation(FacebookPrivateResource)")
    protected void getPrivateResource(FacebookPrivateResource FacebookPrivateResource) {
    }

    /**
     * get the resource from facebook URL
     * @param FacebookPrivateResource url to acccess
     * @return
     */
    @Around("getPrivateResource(FacebookPrivateResource)")
    public String getResource(FacebookPrivateResource FacebookPrivateResource){
        if (!_tokenHeld){
            return "";
        }
        final OAuthRequest request = new OAuthRequest(Verb.GET, FacebookPrivateResource.url(), service);
        //use the service and the token given
        service.signRequest(_userToken, request);
        final Response response = request.send();
        return response.getBody();
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
                    if (browser.getLocation().startsWith("http://www.rotenberg.co.il")) { // make sure we are the right page
                        String url = browser.getLocation();
                        Pattern p = Pattern.compile(".+code=(.+)&state=(.+)#.*");
                        Matcher m = p.matcher(url);
                        if (m.find()) {
                            String code = m.group(1);
                            String value = m.group(2);
                            // Trade the Request Token and Verfier for the Access Token
                            _userToken = service.getAccessToken(code);
                            _tokenHeld = true;

                            // When the token is found, hide the browser frame, and call the utils function to let the
                            // time-window aspects that the log in has finished. proceed the advised function.
                            try {
                                frame.setVisible(false);
                                AspectUtils.loggedIn(_userToken, AuthType.FACEBOOK);
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
        final String authorizationUrl = service.getAuthorizationUrl();
        browser.loadUrl(authorizationUrl);

        return (scene);
    }
}
