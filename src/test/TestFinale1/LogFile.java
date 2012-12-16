package TestFinale1;

import jam.eccezioni.*;
import java.io.*;

public class LogFile {
    private PrintWriter log;
    private void logFile(String s) throws JAMIOException {
        log.println(s);
        log.flush();
    }
    public void startLog(String fileName, String messaggio) throws JAMIOException {
        try {
            File file = new File(fileName + ".log");
            log = new PrintWriter(file);
            logFile(messaggio);
        }
        catch(IOException ioexception)
        {
            throw new JAMIOException(ioexception);
        }
    }
    public void endLog() {
        log.close();
    }
    public void log(String messaggio, Object ExtraObject) throws JAMIOException {
        String temp = "";
        temp += "----> " + (System.nanoTime() / 1000L) + " <----\n";
        temp += messaggio + "\n";
        if(ExtraObject != null)
            temp += ExtraObject.toString() + "\n";
        temp += "<<---------";
        logFile(temp);
    }
    public void log(String s) throws JAMIOException {
        log(s, null);
    }
}
