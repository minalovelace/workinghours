package workinghours;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;

public class FileSystemManager
{
    private static final int WAIT_FOR_PDFLATEX_SEC = 60;
    private static final String INIFILE = "Workinghours.ini";
    private static final String WH_CAL_DATA_FOLDER = "WHCalData";
    private static final String WH_CAL_DATA_ENDING = ".txt";
    private static final String REGEX_FOR_FILENAME = "^\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[_]\\d\\d[_]\\d\\d[.][t][x][t]$";
    private static final Path PFAD = Paths.get(WH_CAL_DATA_FOLDER);
    private static final String COLOR_RAND = "\\cellcolor{gray!40}";
    private static final String COLOR_ECKE = "\\cellcolor{gray!60}";
    private static final String COLOR_SONSTIGER_KOMMENTAR = "\\cellcolor{yellow!60}";
    private static final String PDFLATEX_DEFAULT_MAC = "/Library/TeX/texbin/pdflatex";
    // private static final String PDFLATEX_DEFAULT_WIN = "C:/Program
    // Files/MiKTeX 2.9/miktex/bin/x64/pdflatex.exe";
    private static final String PDFLATEX_ARG = "-halt-on-error";
    private String m_pdflatex = PDFLATEX_DEFAULT_MAC;
    private String m_copyPDFPath;
    private int m_maxSavedActualCals = 64;
    private int m_maxSavedTotalCals = 256;
    private int m_standardTimeForPause = 45;
    private Integer m_tmpIndex = null;

    FileSystemManager()
    {
        readIniFile();
    }

    private void readIniFile()
    {
        File iniFile = new File(PFAD.toAbsolutePath().toString(), INIFILE);
        Map<String, String> iniVars = new HashMap<>();
        iniVars.put("copyPDFPath", null);
        iniVars.put("maxSavedTotalCals", "256");
        iniVars.put("maxSavedActualCals", "64");
        iniVars.put("standardTimeForPause", "45");
        iniVars.put("pdflatex", PDFLATEX_DEFAULT_MAC);

        if (iniFile.exists())
        {
            try
            {
                FileReader fr = new FileReader(iniFile.getAbsoluteFile());
                BufferedReader br = new BufferedReader(fr);
                List<String> linesRead = br.lines().filter(l -> !l.contains("#")).collect(Collectors.toList());
                br.close();
                for (String string : linesRead)
                {
                    if (string.contains("=") && string.split("[=]").length > 1)
                    {
                        String key = string.split("[=]")[0];
                        String value = string.split("[=]")[1];
                        if (iniVars.containsKey(key))
                        {
                            iniVars.put(key, value);
                        }
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        setMaxSavedTotalCals(Integer.parseInt(iniVars.get("maxSavedTotalCals")));
        setMaxSavedActualCals(Integer.parseInt(iniVars.get("maxSavedActualCals")));
        setPDFLATEX(iniVars.get("pdflatex"));
        setCopyPDFPath(iniVars.get("copyPDFPath"));
        setStandardTimeForPause(Integer.parseInt(iniVars.get("standardTimeForPause")));
    }

    Kalender createKalender(int jahr)
    {
        if ((jahr >= 1900) && (jahr <= 2300))
        {
            Kalender kal = new Kalender(jahr);
            saveKalender(kal);
            return kal;
        }
        return null;
    }

    void saveKalender(Kalender kal)
    {
        kal.repairConsistency();
        try
        {
            Gson gson = new Gson();
            String json = gson.toJson(kal);
            File file = new File(PFAD.toAbsolutePath().toString(), createCalName());

            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

            bw.write(json);
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        cleanUpSavedCals();
    }

    private Kalender loadKalender(String nameOfFile)
    {
        File file = new File(PFAD.toAbsolutePath().toString(), nameOfFile);
        String json = "";

        if (file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                json = br.readLine();
                br.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            Kalender kal = gson.fromJson(json, Kalender.class);
            kal.repairConsistency();
            return kal;
        } else
        {
            return null;
        }
    }

    Kalender loadNewestKalender()
    {
        return loadKalender(getAllSavedCals().getLast());
    }

    private void deleteKalender(String nameOfFile)
    {
        File file = new File(PFAD.toAbsolutePath().toString(), nameOfFile);
        if (file.exists())
        {
            file.delete();
            m_tmpIndex = null;
        }
    }

    void undo()
    {
        if (m_tmpIndex == null)
        {
            m_tmpIndex = getAllSavedCals().size() - 2;
        } else if (m_tmpIndex > 0)
        {
            m_tmpIndex--;
        }
    }

    void redo()
    {
        if (m_tmpIndex != null)
        {
            m_tmpIndex++;
            if (getAllSavedCals().size() - 2 < m_tmpIndex)
            {
                m_tmpIndex = null;
            }
        }
    }

    Kalender loadTmpKalender()
    {
        if ((m_tmpIndex > 0) && (m_tmpIndex < getAllSavedCals().size()))
            return loadKalender(getAllSavedCals().get(m_tmpIndex.intValue()));
        else
            return loadNewestKalender();
    }

    String generatePDF(Kalender kal)
    {
        try
        {
            File file = new File(PFAD.toAbsolutePath().toString(), "Workinghours.tex");

            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            calToTeX(kal, bw);
            bw.close();
            callPDFLaTeX(file);
            return PFAD.toAbsolutePath().toString() + "Workinghours.pdf";
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    void copyPDFToPath()
    {
        if (null != getCopyPDFPath())
        {
            File sourcePDF = new File(PFAD.toAbsolutePath().toString(), "Workinghours.pdf");

            if (sourcePDF.exists())
            {
                File targetPDF = new File(Paths.get(getCopyPDFPath()).toAbsolutePath().toString(), "Workinghours.pdf");
                targetPDF.getParentFile().mkdirs();
                try
                {
                    Files.copy(sourcePDF.toPath(), targetPDF.toPath(), REPLACE_EXISTING);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void calToTeX(Kalender kal, BufferedWriter bw) throws IOException
    {
        /* begin of the header */
        bw.write("\\documentclass[10pt, a4paper, landscape]{article}");
        bw.newLine();
        bw.write("\\usepackage[table]{xcolor}");
        bw.newLine();
        bw.write("\\usepackage[utf8]{inputenc}");
        bw.newLine();
        bw.write("\\usepackage{tikz}");
        bw.newLine();
        bw.write("\\usepackage{latexsym}");
        bw.newLine();
        bw.write("\\usepackage{bm,array}");
        bw.newLine();
        bw.write("\\usepackage[margin=0.35in]{geometry}");
        bw.newLine();
        bw.newLine();
        bw.write("\\pagestyle{empty}");
        bw.newLine();
        bw.newLine();
        bw.write("\\begin{document}");
        bw.newLine();
        bw.newLine();
        bw.newLine();
        bw.write("\\begin{center}");
        bw.newLine();
        bw.write("\\Huge Kalender " + kal.getYear());
        bw.newLine();
        bw.write("\\end{center}");
        bw.newLine();
        bw.newLine();
        bw.write("\\newcolumntype{C}{>{\\centering\\arraybackslash}p{1.6em}}");
        bw.newLine();
        bw.newLine();
        bw.write("\\vspace*{8mm}\\hspace*{-10mm}\\scalebox{0.84}{\\begin{tabular}{cccc}");
        bw.newLine();

        /* end of the header and begin of the months for the first page */

        for (int i = 1; i < 13; i++)
        {
            frontPageMonthTeX(kal, i, bw);
        }

        /*
         * end of months for the first page followed by the legend and a newpage-command
         */

        legendTeX(bw);
        bw.write("\\newpage");
        bw.newLine();
        bw.newLine();

        /*
         * begin of pages of the detailed months
         */

        for (int page = 1; page < 7; page++)
        {
            detailedMonthsTeX(kal, page, bw);

            if (page != 6)
            {
                bw.write("\\newpage");
                bw.newLine();
                bw.newLine();
            }
        }

        /*
         * end of pages of the detailed months and begin of the statistics
         */

        statisticsTeX(kal, bw);

        /* end of pdf-file */

        bw.newLine();
        bw.newLine();
        bw.newLine();
        bw.write("\\end{document}");
        bw.newLine();
    }

    private void frontPageMonthTeX(Kalender cal, int month, BufferedWriter bw) throws IOException
    {
        bw.write("\\renewcommand{\\arraystretch}{1.4}");
        bw.newLine();
        bw.write("\\begin{tabular}{CCCCCCCC}");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write("\\multicolumn{8}{c}{\\textbf{\\Large ");

        switch (month)
        {
        case 1:
            bw.write("Januar");
            break;
        case 2:
            bw.write("Februar");
            break;
        case 3:
            bw.write("März");
            break;
        case 4:
            bw.write("April");
            break;
        case 5:
            bw.write("Mai");
            break;
        case 6:
            bw.write("Juni");
            break;
        case 7:
            bw.write("Juli");
            break;
        case 8:
            bw.write("August");
            break;
        case 9:
            bw.write("September");
            break;
        case 10:
            bw.write("Oktober");
            break;
        case 11:
            bw.write("November");
            break;
        case 12:
            bw.write("Dezember");
            break;
        }

        bw.write("}} \\\\[1mm]");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(getCellcolorRand("KW") + " & " + getCellcolorRand("Mo") + " & " + getCellcolorRand("Di") + " & "
                + getCellcolorRand("Mi") + " & " + getCellcolorRand("Do") + " & " + getCellcolorRand("Fr") + " & "
                + getCellcolorRand("Sa") + " & " + getCellcolorRand("So") + " \\\\");

        /* here starts the important part */

        int year = cal.getYear();
        Datum dayOfInterest = new Datum(year, month, 1);

        for (int j = 0; j < 6; j++)
        {
            bw.newLine();
            bw.write("%\\hline");
            bw.newLine();

            if (j == 0)
            {
                bw.write(getCellcolorRand(Integer.toString(dayOfInterest.getWeekOfYear())));

                for (int k = 1; k < 8; k++)
                {
                    bw.write(" & ");
                    if (dayOfInterest.getDayOfWeekAsInt() == k)
                    {
                        bw.write(tagToCellInFrontPageMonthTeX(cal.getTag(dayOfInterest)));
                        dayOfInterest.addToDayOfYear(1);
                    }
                }
                bw.write(" \\\\");
            } else if (dayOfInterest.getMonth() == month)
            {
                bw.write(getCellcolorRand(Integer.toString((dayOfInterest.getWeekOfYear()))));

                for (int k = 1; k < 8; k++)
                {
                    bw.write(" & ");
                    if (dayOfInterest.getMonth() != month)
                        break;
                    bw.write(tagToCellInFrontPageMonthTeX(cal.getTag(dayOfInterest)));
                    dayOfInterest.addToDayOfYear(1);
                }
                bw.write(" \\\\");
            } else
            {
                for (int k = 1; k < 8; k++)
                {
                    bw.write(" & ");
                }
                bw.write(" \\\\");
            }
        }

        /* here ends the important part */

        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();

        switch (month)

        {
        case 4:
        case 8:
            bw.write("\\end{tabular} \\\\[27mm]");
            bw.newLine();
            break;
        case 12:
            bw.write("\\end{tabular} \\\\");
            bw.newLine();
            bw.write("\\end{tabular}}");
            bw.newLine();
            break;
        default:
            bw.write("\\end{tabular} &");
            bw.newLine();
            break;
        }

    }

    private void legendTeX(BufferedWriter bw) throws IOException
    {
        bw.newLine();
        bw.write("\\vspace*{9mm}");
        bw.newLine();
        bw.newLine();
        bw.write("\\noindent\\makebox[\\linewidth]{\\rule{250mm}{0.4pt}}");
        bw.newLine();
        bw.newLine();
        bw.write("\\vspace*{5mm}");
        bw.newLine();
        bw.newLine();
        bw.write("\\center\\begin{tabular}{ClClCl}");
        bw.newLine();
        bw.write(TypeOfDay.WEEKEND.getColor() + " & \\textbf{Wochenende} \\hspace*{15mm} & "
                + TypeOfDay.VACATION.getColor() + " & Urlaub & " + TypeOfDay.BUSINESSTRIP.getColor()
                + " & Dienstreise \\\\[1mm]");
        bw.newLine();
        bw.write("& & & \\\\");
        bw.newLine();
        bw.write(TypeOfDay.HOLIDAY.getColor() + " & \\textbf{Feiertag} & " + TypeOfDay.ILLNESS.getColor()
                + " & Krankheitstag & " + TypeOfDay.STAFFTRAINING.getColor() + " & Fortbildung \\\\[1mm]");
        bw.newLine();
        bw.write("& & & \\\\");
        bw.newLine();
        bw.write(COLOR_RAND + " & \\textbf{Kalenderwoche} & " + TypeOfDay.HOURREDUCTION.getColor()
                + " & Überstundenreduzierung \\hspace*{15mm} & " + COLOR_SONSTIGER_KOMMENTAR
                + " & sonstiger Kommentar \\\\[1mm]");
        bw.newLine();
        bw.write("\\end{tabular}");
        bw.newLine();
        bw.newLine();
    }

    private void detailedMonthsTeX(Kalender cal, int page, BufferedWriter bw) throws IOException
    {
        LinkedList<Tag> monthLeftList = cal.getMonth((page - 1) * 2 + 1).values().stream()
                .filter(tag -> !(TypeOfDay.WEEKEND.equals(tag.getTypeOfDay()))).sorted()
                .collect(Collectors.toCollection(LinkedList::new));
        LinkedList<Tag> monthRightList = cal.getMonth((page - 1) * 2 + 2).values().stream()
                .filter(tag -> !(TypeOfDay.WEEKEND.equals(tag.getTypeOfDay()))).sorted()
                .collect(Collectors.toCollection(LinkedList::new));

        bw.write("\\vspace*{8mm}\\hspace*{-10mm}\\scalebox{0.84}{\\begin{tabular}{cc}");
        bw.newLine();

        for (int i = 1; i < 3; i++)
        {
            bw.write("\\renewcommand{\\arraystretch}{1.4}");
            bw.newLine();
            bw.write("\\begin{tabular}[t]{ccccccc}");
            bw.newLine();
            bw.write("%\\hline");
            bw.newLine();
            bw.write("\\multicolumn{7}{c}{\\textbf{\\Large ");

            switch ((page - 1) * 2 + i)
            {
            case 1:
                bw.write("Januar");
                break;
            case 2:
                bw.write("Februar");
                break;
            case 3:
                bw.write("März");
                break;
            case 4:
                bw.write("April");
                break;
            case 5:
                bw.write("Mai");
                break;
            case 6:
                bw.write("Juni");
                break;
            case 7:
                bw.write("Juli");
                break;
            case 8:
                bw.write("August");
                break;
            case 9:
                bw.write("September");
                break;
            case 10:
                bw.write("Oktober");
                break;
            case 11:
                bw.write("November");
                break;
            case 12:
                bw.write("Dezember");
                break;
            }

            bw.write("}} \\\\[2mm]");
            bw.newLine();
            bw.write("%\\hline");
            bw.newLine();
            bw.write(
                    "\\multicolumn{2}{c}{\\cellcolor{gray!60}\\textbf{Wochentag}} & \\cellcolor{gray!40}\\textbf{Beginn} & \\cellcolor{gray!40}\\textbf{Ende} & \\cellcolor{gray!40}\\textbf{Pause} & \\cellcolor{gray!40}\\textbf{$\\Delta$} & \\cellcolor{gray!40}\\textbf{$\\Sigma\\Delta$} \\\\");
            bw.newLine();
            bw.write("%\\hline");
            bw.newLine();

            LinkedList<Tag> tempMonthList = new LinkedList<Tag>();
            int tempMonthSize = 0;

            if (i == 1)
            {
                tempMonthList = monthLeftList;
                tempMonthSize = monthLeftList.size();
            } else
            {
                tempMonthList = monthRightList;
                tempMonthSize = monthRightList.size();
            }

            for (int j = 0; j < tempMonthSize; j++)
            {
                Tag tag = tempMonthList.removeFirst();
                int sigmaDelta = cal.getSigmaDelta(tag);
                bw.write(tagToCellInDetailedMonthsTeX(tag, sigmaDelta));
                bw.newLine();
                bw.write("%\\hline");
                bw.newLine();
            }

            switch (i)
            {
            case 1:
                bw.write("\\end{tabular}\\hspace*{10mm} &");
                bw.newLine();
                break;
            case 2:
                bw.write("\\end{tabular} \\\\");
                bw.newLine();
                bw.write("\\end{tabular}}");
                bw.newLine();
                break;
            }
        }
    }

    private void statisticsTeX(Kalender kal, BufferedWriter bw) throws IOException
    {
        int urlaubstage = 0;
        int partielleUrlaubstage = 0;
        int ueberstundentage = 0;
        int krankheitstage = 0;
        int dienstreisentage = 0;
        int fortbildungstage = 0;

        Datum datum = new Datum(kal.getYear(), 1, 1);

        while (datum.getYear() == kal.getYear())
        {
            Tag tag = kal.getTag(datum);
            switch (tag.getTypeOfDay())
            {
            case VACATION:
                urlaubstage++;
                break;
            case PARTIALVACATION:
                partielleUrlaubstage += tag.getPartialVacation();
            case HOURREDUCTION:
                ueberstundentage++;
                break;
            case ILLNESS:
                krankheitstage++;
                break;
            case BUSINESSTRIP:
                dienstreisentage++;
                break;
            case STAFFTRAINING:
                fortbildungstage++;
                break;
            default:
                break;
            }
            datum.addToDayOfYear(1);
        }

        datum.addToDayOfYear(-1);
        int sigmaDelta = kal.getSigmaDelta(new Tag(datum));
        int simgaDeltaStunden = sigmaDelta / 60;
        int simgaDeltaMinuten = sigmaDelta % 60;
        String sigmaDeltaStundenMinutenString = "";

        if (simgaDeltaStunden > 0)
        {
            if (simgaDeltaStunden > 1)
                sigmaDeltaStundenMinutenString += Integer.toString(simgaDeltaStunden) + " Stunden";
            else
                sigmaDeltaStundenMinutenString += Integer.toString(simgaDeltaStunden) + " Stunde";
        }
        if (simgaDeltaMinuten > 0)
        {
            if (simgaDeltaMinuten > 1)
                sigmaDeltaStundenMinutenString += " " + Integer.toString(simgaDeltaMinuten) + " Minuten";
            else
                sigmaDeltaStundenMinutenString += " " + Integer.toString(simgaDeltaMinuten) + " Minute";
        }
        if (sigmaDelta <= 0)
        {
            sigmaDeltaStundenMinutenString = "keine";
        }

        String partielleUrlaubstageString = "";
        if (partielleUrlaubstage > 0)
        {
            partielleUrlaubstageString += " und ";
            int partielleUrlaubstageStunden = partielleUrlaubstage / 60;
            int partielleUrlaubstageMinuten = partielleUrlaubstage % 60;

            while (partielleUrlaubstageStunden > 7)
            {
                partielleUrlaubstage = partielleUrlaubstage - 8;
                urlaubstage++;
            }

            if (partielleUrlaubstageStunden > 0)
            {
                if (partielleUrlaubstageStunden > 1)
                    partielleUrlaubstageString += Integer.toString(partielleUrlaubstageStunden) + " Stunden";
                else
                    partielleUrlaubstageString += Integer.toString(partielleUrlaubstageStunden) + " Stunde";
            }
            if (partielleUrlaubstageMinuten > 0)
            {
                if (partielleUrlaubstageMinuten > 1)
                    partielleUrlaubstageString += " " + Integer.toString(partielleUrlaubstageMinuten) + " Minuten";
                else
                    partielleUrlaubstageString += " " + Integer.toString(partielleUrlaubstageMinuten) + " Minute";
            }
        }

        bw.newLine();
        bw.write("\\newpage");
        bw.newLine();
        bw.newLine();
        bw.write("\\vspace*{8mm}\\begin{center}");
        bw.newLine();
        bw.write("\\Huge Statistik ");
        bw.newLine();
        bw.write("\\end{center}");
        bw.newLine();
        bw.newLine();
        bw.write("\\renewcommand{\\arraystretch}{1.4}");
        bw.newLine();
        bw.write("\\vspace*{8mm}\\begin{tabular}[t]{rc}");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Überstunden:} & ");
        bw.write(" " + getColoredSigmaDeltaAsString(sigmaDelta).replace(Integer.toString(sigmaDelta), "")
                + sigmaDeltaStundenMinutenString);
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Eingetragene Urlaubstage:} & ");
        bw.write(" " + TypeOfDay.VACATION.getColor() + Integer.toString(urlaubstage) + partielleUrlaubstageString);
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Überstundenreduzierungstage:} & ");
        bw.write(" " + TypeOfDay.HOURREDUCTION.getColor() + Integer.toString(ueberstundentage));
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Krankheitstage:} & ");
        bw.write(" " + TypeOfDay.ILLNESS.getColor() + Integer.toString(krankheitstage));
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Auf Dienstreise verbrachte Tage:} & ");
        bw.write(" " + TypeOfDay.BUSINESSTRIP.getColor() + Integer.toString(dienstreisentage));
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write(" " + COLOR_RAND);
        bw.write("\\textbf{Auf Fortbildung verbrachte Tage:} & ");
        bw.write(" " + TypeOfDay.STAFFTRAINING.getColor() + Integer.toString(fortbildungstage));
        bw.write(" \\\\");
        bw.newLine();
        bw.write("%\\hline");
        bw.newLine();
        bw.write("\\end{tabular}");
        bw.newLine();
    }

    private String tagToCellInFrontPageMonthTeX(Tag tag)
    {
        String dayAsString = Integer.toString(tag.getDatum().getDay());
        switch (tag.getTypeOfDay())
        {
        case WEEKEND:
            return TypeOfDay.WEEKEND.getColor() + "\\textbf{" + dayAsString + "}";
        case HOLIDAY:
            return TypeOfDay.HOLIDAY.getColor() + "\\textbf{" + dayAsString + "}";
        case VACATION:
            return TypeOfDay.VACATION.getColor() + dayAsString;
        case ILLNESS:
            return TypeOfDay.ILLNESS.getColor() + dayAsString;
        case HOURREDUCTION:
            return TypeOfDay.HOURREDUCTION.getColor() + dayAsString;
        case BUSINESSTRIP:
            return TypeOfDay.BUSINESSTRIP.getColor() + dayAsString;
        case STAFFTRAINING:
            return TypeOfDay.STAFFTRAINING.getColor() + dayAsString;
        default:
            if (tag.isKommentarSet())
            {
                return COLOR_SONSTIGER_KOMMENTAR + dayAsString;
            } else
            {
                return dayAsString;
            }
        }
    }

    private String tagToCellInDetailedMonthsTeX(Tag tag, int sigmaDelta)
    {
        String result = "";

        result = result.concat(" \\cellcolor{gray!40}\\textbf{");
        result = result.concat(tag.getDatum().getDayOfWeekAsShortString());
        result = result.concat("} & \\cellcolor{gray!40}\\textbf{");
        result = result.concat(Integer.toString(tag.getDatum().getDay()));
        result = result.concat(".} & ");

        if (!tag.isNull())
        {
            String color = "";
            switch (tag.getTypeOfDay())
            {
            case BUSINESSTRIP:
                color = TypeOfDay.BUSINESSTRIP.getColor();
                break;
            case STAFFTRAINING:
                color = TypeOfDay.STAFFTRAINING.getColor();
                break;
            default:
                if (tag.isKommentarSet())
                    color = COLOR_SONSTIGER_KOMMENTAR;
            }
            result = result.concat(color + tag.getBegin().toString());
            result = result.concat(" & ");
            result = result.concat(color + tag.getEnd().toString());
            result = result.concat(" & ");
            result = result.concat(color + Integer.toString(tag.getPause()));
            result = result.concat(" Minuten & ");
            result = result.concat(getColoredDeltaAsString(tag.getDelta()));
            result = result.concat(" Minuten & ");
            result = result.concat(getColoredSigmaDeltaAsString(sigmaDelta));
            result = result.concat(" Minuten ");

            if (tag.isKommentarSet())
            {
                if (tag.isOtherComment() || TypeOfDay.BUSINESSTRIP.equals(tag.getTypeOfDay())
                        || TypeOfDay.STAFFTRAINING.equals(tag.getTypeOfDay()))
                {
                    result = result.concat("\\\\");
                    result = result.concat(System.getProperty("line.separator"));
                    result = result.concat("\\multicolumn{7}{c}{");
                    switch (tag.getTypeOfDay())
                    {
                    case BUSINESSTRIP:
                        result = result.concat(TypeOfDay.BUSINESSTRIP.getColor());
                        break;
                    case STAFFTRAINING:
                        result = result.concat(TypeOfDay.STAFFTRAINING.getColor());
                        break;
                    default:
                        if (tag.isKommentarSet())
                            result = result.concat(COLOR_SONSTIGER_KOMMENTAR);
                    }
                    result = result.concat(" \\textbf{");
                    result = result.concat(tag.getKommentar());
                    result = result.concat("}} ");
                }
            }
        } else
        {
            result = result.concat("\\multicolumn{5}{c}{");
            switch (tag.getTypeOfDay())
            {
            case BUSINESSTRIP:
                result = result.concat(TypeOfDay.BUSINESSTRIP.getColor());
                break;
            case HOLIDAY:
                result = result.concat(TypeOfDay.HOLIDAY.getColor());
                break;
            case VACATION:
                result = result.concat(TypeOfDay.VACATION.getColor());
                break;
            case ILLNESS:
                result = result.concat(TypeOfDay.ILLNESS.getColor());
                break;
            case HOURREDUCTION:
                result = result.concat(TypeOfDay.HOURREDUCTION.getColor());
                break;
            case STAFFTRAINING:
                result = result.concat(TypeOfDay.STAFFTRAINING.getColor());
                break;
            default:
                result = result.concat(COLOR_SONSTIGER_KOMMENTAR);
                break;
            }
            result = result.concat(" \\textbf{");
            result = result.concat(tag.getKommentar());
            result = result.concat("}} ");
        }
        return result.concat("\\\\");
    }

    private String getColoredDeltaAsString(int delta)
    {
        if (0 < delta)
        {
            if (100 < delta)
                return "\\cellcolor{orange!50!yellow!100!white}" + Integer.toString(delta);
            else
                return "\\cellcolor{orange!50!yellow!" + Integer.toString(delta) + "!white}" + Integer.toString(delta);
        } else
        {
            int absDelta = Math.abs(delta);
            if (100 < absDelta)
                return "\\cellcolor{orange!50!cyan!100!white}" + Integer.toString(delta);
            else
                return "\\cellcolor{orange!50!cyan!" + Integer.toString(absDelta) + "!white}" + Integer.toString(delta);
        }
    }

    private String getColoredSigmaDeltaAsString(int sigmaDelta)
    {
        int scaledSigmaDelta = sigmaDelta * 10 / 48;
        if (0 < scaledSigmaDelta)
        {
            if (100 < scaledSigmaDelta)
                return "\\cellcolor{orange!50!yellow!100!white}" + Integer.toString(sigmaDelta);
            else
                return "\\cellcolor{orange!50!yellow!" + Integer.toString(scaledSigmaDelta) + "!white}"
                        + Integer.toString(sigmaDelta);
        } else
        {
            int absSigmaDelta = Math.abs(scaledSigmaDelta);
            if (100 < absSigmaDelta)
                return "\\cellcolor{orange!50!cyan!100!white}" + Integer.toString(sigmaDelta);
            else
                return "\\cellcolor{orange!50!cyan!" + Integer.toString(absSigmaDelta) + "!white}"
                        + Integer.toString(sigmaDelta);
        }
    }

    private String getCellcolorRand(String string)
    {
        if (string.equals("KW"))
        {
            return COLOR_ECKE + "\\textbf{KW}";
        } else
        {
            return COLOR_RAND + "\\textbf{" + string + "}";
        }
    }

    private void callPDFLaTeX(File file) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder(Paths.get(getPDFLATEX()).toAbsolutePath().toString(), PDFLATEX_ARG,
                file.getAbsolutePath());
        pb.directory(file.getParentFile());
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File(
                Paths.get(file.getParentFile().getAbsolutePath(), "pdflatex.log").toAbsolutePath().toString()));
        Process p = pb.start();

        try
        {
            p.waitFor(WAIT_FOR_PDFLATEX_SEC, TimeUnit.SECONDS);
            assert p.getInputStream().read() == -1;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method returns a List of all filenames of the calendars in the
     * WH_CAL_DATA_FOLDER.
     *
     * @return A LinkedList of Strings representing the names of the files
     *         containing the calendar-data.
     */
    private LinkedList<String> getAllSavedCals()
    {
        final File whCalDataDir = PFAD.toFile();
        if (whCalDataDir.isDirectory())
        {
            FilenameFilter filter = new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return dir.equals(whCalDataDir) && name.matches(REGEX_FOR_FILENAME);
                }
            };
            LinkedList<String> listFilesInCalDataDir = new LinkedList<>(Arrays.stream(whCalDataDir.listFiles(filter))
                    .map(File::getName).sorted().collect(Collectors.toList()));
            return listFilesInCalDataDir;
        } else
        {
            return new LinkedList<>();
        }
    }

    private void cleanUpSavedCals()
    {
        m_tmpIndex = null;
        LinkedList<String> listOfFiles = getAllSavedCals();
        while (getMaxSavedTotalCals() < listOfFiles.size())
        {
            String newestFile = listOfFiles.peekLast();
            String newestFileShort = newestFile.split("[T]")[0];
            LinkedList<String> listOfNewerFiles = new LinkedList<>(listOfFiles.stream().filter(f ->
            {
                return f.split("[T]")[0].equals(newestFileShort);
            }).collect(Collectors.toList()));
            LinkedList<String> listOfOlderFiles = new LinkedList<>(listOfFiles.stream().filter(f ->
            {
                return !f.split("[T]")[0].equals(newestFileShort);
            }).collect(Collectors.toList()));

            if (getMaxSavedActualCals() < listOfNewerFiles.size())
            {
                deleteKalender(listOfNewerFiles.remove());
            } else if (getMaxSavedTotalCals() - getMaxSavedActualCals() < listOfOlderFiles.size())
            {
                Iterator<String> iter = listOfOlderFiles.iterator();
                boolean hasDeleted = false;
                String olderFile = "";
                while (iter.hasNext())
                {
                    String newerFile = iter.next();
                    if (newerFile.split("[T]")[0].equals(olderFile.split("[T]")[0]))
                    {
                        deleteKalender(olderFile);
                        hasDeleted = true;
                        break;
                    } else
                    {
                        olderFile = newerFile;
                    }
                }

                if (!hasDeleted)
                {
                    deleteKalender(listOfOlderFiles.remove());
                }
            }
            listOfFiles = getAllSavedCals();
        }
    }

    private String createCalName()
    {
        ZonedDateTime jetzt = ZonedDateTime.now();
        return jetzt.toString().replace(":", "_").split("[+]")[0].split("[.]")[0] + WH_CAL_DATA_ENDING;
    }

    private String getPDFLATEX()
    {
        return m_pdflatex;
    }

    private void setPDFLATEX(String pdflatex)
    {
        File pdflatexFile = new File(pdflatex);
        if (pdflatexFile.exists())
        {
            m_pdflatex = pdflatex;
        } else
        {
            m_pdflatex = PDFLATEX_DEFAULT_MAC;
            System.out.println("The path to pdfLaTeX in the .ini-file seems to be incorrect. We will try to use: "
                    + PDFLATEX_DEFAULT_MAC);
        }
    }

    private int getMaxSavedTotalCals()
    {
        return m_maxSavedTotalCals;
    }

    private void setMaxSavedTotalCals(int maxSavedTotalCals)
    {
        if (maxSavedTotalCals < 1)
            m_maxSavedTotalCals = 1;
        else
            m_maxSavedTotalCals = maxSavedTotalCals;
    }

    private int getMaxSavedActualCals()
    {
        return m_maxSavedActualCals;
    }

    private void setMaxSavedActualCals(int maxSavedActualCals)
    {
        if (maxSavedActualCals < 1)
            m_maxSavedActualCals = 1;
        else
            m_maxSavedActualCals = maxSavedActualCals;
    }

    private String getCopyPDFPath()
    {
        return m_copyPDFPath;
    }

    private void setCopyPDFPath(String copyPDFPath)
    {
        m_copyPDFPath = copyPDFPath;
    }

    private void setStandardTimeForPause(int standardTimeForPause)
    {
        m_standardTimeForPause = standardTimeForPause;
    }

    public int getStandardTimeForPause()
    {
        return m_standardTimeForPause;
    }

    public void produceArchive()
    {
        String newestCalendarName = getAllSavedCals().getLast();
        String archiveName = newestCalendarName.split("[T]")[0] + ".zip";
        File newestCalendar = new File(PFAD.toAbsolutePath().toString(), newestCalendarName);
        File zipArchive = new File(Paths.get(getCopyPDFPath()).toAbsolutePath().toString(), archiveName);
        ZipOutputStream os = null;
        BufferedInputStream is = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(newestCalendar));
            os = new ZipOutputStream(new FileOutputStream(zipArchive), StandardCharsets.UTF_8);
            ZipEntry entry = new ZipEntry(newestCalendarName);
            os.putNextEntry(entry);

            byte[] buffer = new byte[8192];
            for (int i; (i = is.read(buffer)) != -1;)
                os.write(buffer, 0, i);

            os.closeEntry();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
