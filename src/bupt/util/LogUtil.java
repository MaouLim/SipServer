package bupt.util;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Created by Maou Lim on 2017/7/13.
 */
public class LogUtil {

    private static AtomicLong atomicLong = new AtomicLong(0);

    public static void lumpedLog(String title, String info, boolean emergent) {

        PrintStream printStream = emergent ? System.err : System.out;
        long seq = atomicLong.getAndIncrement();

        printStream.println(seq + "*******************" + title + "*******************");
        printStream.println(info);
        printStream.println(seq + "**********************************************");
    }
}
