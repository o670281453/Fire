package com.zhiyu.fire.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhiyu.fire.R;
import com.zhiyu.fire.model.UserRequest;

import java.util.Map;
import java.util.WeakHashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * 每个在MainActivity中显示的Fragment通过以下常量来标记，当Fragment添加到MainActivity顶层并和
     * 用户交互的时候，就把对应的常量值赋给status
     */
    public static final int MAIN = 0;
    public static final int BROWSER_FIRE_STATION = 1;
    public static final int BROWSER_MAINTAIN = 2;
    public static final int BROWSER_WATER_SOURCE = 3;
    public static final int ADD_FIRE_STATION = 4;
    public static final int ADD_MAINTAIN = 5;
    public static final int ADD_WATER_SOURCE = 6;
    public static final int SETTING = 7;
    public static final int CHANGE_PASSWORD = 8;
    public static final int OPEN_SOURCE_LICENSE = 9;
    public static final int LOGIN = 10;

    private int fragmentStatus;

    private Map<Integer, Fragment> fragmentWeakHashMap;//用于缓存Fragment的HashMap

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentWeakHashMap = new WeakHashMap<>();
        fragmentWeakHashMap.put(MAIN, getSupportFragmentManager().findFragmentById(R.id.main_fragment));
        toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (fragmentStatus == LOGIN && ((UserRequest.getsUser() == null
                || UserRequest.getsUser().getToken() == null))) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void setFragmentStatus(int fragmentStatus) {
        this.fragmentStatus = fragmentStatus;
    }

    //添加新Fragment到栈顶和用户交互
    void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_main_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //查找Fragment是否存在于HashMap中
    boolean isFragmentExist(int key) {
        return fragmentWeakHashMap.get(key) != null;
    }

    //从HashMap中取Fragment
    Fragment getFragment(int key) {
        return fragmentWeakHashMap.get(key);
    }

    //向HashMap中存入Fragment
    void putFragment(int key, Fragment fragment) {
        fragmentWeakHashMap.put(key, fragment);
    }

    //把Fragment从HashMap中移除
    public void removeFragment(int key) {
        fragmentWeakHashMap.remove(key);
    }

    //添加或移除返回按钮
    private void setBackButton(boolean ifSet) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(ifSet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.main_menu_button:
                switch (fragmentStatus) {
                    case MAIN:
                        if (isFragmentExist(SETTING)) {
                            setFragment(getFragment(SETTING));
                        } else {
                            SettingFragment fragment = new SettingFragment();
                            fragmentWeakHashMap.put(SETTING, fragment);
                            setFragment(fragment);
                        }
                        break;
                    case CHANGE_PASSWORD:
                        ChangePasswordFragment changePasswordFragment = (ChangePasswordFragment)
                                fragmentWeakHashMap.get(CHANGE_PASSWORD);
                        changePasswordFragment.change();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.main_menu_button);
        switch (fragmentStatus) {
            case MAIN:
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setTitle("消防地图");
                setBackButton(false);
                item.setVisible(true);
                item.setIcon(R.drawable.ic_settings_white_24dp);
                break;
            case BROWSER_FIRE_STATION:
                toolbar.setTitle("队站详情");
                setBackButton(true);
                item.setVisible(false);
                break;
            case BROWSER_WATER_SOURCE:
                toolbar.setTitle("水源详情");
                setBackButton(true);
                item.setVisible(false);
                break;
            case BROWSER_MAINTAIN:
                toolbar.setTitle("维护记录");
                item.setVisible(true);
                item.setIcon(R.drawable.ic_border_color_white_18dp);
                break;
            case SETTING:
                toolbar.setTitle("设置");
                setBackButton(true);
                item.setVisible(false);
                break;
            case ADD_WATER_SOURCE:
                toolbar.setTitle("添加新水源");
                setBackButton(true);
                item.setVisible(false);
                break;
            case ADD_MAINTAIN:
                toolbar.setTitle("维护水源");
                item.setVisible(false);
                break;
            case ADD_FIRE_STATION:
                toolbar.setTitle("添加新队站");
                setBackButton(true);
                item.setVisible(false);
                break;
            case CHANGE_PASSWORD:
                toolbar.setTitle("修改密码");
                item.setVisible(true);
                item.setIcon(R.drawable.ic_done_white_24dp);
                break;
            case OPEN_SOURCE_LICENSE:
                toolbar.setTitle("开源许可");
                item.setVisible(false);
                break;
            case LOGIN:
                toolbar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

}