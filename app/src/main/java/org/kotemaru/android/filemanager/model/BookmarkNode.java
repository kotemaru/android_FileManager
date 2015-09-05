package org.kotemaru.android.filemanager.model;

import android.net.Uri;

public class BookmarkNode extends LinkedNode {

    public BookmarkNode(Node parent, Node origin) {
        super(parent, origin);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.BOOKMARK;
    }

    @Override
    public Uri getUri() {
        return Uri.parse("bookmark://?"+Uri.encode(mOrigin.getUri().toString()));
    }

}
