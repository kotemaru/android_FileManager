package org.kotemaru.android.filemanager.model;

import android.net.Uri;

public class ClipNode extends LinkedNode {

    public ClipNode(Node parent, Node origin) {
        super(parent, origin);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CLIP;
    }

    @Override
    public Uri getUri() {
        return Uri.parse("clip://?"+Uri.encode(mOrigin.getUri().toString()));
    }
}
