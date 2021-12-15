package in.arjsna.swipecardlib;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.StringJoiner;

public class Utils {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    public static void entry_log(Object... params) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        StringJoiner sj;

            sj = new StringJoiner(",");

            for (Object param : params) {
                sj.add(params.toString());
            }
//        HiLog.debug(LABEL_LOG, "entry_log"+"ENTRY "+ String.format(
//                " - %s.%s()",
//                trace[1].getClassName(),
//                trace[1].getMethodName()
//        ));
    }
}
