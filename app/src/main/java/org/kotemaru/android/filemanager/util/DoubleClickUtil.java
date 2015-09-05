package org.kotemaru.android.filemanager.util;

/**
 * Created by inou on 2015/08/31.
 */
public class DoubleClickUtil {



    private long mLastClickTime = 0;


    public void onClick() {
        long curTime = System.currentTimeMillis();
        boolean isDoubleClick =  (curTime - mLastClickTime < 200);
        if (isDoubleClick) {
            mLastClickTime = 0;
        } else {
            mLastClickTime = curTime;
        }
    }


}
