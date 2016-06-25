package restserver;

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
    @Path("index.html")
    @Produces("text/html")
    public String rootResourceIndex() throws IOException
    {
        return new String(fileLoader("index.html"), "UTF-8");
    }

    @GET
    @Path("styles.css")
    @Produces("text/css")
    public String rootResourceStyles() throws IOException
    {
        return new String(fileLoader("styles.css"), "UTF-8");
    }

    @GET
    @Path("favicon.ico")
    @Produces("media/image")
    public String rootResourceFavIcon() throws IOException
    {
        return new String(fileLoader("favicon.ico"), "UTF-8");
    }

    @GET
    @Path("systemjs.config.js")
    @Produces("text/javascript")
    public String rootResourceSystemConfigJs() throws IOException
    {
        return new String(fileLoader("systemjs.config.js"), "UTF-8");
    }

    @GET
    @Path("node_modules/{subResources:.*}")
    @Produces("text/javascript")
    public Response nodeModules(@PathParam("subResources") String subResources) throws IOException
    {
        String responseParam = new String(fileLoader("node_modules/" + subResources), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Path("typings/{subResources:.*}")
    @Produces("text/javascript")
    public Response typings(@PathParam("subResources") String subResources) throws IOException
    {
        String responseParam = new String(fileLoader("typings/" + subResources), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Path("app/{fileName}")
    @Produces("text/javascript")
    public Response appFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("app/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Path("app/toh/{fileName}")
    @Produces("text/javascript")
    public Response appToHFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("app/toh/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    @GET
    @Path("app/wiki/{fileName}")
    @Produces("text/javascript")
    public Response appWikiFiles(@PathParam("fileName") String fileName) throws IOException
    {
        String responseParam = new String(fileLoader("app/wiki/" + fileName), "UTF-8");
        return Response.ok(responseParam).build();
    }

    private byte[] fileLoader(String relPath) throws IOException
    {
        java.nio.file.Path path = Paths.get(RELATIVE_BASE_PATH + relPath);
        return Files.readAllBytes(path);
    }
}