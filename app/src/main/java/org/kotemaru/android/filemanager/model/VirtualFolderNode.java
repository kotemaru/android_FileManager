package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.kotemaru.android.filemanager.R;

import java.util.ArrayList;
import java.util.List;

public abstract class VirtualFolderNode extends BaseNode {
    protected List<Node> mChildren = new ArrayList<Node>();
    protected final String mName;

    public VirtualFolderNode(Node parent, String name) {
        super(parent);
        mName = name;
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.bookmark);
    }

    @Override
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly) {
        return mChildren;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Uri getUri() {
        return null;
    }

    @Override
    public String getAbsName() {
        return mName;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
