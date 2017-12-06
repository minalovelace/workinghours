package workinghours;

public enum TypeOfDay
{
    // @formatter:off
    BUSINESSTRIP("businessTrip", "\\cellcolor{pink}"),
    HOLIDAY("holiday", "\\cellcolor{blue!30}"),
    HOURREDUCTION("hourReduction", "\\cellcolor{orange!80}"),
    ILLNESS("illness", "\\cellcolor{red!65}"),
    STAFFTRAINING("staffTraining", "\\cellcolor{cyan!20}"),
    VACATION("vacation", "\\cellcolor{green!40!gray}"),
    PARTIALVACATION("partialvacation", "\\cellcolor{green!40!gray}"),
    WEEKEND("weekend", "\\cellcolor{blue!15}"),
    WORKINGDAY("workingDay", "");
    // @formatter:on

    private final String m_enumName;
    private final String m_colorContent;

    private TypeOfDay(String enumName, String colorContent)
    {
        m_enumName = enumName;
        m_colorContent = colorContent;
    }

    public String getColor()
    {
        return m_colorContent;
    }

    @Override
    public String toString()
    {
        return m_enumName;
    }
}