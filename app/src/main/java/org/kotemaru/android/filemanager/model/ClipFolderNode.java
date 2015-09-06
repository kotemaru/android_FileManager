package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.kotemaru.android.filemanager.R;

public class ClipFolderNode extends LinkedFolderNode {
    public ClipFolderNode(Node parent, String name) {
        super(parent, name);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CLIP_FOLDER;
    }

    public void addChild(Node node) {
        if (!isLinkedChild(node)) {
            mChildren.add(new ClipNode(this, node));
        }
    }

    public void clear() {
        mChildren.clear();
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_clip_blue);
    }


}
