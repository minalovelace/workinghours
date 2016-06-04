package workinghours;

import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

public class Kalender
{

    /*
     * This class serves as a handler for the calendar.
     */
    private ConcurrentHashMap<Integer, Tag> m_tage;
    private int m_year;

    /*
     * Call this method, if a new calendar has to be created and the only
     * information is the year.
     */
    Kalender(int jahr)
    {
        m_tage = createKalender(jahr);
        m_year = jahr;
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
        if (m_tage.isEmpty())
        {
            return true;
        } else
        {
            m_year = m_tage.entrySet().iterator().next().getValue().getDatum().getYear();
        }
        for (Tag value : m_tage.values())
        {
            if (m_year != value.getDatum().getYear())
            {
                System.out.println("The given day " + value.getDatum().datumToString()
                        + " is not part of the actual year of this calendar. We are in the year " + m_year);
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
        m_tage.putAll(new FeiertagWeekend(m_year).getWeekendsHolidays());

        if (!isConsistent())
        {
            for (Tag value : m_tage.values())
            {
                if (m_year != value.getDatum().getYear())
                {
                    Tag removedEntry = m_tage.remove(value.getDatum().getDayOfYear());
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
        if (isConsistent() && (m_year == tag.getDatum().getYear()))
        {
            int tagKey = tag.getDatum().getDayOfYear();
            m_tage.put(tagKey, tag);
        } else
        {
            System.out.println(
                    "The calendar is inconsistent. Please try to fix this, before putting another day into the calendar.");
        }
    }

    Tag getTag(Datum dayOfInterest)
    {
        int dayOfYear = dayOfInterest.getDayOfYear();
        if (m_tage.containsKey(dayOfYear))
        {
            return m_tage.get(dayOfYear);
        } else
        {
            GregorianCalendar calResult = new GregorianCalendar();
            calResult.set(GregorianCalendar.YEAR, m_year);
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
        return m_tage.remove(dayOfYear);
    }

    int getYear()
    {
        return m_year;
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
        for (Tag tag : m_tage.values())
        {
            if (tag.getDatum().getMonth() == i)
                result.put(tag.getDatum().getDayOfYear(), tag);
        }
        return result;
    }
}