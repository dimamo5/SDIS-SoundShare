package auth;

import java.io.Serializable;

/**
 * Created by duarte on 25-05-2016.
 */
public class Token implements Serializable{
    protected String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
