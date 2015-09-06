package org.kotemaru.android.filemanager;


import android.app.Application;

import org.kotemaru.android.filemanager.activity.MainActivity;
import org.kotemaru.android.filemanager.logic.ActionManager;
import org.kotemaru.android.filemanager.model.BookmarkFolderNode;
import org.kotemaru.android.filemanager.model.ClipFolderNode;
import org.kotemaru.android.filemanager.model.FileNode;
import org.kotemaru.android.filemanager.model.NodeTree;
import org.kotemaru.android.filemanager.persistent.Settings;

import java.io.File;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication sInstance;

    private MainActivity mMainActivity;
    private ActionManager mActionManager;

    private NodeTree mNodeTree;
    private BookmarkFolderNode mBookmarkFolderNode;
    private ClipFolderNode mClipFolderNode;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Settings.getSharedPrefs(); // init.

        mNodeTree = createNodeTree();
    }

    private NodeTree createNodeTree() {
        mBookmarkFolderNode = new BookmarkFolderNode(null, "Book mark");
        Settings.getBookmarks(mBookmarkFolderNode);
        mClipFolderNode = new ClipFolderNode(null, "Clip");

        NodeTree folderTree = new NodeTree();
        folderTree.addTopLevel(mBookmarkFolderNode);
        folderTree.addTopLevel(mClipFolderNode);
        folderTree.addTopLevel(new FileNode(null, new File("/")));
        folderTree.refresh();
        return folderTree;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public MainActivity getMainActivity() {
        return mMainActivity;
    }

    public ActionManager getActionManager() {
        return mActionManager;
    }

    public void setMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mActionManager = new ActionManager(mainActivity);
    }

    public NodeTree getNodeTree() {
        return mNodeTree;
    }

    public BookmarkFolderNode getBookmarkFolderNode() {
        return mBookmarkFolderNode;
    }

    public ClipFolderNode getClipFolderNode() {
        return mClipFolderNode;
    }


}
