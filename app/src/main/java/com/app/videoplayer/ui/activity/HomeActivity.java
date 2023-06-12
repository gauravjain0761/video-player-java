package com.app.videoplayer.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.videoplayer.R;
import com.app.videoplayer.databinding.ActivityHomeBinding;
import com.app.videoplayer.ui.fragment.FragmentPlayer;
import com.app.videoplayer.ui.fragment.FragmentSongs;
import com.app.videoplayer.utils.Constants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public static ActivityHomeBinding bindingHome;
    String TAG = HomeActivity.class.getSimpleName();
    ArrayList<String> tabsList = new ArrayList<>();

    public static FragmentPlayer fragmentPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        bindingHome = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bindingHome.getRoot());
        bindingHome.toolbar.setTitle("");
        setSupportActionBar(bindingHome.toolbar);

        try {
            fragmentPlayer = FragmentPlayer.class.newInstance();
            createTabsList();

            bindingHome.viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), tabsList.size()));

            setupPlayScreenFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTabsList() {
        try {
            if (tabsList != null) {
                tabsList.clear();
                tabsList.add(Constants.TAB_SONGS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.mNumOfTabs = tabCount;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentSongs();
                default:
                    return null;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            startActivityForResult(new Intent(this, TrashedSongsActivity.class), 101);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupPlayScreenFragment() {
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, fragmentPlayer, "FragmentPlayer").commitAllowingStateLoss();
            bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}