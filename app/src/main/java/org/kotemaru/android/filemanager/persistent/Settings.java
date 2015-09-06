package org.kotemaru.android.filemanager.persistent;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.Log;

import org.kotemaru.android.filemanager.MyApplication;
import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.model.BookmarkFolderNode;
import org.kotemaru.android.filemanager.model.Node;
import org.kotemaru.android.filemanager.model.NodeUtil;
import org.kotemaru.android.util.WindowUtil;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private static final String TAG = "Settings";
    private static SharedPreferences sSharedPrefs;
    private static int sFilesFragmentWidth;
    private static int sFilesFragmentHeight;

    public enum SortCondition {
        NAME(R.id.name), DATE(R.id.date), SIZE(R.id.size);

        private static final SortCondition[] VALUES = values();
        int mResId;

        SortCondition(int id) {
            mResId = id;
        }

        public int getResId() {
            return mResId;
        }

        public static SortCondition valueOf(int resId) {
            for (SortCondition val : VALUES) {
                if (val.mResId == resId) return val;
            }
            return null;
        }
    }

    public static final SharedPreferences getSharedPrefs() {
        if (sSharedPrefs == null)  {
            sSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
            Point size = WindowUtil.getDisplaySize(MyApplication.getInstance());
            sFilesFragmentHeight = size.y / 2;
            sFilesFragmentWidth = size.x / 2;
        }
        return sSharedPrefs;
    }

    public static boolean isShowNotReadableFiles() {
        return getSharedPrefs().getBoolean("showNotReadableFiles", false);
    }

    public static boolean isShowSubFolder() {
        return getSharedPrefs().getBoolean("showSubFolder", true);
    }

    public static SortCondition getSortCondition() {
        String val = getSharedPrefs().getString("sortCondition", SortCondition.NAME.name());
        return SortCondition.valueOf(val);
    }

    public static void setSortCondition(SortCondition val) {
        SharedPreferences.Editor edit = getSharedPrefs().edit();
        edit.putString("sortCondition", val.name());
        edit.apply();
    }

    public static int getFilesFragmentHeight() {
        return getSharedPrefs().getInt("filesFragmentHeight", sFilesFragmentHeight);
    }

    public static void setFilesFragmentHeight(int val) {
        SharedPreferences.Editor edit = getSharedPrefs().edit();
        edit.putInt("filesFragmentHeight", val);
        edit.apply();
    }

    public static int getFilesFragmentWidth() {
        return getSharedPrefs().getInt("filesFragmentWidth", sFilesFragmentWidth);
    }

    public static void setFilesFragmentWidth(int val) {
        SharedPreferences.Editor edit = getSharedPrefs().edit();
        edit.putInt("filesFragmentWidth", val);
        edit.apply();
    }

    public static BookmarkFolderNode getBookmarks(BookmarkFolderNode bookmarks) {
        String val = getSharedPrefs().getString("bookmarks", "[]");
        bookmarks.fromJson(val);
        return bookmarks;
    }

    public static void setBookmarks(BookmarkFolderNode bookmarks) {
        SharedPreferences.Editor edit = getSharedPrefs().edit();
        edit.putString("bookmarks", bookmarks.toJson());
        edit.apply();
    }
}
