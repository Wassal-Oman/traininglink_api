package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.Course;
import om.metamorph.trainingLinks.model.EnrolledCourse;
import om.metamorph.trainingLinks.model.EnrolledStudent;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by OJP on 11/06/2017.
 */

@Path("enrolments")
public class Enrolments {

    @POST
    @Path("{course_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response enrolToCourse(EnrolledStudent student,
                                  @PathParam("course_id") long courseId,
                                  @HeaderParam("Authorization") String token) {

        if(!Validation.nameIsValid(student.getName())){
             return Response.status(422).entity("Name must be 3 characters at least").build();
        }

        if(!Validation.phoneIsValid(student.getPhone())){
            return Response.status(422).entity("Invalid phone number").build();
        }

        Course course = null;
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                DatabaseManager.getInstance().enrolCourseInDb(courseId,email, student.getName(),student.getPhone());
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

    @POST
    @Path("get")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getEnrolments(String body) {
        String token = new JSONObject(body).getString("access_token");
        ArrayList<EnrolledCourse> courseArrayList = null;
        if (token != null) {
            try {
                String email = DatabaseManager.getInstance().getEmailFromToken(token);
                courseArrayList = DatabaseManager.getInstance().getEnrolmentsByEmail(email);
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
    public Response oldGetEnrolments(){
        return Response.status(Response.Status.GONE).entity("This resource is disused").build();
    }
}
