package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.Course;
import om.metamorph.trainingLinks.model.FeedbackModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by OJP on 12/06/2017.
 */

@Path("feedback")
public class Feedback {

    @GET
    @Path("institute/{institute_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getFeedback(@PathParam("institute_id") long instituteId){
        ArrayList<FeedbackModel> feedbackModels = new ArrayList<FeedbackModel>();
        try {
            feedbackModels = DatabaseManager.getInstance().getFeedbackByInstituteId(instituteId);
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FeedbackNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No feedback for this institute or institute not found").build();
        }

        return Response.status(Response.Status.OK).entity(feedbackModels).build();
    }

    @POST
    @Path("institute/{institute_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postFeedback(@PathParam("institute_id") long instituteId,
                                 @HeaderParam("Authorization") String token,
                                 FeedbackModel feedback) {

        if(!Validation.ratingIsValid(feedback.getRating())){
            return Response.status(422).entity("Rating invalid").build();
        }
        FeedbackModel responseFeedback = null;
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                responseFeedback = DatabaseManager.getInstance().postFeedback(instituteId, email, feedback);
            }  catch (MySQLIntegrityConstraintViolationException e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Institute Not found").build();
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotLoggedInException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
            } catch (InstituteNotFoundException e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Institute Not found").build();
            } catch (FeedbackNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Please provide access token").build();
        }

        return Response.status(Response.Status.CREATED).entity(responseFeedback).build();
    }
}
