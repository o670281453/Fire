package com.zhiyu.fire.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zhiyu.fire.R;
import com.zhiyu.fire.util.DialogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    private MainActivity mActivity;

    private IChangePassword mIChangePassword;

    private EditText etPassword;
    private EditText etPasswordAgain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        view.setClickable(true);
        etPassword = (EditText) view.findViewById(R.id.change_input_password);
        etPasswordAgain = (EditText) view.findViewById(R.id.change_input_password_again);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.CHANGE_PASSWORD);
        mActivity.invalidateOptionsMenu();
    }

    public void setIChangePassword(IChangePassword mIChangePassword) {
        this.mIChangePassword = mIChangePassword;
    }

    private boolean validate(String password, String passwordAgain) {
        if (password.length() > 16 || password.length() < 8) {
            DialogUtil.showDialog(mActivity, "密码应在8至16位之间");
            return false;
        }
        if (!password.equals(passwordAgain)) {
            DialogUtil.showDialog(mActivity, "两次输入密码不一致");
            return false;
        }
        return true;
    }

    public void change() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setMessage("您确定要修改密码吗")
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> {
                    String password = etPassword.getText().toString().trim();
                    String passwordAgain = etPasswordAgain.getText().toString().trim();
                    if (validate(password, passwordAgain)) {
                        if (mIChangePassword != null) {
                            mIChangePassword.change(mActivity, password);
                        }
                    }
                });
        builder.create().show();
    }

    public interface IChangePassword {
        void change(AppCompatActivity activity, String password);
    }

}