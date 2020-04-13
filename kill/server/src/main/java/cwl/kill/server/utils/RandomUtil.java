package cwl.kill.server.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数生成Util
 * @Author long
 * @Date 2020/3/8 10:54
 */
public class RandomUtil {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMssSS");

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * 生成订单编号-时间戳+N位随机数流水号
     * @return
     */
    public static String generateOrderCode(){
        return dateFormat.format(DateTime.now().toDate()) + generateNumber(4);
    }

    public static String generateNumber(final int num){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=1; i<=num ; i++){
            stringBuffer.append(random.nextInt(9));
        }
        return stringBuffer.toString();
    }

    // 测试
    /*public static void main(String[] args) {
        for (int i=0; i<10000 ;i++) {
            System.out.println(generateOrderCode());
        }
    }*/
}
