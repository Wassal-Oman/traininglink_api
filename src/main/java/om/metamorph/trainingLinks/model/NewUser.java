package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 10/05/2017.
 */

//This model is used for login or Registration
public class NewUser extends User {

    private String password;
    @JsonProperty("social_access_token")
    private String socialAccessToken;
    @JsonProperty("token_secret")
    private String tokenSecret;
    @JsonProperty("facebook_id")
    private String socialId;
    @JsonProperty("twitter_id")
    private String twitterId;

    public NewUser(){}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocialAccessToken() {
        return socialAccessToken;
    }

    public void setSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }
}
