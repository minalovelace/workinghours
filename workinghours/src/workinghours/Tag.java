package workinghours;

class Tag implements Comparable<Tag>
{

    private final int WORKING_DAY_MINUTES = 480;
    private Datum m_datum;
    private Uhrzeit m_begin = new Uhrzeit(0, 0);
    private Uhrzeit m_end = new Uhrzeit(0, 0);
    private int m_pause = 0;
    private String m_kommentar = "";
    private boolean m_workingDay = false;
    private boolean m_illness = false;
    private boolean m_hourReduction = false;
    private boolean m_holiday = false;
    private boolean m_vacation = false;
    private boolean m_weekend = false;
    private boolean m_businessTrip = false;
    private boolean m_staffTraining = false;
    private boolean m_kommentarSet = false;

    public Tag(Datum day)
    {
        m_datum = day;
    }

    /**
     * A day consists of different settings. A normal working day consists of
     * the date, starting time, ending time and duration of break. Other days
     * are days of illness, hour-reduction, holiday or weekend.
     */
    public Tag(Datum day, boolean illness, boolean hourReduction, boolean holiday, boolean vacation, boolean weekend)
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
    public Tag(Datum day, Uhrzeit begin, Uhrzeit end, int pause, boolean workingDay, boolean businessTrip,
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

    public Datum getDatum()
    {
        return m_datum;
    }

    public void setDatum(Datum day)
    {
        m_datum = day;
    }

    public Uhrzeit getBegin()
    {
        return m_begin;
    }

    public void setBegin(Uhrzeit begin)
    {
        m_begin = begin;
    }

    public Uhrzeit getEnd()
    {
        return m_end;
    }

    public void setEnd(Uhrzeit end)
    {
        m_end = end;
    }

    public int getPause()
    {
        return m_pause;
    }

    public void setPause(int pause)
    {
        m_pause = pause;
    }

    public String getKommentar()
    {
        return m_kommentar;
    }

    public void setKommentar(String kommentar)
    {
    	if(!kommentar.isEmpty() && kommentar != null)
    	{
    		m_kommentar = kommentar;
    		m_kommentarSet = true;
    	}
    	else
    	{
    		m_kommentarSet = false;
    	}
    }

    public boolean isKommentarSet()
    {
        return m_kommentarSet;
    }

    public boolean isWorkingDay()
    {
        return m_workingDay;
    }

    public void setWorkingDay(boolean workingDay)
    {
        m_workingDay = workingDay;
    }

    public boolean isIllness()
    {
        return m_illness;
    }

    public void setIllness(boolean illness)
    {
        m_illness = illness;
    }

    public boolean isHourReduction()
    {
        return m_hourReduction;
    }

    public void setHourReduction(boolean hourReduction)
    {
        m_hourReduction = hourReduction;
    }

    public boolean isHoliday()
    {
        return m_holiday;
    }

    public void setHoliday(boolean holiday)
    {
        m_holiday = holiday;
    }

    public boolean isVacation()
    {
        return m_vacation;
    }

    public void setVacation(boolean vacation)
    {
        this.m_vacation = vacation;
    }

    public boolean isBusinessTrip()
    {
        return m_businessTrip;
    }

    public void setBusinessTrip(boolean businessTrip)
    {
        this.m_businessTrip = businessTrip;
    }

    public boolean isStaffTraining()
    {
        return m_staffTraining;
    }

    public void setStaffTraining(boolean staffTraining)
    {
        this.m_staffTraining = staffTraining;
    }

    public boolean isWeekend()
    {
        return m_weekend;
    }

    @Override
    public int compareTo(Tag tag)
    {
        int otherTagAsCalendar = tag.getDatum().getDayOfYear();
        int tagAsCalendar = this.getDatum().getDayOfYear();
        return tagAsCalendar - otherTagAsCalendar;
    }

    public int getDelta()
    {
        int minutesFromBeginToEnd = getEnd().getTotalMinutes() - getBegin().getTotalMinutes();
        if (0 < minutesFromBeginToEnd)
        {
            return minutesFromBeginToEnd - getPause() - WORKING_DAY_MINUTES;
        } else if(isHourReduction())
        {
        	return -WORKING_DAY_MINUTES;
        } else
        {
            return 0;
        }
    }

    public boolean isOtherComment()
    {
        return isKommentarSet() && !(isBusinessTrip() || isStaffTraining() || isHoliday() || isHourReduction()
                || isIllness() || isVacation() || isWeekend());
    }
}
