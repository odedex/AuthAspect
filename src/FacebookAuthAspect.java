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



@Aspect
public class FacebookAuthAspect {

    private static ProceedingJoinPoint staticPoint;

    private static JFrame frame;

    String clientId = "";
    String clientSecret = "";
    static String secretState;
    static OAuth20Service service;


    public FacebookAuthAspect() throws ClassNotFoundException, Exception{

        boolean facebookCreds = false;
        String scope = "";

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
                .callback("http://www.rotenberg.co.il/oauth_callback/")
                .build(FacebookApi.instance());


    }

    private static OAuth2AccessToken _userToken;
    private static boolean _tokenHeld = false;



    @Around("@annotation(FacebookAuth) && execution(* *(..))")
    public void aroundBasicAuthAnnot(ProceedingJoinPoint point) {
        OAuth2AccessToken authToken = null;
        if (!_tokenHeld) {
            authToken = AspectUtils.attemptingLogIn(AuthType.FACEBOOK);
//            System.out.println(authToken);
        }
        if (authToken != null) {
            _userToken = authToken;
            _tokenHeld = true;
        }
        if (_tokenHeld) {
            AspectUtils.finishedLogIn();
            try {
                System.out.println("already logged in");
//                AspectUtils.loggedIn(_userToken, AuthType.FACEBOOK);
                point.proceed();
            } catch (Throwable t) {
                System.err.println(t);
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

    @Pointcut(value="@annotation(FacebookPrivateResource)")
    protected void getPrvateResource(FacebookPrivateResource FacebookPrivateResource) {
    }

    @Around("getPrvateResource(FacebookPrivateResource)")
    public String dowork(FacebookPrivateResource FacebookPrivateResource){
        final OAuthRequest request = new OAuthRequest(Verb.GET, FacebookPrivateResource.url(), service);
        service.signRequest(_userToken, request);
        final Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
        return response.getBody();

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

                System.out.println("Browser window closed.");
                frame.setVisible(false);
                AspectUtils.finishedLogIn();
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
//                                        System.out.println("URL: " + browser.getLocation());
                                        if (browser.getLocation().startsWith("http://www.rotenberg.co.il")) {
                                            String url = browser.getLocation();
                                            Pattern p = Pattern.compile(".+code=(.+)&state=(.+)#.*");
                                            Matcher m = p.matcher(url);
                                            if (m.find()) {
                                                String code = m.group(1);
                                                String value = m.group(2);
                                                // Trade the Request Token and Verfier for the Access Token
                                                _userToken = service.getAccessToken(code);
                                                _tokenHeld = true;
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
