package RestServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/")
public class ResourceHandler
{
    private final String RELATIVE_BASE_PATH = "webapp/";

    @GET
    public Response index() throws IOException
    {
        return Response.seeOther(UriBuilder.fromPath("index.html").build()).build();
    }

    @GET
    @Path("{fileName}")
    public String rootResourceFiles(@PathParam("fileName") String fileName) throws IOException
    {
        return new String(fileLoader(fileName), "UTF-8");
    }

    @GET
    @Produces("text/javascript")
    @Path("node_modules/{subResources:.*}")
    public Response nodeModules(@PathParam("subResources") String subResources) throws IOException
    {
        String responseParam = new String(fileLoader("node_modules/" + subResources), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Produces("text/javascript")
    @Path("typings/{subResources:.*}")
    public Response typings(@PathParam("subResources") String subResources) throws IOException
    {
        String responseParam = new String(fileLoader("typings/" + subResources), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Produces("text/javascript")
    @Path("app/{fileName}")
    public Response appFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("app/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    private byte[] fileLoader(String relPath) throws IOException
    {
        java.nio.file.Path path = Paths.get(RELATIVE_BASE_PATH + relPath);
        return Files.readAllBytes(path);
    }
}