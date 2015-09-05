package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.kotemaru.android.filemanager.persistent.Settings;

import java.util.List;

public abstract class LinkedNode extends BaseNode {
    protected final Node mOrigin;

    public LinkedNode(Node parent, Node origin) {
        super(parent);
        mOrigin = origin;
    }

    @Override
    public boolean isChild(Node node) {
        return mOrigin.isChild(node);
    }

    @Override
    public Drawable getIcon(Context context) {
        return mOrigin.getIcon(context);
    }

    @Override
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly) {
        return mOrigin.getChildren(isShowNotReadable, isFolderOnly);
    }

    @Override
    public boolean hasChildren() {
        return mOrigin.hasChildren();
    }

    @Override
    public Node getOrigin() {
        return mOrigin;
    }

    @Override
    public String getTitle() {
        return mOrigin.getAbsName();
    }

    @Override
    public String getAbsName() {
        return mOrigin.getAbsName();
    }

    @Override
    public String getName() {
        return mOrigin.getName();
    }

    @Override
    public boolean isReadable() {
        return mOrigin.isReadable();
    }

    @Override
    public boolean isWritable() {
        return mOrigin.isWritable();
    }
}
