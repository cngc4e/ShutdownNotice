package net.pl3x.bukkit.shutdownnotice;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Adapted from essentials
https://github.com/drtshock/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/DateUtil.java
 */
public class DateUtil {
    private final static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

    public static long parseDateDiff(String time) {
        Matcher matcher = timePattern.matcher(time);
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                    hours = Integer.parseInt(matcher.group(1));
                }
                if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                    minutes = Integer.parseInt(matcher.group(2));
                }
                if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                    seconds = Integer.parseInt(matcher.group(3));
                }
                break;
            }
        }
        if (!found) {
            return -1;
        }
        Calendar calendar = new GregorianCalendar();
        if (hours > 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            calendar.add(Calendar.SECOND, seconds);
        }
        return calendar.getTimeInMillis();
    }
}
