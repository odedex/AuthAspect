import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OdedA on 31-May-16.
 */
public class AspectUtils{

    private static Map authKeys = new HashMap();

    public static OAuth2AccessToken getToken(AuthType type){
        if (authKeys.containsKey(type)){
            return (OAuth2AccessToken) authKeys.get(type);
        }
        return null;
    }


    private static String tokenFileName = "authToken";

    public static void loggedIn(OAuth2AccessToken token, AuthType authType) {
        authKeys.put(authType, token);
    }

    public static OAuth2AccessToken attemptingLogIn(AuthType authType) {

//        System.out.println("RETURNING NULL FROM ASPECTUTILS");
        return null;
    }

    public static void finishedLogIn() {
//        System.out.println("an attempt to log in has ended");
    }

    public static boolean writeAuthToFile(String filePrefix, AuthToken authToken) {
        try{

            FileOutputStream fout = new FileOutputStream(filePrefix.concat(tokenFileName));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(authToken);
            oos.close();
            System.out.println("Done writing token to file");
            return true;

        } catch(Exception ex) {
            System.out.println("FAILED TO WRITE AUTH TOKEN TO FILE!");
            ex.printStackTrace();
            return false;
        }
    }

    public static AuthToken readAuthFromFile(String filePrefix) {
        File f = new File(filePrefix.concat(tokenFileName));
        if(!(f.exists() && !f.isDirectory())) {
            System.out.println("no token found on disk");
            return null;
        }
        try{

            FileInputStream fin = new FileInputStream(filePrefix.concat(tokenFileName));
            ObjectInputStream ois = new ObjectInputStream(fin);
            AuthToken token = (AuthToken) ois.readObject();
            ois.close();

            System.out.println("Done reading token from disk");
            return token;

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
