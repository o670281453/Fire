package com.zhiyu.fire.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiyu.fire.R;
import com.zhiyu.fire.controller.FragmentController.ChangePasswordController;
import com.zhiyu.fire.controller.FragmentController.LoginFragmentController;
import com.zhiyu.fire.controller.FragmentController.OpenSourceController;
import com.zhiyu.fire.model.UserRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private MainActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        view.setClickable(true);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.SETTING);
        mActivity.invalidateOptionsMenu();
    }

    //初始化各个View
    private void initView(View view) {
        TextView tvName = (TextView) view.findViewById(R.id.setting_name);
        TextView tvId = (TextView) view.findViewById(R.id.setting_id);
        TextView tvDepId = (TextView) view.findViewById(R.id.setting_dep_id);
        TextView tvRank = (TextView) view.findViewById(R.id.setting_rank);
        String name = "姓名：" +  UserRequest.getsUser().getName();
        tvName.setText(name);
        String id = "消防编号：" + UserRequest.getsUser().getUsername();
        tvId.setText(id);
        String depId = "所属队站编号：" + String.valueOf(UserRequest.getsUser().getDepartmentID());
        tvDepId.setText(depId);
        String rank = "拥有权限：" + String.valueOf(UserRequest.getsUser().getRank());
        tvRank.setText(rank);
        CardView cvChangePassword = (CardView) view.findViewById(R.id.setting_change_password);
        CardView cvOpenSource = (CardView) view.findViewById(R.id.setting_open_source);
        CardView cvExitLogin = (CardView) view.findViewById(R.id.exit_login_button);
        cvChangePassword.setOnClickListener(v -> {
            if (mActivity.isFragmentExist(MainActivity.CHANGE_PASSWORD)) {
                mActivity.setFragment(mActivity.getFragment(MainActivity.CHANGE_PASSWORD));
            } else {
                ChangePasswordFragment fragment = new ChangePasswordFragment();
                ChangePasswordController controller = new ChangePasswordController();
                fragment.setIChangePassword(controller);
                mActivity.putFragment(MainActivity.CHANGE_PASSWORD, fragment);
                mActivity.setFragment(fragment);
            }
        });
        cvOpenSource.setOnClickListener(v -> {
            if (mActivity.isFragmentExist(MainActivity.OPEN_SOURCE_LICENSE)) {
                mActivity.setFragment(mActivity.getFragment(MainActivity.OPEN_SOURCE_LICENSE));
            } else {
                OpenSourceController controller = new OpenSourceController();
                OpenSourceLicenseFragment fragment = new OpenSourceLicenseFragment();
                fragment.setIOpenSource(controller);
                mActivity.putFragment(MainActivity.OPEN_SOURCE_LICENSE, fragment);
                mActivity.setFragment(fragment);
            }
        });
        cvExitLogin.setOnClickListener(v -> {
            mActivity.onBackPressed();
            UserRequest.setsUser(null);
            SharedPreferences.Editor editor = mActivity.getSharedPreferences(
                    mActivity.getString(R.string.user_file),
                    Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            if (mActivity.isFragmentExist(MainActivity.LOGIN)) {
                mActivity.setFragment(mActivity.getFragment(MainActivity.LOGIN));
            } else {
                LoginFragment loginFragment = new LoginFragment();
                LoginFragmentController controller = new LoginFragmentController();
                loginFragment.setFragmentLifeCycle(controller);
                loginFragment.setILoginInterface(controller);
                mActivity.putFragment(MainActivity.LOGIN, loginFragment);
                mActivity.setFragment(loginFragment);
            }
        });
    }

}