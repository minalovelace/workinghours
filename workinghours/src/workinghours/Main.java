package workinghours;

public class Main
{

    public static void main(String[] args)
    {
        // TODO implement .ini-reader to set the path to pdfLaTeX in an
        // .ini-file.
        CommandLineInterpreterManager clim = new CommandLineInterpreterManager();
        clim.interpret(args);
    }
}