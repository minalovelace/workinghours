package workinghours;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

class FeiertagWeekend
{
    /*
     * Provides the user of this class with informations about weekends and
     * holidays for a given year. It can also return a HashMap<Integer, Tag>
     * with all holidays and weekends of the given year.
     */
    private int m_jahr;
    private HashMap<Integer, Tag> m_tage;

    FeiertagWeekend(int jahr)
    {
        m_jahr = jahr;
        m_tage = createWeekendsHolidays();
    }

    HashMap<Integer, Tag> getWeekendsHolidays()
    {
        return m_tage;
    }

    private HashMap<Integer, Tag> createWeekendsHolidays()
    {
        HashMap<Integer, Tag> tage = new HashMap<Integer, Tag>();

        /* Create and add all fixed holidays: */
        /* Neujahr */
        Datum neujahr = new Datum(m_jahr, 1, 1);
        Tag neujahrTag = new Tag(neujahr, TypeOfDay.HOLIDAY);
        neujahrTag.setKommentar("Neujahr");
        tage.put(neujahr.getDayOfYear(), neujahrTag);
        /* Heilige Drei Könige */
        Datum hdk = new Datum(m_jahr, 1, 6);
        Tag hdkTag = new Tag(hdk, TypeOfDay.HOLIDAY);
        tage.put(hdk.getDayOfYear(), hdkTag);
        hdkTag.setKommentar("Heilige Drei Könige");
        /* Tag der Arbeit */
        Datum tda = new Datum(m_jahr, 5, 1);
        Tag tdaTag = new Tag(tda, TypeOfDay.HOLIDAY);
        tdaTag.setKommentar("Tag der Arbeit");
        tage.put(tda.getDayOfYear(), tdaTag);
        /* Tag der deutschen Einheit */
        Datum tdde = new Datum(m_jahr, 10, 3);
        Tag tddeTag = new Tag(tdde, TypeOfDay.HOLIDAY);
        tddeTag.setKommentar("Tag der deutschen Einheit");
        tage.put(tdde.getDayOfYear(), tddeTag);
        /* Allerheiligen */
        Datum allerheiligen = new Datum(m_jahr, 11, 1);
        Tag allerheiligenTag = new Tag(allerheiligen, TypeOfDay.HOLIDAY);
        allerheiligenTag.setKommentar("Allerheiligen");
        tage.put(allerheiligen.getDayOfYear(), allerheiligenTag);
        /* 1. Weihnachtstag */
        Datum ersterWT = new Datum(m_jahr, 12, 25);
        Tag ersterWTTag = new Tag(ersterWT, TypeOfDay.HOLIDAY);
        ersterWTTag.setKommentar("1. Weihnachtstag");
        tage.put(ersterWT.getDayOfYear(), ersterWTTag);
        /* 2. Weihnachtstag */
        Datum zweiterWT = new Datum(m_jahr, 12, 26);
        Tag zweiterWTTag = new Tag(zweiterWT, TypeOfDay.HOLIDAY);
        zweiterWTTag.setKommentar("2. Weihnachtstag");
        tage.put(zweiterWT.getDayOfYear(), zweiterWTTag);

        /* Create and add all flexible holidays: */
        /* Ostersonntag */
        GregorianCalendar ostersonntag = easterSunday(m_jahr);
        Tag ostersonntagTag = new Tag(new Datum(ostersonntag), TypeOfDay.HOLIDAY);
        ostersonntagTag.setKommentar("Ostersonntag");
        tage.put(ostersonntag.get(Calendar.DAY_OF_YEAR), ostersonntagTag);
        /* Ostermontag */
        GregorianCalendar ostermontag = ostersonntag;
        ostermontag.add(Calendar.DAY_OF_YEAR, 1);
        Tag ostermontagTag = new Tag(new Datum(ostermontag), TypeOfDay.HOLIDAY);
        ostermontagTag.setKommentar("Ostermontag");
        tage.put(ostermontag.get(Calendar.DAY_OF_YEAR), ostermontagTag);
        /* Karfreitag */
        GregorianCalendar karfreitag = ostersonntag;
        karfreitag.add(Calendar.DAY_OF_YEAR, -3);
        Tag karfreitagTag = new Tag(new Datum(karfreitag), TypeOfDay.HOLIDAY);
        karfreitagTag.setKommentar("Karfreitag");
        tage.put(karfreitag.get(Calendar.DAY_OF_YEAR), karfreitagTag);
        /* Christi Himmelfahrt */
        GregorianCalendar christihimmel = ostersonntag;
        christihimmel.add(Calendar.DAY_OF_YEAR, 41);
        Tag christihimmelTag = new Tag(new Datum(christihimmel), TypeOfDay.HOLIDAY);
        christihimmelTag.setKommentar("Christi Himmelfahrt");
        tage.put(christihimmel.get(Calendar.DAY_OF_YEAR), christihimmelTag);
        /* Pfingstsonntag */
        GregorianCalendar pfingstsonntag = ostersonntag;
        pfingstsonntag.add(Calendar.DAY_OF_YEAR, 10);
        Tag pfingstsonntagTag = new Tag(new Datum(pfingstsonntag), TypeOfDay.HOLIDAY);
        pfingstsonntagTag.setKommentar("Pfingstsonntag");
        tage.put(pfingstsonntag.get(Calendar.DAY_OF_YEAR), pfingstsonntagTag);
        /* Pfingstmontag */
        GregorianCalendar pfingstmontag = ostersonntag;
        pfingstmontag.add(Calendar.DAY_OF_YEAR, 1);
        Tag pfingstmontagTag = new Tag(new Datum(pfingstmontag), TypeOfDay.HOLIDAY);
        pfingstmontagTag.setKommentar("Pfingstmontag");
        tage.put(pfingstmontag.get(Calendar.DAY_OF_YEAR), pfingstmontagTag);
        /* Fronleichnam */
        GregorianCalendar fronleichnam = ostersonntag;
        fronleichnam.add(Calendar.DAY_OF_YEAR, 10);
        Tag fronleichnamTag = new Tag(new Datum(fronleichnam), TypeOfDay.HOLIDAY);
        fronleichnamTag.setKommentar("Fronleichnam");
        tage.put(fronleichnam.get(Calendar.DAY_OF_YEAR), fronleichnamTag);

        /* Find all weekends */
        GregorianCalendar cal = new GregorianCalendar(m_jahr, 0, 1);
        /*
         * The while loop ensures that we are only checking dates in the
         * specified year.
         */
        while (cal.get(GregorianCalendar.YEAR) == m_jahr)
        {
            /*
             * The switch checks the day of the week for Saturdays and Sundays
             */
            switch (cal.get(GregorianCalendar.DAY_OF_WEEK))
            {
            case GregorianCalendar.SATURDAY:
            case GregorianCalendar.SUNDAY:
                Tag wochenende = new Tag(new Datum(cal), TypeOfDay.WEEKEND);
                tage.putIfAbsent(cal.get(GregorianCalendar.DAY_OF_YEAR), wochenende);
                break;
            }
            cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }
        
        /* Calculate special holidays of the specified year. */
        specialHolidays(tage);
        
        return tage;
    }

    private void specialHolidays(HashMap<Integer, Tag> tage)
    {
        if(m_jahr == 2017)
        {
            GregorianCalendar luther = new GregorianCalendar(m_jahr, 9, 31);
            Tag lutherTag = new Tag(new Datum(luther), TypeOfDay.HOLIDAY);
            lutherTag.setKommentar("500. Reformationstag");
            tage.put(luther.get(Calendar.DAY_OF_YEAR), lutherTag);
        }
    }

    /**
     *
     * Returns the date of Easter Sunday for a given year. This algorithm has
     * been invented by C.F. Gauß.
     *
     * @param year
     *            > 1583
     * @return The date of Easter Sunday for a given year.
     */
    private GregorianCalendar easterSunday(int year)
    {
        int i = year % 19;
        int j = year / 100;
        int k = year % 100;

        int l = (19 * i + j - (j / 4) - ((j - ((j + 8) / 25) + 1) / 3) + 15) % 30;
        int m = (32 + 2 * (j % 4) + 2 * (k / 4) - l - (k % 4)) % 7;
        int n = l + m - 7 * ((i + 11 * l + 22 * m) / 451) + 114;

        int month = n / 31;
        int day = (n % 31) + 1;

        return new GregorianCalendar(year, month - 1, day);
    }
}
