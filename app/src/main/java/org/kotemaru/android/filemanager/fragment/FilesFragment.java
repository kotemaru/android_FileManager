package org.kotemaru.android.filemanager.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.activity.MainActivity;
import org.kotemaru.android.filemanager.logic.ActionManager;
import org.kotemaru.android.filemanager.model.Node;
import org.kotemaru.android.filemanager.model.NodeUtil;
import org.kotemaru.android.filemanager.persistent.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * Created by inou on 2015/08/30.
 */
public class FilesFragment extends Fragment {
    private static final String TAG = "FilesFragment";
    private ListView mListView;
    private View mNoFiles;
    private FilesAdapter mFilesAdapter;
    private TextView mBreadcrumb;
    private long mLastClickTime = 0;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Stack<Node> mHistory = new Stack<Node>();
    private View mRootView;
    private int mOrientation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_files, container, false);
        mRootView = view;
        mFilesAdapter = new FilesAdapter();
        //mFilesAdapter.setFolder(null);
        mListView = (ListView) view.findViewById(R.id.files);
        mListView.setAdapter(mFilesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Node node = mFilesAdapter.getNode(position);
                getActionManager().onClick(false, node);
            }
        });

        registerForContextMenu(mListView);

        mNoFiles = view.findViewById(R.id.noFiles);
        mBreadcrumb = (TextView) view.findViewById(R.id.breadcrumb);
        mBreadcrumb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "mBreadcrumb.onTouch:" + event);
                if (MotionEvent.ACTION_MOVE == event.getAction()) {
                    addFragmentSize((int) -event.getX(), (int) -event.getY());
                }
                return true;
            }
        });


        return view;
    }
    public void refresh() {
        mFilesAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
        mOrientation = getActivity().getResources().getConfiguration().orientation;
        setFragmentSize(Settings.getFilesFragmentWidth(), Settings.getFilesFragmentHeight());
    }

    @Override
    public void onPause() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Settings.setFilesFragmentHeight(params.height);
        } else {
            Settings.setFilesFragmentWidth(params.width);
        }
        super.onPause();
    }
    private void setFragmentSize(int x, int y) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height = y;
        } else {
            params.width = x;
        }
        mRootView.setLayoutParams(params);
    }

    private void addFragmentSize(int x, int y) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height += y;
        } else {
            params.width += x;
        }
        mRootView.setLayoutParams(params);
    }


    public void setFolder(Node folder) {
        mFilesAdapter.setFolder(folder);
        mBreadcrumb.setText(folder.getAbsName());
        mHistory.push(folder);
    }

    public boolean backFolder() {
        if (mHistory.isEmpty()) return false;
        mHistory.pop();
        if (mHistory.isEmpty()) return false;
        Node folder = mHistory.peek();
        mFilesAdapter.setFolder(folder);
        mBreadcrumb.setText(folder.getAbsName());
        return true;
    }
    public void reload() {
        if (mHistory.isEmpty()) return;
        mFilesAdapter.setFolder(mHistory.peek());
    }

    public boolean onBackPressed() {
        return backFolder();
    }


    public boolean hasSelectedFile() {
        return mFilesAdapter.hasSelectedFile();
    }

    public List<Node> getSelectedFile() {
        return mFilesAdapter.getSelectedFile();
    }


    class FilesAdapter extends BaseAdapter {
        private LayoutInflater mInflater = getActivity().getLayoutInflater();
        private Node mFolder;
        private List<Node> mNodeList;

        FilesAdapter() {
        }

        public List<Node> getNodeList() {
            return mNodeList;
        }

        public void setNodeList(List<Node> fileItems) {
            mNodeList = fileItems;
            notifyDataSetChanged();
        }

        public void setFolder(Node folder) {
            if (!folder.hasChildren()) return;

            mFolder = folder;
            mNodeList = folder.getChildren(Settings.isShowNotReadableFiles(), false);
            mNoFiles.setVisibility(mNodeList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            NodeUtil.sort(mNodeList);
            notifyDataSetChanged();
        }

        public boolean hasSelectedFile() {
            if (mNodeList == null) return false;
            for (Node fileItem : mNodeList) {
                if (fileItem.isSelected()) return true;
            }
            return false;
        }

        public List<Node> getSelectedFile() {
            List<Node> list = new ArrayList<Node>(10);
            if (mNodeList == null) return list;
            for (Node fileItem : mNodeList) {
                if (fileItem.isSelected()) list.add(fileItem);
            }
            return list;
        }

        @Override
        public int getCount() {
            if (mNodeList == null) return 0;
            return mNodeList.size();
        }

        @Override
        public Object getItem(int position) {
            return getNode(position);
        }

        public Node getNode(int position) {
            if (mNodeList == null) return null;
            return mNodeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null) view = createItemView(parent);

            ListItemInfo info = (ListItemInfo) view.getTag();
            info.position = position;

            Node fileItem = getNode(position);
            info.title.setText(fileItem.getTitle());
            info.size.setText(getSizeStr(fileItem));
            info.datetime.setText(getDatetimeStr(fileItem));
            info.icon.setImageDrawable(fileItem.getIcon(getActivity()));
            view.setSelected(fileItem.isSelected());
            view.setBackgroundColor(fileItem.isSelected() ? Color.CYAN : Color.LTGRAY); // TODO:リソース化
            view.setAlpha(fileItem.isReadable() ? 1.0F : 0.2F);
            info.cannotWrite.setVisibility(fileItem.isWritable() ? View.INVISIBLE : View.VISIBLE);
            return view;
        }

        private View createItemView(ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.item_file, null, false);
            ListItemInfo info = new ListItemInfo();
            info.title = (TextView) view.findViewById(R.id.title);
            info.size = (TextView) view.findViewById(R.id.size);
            info.datetime = (TextView) view.findViewById(R.id.datetime);
            info.icon = (ImageView) view.findViewById(R.id.icon);
            info.cannotWrite = (ImageView) view.findViewById(R.id.cannotWrite);
            view.setTag(info);
            return view;
        }

    }

    public static class ListItemInfo {
        int position;
        TextView title;
        ImageView icon;
        ImageView cannotWrite;
        TextView size;
        TextView datetime;
    }

    private String getSizeStr(Node node) {
        float size = (float) node.getContentSize();
        if (size < 9999) return String.format("%5.0f", size);
        if (size < 1024 * 1024) return String.format("%5.1fk", size / 1024);
        if (size < 1024 * 1024 * 1024) return String.format("%5.1fM", size / 1024 / 1024);
        return String.format("%5.1fG", size / 1024 / 1024 / 1024);
    }

    private String getDatetimeStr(Node node) {
        long time = node.getLastModified();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // TODO:チューン
        return fmt.format(new Date(time));
    }

    private ActionManager getActionManager() {
        return ((MainActivity)getActivity()).getActionManager();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("TAG", "onCreateContextMenu");
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.files) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Node node = mFilesAdapter.getNode(info.position);
            getActionManager().createContextMenu(menu, node);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Node node = mFilesAdapter.getNode(info.position);
        return getActionManager().doMenuAction(item, node);
    }

}
