package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Node {
    public enum NodeType {
        BOOKMARK_FOLDER("bookmarks"),
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
    public Node getOrCreateChild(CharSequence name) throws IOException;
    public List<Node> getChildren(boolean isShowNotReadable, boolean isFolderOnly);
    public boolean hasChildren();
    public int getNestLevel();
    public boolean isOpened();
    public void setOpened(boolean isOpened);
    public NodeType getNodeType();
    public Node getOrigin();
    public boolean eq(Node node);

    public Uri getUri();
    public String getTitle();
    public String getAbsName();
    public String getName();
    public boolean isReadable();
    public boolean isWritable();
    public long getContentSize();
    public long getLastModified();

    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
    public void rename(CharSequence newName) throws IOException;
    public void delete() throws IOException;
    public void copyTo(Node destNode) throws IOException;
    public void moveTo(Node destNode) throws IOException;
}
