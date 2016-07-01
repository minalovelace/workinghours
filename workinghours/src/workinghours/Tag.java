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
    @SerializedName("workingDay")
    private boolean m_workingDay = false;
    @SerializedName("illness")
    private boolean m_illness = false;
    @SerializedName("hourReduction")
    private boolean m_hourReduction = false;
    @SerializedName("holiday")
    private boolean m_holiday = false;
    @SerializedName("vacation")
    private boolean m_vacation = false;
    @SerializedName("weekend")
    private boolean m_weekend = false;
    @SerializedName("businessTrip")
    private boolean m_businessTrip = false;
    @SerializedName("staffTraining")
    private boolean m_staffTraining = false;
    @SerializedName("kommentarSet")
    private boolean m_kommentarSet = false;

    Tag(Datum day)
    {
        m_datum = day;
    }

    /**
     * A day consists of different settings. A normal working day consists of
     * the date, starting time, ending time and duration of break. Other days
     * are days of illness, hour-reduction, holiday or weekend.
     */
    Tag(Datum day, boolean illness, boolean hourReduction, boolean holiday, boolean vacation, boolean weekend)
    {
        m_datum = day;
        m_illness = illness;
        m_hourReduction = hourReduction;
        m_holiday = holiday;
        m_vacation = vacation;
        m_weekend = weekend;

    }

    /**
     * A Call to this constructor generates a normal working-day, which means
     * that isWorkingDay() returns <code>true</code>.
     *
     * @param day
     * @param begin
     * @param end
     * @param pause
     */
    Tag(Datum day, Uhrzeit begin, Uhrzeit end, int pause, boolean workingDay, boolean businessTrip,
            boolean staffTraining)
    {
        m_datum = day;
        m_begin = begin;
        m_end = end;
        m_pause = pause;
        m_workingDay = workingDay;
        m_businessTrip = businessTrip;
        m_staffTraining = staffTraining;
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

    boolean isWorkingDay()
    {
        return m_workingDay;
    }

    void setWorkingDay(boolean workingDay)
    {
        m_workingDay = workingDay;
    }

    boolean isIllness()
    {
        return m_illness;
    }

    void setIllness(boolean illness)
    {
        m_illness = illness;
    }

    boolean isHourReduction()
    {
        return m_hourReduction;
    }

    void setHourReduction(boolean hourReduction)
    {
        m_hourReduction = hourReduction;
    }

    boolean isHoliday()
    {
        return m_holiday;
    }

    void setHoliday(boolean holiday)
    {
        m_holiday = holiday;
    }

    boolean isVacation()
    {
        return m_vacation;
    }

    void setVacation(boolean vacation)
    {
        m_vacation = vacation;
    }

    boolean isBusinessTrip()
    {
        return m_businessTrip;
    }

    void setBusinessTrip(boolean businessTrip)
    {
        m_businessTrip = businessTrip;
    }

    boolean isStaffTraining()
    {
        return m_staffTraining;
    }

    void setStaffTraining(boolean staffTraining)
    {
        m_staffTraining = staffTraining;
    }

    boolean isWeekend()
    {
        return m_weekend;
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
        } else if (isHourReduction())
        {
            return -WORKING_DAY_MINUTES;
        } else
        {
            return 0;
        }
    }

    boolean isOtherComment()
    {
        return isKommentarSet() && !(isBusinessTrip() || isStaffTraining() || isHoliday() || isHourReduction()
                || isIllness() || isVacation() || isWeekend());
    }
}
