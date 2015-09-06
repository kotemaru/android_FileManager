// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.filemanager.logic;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import org.kotemaru.android.filemanager.MyApplication;
import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.fragment.FilesFragment;
import org.kotemaru.android.filemanager.fragment.FoldersFragment;
import org.kotemaru.android.filemanager.model.BookmarkFolderNode;
import org.kotemaru.android.filemanager.model.ClipFolderNode;
import org.kotemaru.android.filemanager.model.Node;
import org.kotemaru.android.filemanager.persistent.Settings;

import java.io.IOException;
import java.util.List;

public class ActionManager {
    private static final String TAG = ActionManager.class.getSimpleName();
    private final MyApplication myApp = MyApplication.getInstance();

    private final Activity mActivity;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable mSingleClickTask;
    private Node mMenuTargetNode;

    public ActionManager(Activity activity) {
        mActivity = activity;
    }

    private FilesFragment getFilesFragment() {
        FragmentManager fm = mActivity.getFragmentManager();
        return (FilesFragment) fm.findFragmentById(R.id.filesFragment);
    }

    public FoldersFragment getFoldersFragment() {
        FragmentManager fm = mActivity.getFragmentManager();
        return (FoldersFragment) fm.findFragmentById(R.id.foldersFragment);
    }


    public void createContextMenu(ContextMenu menu, Node node) {
        MenuInflater inflater = mActivity.getMenuInflater();
        menu.setHeaderTitle(node.getTitle());
        switch (node.getNodeType()) {
            case LOCAL_FILE:
                if (node.hasChildren()) {
                    inflater.inflate(R.menu.context_folder, menu);
                } else {
                    inflater.inflate(R.menu.context_file, menu);
                }
                break;
            case BOOKMARK_FOLDER:
                inflater.inflate(R.menu.context_bookmark_folder, menu);
                break;
            case BOOKMARK:
                inflater.inflate(R.menu.context_bookmark, menu);
                break;
            case CLIP_FOLDER:
                inflater.inflate(R.menu.context_clip_folder, menu);
                break;
            case CLIP:
                inflater.inflate(R.menu.context_clip, menu);
                break;
        }
        mMenuTargetNode = node;
    }

    public void onClick(final boolean isFolders, final Node node) {
        if (mSingleClickTask == null) {
            mSingleClickTask = new Runnable() {
                @Override
                public void run() {
                    mSingleClickTask = null;
                    doSingleClick(isFolders, node);
                }
            };
            mUiHandler.postDelayed(mSingleClickTask, 300);
        } else {
            mUiHandler.removeCallbacks(mSingleClickTask);
            mSingleClickTask = null;
            doDoubleClick(isFolders, node);
        }
    }


    public void doSingleClick(boolean isFolders, Node node) {
        Log.d(TAG, "doSingleClick:" + isFolders + ":" + node.getAbsName());
        if (isFolders) {
            open(node);
        } else {
            ClipFolderNode clips = myApp.getClipFolderNode();
            if (clips.isLinkedChild(node)) {
                clips.removeChild(node);
            } else {
                clips.addChild(node);
            }
            getFilesFragment().refresh();
            getFoldersFragment().refresh();
        }
    }

    public void doDoubleClick(boolean isFolders, Node node) {
        Log.d(TAG, "doDoubleClick:" + isFolders + ":" + node.getAbsName());
        open(node);
    }


    public boolean doMenuAction(MenuItem item, Node node) {
        Log.d(TAG, "doMenuAction:" + item + "," + node);
        int id = item.getItemId();
        for (MenuAction action : mMenuActions) {
            if (action.mMenuItemId == id) {
                try {
                    action.doAction(item, mMenuTargetNode, getFoldersFragment(), getFilesFragment());
                } catch (Exception e) {
                    Log.e(TAG, "doMenuAction:" + e.getMessage(), e);
                    DialogManager.openErrorDialog(mActivity, e);
                }
                mMenuTargetNode = null;
                return true;
            }
        }
        return false;
    }


    private void open(Node fileItem) {
        if (!fileItem.hasChildren()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String ext = MimeTypeMap.getFileExtensionFromUrl(fileItem.getName()).toLowerCase();
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            //intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), mime);
            intent.setDataAndType(fileItem.getUri(), mime);
            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            getFilesFragment().setFolder(fileItem);
            getFoldersFragment().refresh();
        }
    }

    abstract class MenuAction {
        final int mMenuItemId;

        public MenuAction(int menuItemId) {
            mMenuItemId = menuItemId;
        }

        public abstract void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception;
    }

    private MenuAction[] mMenuActions = {
            // -------------------------------------------------------------------------
            // File menu
            new MenuAction(R.id.open) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    open(node);
                }
            },
            new MenuAction(R.id.openText) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    // TODO:
                }
            },
            new MenuAction(R.id.rename) {
                @Override
                public void doAction(MenuItem item, final Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    DialogManager.openRenameDialog(mActivity, node.getName(), new DialogManager.OnRenameListener() {
                        @Override
                        public void onRename(CharSequence name) {
                            try {
                                node.rename(name);
                            } catch (Exception e) {
                                DialogManager.openErrorDialog(mActivity, e);
                            }
                        }
                    });
                }
            },
            new MenuAction(R.id.delete) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    node.delete();
                }
            },
            new MenuAction(R.id.copy_from_clip) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    List<Node> clips = myApp.getClipFolderNode().getChildren(false, false);
                    for (Node clip : clips) {
                        Node dstNode = node.getOrCreateChild(clip.getName());
                        // TODO: dstNode.isExists ほげほげ
                        clip.copyTo(dstNode);
                    }
                }
            },
            new MenuAction(R.id.move_from_clip) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    List<Node> clips = myApp.getClipFolderNode().getChildren(false, false);
                    for (Node clip : clips) {
                        Node dstNode = node.getOrCreateChild(clip.getName());
                        // TODO: dstNode.isExists ほげほげ
                        clip.moveTo(dstNode);
                    }
                }
            },

            // ---------------------------------------------------------
            new MenuAction(R.id.bookmark) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    myApp.getBookmarkFolderNode().addChild(node);
                    folders.refresh();
                    Settings.setBookmarks(myApp.getBookmarkFolderNode());
                }
            },
            new MenuAction(R.id.bookmark_rename) {
                @Override
                public void doAction(MenuItem item, Node node, final FoldersFragment folders, FilesFragment files) throws Exception {
                    final BookmarkFolderNode bookmarks = (BookmarkFolderNode) node;
                    DialogManager.openRenameDialog(mActivity, node.getName(), new DialogManager.OnRenameListener() {
                        @Override
                        public void onRename(CharSequence name) {
                            try {
                                bookmarks.rename(name);
                                folders.refresh();
                                Settings.setBookmarks(myApp.getBookmarkFolderNode());
                            } catch (IOException e) {
                                DialogManager.openErrorDialog(mActivity, e);
                            }
                        }
                    });
                }
            },
            new MenuAction(R.id.bookmark_from_clip) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    BookmarkFolderNode bookmarks = (BookmarkFolderNode) node;
                    List<Node> clips = myApp.getClipFolderNode().getChildren(false, false);
                    for (Node clip : clips) {
                        bookmarks.addChild(clip.getOrigin());
                    }
                    myApp.getClipFolderNode().clear();
                    folders.refresh();
                    Settings.setBookmarks(myApp.getBookmarkFolderNode());
                }
            },
            new MenuAction(R.id.bookmark_delete) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    myApp.getBookmarkFolderNode().removeChild(node);
                    folders.refresh();
                    Settings.setBookmarks(myApp.getBookmarkFolderNode());
                }
            },
            new MenuAction(R.id.bookmark_new_folder) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    BookmarkFolderNode parent = myApp.getBookmarkFolderNode();
                    if (node instanceof BookmarkFolderNode) {
                        parent = (BookmarkFolderNode) node;
                    }
                    parent.addChild(new BookmarkFolderNode(parent, "Book mark sub"));
                    folders.refresh();
                    Settings.setBookmarks(myApp.getBookmarkFolderNode());
                }
            },
            // ---------------------------------------------------------
            new MenuAction(R.id.clip_bookmark_all) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    List<Node> clips = myApp.getClipFolderNode().getChildren(false, false);
                    BookmarkFolderNode bookmarks = myApp.getBookmarkFolderNode();
                    for (Node clip : clips) bookmarks.addChild(clip);
                    folders.refresh();
                    Settings.setBookmarks(bookmarks);
                }
            },
            new MenuAction(R.id.clip_cancel) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    myApp.getClipFolderNode().removeChild(node);
                    folders.refresh();
                    files.refresh();
                }
            },
            new MenuAction(R.id.clip_clear) {
                @Override
                public void doAction(MenuItem item, Node node, FoldersFragment folders, FilesFragment files) throws Exception {
                    myApp.getClipFolderNode().clear();
                    folders.refresh();
                }
            },
    };


}
