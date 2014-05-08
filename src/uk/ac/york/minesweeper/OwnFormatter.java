package uk.ac.york.minesweeper;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// Klasse für den eigenen Formatter
public class OwnFormatter extends Formatter {

    private final Date date = new Date();

    private final DateFormat dateformatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    @Override
    public final String format(LogRecord record) {

        date.setTime(record.getMillis());
        StringBuffer sb = new StringBuffer();

        // Date
        sb.append("\n");
        sb.append("Datum: " + dateformatter.format(date)  );
        sb.append("\n");

        // Class + Logger
        if (record.getSourceClassName() != null) {

            sb.append("Logger: " + record.getLoggerName() + "\n");
            sb.append("Class: " + record.getSourceClassName() + "\n");

        }

        // Level
        sb.append("Level: " + record.getLevel().getName() + "\n");

        // Method
        if (record.getSourceMethodName() != null) {
            sb.append("Method: " + record.getSourceMethodName() + "()" + "\n");
        }

        // Message
        sb.append("Message: " + record.getMessage() +"\n");

        // Newline
        sb.append(System.getProperty("line.separator") );

        return sb.toString();
    }

}
