import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuthService;

/**
 * TODO: document this
 */
public class FacebookScribeAuthenticator {

    public static final String STATE = "state";
    private String applicationHost;
    private OAuthService oAuthService;
    // Jackson ObjectMapper


    public FacebookScribeAuthenticator(
                    String clientId,
                    String clientSecret,
                    String applicationHost) {
        this.applicationHost = applicationHost;
        this.oAuthService = buildOAuthService(clientId, clientSecret);

    }


    private OAuthService buildOAuthService(String clientId,
                                           String clientSecret) {
        // The callback must match Site-Url in the Facebook app settings
        return new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .callback(applicationHost + "/auth/facebook/callback")
                .build(FacebookApi.instance());
    }
}