package workinghours;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

class Datum
{

    /*
     * A Datum consists of three numbers. Those are the year, the month and the
     * day.
     */
    private int m_year;
    private int m_month;
    private int m_day;

    Datum(String datum)
    {
        m_year = stringToDatum(datum).getYear();
        m_month = stringToDatum(datum).getMonth();
        m_day = stringToDatum(datum).getDay();
    }

    Datum(GregorianCalendar cal)
    {
        setDatum(cal);
    }

    Datum(int year, int month, int day)
    {
        m_year = year;
        m_month = month;
        m_day = day;
        if (!isDatum())
        {
            System.out.println(
                    "Something went wrong. The given Datum is not a Datum. This error message was produced by the constructor Datum(int year, int month, int day).");
        }
    }

    private void setDatum(GregorianCalendar cal)
    {
        m_year = cal.get(GregorianCalendar.YEAR);
        m_month = cal.get(GregorianCalendar.MONTH) + 1;
        m_day = cal.get(GregorianCalendar.DAY_OF_MONTH);
        if (!isDatum())
        {
            System.out.println(
                    "Something went wrong. The given Datum is not a Datum. This error message was produced by the constructor Datum(Calendar cal).");
        }
    }

    int getYear()
    {
        return m_year;
    }

    int getMonth()
    {
        return m_month;
    }

    int getDay()
    {
        return m_day;
    }

    Datum stringToDatum(String datumAsString)
    {
        Datum result = null;
        if ((datumAsString.contains("-")) && (datumAsString.split("-").length == 3))
        {
            String[] splitDatumString = datumAsString.split("-");
            result = new Datum(Integer.parseInt(splitDatumString[0]), Integer.parseInt(splitDatumString[1]),
                    Integer.parseInt(splitDatumString[2]));
        }

        if ((datumAsString.contains(".")) && (datumAsString.split(".").length == 3))
        {
            String[] splitDatumString = datumAsString.split(".");
            int jahr = Integer.parseInt(splitDatumString[2]);
            /*
             * We add 2000 to the year, because with think that the user wrote
             * e.g. 03.05.08 and meant 03.05.2008.
             */
            if (jahr < 100)
            {
                jahr += 2000;
            }
            result = new Datum(jahr, Integer.parseInt(splitDatumString[1]), Integer.parseInt(splitDatumString[0]));
        }

        if (result.isDatum())
        {
            return result;
        } else
        {
            System.out.println("stringToDatum had to parse a String, which was not a Datum. Returned null. Input was: "
                    + datumAsString);
            return null;
        }
    }

    /*
     * Returns a String representation of the Datum in the format yyyy-mm-dd.
     */
    String datumToString()
    {
        return m_year + "-" + m_month + "-" + m_day;
    }

    /*
     * Returns true, if the given Datum is a real Datum between 1900 and 2300.
     * Otherwise false.
     */
    boolean isDatum()
    {
        boolean result = false;
        /* Monate mit 30 Tagen */
        Set<Integer> thirtyDays = new HashSet<Integer>();
        thirtyDays.add(4);
        thirtyDays.add(6);
        thirtyDays.add(9);
        thirtyDays.add(11);
        /* Monate mit 31 Tagen */
        Set<Integer> thirtyOneDays = new HashSet<Integer>();
        thirtyOneDays.add(1);
        thirtyOneDays.add(3);
        thirtyOneDays.add(5);
        thirtyOneDays.add(7);
        thirtyOneDays.add(8);
        thirtyOneDays.add(10);
        thirtyOneDays.add(12);

        if ((m_year > 1900) && (m_year < 2301))
        {
            if ((m_month > 0) && (m_month < 13))
            {
                if ((m_day > 0) && (m_day < 32))
                {
                    if (thirtyOneDays.contains(new Integer(m_month)))
                    {
                        result = true;
                    } else if ((thirtyDays.contains(new Integer(m_month))) && (m_day < 31))
                    {
                        result = true;
                    } else if ((m_month == 2) && (m_day < 30) && isSchaltjahr())
                    {
                        result = true;
                    } else if ((m_month == 2) && (m_day < 29) && !isSchaltjahr())
                    {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    private boolean isSchaltjahr()
    {
        boolean result = false;

        if (m_year % 400 == 0)
        {
            result = true;
        } else if (m_year % 100 == 0)
        {
            result = false;
        } else if (m_year % 4 == 0)
        {
            result = true;
        }

        return result;
    }

    /**
     *
     * @return Die Kalenderwoche
     */
    int getWeekOfYear()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(m_year, m_month - 1, m_day);
        return datumAsCalendar.get(GregorianCalendar.WEEK_OF_YEAR);
    }

    /**
     *
     * @return Den Wochentag als String
     */
    private String getDayOfWeekAsString()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(m_year, m_month - 1, m_day);

        switch (datumAsCalendar.get(GregorianCalendar.DAY_OF_WEEK))
        {
        case GregorianCalendar.MONDAY:
            return "Montag";
        case GregorianCalendar.TUESDAY:
            return "Dienstag";
        case GregorianCalendar.WEDNESDAY:
            return "Mittwoch";
        case GregorianCalendar.THURSDAY:
            return "Donnerstag";
        case GregorianCalendar.FRIDAY:
            return "Freitag";
        case GregorianCalendar.SATURDAY:
            return "Samstag";
        case GregorianCalendar.SUNDAY:
            return "Sonntag";
        default:
            return null;
        }
    }

    /**
     *
     * @return Den abgekürzten Wochentag als String
     */
    String getDayOfWeekAsShortString()
    {
        String weekday = getDayOfWeekAsString();
        return weekday.substring(0, 2) + ".";
    }

    /**
     *
     * @return Den Wochentag als int
     */
    int getDayOfWeekAsInt()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(m_year, m_month - 1, m_day);

        switch (datumAsCalendar.get(GregorianCalendar.DAY_OF_WEEK))
        {
        case GregorianCalendar.MONDAY:
            return 1;
        case GregorianCalendar.TUESDAY:
            return 2;
        case GregorianCalendar.WEDNESDAY:
            return 3;
        case GregorianCalendar.THURSDAY:
            return 4;
        case GregorianCalendar.FRIDAY:
            return 5;
        case GregorianCalendar.SATURDAY:
            return 6;
        case GregorianCalendar.SUNDAY:
            return 7;
        default:
            return -1;
        }
    }

    void addToDayOfYear(int i)
    {
        GregorianCalendar cal = new GregorianCalendar(m_year, m_month - 1, m_day);
        cal.add(GregorianCalendar.DAY_OF_YEAR, i);
        setDatum(cal);
    }

    /**
     * @return Returns the day of the year. The first day of the year is 1.
     */
    int getDayOfYear()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(m_year, m_month - 1, m_day);
        return datumAsCalendar.get(GregorianCalendar.DAY_OF_YEAR);
    }

}
