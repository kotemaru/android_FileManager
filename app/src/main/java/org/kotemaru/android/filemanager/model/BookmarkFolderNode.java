package org.kotemaru.android.filemanager.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kotemaru.android.filemanager.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class BookmarkFolderNode extends LinkedFolderNode {
    private static final String TAG = BookmarkFolderNode.class.getSimpleName();

    public BookmarkFolderNode(Node parent, String name) {
        super(parent, name);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.BOOKMARK_FOLDER;
    }

    public void addChild(Node node) {
        mChildren.add(new BookmarkNode(this, node.getOrigin()));
    }

    public void addChild(BookmarkNode node) {
        mChildren.add(node);
    }

    public void addChild(BookmarkFolderNode node) {
        mChildren.add(node);
    }

    public void removeChild(Node node) {
        Iterator<Node> ite = mChildren.iterator();
        while (ite.hasNext()) {
            Node child = ite.next();
            if (child.getOrigin() == node.getOrigin()) ite.remove();
        }
    }

    @Override
    public Uri getUri() {
        return Uri.parse("bookmarks:///" + Uri.encode(mName));
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.bookmark);
    }

    public String toJson() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("[ ");
        for (Node child : mChildren) {
            if (child instanceof BookmarkFolderNode) {
                sbuf.append("{\"folder\":\"").append(child.getUri().toString()).append("\",")
                        .append("\"children\":").append(((BookmarkFolderNode) child).toJson())
                        .append("},");
            } else {
                sbuf.append('"').append(child.getUri().toString()).append("\",");
            }
        }
        sbuf.setLength(sbuf.length()-1);
        sbuf.append(']');
        return sbuf.toString();
    }
    public void fromJson(String json) {
        Log.d(TAG, "fromJson:" + json);
        try {
            JSONArray array = new JSONArray(json);
            fromJson(array);
        } catch (JSONException e) {
            Log.e(TAG, "e", e);
        }
    }
    private void fromJson(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                Object item = array.get(i);
                if (item instanceof String) {
                    String url = item.toString().trim();
                    mChildren.add(NodeUtil.createNodeFromUrl(this, url));
                } else {
                    JSONObject obj = (JSONObject) item;
                    String url = obj.getString("folder").trim();
                    BookmarkFolderNode folder = (BookmarkFolderNode) NodeUtil.createNodeFromUrl(this, url);
                    mChildren.add(folder);
                    folder.fromJson(obj.getJSONArray("children"));
                }
            }
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Bad bookmark url", e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
