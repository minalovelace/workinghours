package restserver;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/api")
public class ApiRestCallHandler
{

    @GET
    @Path("heroes")
    @Produces("application/json")
    public Response getHeroes() throws IOException
    {
        return Response.ok(Heroes.getSingleton().toString()).build();
    }

    @GET
    @Path("heroes/{id}")
    @Produces("application/json")
    public Response getHero(@PathParam("id") int id) throws IOException
    {
        String resultJson = Heroes.getSingleton().toString();
        Heroes heroes = new Gson().fromJson(resultJson, Heroes.class);
        for (Hero hero : heroes.getHeroes())
        {
            if (hero.getId() == id)
                return Response.ok(hero.toString()).build();
        }
        return Response.status(404).build();
    }

    @POST
    @Path("heroes")
    @Consumes("application/json")
    public void addHero(String json) throws IOException
    {
        String name = json.split("\"")[3];
        Heroes.getSingleton().addHero(name);
    }

}
