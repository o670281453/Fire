package com.zhiyu.fire.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhiyu.fire.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWaterSourceFragment extends Fragment {

    private MainActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_water_source, container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.ADD_WATER_SOURCE);
        mActivity.invalidateOptionsMenu();
    }

}