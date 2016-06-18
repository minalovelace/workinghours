package RestServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class ResourceHandler
{
    private final String RELATIVE_BASE_PATH = "webapp/";

    @GET
    public String index() throws IOException
    {
        return new String(fileLoader("index.html"), "UTF-8");
    }

    @GET
    @Path("css/{fileName}")
    public String cssFiles(@PathParam("fileName") String fileName) throws IOException
    {
        return new String(fileLoader("css/" + fileName), "UTF-8");
    }

    @GET
    @Produces("text/javascript")
    @Path("lib/angular/{fileName}")
    public Response angularFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("lib/angular/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Produces("text/javascript")
    @Path("app/{fileName}")
    public Response appFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("lib/angular/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    private byte[] fileLoader(String relPath) throws IOException
    {
        java.nio.file.Path path = Paths.get(RELATIVE_BASE_PATH + relPath);
        return Files.readAllBytes(path);
    }
}