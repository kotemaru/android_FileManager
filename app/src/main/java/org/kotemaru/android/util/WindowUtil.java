package org.kotemaru.android.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;


/**
 * 雑多なユーティリティ。
 * @author kotemaru.org
 */
public class WindowUtil {
	public static final String TAG = WindowUtil.class.getSimpleName();


	/**
	 * システムのステータスバーの高さを得る。
	 * @param context
	 * @return ステータスバーの高さpx値
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * ディスプレイサイズを得る。
	 * ステータスバーは含むがナビゲーションバーは含まない。
	 * @param context
	 * @return
	 */
	public static Point getDisplaySize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		return point;
	}


	/**
	 * オーバレイ・レイヤ用のパラメータを返す。
	 * @return
	 */
	public static WindowManager.LayoutParams getWindowLayoutParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.START;
		return params;
	}


	public static int sp2px(Context context, float sp) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * scale + 0.5f);
	}
	public static int dp2px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}