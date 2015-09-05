package org.kotemaru.android.filemanager.model;


import org.kotemaru.android.filemanager.persistent.Settings;

import java.util.ArrayList;
import java.util.List;

public class NodeTree {
    ArrayList<Node> mTopLevelList = new ArrayList<Node>(); // TODO:チューニング
    ArrayList<Node> mList = new ArrayList<Node>(); // TODO:チューニング

    public NodeTree() {
    }

    public void addTopLevel(Node Node) {
        mTopLevelList.add(Node);
    }

    public void refresh() {
        mList.clear();
        for (Node node : mTopLevelList) {
            mList.add(node);
            if (node.isOpened()) refresh(node);
        }
    }
    private void refresh(Node node) {
        if (!node.hasChildren()) return;
        List<Node> children = node.getChildren(Settings.isShowNotReadableFiles(), true);
        NodeUtil.sortFolderList(children);
        for (Node child : children) {
            mList.add(child);
            if (child.isOpened()) refresh(child);
        }
    }

    public void open(int index) {
        Node node = mList.get(index);
        if (!node.hasChildren()) return;
        node.setOpened(true);
        refresh();
    }

    public void close(int index) {
        Node node = mList.get(index);
        if (!node.hasChildren()) return;
        node.setOpened(false);
        refresh();
    }

    public int size() {
        return mList.size();
    }

    public Node getNode(int index) {
        return mList.get(index);
    }

    public void toggle(int index) {
        Node node = mList.get(index);
        if (node.isOpened()) {
            close(index);
        } else {
            open(index);
        }
    }

    public Node select(int index) {
        for (Node node : mList) node.setSelected(false);
        Node node = mList.get(index);
        node.setSelected(true);
        return node;
    }
}
