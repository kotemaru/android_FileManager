package org.kotemaru.android.filemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.kotemaru.android.filemanager.MyApplication;
import org.kotemaru.android.filemanager.R;
import org.kotemaru.android.filemanager.persistent.Settings;
import org.kotemaru.android.filemanager.fragment.FilesFragment;

public class MainActivity extends Activity {
    private final MyApplication myApp = MyApplication.getInstance();
    private boolean mClipMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp.setMainActivity(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem  item = menu.findItem(Settings.getSortCondition().getResId());
        if (item != null) item.setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.name:
            case R.id.date:
            case R.id.size:
                item.setChecked(true);
                Settings.setSortCondition(Settings.SortCondition.valueOf(item.getItemId()));
                FilesFragment fragment = (FilesFragment) getFragmentManager().findFragmentById(R.id.filesFragment);
                fragment.reload();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FilesFragment fragment = (FilesFragment) getFragmentManager().findFragmentById(R.id.filesFragment);
        if (!fragment.onBackPressed()) {
            finish();
        }
    }
}
