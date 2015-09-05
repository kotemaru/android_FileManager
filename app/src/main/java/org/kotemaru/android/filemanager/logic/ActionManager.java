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

import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.fragment.FilesFragment;
import org.kotemaru.android.filemanager.fragment.FoldersFragment;
import org.kotemaru.android.filemanager.model.BookmarkFolderNode;
import org.kotemaru.android.filemanager.model.Node;
import org.kotemaru.android.filemanager.persistent.Settings;

import java.util.List;

public class ActionManager {
    private static final String TAG = ActionManager.class.getSimpleName();
    private final Activity mActivity;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable mSingleClickTask;

    public ActionManager(Activity activity) {
        mActivity = activity;
    }

    private FilesFragment getFilesFragment() {
        FragmentManager fm = mActivity.getFragmentManager();
        return (FilesFragment) fm.findFragmentById(R.id.filesFragment);
    }

    private FoldersFragment getFoldersFragment() {
        FragmentManager fm = mActivity.getFragmentManager();
        return (FoldersFragment) fm.findFragmentById(R.id.foldersFragment);
    }


    public void createContextMenu(ContextMenu menu, Node node) {
        MenuInflater inflater = mActivity.getMenuInflater();
        menu.setHeaderTitle(node.getName());
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
            if (node.isSelected()) {
                node.setSelected(false);
                getFoldersFragment().getClipFolderNode().remove(node);
            } else {
                node.setSelected(true);
                getFoldersFragment().getClipFolderNode().add(node);
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
        int id = item.getItemId();
        for (MenuAction action : mMenuActions) {
            if (action.mMenuItemId == id) {
                action.doAction(item, node);
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

        public abstract void doAction(MenuItem item, Node node);
    }

    private MenuAction[] mMenuActions = {
            new MenuAction(R.id.open) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    open(node);
                }
            },
            new MenuAction(R.id.openText) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.rename) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.delete) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.openText) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.copy_from_clip) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.move_from_clip) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },

            // ---------------------------------------------------------
            new MenuAction(R.id.bookmark) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    getFoldersFragment().getBookmarkFolderNode().add(node);
                    getFoldersFragment().refresh();
                    Settings.setBookmarks(getFoldersFragment().getBookmarkFolderNode().getChildren(false, false));
                }
            },
            new MenuAction(R.id.bookmark_delete) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    getFoldersFragment().getBookmarkFolderNode().remove(node);
                    getFoldersFragment().refresh();
                    Settings.setBookmarks(getFoldersFragment().getBookmarkFolderNode().getChildren(false, false));
                }
            },
            new MenuAction(R.id.bookmark_new_folder) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    getFoldersFragment().getBookmarkFolderNode().remove(node);
                    getFoldersFragment().refresh();
                    Settings.setBookmarks(getFoldersFragment().getBookmarkFolderNode().getChildren(false, false));
                }
            },
            // ---------------------------------------------------------
            new MenuAction(R.id.clip_copy_to_folder) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.clip_move_to_folder) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    // TODO:
                }
            },
            new MenuAction(R.id.clip_bookmark_all) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    List<Node> clips = getFoldersFragment().getClipFolderNode().getChildren(false, false);
                    BookmarkFolderNode bookmarks = getFoldersFragment().getBookmarkFolderNode();
                    for (Node clip : clips) bookmarks.add(clip);
                    getFoldersFragment().refresh();
                    Settings.setBookmarks(bookmarks.getChildren(false, false));
                }
            },
            new MenuAction(R.id.clip_cancel) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    getFoldersFragment().getClipFolderNode().remove(node);
                    node.setSelected(false);
                    getFoldersFragment().refresh();
                }
            },
            new MenuAction(R.id.clip_clear) {
                @Override
                public void doAction(MenuItem item, Node node) {
                    getFoldersFragment().getClipFolderNode().clear();
                    getFoldersFragment().refresh();
                }
            },
    };


}
