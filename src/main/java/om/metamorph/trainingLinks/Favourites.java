package om.metamorph.trainingLinks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.Course;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by OJP on 11/06/2017.
 */

@Path("favourites")
public class Favourites {

    @POST
    @Path("courses/{course_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response favouriteCourse(@PathParam("course_id") long courseId, @HeaderParam("Authorization") String token) {

        Course course = null;
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                DatabaseManager.getInstance().favouriteCourseInDb(courseId,email);
                course = DatabaseManager.getInstance().getCourseByIdFromDb(courseId);
            } catch (MySQLIntegrityConstraintViolationException e){
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotLoggedInException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
            } catch (CourseNotFoundException e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Please provide access token").build();
        }

        return Response.status(Response.Status.CREATED).entity(course).build();
    }

    @DELETE
    @Path("courses/{course_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteFavouriteCourse(@PathParam("course_id") long courseId,
                                          @HeaderParam("Authorization") String token) {
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                DatabaseManager.getInstance().removeFavouriteFromDb(courseId,email);;
            } catch (MySQLIntegrityConstraintViolationException e){
                return Response.status(Response.Status.NOT_FOUND).entity("Favourite not found").build();
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotLoggedInException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
            } catch (CourseNotFoundException e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Favourite not found").build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Please provide access token").build();
        }

        return Response.status(Response.Status.NO_CONTENT).entity("Course Deleted").build();
    }

    @POST
    @Path("get")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUserFavourites(String body) {
        String token = new JSONObject(body).getString("access_token");
        ArrayList<Course> courseArrayList = null;
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                courseArrayList = DatabaseManager.getInstance().getFavouritesByEmail(email);
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotLoggedInException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
            } catch (CourseNotFoundException e) {
                return Response.status(Response.Status.NO_CONTENT).entity("No Favourite Courses").build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Please provide access token").build();
        }

        return Response.status(Response.Status.OK).entity(courseArrayList).build();
    }

    @GET
    public Response oldGetFavouritess(){
        return Response.status(Response.Status.GONE).entity("This resource is disused").build();
    }
}
