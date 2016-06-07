import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import javafx.application.Platform;
import javafx.concurrent.Worker;
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

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Created by OdedA on 31-May-16.
 */
public class AspectUtils {


    private static String tokenFileName = "authToken";

    public static void loggedIn(OAuth2AccessToken token, AuthType authType) {
    }

    public static OAuth2AccessToken attemptingLogIn(AuthType authType) {
        return null;
    }

    public static boolean writeAuthToFile(String filePrefix, AuthToken authToken) {
        try{
            FileOutputStream fout = new FileOutputStream(filePrefix.concat(tokenFileName));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(authToken);
            oos.close();
            System.out.println("Done writing");
            return true;

        } catch(Exception ex) {
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

            System.out.println("Done reading");
            return token;

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
