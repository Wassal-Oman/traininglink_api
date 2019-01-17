package om.metamorph.trainingLinks;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import om.metamorph.trainingLinks.database.DatabaseManager;
import om.metamorph.trainingLinks.model.Advertisement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by OJP on 20/06/2017.
 */

@Path("advertisements")
public class Advertisements {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAllAdvertisements(){

        ArrayList<Advertisement> advertisements = null;
        try {
            advertisements = DatabaseManager.getInstance().getAllAdvertisementsFromDb();
        } catch (MySQLNonTransientConnectionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to Database").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(advertisements.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).entity("No advertisements in database").build();
        }
        else{
            int random = (int) (Math.random() * advertisements.size());
            return Response.status(Response.Status.OK).entity(advertisements.get(random)).build();
        }
    }
}
