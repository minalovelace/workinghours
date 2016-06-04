package workinghours;

class CommandLineInterpreterManager
{

    private KalenderManager m_km = new KalenderManager();

    CommandLineInterpreterManager()
    {
    }

    void interpret(String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].equals("-pdf"))
            {
                getKm().generatePDF();
            } else if (args[0].equals("-start"))
            {
                getKm().startJersey();
            } else if (args[0].equals("-man"))
            {
                getManual();
            } else if (args[0].equals("-cc"))
            {
                getKm().repairConsistency();
            } else
            {
                getErrorMessage();
            }
        } else if (args.length == 2)
        {
            if (args[0].equals("-b"))
            {
                getKm().setBusinessTrip(args[1]);
            } else if (args[0].equals("-c"))
            {
                try
                {
                    getKm().createCalender(args[1]);
                } catch (NumberFormatException e)
                {
                    getErrorMessage();
                }
            } else if (args[0].equals("-d"))
            {
                getKm().deleteDay(args[1]);
            } else if (args[0].equals("-hr"))
            {
                getKm().setHourReduction(args[1]);
            } else if (args[0].equals("-i"))
            {
                getKm().setIllness(args[1]);
            } else if (args[0].equals("-s"))
            {
                getKm().setStaffTraining(args[1]);
            } else if (args[0].equals("-v"))
            {
                getKm().setVacation(args[1]);
            }
        } else if (args.length == 3)
        {
            if (args[0].equals("-k"))
            {
                getKm().setKommentar(args[1], args[2]);
            }
        } else if (args.length == 5)
        {
            if (args[0].equals("-n"))
            {
                getKm().setNewWorkingDay(args[1], args[2], args[3], args[4]);
            }
        } else
        {
            getErrorMessage();
        }
    }

    private void getManual()
    {
        System.out.println("Name:");
        System.out.println("      Workinghours");
        System.out.println("");
        System.out.println("Synopsis:");
        System.out.println(
                "   Workinghours [-bcdfhiknprs] [-b String] [-h String] [-hr String] [-i String] [-k String String] [-n String String String String] [-pdf] [-s String] [-v String]");
        System.out.println("");
        System.out.println("Description:");
        System.out.println("");
        System.out.println(
                "The Workinghours utility generates a calendar, saves changes to it and produces a pdf-file with all the information needed for the employee to protocol the amount of worked hours, holidays and other usefull stuff.");
        System.out.println("");
        System.out.println("      The following options are available:");
        System.out.println("");
        System.out.println("-b");
        System.out.println("     Followed by the date of a day, which is a day of a businesstrip.");
        System.out.println("");
        System.out.println("-c");
        System.out.println("     Followed by the year of a new calendar, which will be created.");
        System.out.println("");
        System.out.println("-cc");
        System.out.println("     Try to repair the consistency of the newest calendar.");
        System.out.println("");
        System.out.println("-d");
        System.out.println("     Followed by the date of a day to be deleted.");
        System.out.println("");
        System.out.println("-hr");
        System.out.println("     Followed by the date of a day off due to hour reduction.");
        System.out.println("");
        System.out.println("-i");
        System.out.println("     Followed by the date of a day off due to illness.");
        System.out.println("");
        System.out.println("-k");
        System.out.println("     Followed by a comment (Kommentar) the user can set.");
        System.out.println("");
        System.out.println("-man");
        System.out.println("     Prints out this manual.");
        System.out.println("");
        System.out.println("-n");
        System.out.println("     Followed by a normal working day consisting of");
        System.out.println("     the first part as the date,");
        System.out.println("     the seconod part as the starting time,");
        System.out.println("     the third part as the ending time and");
        System.out.println("     the fourth part as the pause in minutes.");
        System.out.println("");
        System.out.println("-pdf");
        System.out.println("     This command produces the pdf-file.");
        System.out.println("");
        System.out.println("-s");
        System.out.println("     Followed by the date of a day, which is a staff training day.");
        System.out.println("");
        System.out.println("-v");
        System.out.println("     Followed by the date of a vacation.");
        System.out.println("");
        System.out.println("-start");
        System.out.println("     Starts the REST-Server.");
        System.out.println("");
    }

    private void getErrorMessage()
    {
        System.out
                .println("Error: Workinghours can't interpret your input. Try '-man' for the manual of Workinghours.");
    }

    private KalenderManager getKm()
    {
        return m_km;
    }
}
