package org.kotemaru.android.filemanager.model;


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
    public boolean isSelected() {
        return mIsSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mIsSelected = selected;
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
}
