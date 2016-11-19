package workinghours;

import java.time.LocalDateTime;

public class KalenderManager
{
    private FileSystemManager m_fsm = new FileSystemManager();

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

    public Kalender setSigmaDeltaLastYear(String sigmaDeltaLastYear)
    {
        Kalender kal = getFsm().loadNewestKalender();
        kal.setSigmaDeltaLastYear(Integer.parseInt(sigmaDeltaLastYear));
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

    public Kalender setClockIn()
    {
        LocalDateTime now = LocalDateTime.now();
        Datum datum = new Datum(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Uhrzeit beginn = new Uhrzeit(now.getHour(), now.getMinute());
        Uhrzeit ende = new Uhrzeit(now.getHour(), now.getMinute());
        Integer pause = getFsm().getStandardTimeForPause();
        Tag tag = new Tag(datum, beginn, ende, pause, true, false, false);
        Kalender kal = getFsm().loadNewestKalender();
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public Kalender setClockOut()
    {
        LocalDateTime now = LocalDateTime.now();
        Datum datum = new Datum(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Kalender kal = getFsm().loadNewestKalender();
        Tag tag = kal.getTag(datum);
        tag.setEnd(new Uhrzeit(now.getHour(), now.getMinute()));
        tag.setPause(getFsm().getStandardTimeForPause());
        kal.putTag(tag);
        getFsm().saveKalender(kal);
        return kal;
    }

    public void produceArchive()
    {
        getFsm().produceArchive();
    }
}
