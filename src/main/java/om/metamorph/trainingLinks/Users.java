package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.*;
import org.json.JSONObject;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.ExecutionException;


/**
 * Created by Sabri on 27/09/16.
 */
@Path("users")
public class Users {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createNewUser(NewUser newUser) {

        //validate user and store in db
        try {
            if(newUserIsValid(newUser)) {
                String token = null;
                try {
                    //generate Access Token
                    token = AuthenticationManager.generateUserToken();
                    DatabaseManager.getInstance().createNewUser(newUser);
                    token = DatabaseManager.getInstance().insertTokenIntoDb(newUser,
                            AuthenticationManager.generateUserToken());
                } catch(MySQLIntegrityConstraintViolationException e) {
                    return Response.status(Response.Status.CONFLICT).build();
                } catch (MySQLNonTransientConnectionException e) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
                }  catch (Exception e) {
                    System.out.println(e);
                }

                User user = new User();
                user.setName(newUser.getName());
                user.setEmail(newUser.getEmail());
                user.setPhone(newUser.getPhone());
                user.setAuthenticationType(newUser.getAuthenticationType());

                return Response.status(Response.Status.CREATED).header("Authorization", token).entity(user).build();
            }
            else {
                return Response.status(422).build();
            }
        } catch (FacebookAccessTokenInvalidException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook Access Token Invalid").build();
        }
    }

    private boolean newUserIsValid(NewUser newUser) throws FacebookAccessTokenInvalidException {

        //validate common properties
        if(!(Validation.emailIsValid(newUser.getEmail()) &&
                Validation.nameIsValid(newUser.getName()) &&
                Validation.phoneIsValid(newUser.getPhone()))){
            return false;
        }
        //validate if email login
        if(newUser.getAuthenticationType().equals(Validation.AUTHENTICATION_TYPE_EMAIL)){
            if(!(Validation.passwordIsValid(newUser.getPassword()))){
                return false;
            }
        }
        else {
            return true;
        }
        return true;
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response loginUser(NewUser user){
        String accessToken = null;
        UserRow userRow = null;
        //decide what kinda login first
        try{
            if(user.getAuthenticationType().equals("facebook")){
//                facebookLogin(user);
                if(!Validation.facebookAccessTokenIsValid("asdfas"," asdfa")){
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook Token Invalid").build();
                }
                else{
                    //check if email exists
                    if(!DatabaseManager.getInstance().emailExists(user.getEmail())){
                        DatabaseManager.getInstance().createNewUser(user);
                    }
                    userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
                    accessToken = facebookLogin(user);
                }
            }
            else if(user.getAuthenticationType().equals("twitter")){
//                twitterLogin(user);
            }
            else if(user.getAuthenticationType().equals("email")){
                if(emailLoginIsValid(user)) {
                    userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
                    accessToken = emailLogin(user);

                }
                else{
                    return Response.status(422).entity("Make sure email, password & phone number is valid").build();
                }
            }
            else {
                return Response.status(422).entity("Make Authentication type is valid").build();
            }
        }catch (CredentialException e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Error","User not found");
            return Response.status(Response.Status.NOT_FOUND).entity(jsonObject.toString()).build();
        }
        catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        }  catch (UserPasswordWrongException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Wrong Password").build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        User responseUser = new User();
        responseUser.setEmail(userRow.getEmail());
        responseUser.setName(userRow.getName());
        responseUser.setPhone(userRow.getPhone());
        responseUser.setAuthenticationType(userRow.getAuthenticationType());
        return Response.status(200).header("Authorization",accessToken).entity(responseUser).build();
    }

    @POST
    @Path("facebook_login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response facebookLogin2(NewUser user){
        //validate email
        if(!Validation.emailIsValid(user.getEmail())){
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Email").build();
        }

        try {
            //check if user exists
            if(!DatabaseManager.getInstance().emailExists(user.getEmail())){
                //validate name, phone, facebook
                if(!Validation.nameIsValid(user.getName())){
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Name").build();
                }
                else if(!Validation.phoneIsValid(user.getPhone())){
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid phone").build();
                }
                else if(!Validation.facebookAccessTokenIsValid(user.getSocialId(),user.getSocialAccessToken())){
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook Token is Invalid").build();
                }
                else{
                    //create new Facebook user
                    DatabaseManager.getInstance().createFacebookUser(user);
                    String token = DatabaseManager.getInstance().insertTokenIntoDb(user,
                            AuthenticationManager.generateUserToken());

                    //respond with user object
                    User responseUser = new User();
                    responseUser.setEmail(user.getEmail());
                    responseUser.setName(user.getName());
                    responseUser.setPhone(user.getPhone());
                    responseUser.setAuthenticationType(Validation.AUTHENTICATION_TYPE_FACEBOOK);
                    return Response.status(200).header("Authorization",token).entity(responseUser).build();
                }
            }
            else{
                UserRow userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
                if(!userRow.getAuthenticationType().equals(Validation.AUTHENTICATION_TYPE_FACEBOOK)){
                    return Response.status(Response.Status.CONFLICT).entity("Email is registered with: "
                            + userRow.getAuthenticationType()).build();
                }
                else if(!Validation.facebookAccessTokenIsValid(user.getSocialId(),user.getSocialAccessToken())){
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook Token is Invalid").build();
                }
                else {
                    String token = DatabaseManager.getInstance().insertTokenIntoDb(user,
                            AuthenticationManager.generateUserToken());
                    //respond with user object
                    User responseUser = new User();
                    responseUser.setEmail(userRow.getEmail());
                    responseUser.setName(userRow.getName());
                    responseUser.setPhone(userRow.getPhone());
                    responseUser.setAuthenticationType(userRow.getAuthenticationType());
                    return Response.status(200).header("Authorization",token).entity(responseUser).build();
                }
            }
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        }  catch (CredentialException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("D=").build();
    }

    @POST
    @Path("twitter_login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response twitterLogin(NewUser user){
        //validate email
        if(!Validation.emailIsValid(user.getEmail())){
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Email").build();
        }

        try {
            //check if user exists
            if(!DatabaseManager.getInstance().emailExists(user.getEmail())){
                //validate name, phone, facebook
                if(!Validation.nameIsValid(user.getName())){
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Name").build();
                }
                else if(!Validation.phoneIsValid(user.getPhone())){
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid phone").build();
                }
                else if(!Validation.twitterAccessTokenIsValid(user.getTwitterId(),user.getSocialAccessToken(),user.getTokenSecret())){
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Twitter Token is Invalid").build();
                }
                else{
                    //create new Twitter user
                    DatabaseManager.getInstance().createTwitterUser(user);
                    String token = DatabaseManager.getInstance().insertTokenIntoDb(user,
                            AuthenticationManager.generateUserToken());

                    //respond with user object
                    User responseUser = new User();
                    responseUser.setEmail(user.getEmail());
                    responseUser.setName(user.getName());
                    responseUser.setPhone(user.getPhone());
                    responseUser.setAuthenticationType(Validation.AUTHENTICATION_TYPE_TWITTER);
                    return Response.status(200).header("Authorization",token).entity(responseUser).build();
                }
            }
            else{
                UserRow userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
                if(!userRow.getAuthenticationType().equals(Validation.AUTHENTICATION_TYPE_TWITTER)){
                    return Response.status(Response.Status.CONFLICT).entity("Email is registered with: "
                            + userRow.getAuthenticationType()).build();
                }
                else if(!Validation.twitterAccessTokenIsValid(user.getTwitterId(),user.getSocialAccessToken(),user.getTokenSecret())){
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Twitter Token is Invalid").build();
                }
                else {
                    String token = DatabaseManager.getInstance().insertTokenIntoDb(user,
                            AuthenticationManager.generateUserToken());
                    //respond with user object
                    User responseUser = new User();
                    responseUser.setEmail(userRow.getEmail());
                    responseUser.setName(userRow.getName());
                    responseUser.setPhone(userRow.getPhone());
                    responseUser.setAuthenticationType(userRow.getAuthenticationType());
                    return Response.status(200).header("Authorization",token).entity(responseUser).build();
                }
            }
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        }  catch (CredentialException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (TwitterCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.toString()).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("D=").build();
    }


    private boolean emailLoginIsValid(NewUser newUser){
        return Validation.emailIsValid(newUser.getEmail()) &&
                Validation.passwordIsValid(newUser.getPassword());
    }

    private String emailLogin(NewUser user) throws SQLException, CredentialException, UserPasswordWrongException {
        UserRow userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
        if (!AuthenticationManager.check(user.getPassword(), userRow.getPasswordHash(), userRow.getPasswordSalt())) {
            throw new UserPasswordWrongException();
        }
        else {
            return DatabaseManager.getInstance()
                    .insertTokenIntoDb(user, AuthenticationManager.generateUserToken());
        }
    }

    private String facebookLogin(NewUser user) throws SQLException, CredentialException, UserPasswordWrongException {

        return DatabaseManager.getInstance().insertTokenIntoDb(user, AuthenticationManager.generateUserToken());
    }

    @Path("logout")
    @POST
    public Response logout(@HeaderParam("Authorization") String accessToken){
        try{
            DatabaseManager.getInstance().deleteAccessToken(accessToken);

        }catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }catch (CredentialNotFoundException e){
            return Response.status(Response.Status.OK).entity("User is already Logged out").build();
        }
        return Response.status(Response.Status.OK).entity("User Logged out").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(UserUpdateRequest userUpdateRequest, @HeaderParam("Authorization") String token){

        //Check if user is logged in
        User user = null;
        if (token != null) {

            //get Email from token
            String email = null;
            try {
                email = DatabaseManager.getInstance().getEmailFromToken(token);

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotLoggedInException e) {
                e.printStackTrace();
            }

            //update phone
            if(userUpdateRequest.getPhone()!= null){
                if(Validation.phoneIsValid(userUpdateRequest.getPhone())) {
                    try {
                        updatePhone(email, userUpdateRequest.getPhone());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            //get User Data after updating phone number
            user = new User();
            user.setEmail(email);
            UserRow userRow = null;
            try {
                userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);
            } catch (CredentialException e) {
                e.printStackTrace();
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            user.setName(userRow.getName());
            user.setPhone(userRow.getPhone());
            user.setAuthenticationType(userRow.getAuthenticationType());

            //update password
            if(userUpdateRequest.getNewPassword()!=null){
                if(Validation.phoneIsValid(userUpdateRequest.getNewPassword())){
                    try {
                        updatePassword(email, userUpdateRequest.getOldPassword(), userUpdateRequest.getNewPassword());
                    } catch (MySQLNonTransientConnectionException e) {
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (CredentialException e) {
                        return Response.status(Response.Status.FORBIDDEN).entity("Please ensure correct OLD password").build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Please provide access token").build();
        }

        return Response.status(Response.Status.OK).entity(user).build();
    }

    private void updatePhone(String email, String phone) throws SQLException {
        DatabaseManager.getInstance().updatePhone(email, phone);
    }

    private void updatePassword(String email, String oldPassword, String newPassword) throws Exception {
        User user = new User();
        user.setEmail(email);
        UserRow userRow = DatabaseManager.getInstance().getAuthenticatedUserData(user);

        //check old password is correct
        if (!AuthenticationManager.check(oldPassword,
                userRow.getPasswordHash(), userRow.getPasswordSalt())) {
            throw new CredentialException();
        }
        else{
            DatabaseManager.getInstance().updatePassword(email,newPassword);
        }
    }

    @Path("forgot_password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(Email email){
        if(!Validation.emailIsValid(email.getEmail())){
            return Response.status(Response.Status.BAD_REQUEST).entity("Please enter a valid email").build();
        }
        try{
            if(!DatabaseManager.getInstance().emailExists(email.getEmail())){
                return Response.status(422).entity("User email does not exist").build();
            }
        }catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity("Email sent to user").build();
    }

    @Path("check_registration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUserRegistration(@QueryParam("email") String email){
        try {
            JSONObject response = new JSONObject();
            response.put("registered",DatabaseManager.getInstance().emailExists(email));
            return Response.status(Response.Status.OK).entity(response.toString()).build();

        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}