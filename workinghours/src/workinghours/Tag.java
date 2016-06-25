package workinghours;

class Tag implements Comparable<Tag>
{

    private final int WORKING_DAY_MINUTES = 480;
    private Datum datum;
    private Uhrzeit begin = new Uhrzeit(0, 0);
    private Uhrzeit end = new Uhrzeit(0, 0);
    private int pause = 0;
    private String kommentar = "";
    private boolean workingDay = false;
    private boolean illness = false;
    private boolean hourReduction = false;
    private boolean holiday = false;
    private boolean vacation = false;
    private boolean weekend = false;
    private boolean businessTrip = false;
    private boolean staffTraining = false;
    private boolean kommentarSet = false;

    Tag(Datum day)
    {
        datum = day;
    }

    /**
     * A day consists of different settings. A normal working day consists of
     * the date, starting time, ending time and duration of break. Other days
     * are days of illness, hour-reduction, holiday or weekend.
     */
    Tag(Datum day, boolean illness, boolean hourReduction, boolean holiday, boolean vacation, boolean weekend)
    {
        datum = day;
        this.illness = illness;
        this.hourReduction = hourReduction;
        this.holiday = holiday;
        this.vacation = vacation;
        this.weekend = weekend;

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
        this.datum = day;
        this.begin = begin;
        this.end = end;
        this.pause = pause;
        this.workingDay = workingDay;
        this.businessTrip = businessTrip;
        this.staffTraining = staffTraining;
    }

    Datum getDatum()
    {
        return this.datum;
    }

    void setDatum(Datum day)
    {
        this.datum = day;
    }

    Uhrzeit getBegin()
    {
        return this.begin;
    }

    void setBegin(Uhrzeit begin)
    {
        this.begin = begin;
    }

    Uhrzeit getEnd()
    {
        return this.end;
    }

    void setEnd(Uhrzeit end)
    {
        this.end = end;
    }

    int getPause()
    {
        return this.pause;
    }

    void setPause(int pause)
    {
        this.pause = pause;
    }

    String getKommentar()
    {
        return this.kommentar;
    }

    void setKommentar(String kommentar)
    {
        if (!kommentar.isEmpty() && kommentar != null)
        {
            this.kommentar = kommentar;
            this.kommentarSet = true;
        } else
        {
            this.kommentarSet = false;
        }
    }

    boolean isKommentarSet()
    {
        return this.kommentarSet;
    }

    boolean isWorkingDay()
    {
        return this.workingDay;
    }

    void setWorkingDay(boolean workingDay)
    {
        this.workingDay = workingDay;
    }

    boolean isIllness()
    {
        return this.illness;
    }

    void setIllness(boolean illness)
    {
        this.illness = illness;
    }

    boolean isHourReduction()
    {
        return this.hourReduction;
    }

    void setHourReduction(boolean hourReduction)
    {
        this.hourReduction = hourReduction;
    }

    boolean isHoliday()
    {
        return this.holiday;
    }

    void setHoliday(boolean holiday)
    {
        this.holiday = holiday;
    }

    boolean isVacation()
    {
        return this.vacation;
    }

    void setVacation(boolean vacation)
    {
        this.vacation = vacation;
    }

    boolean isBusinessTrip()
    {
        return this.businessTrip;
    }

    void setBusinessTrip(boolean businessTrip)
    {
        this.businessTrip = businessTrip;
    }

    boolean isStaffTraining()
    {
        return this.staffTraining;
    }

    void setStaffTraining(boolean staffTraining)
    {
        this.staffTraining = staffTraining;
    }

    boolean isWeekend()
    {
        return this.weekend;
    }

    @Override
    public int compareTo(Tag tag)
    {
        int otherTagAsCalendar = tag.getDatum().getDayOfYear();
        int tagAsCalendar = this.getDatum().getDayOfYear();
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
