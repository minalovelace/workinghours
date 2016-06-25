package workinghours;

import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

public class Kalender
{

    /*
     * This class serves as a handler for the calendar.
     */
    private ConcurrentHashMap<Integer, Tag> tage;
    private int year;

    /*
     * Call this method, if a new calendar has to be created and the only
     * information is the year.
     */
    Kalender(int jahr)
    {
        tage = createKalender(jahr);
        year = jahr;
        isConsistent();
    }

    /**
     * This method checks if all the days are in the same given year.
     *
     * @return <code>true</code>, when all days are in the same year.
     *         <code>false</code> otherwise.
     */
    private boolean isConsistent()
    {
        if (tage.isEmpty())
        {
            return true;
        } else
        {
            year = tage.entrySet().iterator().next().getValue().getDatum().getYear();
        }
        for (Tag value : tage.values())
        {
            if (year != value.getDatum().getYear())
            {
                System.out.println("The given day " + value.getDatum().datumToString()
                        + " is not part of the actual year of this calendar. We are in the year " + year);
                System.out.println("The calendar is inconsistent.");
                System.out.println("Try -cc to fix this.");
                return false;
            }
        }
        return true;
    }

    /**
     * This method removes every entry from the calendar, if the entry is not
     * part of the actual year. Use this method with caution!
     */
    void repairConsistency()
    {
        tage.putAll(new FeiertagWeekend(year).getWeekendsHolidays());

        if (!isConsistent())
        {
            for (Tag value : tage.values())
            {
                if (year != value.getDatum().getYear())
                {
                    Tag removedEntry = tage.remove(value.getDatum().getDayOfYear());
                    System.out.println("The following entry has been removed from the calendar: "
                            + removedEntry.getDatum().datumToString());
                }
            }
        }
    }

    private ConcurrentHashMap<Integer, Tag> createKalender(int jahr)
    {
        ConcurrentHashMap<Integer, Tag> result = new ConcurrentHashMap<>();
        result.putAll(new FeiertagWeekend(jahr).getWeekendsHolidays());
        return result;
    }

    void putTag(Tag tag)
    {
        if (isConsistent() && (year == tag.getDatum().getYear()))
        {
            int tagKey = tag.getDatum().getDayOfYear();
            tage.put(tagKey, tag);
        } else
        {
            System.out.println(
                    "The calendar is inconsistent. Please try to fix this, before putting another day into the calendar.");
        }
    }

    Tag getTag(Datum dayOfInterest)
    {
        int dayOfYear = dayOfInterest.getDayOfYear();
        if (tage.containsKey(dayOfYear))
        {
            return tage.get(dayOfYear);
        } else
        {
            GregorianCalendar calResult = new GregorianCalendar();
            calResult.set(GregorianCalendar.YEAR, year);
            calResult.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
            return new Tag(new Datum(calResult));
        }
    }

    /**
     *
     * @param dayOfInterest
     *            The day to be removed.
     * @return The Tag, which has been removed, if it was in the Kalender.
     *         Otherwise <code>null</code>.
     */
    Tag removeTag(Datum dayOfInterest)
    {
        int dayOfYear = dayOfInterest.getDayOfYear();
        return tage.remove(dayOfYear);
    }

    int getYear()
    {
        return year;
    }

    int getSigmaDelta(Tag tag)
    {
        Datum dayOfInterest = new Datum(getYear(), 1, 1);
        int sigmaDelta = getTag(dayOfInterest).getDelta();

        for (int i = 1; i < tag.getDatum().getDayOfYear(); i++)
        {
            dayOfInterest.addToDayOfYear(1);
            sigmaDelta = sigmaDelta + getTag(dayOfInterest).getDelta();
        }
        return sigmaDelta;
    }

    /**
     *
     * @return The month as a {@link ConcurrentHashMap} with the day of the year
     *         as a key and the Tag as a value specified by int i: January is 1,
     *         February is 2 and so on.
     */
    ConcurrentHashMap<Integer, Tag> getMonth(int i)
    {
        ConcurrentHashMap<Integer, Tag> result = new ConcurrentHashMap<>();
        for (Tag tag : tage.values())
        {
            if (tag.getDatum().getMonth() == i)
                result.put(tag.getDatum().getDayOfYear(), tag);
        }
        return result;
    }
}