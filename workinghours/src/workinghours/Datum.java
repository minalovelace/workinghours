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
    private int year;
    private int month;
    private int day;

    Datum(String datum)
    {
        this.year = stringToDatum(datum).getYear();
        this.month = stringToDatum(datum).getMonth();
        this.day = stringToDatum(datum).getDay();
    }

    Datum(GregorianCalendar cal)
    {
        setDatum(cal);
    }

    Datum(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        if (!isDatum())
        {
            System.out.println(
                    "Something went wrong. The given Datum is not a Datum. This error message was produced by the constructor Datum(int year, int month, int day).");
        }
    }

    private void setDatum(GregorianCalendar cal)
    {
        this.year = cal.get(GregorianCalendar.YEAR);
        this.month = cal.get(GregorianCalendar.MONTH) + 1;
        this.day = cal.get(GregorianCalendar.DAY_OF_MONTH);
        if (!isDatum())
        {
            System.out.println(
                    "Something went wrong. The given Datum is not a Datum. This error message was produced by the constructor Datum(Calendar cal).");
        }
    }

    int getYear()
    {
        return this.year;
    }

    int getMonth()
    {
        return this.month;
    }

    int getDay()
    {
        return this.day;
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

        if ((datumAsString.contains(".")) && (datumAsString.split("[\\.]").length == 3))
        {
            String[] splitDatumString = datumAsString.split("[\\.]");
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

        if (result != null && result.isDatum())
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
        return this.year + "-" + this.month + "-" + this.day;
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

        if ((this.year > 1900) && (this.year < 2301))
        {
            if ((this.month > 0) && (this.month < 13))
            {
                if ((this.day > 0) && (this.day < 32))
                {
                    if (thirtyOneDays.contains(new Integer(this.month)))
                    {
                        result = true;
                    } else if ((thirtyDays.contains(new Integer(this.month))) && (this.day < 31))
                    {
                        result = true;
                    } else if ((this.month == 2) && (this.day < 30) && isSchaltjahr())
                    {
                        result = true;
                    } else if ((this.month == 2) && (this.day < 29) && !isSchaltjahr())
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

        if (this.year % 400 == 0)
        {
            result = true;
        } else if (this.year % 100 == 0)
        {
            result = false;
        } else if (this.year % 4 == 0)
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
        GregorianCalendar datumAsCalendar = new GregorianCalendar(this.year, this.month - 1, this.day);
        return datumAsCalendar.get(GregorianCalendar.WEEK_OF_YEAR);
    }

    /**
     *
     * @return Den Wochentag als String
     */
    private String getDayOfWeekAsString()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(this.year, this.month - 1, this.day);

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
        GregorianCalendar datumAsCalendar = new GregorianCalendar(this.year, this.month - 1, this.day);

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
        GregorianCalendar cal = new GregorianCalendar(this.year, this.month - 1, this.day);
        cal.add(GregorianCalendar.DAY_OF_YEAR, i);
        setDatum(cal);
    }

    /**
     * @return Returns the day of the year. The first day of the year is 1.
     */
    int getDayOfYear()
    {
        GregorianCalendar datumAsCalendar = new GregorianCalendar(this.year, this.month - 1, this.day);
        return datumAsCalendar.get(GregorianCalendar.DAY_OF_YEAR);
    }

}
