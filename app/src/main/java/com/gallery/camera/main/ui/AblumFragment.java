package com.gallery.camera.main.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by kachem on 2016/3/27.
 */
public class AblumFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init(){
        super.init();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(new PrivacyLockerAdapter(new ArrayList<String>()));
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener());
    }
}
