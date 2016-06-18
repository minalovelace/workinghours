package RestServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class ResourceHandler
{
    private final String RELATIVE_BASE_PATH = "webapp/";

    @GET
    public String index() throws IOException
    {
        return new String(fileLoader(Paths.get(RELATIVE_BASE_PATH + "index.html")), "UTF-8");
    }

    private byte[] fileLoader(java.nio.file.Path path) throws IOException
    {
        return Files.readAllBytes(path);
    }
}