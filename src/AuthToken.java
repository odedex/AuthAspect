import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.Serializable;
import java.util.Date;

/**
 * Wrapper class to represent a single authentication
 */
public class AuthToken implements Serializable {
    public OAuth2AccessToken token;
    public AuthType type;
    public Date date;

    /**
     * constructor
     * @param tok supplied by the authentication service (e.g. facebook)
     * @param auth enum of the authentication type
     */
    public AuthToken(OAuth2AccessToken tok, AuthType auth) {
        this.token = tok;
        this.type = auth;
        this.date = new Date();
    }

    /**
     * equals implementation
     * @param other token to check
     * @return tue iff other is equal to this token
     */
    public boolean equals(Object other) {
        if (other instanceof AuthToken) {
            AuthToken o = (AuthToken) other;
            return (o.token.equals(this.token) && o.type.equals(this.type));
        }
        return false;
    }
}
