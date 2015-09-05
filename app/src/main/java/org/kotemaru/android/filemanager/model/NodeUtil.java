package org.kotemaru.android.filemanager.model;

import android.net.Uri;

import org.kotemaru.android.filemanager.persistent.Settings;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NodeUtil {
    private static Comparator<Node> sNameComparator = new Comparator<Node>() {
        @Override
        public int compare(Node lhs, Node rhs) {
            if (lhs.hasChildren() != rhs.hasChildren()) {
                return lhs.hasChildren() ? -1 : 1;
            }
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    };
    private static Comparator<Node> sDateComparator = new Comparator<Node>() {
        @Override
        public int compare(Node lhs, Node rhs) {
            return (int) (rhs.getLastModified() - lhs.getLastModified());
        }
    };
    private static Comparator<Node> sSizeComparator = new Comparator<Node>() {
        @Override
        public int compare(Node lhs, Node rhs) {
            return (int) (rhs.getContentSize() - lhs.getContentSize());
        }
    };

    public static void sortFolderList(List<Node> list) {
        Collections.sort(list, sNameComparator);
    }


    public static void sort(List<Node> list) {
        switch (Settings.getSortCondition()) {
            case NAME:
                Collections.sort(list, sNameComparator);
                break;
            case DATE:
                Collections.sort(list, sDateComparator);
                break;
            case SIZE:
                Collections.sort(list, sSizeComparator);
                break;
            default:
        }
    }

    public static Node createNodeFromUrl(Node parent, String url) throws IllegalArgumentException {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        Node.NodeType type = Node.NodeType.getType(scheme);
        switch (type) {
            case CLIP:
                return new ClipNode(parent, createNodeFromUrl(parent, uri.getQuery()));
            case BOOKMARK:
                return new ClipNode(parent, createNodeFromUrl(parent, uri.getQuery()));
            case LOCAL_FILE:
                return new FileNode(parent, new File(uri.getPath()));
            default:
        }
        return null;
    }
}
