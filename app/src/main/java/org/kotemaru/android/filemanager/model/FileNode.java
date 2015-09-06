package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.util.IconUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileNode extends BaseNode {
    private final File mFile;
    private final String mTitle;
    private final int mIconResId;
    private HashMap<CharSequence, FileNode> mChildMap;

    public FileNode(Node parent, File file) {
        super(parent);
        mFile = file;
        mTitle = file.getName() + (file.isDirectory() ? "/" : "");

        mIconResId = mFile.isDirectory()
                ? isOpened() ? R.drawable.folder_open : R.drawable.folder
                : IconUtil.getFileIcon(mTitle);
    }

    public File getFile() {
        return mFile;
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
    public Node getOrCreateChild(CharSequence name) throws IOException {
        if (!mFile.isDirectory()) throw new IOException("Not folder.");
        if (mChildMap == null) mChildMap = new HashMap<CharSequence, FileNode>();
        FileNode node = mChildMap.get(name);
        if (node == null) {
            node = new FileNode(this, new File(mFile, name.toString()));
        }
        return node;
    }

    @Override
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly) {
        if (mFile.isFile()) return null;
        File[] files = mFile.listFiles();
        if (mChildMap == null) mChildMap = new HashMap<CharSequence, FileNode>();

        ArrayList<Node> list = new ArrayList<Node>();
        for (File file : files) {
            boolean isNeed = (isShowNotReadable || file.canRead())
                    && (!isFolderOnly || file.isDirectory());
            if (isNeed) {
                FileNode node = mChildMap.get(file.getName());
                if (node == null) {
                    node = new FileNode(this, file);
                    mChildMap.put(file.getName(), node);
                }
                list.add(node);
            }
        }
        return list;
    }

    @Override
    public boolean eq(Node node) {
        if (node.getNodeType() != NodeType.LOCAL_FILE) return false;
        File file = ((FileNode) node).mFile;
        return file.equals(mFile);
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


    @Override
    public InputStream getInputStream() throws IOException {
        if (mFile.isDirectory()) throw new IOException("Cant open folder stream.");
        return new FileInputStream(mFile);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mFile.isDirectory()) throw new IOException("Cant open folder stream.");
        return new FileOutputStream(mFile);
    }

    @Override
    public void rename(CharSequence newName) throws IOException {
        File toFile = new File(mFile.getParentFile(), newName.toString());
        boolean isSuccess = mFile.renameTo(toFile);
        if (!isSuccess) {
            throw new IOException("Cant rename " + getName() + " to " + newName);
        }
    }

    @Override
    public void delete() throws IOException {
        boolean isSuccess = mFile.delete();
        if (!isSuccess) {
            throw new IOException("Cant delete " + getName());
        }
    }

}
