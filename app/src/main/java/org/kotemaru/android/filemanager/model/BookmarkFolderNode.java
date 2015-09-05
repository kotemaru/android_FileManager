package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.kotemaru.android.filemanager.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookmarkFolderNode extends VirtualFolderNode {
    public BookmarkFolderNode(Node parent, String name) {
        super(parent, name);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.BOOKMARK_FOLDER;
    }

    public void add(Node node) {
        mChildren.add(new BookmarkNode(this, node.getOrigin()));
    }

    public void add(BookmarkNode node) {
        mChildren.add(node);
    }

    public void add(BookmarkFolderNode node) {
        mChildren.add(node);
    }
    public void remove(Node node) {
        Iterator<Node> ite = mChildren.iterator();
        while (ite.hasNext()) {
            Node child = ite.next();
            if (child.getOrigin() == node.getOrigin()) ite.remove();
        }
    }
    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.bookmark);
    }
}
