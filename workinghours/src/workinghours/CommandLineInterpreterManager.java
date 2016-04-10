package workinghours;

class CommandLineInterpreterManager
{

    private FileSystemManager m_fsm = new FileSystemManager();

    CommandLineInterpreterManager()
    {
    }

    void interpret(String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].equals("-pdf"))
            {
                getFsm().generatePDF(getFsm().loadNewestKalender());
                getFsm().copyPDFToPath();
            } else if (args[0].equals("-gui"))
            {
                // TODO launch gui
//                final Display display = new Display();
//                final Shell shell = new Shell(display);
//                shell.setText("Arbeitszeit");
//                // shell.setSize(600, 600);
//                shell.setLayout(new FillLayout());
//                @SuppressWarnings("unused")
//                final GUIManager guim = new GUIManager(shell, SWT.EMBEDDED);
//                shell.pack();
//                shell.open();
//                while (!shell.isDisposed())
//                {
//                    if (!display.readAndDispatch())
//                    {
//                        display.sleep();
//                    }
//                }
//                display.dispose();
            } else if (args[0].equals("-man"))
            {
                getManual();
            } else if (args[0].equals("-cc"))
            {
                Kalender kal = getFsm().loadNewestKalender();
                kal.repairConsistency();
                getFsm().saveKalender(kal);
            } else
            {
                getErrorMessage();
            }
        } else if (args.length == 2)

        {
            if (args[0].equals("-b"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag tag = kal.getTag(datum);
                tag.setBusinessTrip(true);
                kal.putTag(tag);
                getFsm().saveKalender(kal);
            } else if (args[0].equals("-c"))
            {
                try
                {
                    Integer jahr = Integer.valueOf(args[1]);
                    getFsm().createKalender(jahr);
                } catch (NumberFormatException e)
                {
                    getErrorMessage();
                }
            } else if (args[0].equals("-d"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                kal.removeTag(datum);
                getFsm().saveKalender(kal);
            } else if (args[0].equals("-hr"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag oldTag = kal.getTag(datum);
                Tag newTag = new Tag(datum);
                newTag.setKommentar(oldTag.getKommentar());
                newTag.setHourReduction(true);
                kal.putTag(newTag);
                getFsm().saveKalender(kal);
            } else if (args[0].equals("-i"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag oldTag = kal.getTag(datum);
                Tag newTag = new Tag(datum);
                newTag.setKommentar(oldTag.getKommentar());
                newTag.setIllness(true);
                kal.putTag(newTag);
                getFsm().saveKalender(kal);
            } else if (args[0].equals("-s"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag tag = kal.getTag(datum);
                tag.setStaffTraining(true);
                kal.putTag(tag);
                getFsm().saveKalender(kal);
            } else if (args[0].equals("-v"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag oldTag = kal.getTag(datum);
                Tag newTag = new Tag(datum);
                newTag.setKommentar(oldTag.getKommentar());
                newTag.setVacation(true);
                kal.putTag(newTag);
                getFsm().saveKalender(kal);
            }
        } else if (args.length == 3)
        {
            if (args[0].equals("-k"))
            {
                Datum datum = new Datum(args[1]);
                Kalender kal = getFsm().loadNewestKalender();
                Tag tag = kal.getTag(datum);
                tag.setKommentar(args[2]);
                kal.putTag(tag);
                getFsm().saveKalender(kal);
            }
        } else if (args.length == 5)
        {
            if (args[0].equals("-n"))
            {
                Datum datum = new Datum(args[1]);
                Uhrzeit beginn = new Uhrzeit(args[2]);
                Uhrzeit ende = new Uhrzeit(args[3]);
                Integer pause = Integer.parseInt(args[4]);
                Tag tag = new Tag(datum, beginn, ende, pause, true, false, false);
                Kalender kal = getFsm().loadNewestKalender();
                kal.putTag(tag);
                getFsm().saveKalender(kal);
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
    }

    private void getErrorMessage()
    {
        System.out
                .println("Error: Workinghours can't interpret your input. Try '-man' for the manual of Workinghours.");
    }

    private FileSystemManager getFsm()
    {
        return m_fsm;
    }
}
