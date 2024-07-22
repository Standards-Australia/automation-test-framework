package commonfactory;

import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SupportUtil {

    public static String emailaddress;

    /**
     * Returns the formatted date and time that is a certain number of days before the current date and time.
     *
     * @param days the number of days to subtract from the current date and time
     * @return string in the format "yyyy-MM-dd HH:mm:ss.SSSSSSS"
     */
    public static String getPreviousDateTime(int days) {
        String pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lessThanCurrentDate = currentDate.minusDays(days);
        String formattedDate = lessThanCurrentDate.format(formatter);
        return formattedDate;
    }

    private static final String ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String randomAlphaString(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_STRING.length());
            builder.append(ALPHA_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static int randomInteger() {
        {
            int range = (999 - 100) + 1;
            return (int) (Math.random() * range) + 100;
        }

    }

    public static String randomNumbers(int size) {
        Random rand = new Random();
        int ctr = (int) Math.pow(10, size);
        int range = ((9 * ctr) - ctr) + 1;
        String value = StringUtils.rightPad(String.valueOf((int) (Math.random() * range) + ctr), size, String.valueOf(rand.nextInt(10)));
        String ret = value.length() == size ? value : StringUtils.right(value, size);
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(size);
        Double dblRet = Double.parseDouble(ret);
        return dblRet == 0 ? "1" : df.format(dblRet);
    }

    public int randomIntFromRange(List<?> lists) {
        Random random = new Random();
        return random.nextInt(lists.size()) + 1;
    }

    public int randomIntFromNumber(int num) {
        Random random = new Random();
        return random.nextInt(num);
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String randomString(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    /**
     * @return
     */
    public String getTodaysDate() {
        String pattern = "dd/MM/yyyy";
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public String getFutureDate(int... addDays) {
        int intDays = addDays.length > 0 ? addDays[0] : 0;
        String pattern = "dd/MM/yyyy";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, intDays);
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    public String getFutureYear(int... addYears) {
        int intYear = addYears.length > 0 ? addYears[0] : 0;
        String pattern = "dd/MM/yyyy";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, intYear);
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    public String getTimeStamp() {
        String pattern = "ddMMyyyy_hhmmss";
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public String getFutureDateyyyyMMdd(int... addDays) throws Exception {
        int intDays = addDays.length > 0 ? addDays[0] : 0;
        String pattern = "yyyy-MM-dd";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, intDays);
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    public boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * Create new unique gmail id using base gmail id
     *
     * @param baseEmail
     * @return
     */
    public String getUniqueGmailID(String baseEmail) {
        emailaddress = baseEmail + getTimeStamp() + "@gmail.com";
        return emailaddress;
    }
}
