package workinghours;

import com.google.gson.annotations.SerializedName;

class Tag implements Comparable<Tag>
{
    private final int WORKING_DAY_MINUTES = 480;
    @SerializedName("datum")
    private Datum m_datum;
    @SerializedName("begin")
    private Uhrzeit m_begin = new Uhrzeit(0, 0);
    @SerializedName("end")
    private Uhrzeit m_end = new Uhrzeit(0, 0);
    @SerializedName("pause")
    private int m_pause = 0;
    @SerializedName("kommentar")
    private String m_kommentar = "";
    @SerializedName("kommentarSet")
    private boolean m_kommentarSet = false;
    @SerializedName("typeOfDay")
    private TypeOfDay m_typeOfDay;

    Tag(Datum day)
    {
        m_datum = day;
    }

    /**
     * A day consists of different settings. A normal working day consists of
     * the date, starting time, ending time and duration of break. Other days
     * are days of illness, hour-reduction, holiday or weekend.
     */
    Tag(Datum day, TypeOfDay typeOfDay)
    {
        m_datum = day;
        m_typeOfDay = typeOfDay;
    }

    /**
     * A Call to this constructor generates a normal working-day, which means
     * that isWorkingDay() returns <code>true</code>.
     */
    Tag(Datum day, Uhrzeit begin, Uhrzeit end, int pause, TypeOfDay typeOfDay)
    {
        m_datum = day;
        m_begin = begin;
        m_end = end;
        m_pause = pause;
        m_typeOfDay = typeOfDay;
    }

    Datum getDatum()
    {
        return m_datum;
    }

    void setDatum(Datum day)
    {
        m_datum = day;
    }

    Uhrzeit getBegin()
    {
        return m_begin;
    }

    void setBegin(Uhrzeit begin)
    {
        m_begin = begin;
    }

    Uhrzeit getEnd()
    {
        return m_end;
    }

    void setEnd(Uhrzeit end)
    {
        this.m_end = end;
    }

    int getPause()
    {
        return m_pause;
    }

    void setPause(int pause)
    {
        m_pause = pause;
    }

    String getKommentar()
    {
        return m_kommentar;
    }

    void setKommentar(String kommentar)
    {
        if (!kommentar.isEmpty() && kommentar != null)
        {
            m_kommentar = kommentar;
            m_kommentarSet = true;
        } else
        {
            m_kommentarSet = false;
        }
    }

    boolean isKommentarSet()
    {
        return m_kommentarSet;
    }

    TypeOfDay getTypeOfDay()
    {
        if (m_typeOfDay == null)
            m_typeOfDay = TypeOfDay.WORKINGDAY;
        return m_typeOfDay;
    }

    void setTypeOfDay(TypeOfDay typeOfDay)
    {
        m_typeOfDay = typeOfDay;
    }

    @Override
    public int compareTo(Tag tag)
    {
        int otherTagAsCalendar = tag.getDatum().getDayOfYear();
        int tagAsCalendar = getDatum().getDayOfYear();
        return tagAsCalendar - otherTagAsCalendar;
    }

    int getDelta()
    {
        int minutesFromBeginToEnd = getEnd().getTotalMinutes() - getBegin().getTotalMinutes();
        if (0 < minutesFromBeginToEnd)
        {
            return minutesFromBeginToEnd - getPause() - WORKING_DAY_MINUTES;
        } else if (TypeOfDay.HOURREDUCTION.equals(m_typeOfDay))
        {
            return -WORKING_DAY_MINUTES;
        } else
        {
            return 0;
        }
    }

    boolean isOtherComment()
    {
        return isKommentarSet() && TypeOfDay.WORKINGDAY.equals(m_typeOfDay);
    }
}
