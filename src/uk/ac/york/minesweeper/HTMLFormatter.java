package uk.ac.york.minesweeper;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

// Klasse für den eigenen Formatter
public class HTMLFormatter extends Formatter {

    private final Date date = new Date();

    private final DateFormat dateformatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    @Override
    public final String format(LogRecord record) {

        date.setTime(record.getMillis());
        StringBuffer sb = new StringBuffer();

        //HTML Tag vorsetzen.
        if (record.getLevel() == Level.SEVERE) {

            sb.append("<font color='red'><b>");
        }   else if (record.getLevel() == Level.INFO) {

            sb.append("<font color='orange'>");

        }   else if (record.getLevel() == Level.WARNING) {

            sb.append("<font color='red'>");

        }   else {

            sb.append("<font color='green'>");

        }

        // Date
        sb.append("</br>" +"-------------------------------------------------------------------" + "</br>");
        sb.append("Datum: " + dateformatter.format(date)  );
        sb.append("</br>");

        // Class + Logger
        if (record.getSourceClassName() != null) {

            sb.append("Logger: " + record.getLoggerName() + "</br>");
            sb.append("Class: " + record.getSourceClassName() + "</br>");

        }

        // Level
            sb.append("Level: " + record.getLevel().getName() + "</br>");

        // Method
        if (record.getSourceMethodName() != null) {
            sb.append("Method: " + record.getSourceMethodName() + "()" + "</br>");
        }

        // Message
        sb.append("Message: " + record.getMessage() +"</br>" + "-------------------------------------------------------------------");


        //HTML Schnipsel anhängen
        if (record.getLevel() == Level.SEVERE) {

            sb.append("</font></b>");
        }   else if (record.getLevel() == Level.INFO) {

            sb.append("</font>");

        }   else if (record.getLevel() == Level.WARNING) {

            sb.append("</font>");

        }   else {

            sb.append("</font>");

        }

        // Newline
        sb.append(System.getProperty("line.separator") );

        return sb.toString();
    }

}
