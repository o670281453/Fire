package com.zhiyu.fire.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zhiyu.fire.R;
import com.zhiyu.fire.util.DialogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private MainActivity mActivity;

    private EditText etUserId;
    private EditText etPassword;

    private FragmentLifeCycle mFragmentLifeCycle;

    private ILoginInterface mILoginInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.setClickable(true);
        etUserId = (EditText) view.findViewById(R.id.login_account);
        etPassword = (EditText) view.findViewById(R.id.login_password);
        Button bLogin = (Button) view.findViewById(R.id.login_button);
        bLogin.setOnClickListener(v -> login());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.LOGIN);
        mActivity.invalidateOptionsMenu();
        if (isFragmentLifeCycle()) {
            mFragmentLifeCycle.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isFragmentLifeCycle()) {
            mFragmentLifeCycle.onStop();
        }
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private boolean isFragmentLifeCycle() {
        return mFragmentLifeCycle != null;
    }

    public void setFragmentLifeCycle(FragmentLifeCycle mFragmentLifeCycle) {
        this.mFragmentLifeCycle = mFragmentLifeCycle;
    }

    public void setILoginInterface(ILoginInterface mILoginInterface) {
        this.mILoginInterface = mILoginInterface;
    }

    private void login() {
        final String userId = etUserId.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if (validate(userId, password)) {
            if (mILoginInterface != null) {
                mILoginInterface.login(mActivity, userId, password);
            }
        }
    }

    private boolean validate(String userId, String password) {
        if (userId.length() == 0) {
            DialogUtil.showDialog(mActivity, "消防编号不能为空");
            return false;
        }
        if (password.length() > 16 || password.length() < 8) {
            DialogUtil.showDialog(mActivity, "输入密码不正确");
            return false;
        }
        return true;
    }

    public interface ILoginInterface {
        void login(AppCompatActivity activity, String userId, String password);
    }

}