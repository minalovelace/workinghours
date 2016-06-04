package workinghours;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Scanner;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class KalenderManager
{
    private FileSystemManager m_fsm = new FileSystemManager();
    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    public static final String ROOT_PATH = "webapp";

    public KalenderManager()
    {
    }

    public String generatePDF()
    {
        String result = getFsm().generatePDF(getFsm().loadNewestKalender());
        getFsm().copyPDFToPath();
        return result;
    }

    public Kalender repairConsistency()
    {
        Kalender kal = getFsm().loadNewestKalender();
        kal.repairConsistency();
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setBusinessTrip(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag tag = kal.getTag(datum);
        tag.setBusinessTrip(true);
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender createCalender(String datumString)
    {
        Integer jahr = Integer.valueOf(datumString);
        return getFsm().createKalender(jahr);
    }

    public Kalender deleteDay(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        kal.removeTag(datum);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setHourReduction(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag oldTag = kal.getTag(datum);
        Tag newTag = new Tag(datum);
        newTag.setKommentar(oldTag.getKommentar());
        newTag.setHourReduction(true);
        kal.putTag(newTag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setIllness(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag oldTag = kal.getTag(datum);
        Tag newTag = new Tag(datum);
        newTag.setKommentar(oldTag.getKommentar());
        newTag.setIllness(true);
        kal.putTag(newTag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setStaffTraining(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag tag = kal.getTag(datum);
        tag.setStaffTraining(true);
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setVacation(String datumString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag oldTag = kal.getTag(datum);
        Tag newTag = new Tag(datum);
        newTag.setKommentar(oldTag.getKommentar());
        newTag.setVacation(true);
        kal.putTag(newTag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setKommentar(String datumString, String kommentarString)
    {
        Datum datum = new Datum(datumString);
        Kalender kal = getFsm().loadNewestKalender();
        Tag tag = kal.getTag(datum);
        tag.setKommentar(kommentarString);
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setNewWorkingDay(String datumString, String uhrzeitStart, String uhrzeitEnd, String pauseString)
    {
        Datum datum = new Datum(datumString);
        Uhrzeit beginn = new Uhrzeit(uhrzeitStart);
        Uhrzeit ende = new Uhrzeit(uhrzeitEnd);
        Integer pause = Integer.parseInt(pauseString);
        Tag tag = new Tag(datum, beginn, ende, pause, true, false, false);
        Kalender kal = getFsm().loadNewestKalender();
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender loadNewestKalender()
    {
        try
        {
            return getFsm().loadNewestKalender();
        } catch (RuntimeException e)
        {
            return createCalender(String.valueOf(LocalDateTime.now().getYear()));
        }
    }

    private FileSystemManager getFsm()
    {
        return m_fsm;
    }

    void startJersey()
    {
        if (!getFsm().getUnlockJerseyServer())
        {
            System.out.println("This feature is not implemented yet.");
            // return;
        }

        try
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
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, false);
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();
            System.out.println("------------------------------");
            System.out.println("Jersey is up and running.");
            System.out.println("Press 'Enter' to stop Jersey.");
            System.out.println("------------------------------");
            Scanner console = new Scanner(System.in);
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
