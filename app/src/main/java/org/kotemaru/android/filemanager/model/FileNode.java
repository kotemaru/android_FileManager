package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.util.IconUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class FileNode extends BaseNode {
    private final File mFile;
    private final String mTitle;
    private final int mIconResId;
    private HashMap<File, FileNode> mChildMap;

    public FileNode(Node parent, File file) {
        super(parent);
        mFile = file;
        mTitle = file.getName() + (file.isDirectory() ? "/" : "");

        mIconResId = mFile.isDirectory()
                ? isOpened() ? R.drawable.folder_open : R.drawable.folder
                : IconUtil.getFileIcon(mTitle);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.LOCAL_FILE;
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(mIconResId);
    }

    @Override
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly) {
        if (mFile.isFile()) return null;
        File[] files = mFile.listFiles();
        if (mChildMap == null) mChildMap = new HashMap<File, FileNode>();

        ArrayList<Node> list = new ArrayList<Node>();
        for (File file : files) {
            boolean isNeed = (isShowNotReadable || file.canRead())
                    && (!isFolderOnly || file.isDirectory());
            if (isNeed) {
                FileNode node = mChildMap.get(file);
                if (node == null) {
                    node = new FileNode(this, file);
                    mChildMap.put(file, node);
                }
                list.add(node);
            }
        }
        return list;
    }

    @Override
    public boolean hasChildren() {
        return mFile.isDirectory();
    }

    @Override
    public Uri getUri() {
        return Uri.parse("file://" + getAbsName());
    }

    @Override
    public String getAbsName() {
        return mFile.getAbsolutePath();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public boolean isReadable() {
        return mFile.canRead() && (mFile.isFile() || mFile.canExecute());
    }

    @Override
    public boolean isWritable() {
        return mFile.canWrite();
    }

    @Override
    public long getContentSize() {
        return mFile.length();
    }

    @Override
    public long getLastModified() {
        return mFile.lastModified();
    }
}
