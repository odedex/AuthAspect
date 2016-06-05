import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.lang.annotation.Annotation;
import java.util.Random;
import java.util.Scanner;

@FacebookCreds(clientId = "497051750494292", secret = "cbb4519a4b8f0a22647150076f12c7e9")
@GoogleCreds(clientId = "657134573408-sq3tl4d6fl2l3blrr43o3lfh3vijkkui.apps.googleusercontent.com", secret = "PxieMAkZ-UoAP2R_MP_ENLVS", scope="profile https://www.googleapis.com/auth/tasks")
public class Main {

    public static void test(){
        System.out.println("here");
    }

    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.5/me";

    public static void facebookConnectionInit(){





        // Replace these with your client id and secret
        final String clientId = "497051750494292";
        final String clientSecret = "cbb4519a4b8f0a22647150076f12c7e9";
        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuth20Service service = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .state(secretState)
                .callback("http://www.rotenberg.co.il/oauth_callback/")
                .build(FacebookApi.instance());

        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
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
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken
                + ", 'rawResponse'='" + accessToken.getRawResponse() + "')");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        final Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");



    }
    public static void main(String[] args) throws ClassNotFoundException {

//        test();
//        Counter counter = new Counter();
//        counter.Increase();
//        counter.Increase();
//        counter.Increase();
//        counter.Decrease();
//        counter.Decrease();
//        System.out.println(counter.Get());
        Frame frame = new Frame();
//        facebookConnectionInit();

//
    }


}
