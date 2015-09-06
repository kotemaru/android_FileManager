package org.kotemaru.android.filemanager.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.kotemaru.android.filemanager.MyApplication;
import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.model.Node;
import org.kotemaru.android.filemanager.model.NodeTree;
import org.kotemaru.android.util.WindowUtil;

public class FoldersFragment extends Fragment {
    private final MyApplication myApp = MyApplication.getInstance();
    private ListView mListView;
    private NodesAdapter mNodesAdapter;
    private int mIndentPx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        mIndentPx = WindowUtil.dp2px(getActivity(), 10);

        mNodesAdapter = new NodesAdapter();
        mNodesAdapter.setNodeTree(myApp.getNodeTree());
        mListView = (ListView) view.findViewById(R.id.folders);
        mListView.setAdapter(mNodesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Node node = myApp.getNodeTree().select(position);
                myApp.getActionManager().onClick(true, node);
            }
        });
        registerForContextMenu(mListView);
        return view;
    }

    public void refresh() {
        myApp.getNodeTree().refresh();
        mNodesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    class NodesAdapter extends BaseAdapter {
        private NodeTree mNodeTree;
        private LayoutInflater mInflater = getActivity().getLayoutInflater();

        NodesAdapter() {
        }

        public void setNodeTree(NodeTree folderTree) {
            mNodeTree = folderTree;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mNodeTree == null) return 0;
            return mNodeTree.size();
        }

        @Override
        public Object getItem(int position) {
            return getNode(position);
        }

        public Node getNode(int position) {
            if (mNodeTree == null) return null;
            return mNodeTree.getNode(position);
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

            Node node = getNode(position);
            info.title.setText(node.getTitle());
            view.setPadding(node.getNestLevel() * mIndentPx, 0, 0, 0);

            info.icon.setImageDrawable(node.getIcon(getActivity()));
            info.toggle.setImageResource(node.isOpened() ? R.drawable.triangle_down : R.drawable.triangle_right);
            info.toggle.setVisibility(node.hasChildren() ? View.VISIBLE : View.INVISIBLE);
            info.clipped.setVisibility(View.INVISIBLE);
            info.bookmarked.setVisibility(node.getNodeType() == Node.NodeType.BOOKMARK ? View.VISIBLE : View.INVISIBLE);

            boolean isSelected = myApp.getNodeTree().isSelected(node);
            view.setSelected(isSelected);
            view.setBackgroundColor(isSelected ? Color.LTGRAY : Color.WHITE); // TODO:リソース化

            boolean isRead = node.isReadable();
            view.setAlpha(isRead ? 1.0F : 0.2F);
            //info.title.setAlpha(isRead ? 1.0F : 0.2F);
            boolean isWrite = node.isWritable();
            info.cannotWrite.setVisibility(isWrite ? View.INVISIBLE : View.VISIBLE);
            return view;
        }

        private View createItemView(ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.item_folder, null, false);
            ListItemInfo info = new ListItemInfo();
            info.title = (TextView) view.findViewById(R.id.title);
            info.toggle = (ImageView) view.findViewById(R.id.toggle);
            info.icon = (ImageView) view.findViewById(R.id.icon);
            info.clipped = (ImageView) view.findViewById(R.id.clipped);
            info.bookmarked = (ImageView) view.findViewById(R.id.bookmarked);
            info.cannotWrite = (ImageView) view.findViewById(R.id.cannotWrite);
            info.toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    ListItemInfo info = (ListItemInfo) view.getTag();
                    mNodeTree.toggle(info.position);
                    mNodesAdapter.setNodeTree(mNodeTree);
                }
            });
            view.setTag(info);
            return view;
        }
    }

    public static class ListItemInfo {
        int position;
        TextView title;
        ImageView toggle;
        ImageView icon;
        ImageView cannotWrite;
        ImageView clipped;
        ImageView bookmarked;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("TAG", "onCreateContextMenu");
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.folders) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Node node = mNodesAdapter.getNode(info.position);
            myApp.getActionManager().createContextMenu(menu, node);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Node node = mNodesAdapter.getNode(info.position);
        return myApp.getActionManager().doMenuAction(item, node);
    }
}

