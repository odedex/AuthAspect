import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.*;

/**
 *  Utils class for the project
 */
public class AspectUtils{

    private static String tokenFileName = "authToken";

    /**
     * Called upon successful log in's by authentication aspects.
     * Used as a joinpoints for the time-window aspects.
     * @param token supplied by the authentication service (e.g. facebook)
     * @param authType enum of the authentication type
     */
    public static void loggedIn(OAuth2AccessToken token, AuthType authType) {

    }

    /**
     * Called when an authentication aspect is about to start the process of logging in.
     * Used as a joinpoint for time-window aspects.
     * @param authType enum of the authentication type
     * @return token if one exists on the disk
     */
    public static OAuth2AccessToken attemptingLogIn(AuthType authType) {
        return null;
    }

    /**
     * Called when a login process is finished. Also called when the browser windows closes.
     * Used as a joinpoint for time-window aspects
     */
    public static void finishedLogIn() {
    }

    /**
     * Write an authentication token to a local file. Note that the token is not encrypted when written to the file!
     * @param filePrefix path prefix of the file to be written to the disk
     * @param authToken authentication token to write
     * @return true iff the file was successfully written to disk
     */
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

    /**
     * Read an authentication token from a local file.
     * @param filePrefix path prefix of the file to be read from the disk
     * @return AuthToken if successfully read from disk. null otherwise.
     */
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
