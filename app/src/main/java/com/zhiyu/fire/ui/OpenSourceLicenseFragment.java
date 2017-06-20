package com.zhiyu.fire.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhiyu.fire.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenSourceLicenseFragment extends Fragment {

    private MainActivity mActivity;

    private IOpenSource mIOpenSource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_source_license, container, false);
        view.setClickable(true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.open_source_recycler_view);
        if (mIOpenSource != null) {
            mIOpenSource.initData(mActivity, recyclerView);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.OPEN_SOURCE_LICENSE);
        mActivity.invalidateOptionsMenu();
    }

    public void setIOpenSource(IOpenSource mIOpenSource) {
        this.mIOpenSource = mIOpenSource;
    }

    public interface IOpenSource {
        //初始化数据，然后构建Adapter
        void initData(AppCompatActivity activity, RecyclerView recyclerView);
    }

}