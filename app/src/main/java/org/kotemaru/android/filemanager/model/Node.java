package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.List;

public interface Node {
    public enum NodeType {
        BOOKMARK_FOLDER(null),
        BOOKMARK("bookmark"),
        LOCAL_FILE("file"),
        NETWORK_FILE("net"),
        CLIP_FOLDER(null),
        CLIP("clip");

        private String mScheme;
        NodeType(String scheme) {
            mScheme = scheme;
        }

        private static NodeType[] VALUES = values();
        public static NodeType getType(String scheme) {
            for (NodeType type : VALUES) {
                if (scheme.equals(type.mScheme)) return type;
            }
            throw new IllegalArgumentException("Unknown scheme "+scheme);
        }
    }


    public Drawable getIcon(Context context);
    public Node getParent();
    public boolean isChild(Node node);
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly);
    public boolean hasChildren();
    public int getNestLevel();
    public boolean isSelected();
    public void setSelected(boolean b);
    public boolean isOpened();
    public void setOpened(boolean isOpened);
    public NodeType getNodeType();
    public Node getOrigin();

    public Uri getUri();
    public String getTitle();
    public String getAbsName();
    public String getName();
    public boolean isReadable();
    public boolean isWritable();
    public long getContentSize();
    public long getLastModified();
}
