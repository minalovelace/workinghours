package workinghours;

class Uhrzeit
{

    /*
     * The time of the day. It will be saved as hours, minutes and total
     * minutes. Total minutes is the time in minutes from midnight to the given
     * time of the day.
     */
    private final int m_hours;
    private final int m_minutes;

    public Uhrzeit(int hours, int minutes)
    {
        m_hours = hours;
        m_minutes = minutes;
    }

    public Uhrzeit(String uhrzeit)
    {
        m_hours = stringToUhrzeit(uhrzeit).getHours();
        m_minutes = stringToUhrzeit(uhrzeit).getMinutes();
    }

    private int getHours()
    {
        return m_hours;
    }

    private int getMinutes()
    {
        return m_minutes;
    }

    int getTotalMinutes()
    {
        int totalMinutes = m_hours * 60 + m_minutes;
        return totalMinutes;
    }

    private Uhrzeit stringToUhrzeit(String uhrzeitAsString)
    {
        Uhrzeit result = null;
        if ((uhrzeitAsString.contains(":")) && (uhrzeitAsString.split(":").length == 2))
        {
            Integer stunden = Integer.parseInt(uhrzeitAsString.split(":")[0]);
            Integer minuten = Integer.parseInt(uhrzeitAsString.split(":")[1]);

            if ((stunden > 0) && (stunden < 24) && (minuten > -1) && (minuten < 60))
            {
                result = new Uhrzeit(stunden, minuten);
            } else
            {
                System.out.println(
                        "stringToUhrzeit had to parse a String, which was not an Uhrzeit. Returned null. Input was: "
                                + uhrzeitAsString);
            }
        }
        return result;
    }

    /**
     *
     * @return A String-representation of the time. The format is "hh:mm Uhr".
     */
    @Override
    public String toString()
    {
        if (10 > getMinutes())
            return Integer.toString(m_hours) + ":0" + Integer.toString(m_minutes) + " Uhr";
        else
            return Integer.toString(m_hours) + ":" + Integer.toString(m_minutes) + " Uhr";
    }

}
