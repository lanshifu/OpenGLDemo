

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
public class Test {



    @org.junit.Test
    public void mainTest() {

        System.out.println(stampToDate("1230735828391"));

        assertEquals("1", "1");
    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
