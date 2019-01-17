package om.metamorph.trainingLinks;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.apache.commons.validator.routines.EmailValidator;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sabri K on 15/05/2017.
 */
public class Validation {

    public static final long URGENT_TIME = 259200; //number of seconds in 3 days
    public static final long SOON_TIME = 604800; //number of seconds in 3 days
    public static int FULL_NAME_MIN_LENGTH = 3;
    public static int PASSWORD_MIN_LENGTH = 6;
    public static String AUTHENTICATION_TYPE_EMAIL = "email";
    public static String AUTHENTICATION_TYPE_FACEBOOK = "facebook";
    public static String AUTHENTICATION_TYPE_TWITTER = "twitter";

    public static boolean emailIsValid(String email){
        System.out.print("yellow");
        return email!= null && EmailValidator.getInstance().isValid(email);
    }

    public static boolean nameIsValid(String name) {
        return name != null && (name.length() >= FULL_NAME_MIN_LENGTH);
    }

    public static boolean passwordIsValid(String password){
        return password != null && (password.length() >= PASSWORD_MIN_LENGTH);
    }

    public static boolean phoneIsValid(String phone){
        return phone != null;
    }

    public static boolean authenticationTypeIsValid(String authenticationType){
        return authenticationType != null && ( authenticationType.equals(AUTHENTICATION_TYPE_EMAIL) ||
                                        authenticationType.equals(AUTHENTICATION_TYPE_FACEBOOK) ||
                                        authenticationType.equals(AUTHENTICATION_TYPE_TWITTER));
    }

    public static boolean socialAccessTokenIsValid(String SocialAccessToken){
        return true;
    }

    public static boolean ratingIsValid(short rating) {
        return (rating >= 1 && rating <= 5);
    }

    public static boolean facebookAccessTokenIsValid(String facebookId, String token) {
        JerseyClient client = JerseyClientBuilder.createClient();
        JerseyWebTarget webTarget =
                client.target("https://graph.facebook.com/me?access_token=" + token);
        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
        if(response.getStatus() == Response.Status.OK.getStatusCode()){
            response.bufferEntity();
            JSONObject jsonObject = new JSONObject(response.readEntity(String.class));
            String responseFacebookId = jsonObject.getString("id");
            return responseFacebookId.equals(facebookId);
        }
        else{
            return false;
        }
    }

    public static boolean twitterAccessTokenIsValid(String twitterId, String twitterAccessToken, String twitterTokenSecret)
            throws IOException, ExecutionException, InterruptedException, TwitterCredentialsException {

        /*final OAuth10aService service = new ServiceBuilder(TwitterConfig.TWITTER_CONSUMER_KEY)
                .apiKey(TwitterConfig.TWITTER_CONSUMER_KEY)
                .apiSecret(TwitterConfig.TWITTER_CONSUMER_SECRET)
                .build(TwitterApi.instance());

        OAuth1AccessToken oAuth1AccessToken = new OAuth1AccessToken(twitterAccessToken,twitterTokenSecret);

        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
        service.signRequest(oAuth1AccessToken, request); // the access token from step 4
        final com.github.scribejava.core.model.Response response = service.execute(request);
        System.out.println(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        String responseTwitterId = jsonObject.getString("id_str");
        return responseTwitterId.equals(twitterId);*/

        return true;
    }
}
