package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.Course;
import om.metamorph.trainingLinks.model.CourseCategory;
import om.metamorph.trainingLinks.model.CourseFilter;
import om.metamorph.trainingLinks.model.CourseLocations;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by OJP on 28/05/2017.
 */

@Path("courses")
public class Courses {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCourse(@DefaultValue("0")@QueryParam("offset")long offset,
                              @DefaultValue("9223372036854775807")@QueryParam("limit") long limit,
                              @DefaultValue("false")@QueryParam("is_featured") boolean isFeatured,
                              @DefaultValue("false")@QueryParam("is_urgent") boolean isUrgent,
                              @DefaultValue("false")@QueryParam("is_ending") boolean isEnding,
                              @DefaultValue("")@QueryParam("search_query") String searchQuery){

        ArrayList<Course> courses = new ArrayList<Course>();
        try {
            if(isFeatured){
                courses = DatabaseManager.getInstance().getFeaturedCourses(offset,limit);
            }
            else if(isUrgent){
                courses = DatabaseManager.getInstance().getUrgentCourses(offset,limit);
            }
            else if(isEnding){
                courses = DatabaseManager.getInstance().getEndingCourses(offset,limit);
            }
            else if(!searchQuery.equals("")){
                courses = DatabaseManager.getInstance().getSearchCourses(searchQuery, offset,limit);
            }
            else {
                courses = DatabaseManager.getInstance().getCourses(offset, limit);
            }
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoCoursesException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(courses).build();
        }

        return Response.status(Response.Status.OK).entity(courses).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getFilteredCourse(@DefaultValue("0")@QueryParam("offset")long offset,
                                      @DefaultValue("9223372036854775807")@QueryParam("limit") long limit,
                                      CourseFilter courseFilter){

        ArrayList<Course> courses = new ArrayList<Course>();
        try {
            if(courseFilter.getInstituteId()!= null) {
                courses = DatabaseManager.getInstance()
                        .getCoursesByInstitute(courseFilter.getInstituteId(),offset, limit);
            }
            else if(courseFilter.getCourseCategories() != null && !courseFilter.getCourseCategories().isEmpty()){
                courses = DatabaseManager.getInstance()
                        .getCoursesByCategory(courseFilter.getCourseCategories(), offset, limit);
            }
            else if(courseFilter.getCourseLocations() != null){
                courses = courses = DatabaseManager.getInstance()
                        .getCoursesByLocation(courseFilter.getCourseLocations(),offset, limit);
            }
            else {
                courses = DatabaseManager.getInstance().getCourses(offset, limit);
            }
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoCoursesException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(courses).build();
        }
        return Response.status(Response.Status.OK).entity(courses).build();
    }

    @GET
    @Path("{course_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCourseById(@PathParam("course_id") int courseId){

        Course course = null;
        try{
            course = DatabaseManager.getInstance().getCourseByIdFromDb(courseId);
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e){
            e.printStackTrace();
        } catch (CourseNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course ID: " + courseId + " Not found").build();
        }

        return Response.status(Response.Status.OK).entity(course).build();
    }

    @GET
    @Path("locations")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCourseLocations(){
        CourseLocations courseLocations = null;
        try{
            courseLocations = DatabaseManager.getInstance().getDistinctLocations();
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(courseLocations.getLocations().isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).entity("No courses").build();
        }
        else{
            return Response.status(Response.Status.OK).entity(courseLocations).build();
        }
    }

    @GET
    @Path("categories")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCourseCategories(){
        ArrayList<CourseCategory> courseCategories = new ArrayList<CourseCategory>();
        try{
            courseCategories = DatabaseManager.getInstance().getCourseCategories();
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(courseCategories.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).entity("No courses in this category").build();
        }
        else{
            return Response.status(Response.Status.OK).entity(courseCategories).build();
        }
    }
}
