package org.kotemaru.android.filemanager.model;


import org.kotemaru.android.filemanager.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseNode implements Node {
    protected final Node mParent;
    protected boolean mIsOpened;
    protected boolean mIsSelected;

    public BaseNode(Node parent) {
        mParent = parent;
    }

    @Override
    public int getNestLevel() {
        int level = 0;
        Node parent = mParent;
        while (parent != null) {
            parent = parent.getParent();
            level++;
        }
        return level;
    }

    @Override
    public Node getOrigin() {
        return this;
    }

    @Override
    public Node getParent() {
        return mParent;
    }

    @Override
    public boolean isChild(Node node) {
        return node.getParent() == this;
    }

    @Override
    public boolean isOpened() {
        return mIsOpened;
    }

    @Override
    public void setOpened(boolean isOpened) {
        mIsOpened = isOpened;
    }


    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public long getContentSize() {
        return 0;
    }

    @Override
    public long getLastModified() {
        return 0;
    }


    @Override
    abstract public InputStream getInputStream() throws IOException;

    @Override
    abstract public OutputStream getOutputStream() throws IOException;

    @Override
    abstract public void rename(CharSequence newName) throws IOException;

    @Override
    abstract public void delete() throws IOException;

    @Override
    public void copyTo(Node destNode) throws IOException {
        FileUtil.copy(this, destNode);
    }
    @Override
    public void moveTo(Node destNode) throws IOException {
        copyTo(destNode);
        delete();
    }

    @Override
    public String toString() {
        return "Node["+getNodeType()+":"+getAbsName()+"]";
    }
}
