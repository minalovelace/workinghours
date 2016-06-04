package RestServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import workinghours.Kalender;

@Path("message")
public class MessageResource
{
    private final String WEBLIB_ROOT = "webapp";

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String message()
    {
        return loadFromFile(Paths.get(WEBLIB_ROOT, "index.html").toFile());
    }

    private String loadFromFile(File file)
    {
        String result = "";

        if (file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                result = br.lines().map(line -> line.trim()).collect(Collectors.joining(""));
                br.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String kalToJson(Kalender kal)
    {
        Gson gson = new Gson();
        return gson.toJson(kal);
    }
}