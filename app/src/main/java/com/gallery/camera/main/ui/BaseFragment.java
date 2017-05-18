package com.gallery.camera.main.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gallery.camera.framework.baseui.BaseActivity;
import com.gallery.camera.privacy.solomon.R;
import com.gallery.camera.framework.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by kachem on 2016/3/27.
 */
public class BaseFragment extends Fragment implements BaseActivity.ActivityListener {
    public static final int PICTURE_PAGER = 0;
    public static final int VIDEO_PAGER = 1;

    private String url = "https://s-media-cache-ak0.pinimg.com/736x/50/b4/be/50b4beaf75b47235d595d1818c73911b.jpg";

    protected int curpager;
    protected RecyclerView recyclerView;

    protected View view;

    private Point screen;
    private FragmentListener fragmentListener;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.privacy_layout, container, false);
        init();
        return view;
    }

    protected void init() {
        screen = DeviceUtils.getScreen();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        activity = (MainActivity) getActivity();
        activity.setActivityListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //此处为返回键功能
            case R.id.fab_menu:
                //Log.i("kachem","fabmenu from activity");
                break;
            //此处为解锁键
            case R.id.fab_add:
                Log.i("kachem","fabadd from activity");
                break;
            //此处为删除键或编辑键
            case R.id.fab_edit:
                Log.i("kachem","fabedit from activity");
                break;
        }
    }

    protected class PrivacyLockerAdapter extends RecyclerView.Adapter<PrivacyLockerAdapter.ViewHolder> {
        private ArrayList<String> list;

        public PrivacyLockerAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(
                    R.layout.layout_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Glide.with(getActivity()).load(url).asBitmap().centerCrop().into(holder.img);
        }

        @Override
        public int getItemCount() {
            return 120;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img, flagCheck;

            public ViewHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.img);
                flagCheck = (ImageView) itemView.findViewById(R.id.flag_check);
                int w;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                w = (screen.x - DeviceUtils.dp2px(24)) / 3;
                params.width = w;
                params.height = w;
                itemView.setLayoutParams(params);
            }
        }
    }

    protected class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        boolean flag = false; //标识是否已执行动画

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if (dy > 0 && !flag) {
                fragmentListener.onScrollhide();
                flag = true;
            } else if (dy < 0 && flag) {
                fragmentListener.onScrollShow();
                flag = false;
            }
        }

    }

    public void setFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    public interface FragmentListener {

        /**
         * 当recyclerView向上滚动时菜单按钮浮出
         */
        void onScrollShow();

        /**
         * 当recyclerView向下滚动时菜单按钮向下移动隐藏
         */
        void onScrollhide();
    }
}
