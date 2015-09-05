package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.kotemaru.android.filemanager.R;

import java.util.Iterator;

public class ClipFolderNode extends VirtualFolderNode {
    public ClipFolderNode(Node parent, String name) {
        super(parent, name);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CLIP_FOLDER;
    }

    public void add(Node node) {
        mChildren.add(new ClipNode(this, node));
    }
    public void remove(Node node) {
        Iterator<Node> ite = mChildren.iterator();
        while (ite.hasNext()) {
            Node child = ite.next();
            if (child.getOrigin() == node.getOrigin()) ite.remove();
        }
    }


    public void clear() {
        for (Node child : mChildren) {
            child.setSelected(false);
        }
        mChildren.clear();
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_clip_blue);
    }


    public boolean isLinkedChild(Node node) {
        for (Node child : mChildren) {
            if (child.getOrigin() == node) return true;
        }
        return false;
    }


}
