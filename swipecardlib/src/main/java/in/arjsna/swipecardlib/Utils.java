package in.arjsna.swipecardlib;

import android.util.Log;


public class Utils {


    private static final String TAG = "entry_log" ;

    public static void entry_log(Object... params) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();


        StringBuilder sj = new StringBuilder(",");

        for (Object param : params) {
            sj.append(params.toString());
        }
        Log.d(TAG, "entry_log: " + String.format(
                " - %s.%s()",
                trace[1].getClassName(),
                trace[1].getMethodName()
        ));

    }
}
