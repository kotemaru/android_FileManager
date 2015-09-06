package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class LinkedNode extends BaseNode {
    protected final Node mOrigin;

    public LinkedNode(Node parent, Node origin) {
        super(parent);
        mOrigin = origin;
    }

    @Override
    public Node getOrCreateChild(CharSequence name) throws IOException {
        throw new IOException("Cant create child.");
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
    public boolean eq(Node node) {
        return getOrigin().eq(node.getOrigin());
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

    @Override
    public InputStream getInputStream() throws IOException {
        return mOrigin.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return mOrigin.getOutputStream();
    }

    @Override
    public void rename(CharSequence newName) throws IOException {
        throw new IOException("Cant rename linked item.");
    }

    @Override
    public void delete() throws IOException {
        try {
            LinkedFolderNode parent = (LinkedFolderNode) getParent();
            parent.removeChild(this);
        } catch (ClassCastException e) {
            throw new IOException(e);
        }
    }
}
