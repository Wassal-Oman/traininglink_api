package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.BaseInstitute;
import om.metamorph.trainingLinks.model.Course;
import om.metamorph.trainingLinks.model.Institute;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by OJP on 11/06/2017.
 */

@Path("institutes")
public class Institutes {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getInstitutes(@DefaultValue("false") @QueryParam("title_only") boolean titleOnly){
        ArrayList<? extends BaseInstitute> institutes = null;

        if(titleOnly){
            try {
                institutes = DatabaseManager.getInstance().getInstituteNames();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                institutes = DatabaseManager.getInstance().getInstitutesWithDetails();
            } catch (MySQLNonTransientConnectionException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        if(institutes.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).entity("No Institutes in database").build();
        }
        else{
            return Response.status(Response.Status.OK).entity(institutes).build();
        }
    }

    @GET
    @Path("{institute_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCourseById(@PathParam("institute_id") int instituteId){

        Institute institute = null;
        try{
            institute = DatabaseManager.getInstance().getInstituteByIdFromDb(instituteId);
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e){
            e.printStackTrace();
        } catch (InstituteNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Institute ID: " + instituteId + " Not found").build();
        }

        return Response.status(Response.Status.OK).entity(institute).build();
    }
}
