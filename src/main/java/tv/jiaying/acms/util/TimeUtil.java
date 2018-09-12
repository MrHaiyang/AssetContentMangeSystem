package tv.jiaying.acms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    private static DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * string格式为yyyy-mm-dd
     *
     * @param date
     * @return
     */
    public static Date getDateByyyyyMMdd(String date) {
        Date dirDate = null;
        try {
            dirDate = sdf1.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return dirDate;
    }
}
