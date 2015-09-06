package org.kotemaru.android.filemanager.model;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LinkedFolderNode extends BaseNode {
    protected List<Node> mChildren = new ArrayList<Node>();
    protected String mName;

    public LinkedFolderNode(Node parent, String name) {
        super(parent);
        mName = name;
    }

    public void removeChild(Node node) {
        Iterator<Node> ite = mChildren.iterator();
        while (ite.hasNext()) {
            Node child = ite.next();
            if (child.getOrigin() == node.getOrigin()) ite.remove();
        }
    }

    public boolean isLinkedChild(Node node) {
        for (Node child : mChildren) {
            if (child.eq(node)) return true;
        }
        return false;
    }

    @Override
    public Node getOrCreateChild(CharSequence name) throws IOException {
        for (Node child : mChildren) {
            if (name.equals(child.getName())) return child;
        }
        throw new IOException("Cant create child.");
    }

    @Override
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly) {
        return mChildren;
    }

    @Override
    public boolean eq(Node node) {
        return this == node;
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


    @Override
    public void rename(CharSequence newName) throws IOException {
        mName = newName.toString();
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

    @Override
    public InputStream getInputStream() throws IOException {
        throw new IOException("Cant open link folder");
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Cant open link folder");
    }
}
