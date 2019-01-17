package om.metamorph.trainingLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by OJP on 16/05/2017.
 */

@Path("test")
public class Test {

    @GET
    @Path("string")
    public String test(){
        return "Web Service works!";
    }

    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String jsonTest(){
        return "{\"message\":\"message Content\"}";
    }
}
