// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.filemanager.util;

import android.util.Log;
import android.webkit.MimeTypeMap;

import org.kotemaru.android.filemanager.R;

import java.util.Locale;

public class IconUtil {

    private static final String TAG = "IconUtil";

    public static int getFileIcon(String name) {
        int idx = name.lastIndexOf('.');
        String ext = (idx >= 0) ? name.substring(idx) : "";
        ext = ext.length() > 0 ? ext.substring(1) : "";
        ext = ext.toLowerCase(Locale.US);

        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (mime == null) mime = "";
        Log.d(TAG, "getFileIcon:" + ext + ":" + mime);
        for (IconDef def : ICONS) {
            if (def.isMatch(mime, ext)) return def.mResId;
        }
        return R.drawable.file_normal;
    }

    private static abstract class IconDef {
        int mResId;

        public IconDef(int resId) {
            mResId = resId;
        }

        public abstract boolean isMatch(String mime, String ext);
    }

    private static final IconDef[] ICONS = {
            new IconDef(R.drawable.file_audio) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return mime.startsWith("audio/");
                }
            },
            new IconDef(R.drawable.file_image) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return mime.startsWith("image/");
                }
            },
            new IconDef(R.drawable.file_video) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return mime.startsWith("video/");
                }
            },
            new IconDef(R.drawable.file_css) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "css".equals(ext);
                }
            },
            new IconDef(R.drawable.file_html) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "html".equals(ext);
                }
            },
            new IconDef(R.drawable.file_csv) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "csv".equals(ext);
                }
            },
            new IconDef(R.drawable.file_excel) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "xls".equals(ext) || "xlss".equals(ext);
                }
            },

            new IconDef(R.drawable.file_package) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "apk".equals(ext);
                }
            },
            new IconDef(R.drawable.file_pdf) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "pdf".equals(ext);
                }
            },

            new IconDef(R.drawable.file_word) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "doc".equals(ext) || "docx".equals(ext);
                }
            },
            new IconDef(R.drawable.file_xml) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return mime.endsWith("/xml");
                }
            },
            new IconDef(R.drawable.file_zip) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return "zip".equals(ext) || "rar".equals(ext) || "lha".equals(ext) || "7z".equals(ext);
                }
            },
            new IconDef(R.drawable.file_text) {
                @Override
                public boolean isMatch(String mime, String ext) {
                    return mime.startsWith("text/");
                }
            },
    };


}

