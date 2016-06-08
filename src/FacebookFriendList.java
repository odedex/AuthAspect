///**
// * Created by Nadav on 03/05/2016.
// */
//import com.restfb.Connection;
//import com.restfb.DefaultFacebookClient;
//import com.restfb.FacebookClient;
//import com.restfb.Version;
//import com.restfb.exception.FacebookException;
//import com.restfb.scope.ScopeBuilder;
//import com.restfb.scope.UserDataPermissions;
//import com.restfb.types.User;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLEncoder;
//
//import static java.lang.System.out;
//
//public class FacebookFriendList {
//
//    final String MY_APP_ID = "497051750494292";
//    final String MY_APP_SECRET = "cbb4519a4b8f0a22647150076f12c7e9";
//    final String redirectURL = "http://www.rotenberg.co.il/redirect";
//
//    public FacebookFriendList() {
//        ScopeBuilder scopeBuilder = new ScopeBuilder();
//        scopeBuilder.addPermission(UserDataPermissions.USER_BIRTHDAY);
//        scopeBuilder.addPermission(UserDataPermissions.USER_ABOUT_ME);
//        FacebookClient client = new DefaultFacebookClient(Version.VERSION_2_2);
//        String loginDialogUrlString = client.getLoginDialogUrl(MY_APP_ID, redirectURL, scopeBuilder);
//
//        FacebookClient.AccessToken accessToken =
//                new DefaultFacebookClient(Version.VERSION_2_5).obtainAppAccessToken(MY_APP_ID,MY_APP_SECRET );
//
//        out.println("My application access token: " + accessToken);
//
//
//    }
//}