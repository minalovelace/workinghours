package restserver;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyStarterService
{
    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    public static void startJersey()
    {
        try (Scanner console = new Scanner(System.in))
        {
            // Use these lines of code to omit the output of Jersey. One can
            // also stream this output to a logfile.
            // PrintStream err = new PrintStream(new OutputStream()
            // {
            //
            // @Override
            // public void write(int b) throws IOException
            // {
            // }
            // });
            // PrintStream oldSysErr = System.err;
            // System.setErr(err);
            ResourceConfig configuration = new ResourceConfig();
            configuration.packages(true, "restserver");
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, configuration, false);
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();
            System.out.println("");
            System.out.println("");
            System.out.println("This feature is experimental and still in development.");
            System.out.println("Be careful when using it.");
            System.out.println("");
            System.out.println("------------------------------------");
            System.out.println(" The REST-Server is up and running.");
            System.out.println("   Press 'Enter' to stop it.");
            System.out.println("------------------------------------");
            console.hasNextLine();
            server.shutdownNow();
            // Thread.currentThread().join();
            // System.setErr(oldSysErr);
        } catch (IllegalArgumentException | IOException e)
        {
            e.printStackTrace();
        }
    }

}
