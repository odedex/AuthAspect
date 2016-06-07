import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by OdedA on 05-Jun-16.
 */
public class AuthToken implements Serializable {
    public OAuth2AccessToken token;
    public AuthType type;
    public Date date;

    public AuthToken(OAuth2AccessToken tok,AuthType auth) {
        this.token = tok;
        this.type = auth;
        this.date = new Date();
    }

    public boolean equals(Object other) {
        if (other instanceof AuthToken) {
            AuthToken o = (AuthToken) other;
            return (o.token.equals(this.token) && o.type.equals(this.type));
        }
        return false;
    }
}
