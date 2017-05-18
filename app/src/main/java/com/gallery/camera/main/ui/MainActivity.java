package com.gallery.camera.main.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.gallery.camera.camera.ui.CameraActivity;
import com.gallery.camera.framework.baseui.BaseActivity;
import com.gallery.camera.framework.custom.TestActivity;
import com.gallery.camera.framework.utils.DeviceUtils;
import com.gallery.camera.lollipop.LollipopActivity;
import com.gallery.camera.privacy.solomon.R;
import com.iobit.mobilecare.IConfigAIDL;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener,
        BaseFragment.FragmentListener {
    private ArrayList<BaseFragment> fragList;
    private String[] title = {"Photo", "Video"};
    private FloatingActionButton fabMenu, fabAdd, fabEdit;
    private FloatingActionButton fab1, fab2; //仅用于动画展示
    private boolean isOpen = false; //浮动按钮菜单是否打开

    private IConfigAIDL aidl;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidl = IConfigAIDL.Stub.asInterface(service);
            if (aidl != null) {
                try {
                    boolean allowShow = aidl.allowShow();
                    Toast.makeText(MainActivity.this, "allowShow-->" + allowShow, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    private void getData() {
        Intent intent = new Intent("com.iobit.mobilecare.chargingscreen.config");
        intent.setPackage("com.iobit.mobilecare");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
    }

    @Override
    protected void initViews() {
        fabMenu = (FloatingActionButton) findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        fabAdd.setOnClickListener(this);
        fabEdit.setOnClickListener(this);

        fabMenu.setOnTouchListener(this);
        fab1.setOnTouchListener(this);
        fab2.setOnTouchListener(this);

        fragList = new ArrayList<BaseFragment>();
        AblumFragment ablumFragment = new AblumFragment();
        VideoFragment videoFragment = new VideoFragment();
        fragList.add(ablumFragment);
        fragList.add(videoFragment);

        ablumFragment.setFragmentListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentPagerAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new PagerChangeListener());
        TabLayout tab = (TabLayout) findViewById(R.id.tab_layout);
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.addTab(tab.newTab().setText(title[0]));
        tab.addTab(tab.newTab().setText(title[1]));
        tab.setupWithViewPager(viewPager);
        tab.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shotcut:
                createCut();
                break;
            case R.id.set_city:

                break;
        }

        return true;
    }

    /**
     * 创建快捷方式
     */
    private void createCut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.desk_shot));
        shortcut.putExtra("duplicate", false);//不允许重复创建
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(mContext, CameraActivity.class.getName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(
                mContext, R.mipmap.shotcut);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        sendBroadcast(shortcut);
    }

    /**
     * 按钮展开和收缩的动画
     *
     * @param isShow true 展开  false 收缩
     */
    private void buttonAnim(boolean isShow) {
        isOpen = isShow;
        ObjectAnimator animatorAdd, animatorEdit, animatorMenu;
        if (isShow) {
            animatorAdd = ObjectAnimator.ofFloat(fabAdd, "translationY", DeviceUtils.dp2px(74), 0);
            animatorEdit = ObjectAnimator.ofFloat(fabEdit, "translationY", DeviceUtils.dp2px(149), 0);
            animatorMenu = ObjectAnimator.ofFloat(fabMenu, "rotation", 0, 90);
        } else {
            animatorAdd = ObjectAnimator.ofFloat(fabAdd, "translationY", 0, DeviceUtils.dp2px(74));
            animatorEdit = ObjectAnimator.ofFloat(fabEdit, "translationY", 0, DeviceUtils.dp2px(149));
            animatorMenu = ObjectAnimator.ofFloat(fabMenu, "rotation", 90, 0);
        }

        animatorAdd.setDuration(200);
        animatorAdd.setInterpolator(new LinearInterpolator());
        animatorAdd.addListener(new AnimatorListener(fabAdd, isShow));
        animatorAdd.start();

        animatorEdit.setDuration(200);
        animatorEdit.setInterpolator(new LinearInterpolator());
        animatorEdit.setStartDelay(50);
        animatorEdit.addListener(new AnimatorListener(fabEdit, isShow));
        animatorEdit.start();

        animatorMenu.setDuration(200);
        animatorMenu.setInterpolator(new LinearInterpolator());
        animatorMenu.addListener(new AnimatorListener(fabMenu, isShow));
        animatorMenu.start();
    }

    @Override
    public void onClick(View v) {
        activityListener.onClick(v); //向fragment中传递事件

        switch (v.getId()) {
            case R.id.fab_add:
                //buttonAnim(false);
                startActivity(new Intent(this, TestActivity.class));
                break;
            case R.id.fab_edit:
                //buttonAnim(false);
                startActivity(new Intent(this, LollipopActivity.class));
                break;
        }
    }

    private float xDown;
    private float yDown;
    private float xTouch;
    private float yTouch;
    private float preX, preY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        fab1.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOpen)
                    break;
                xTouch = event.getRawX();
                yTouch = event.getRawY();
                updateView((xTouch - xDown), (yTouch - yDown));
                break;
            case MotionEvent.ACTION_UP:
                if ((xDown - event.getRawX() < 10) && (yDown - event.getRawY()) < 10) {
                    if (!isOpen) {
                        buttonAnim(true);
                    } else {
                        buttonAnim(false);
                    }
                }
                updateView(0, 0);
                preX = 0;
                preY = 0;
                break;
        }
        return true;
    }

    /**
     * 拖动动画的实时刷新
     *
     * @param leftX
     * @param leftY
     */
    private void updateView(float leftX, float leftY) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(fabMenu, "translationX", preX, leftX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(fabMenu, "translationY", preY, leftY);
        ObjectAnimator animator1X = ObjectAnimator.ofFloat(fab1, "translationX", preX, leftX);
        ObjectAnimator animator1Y = ObjectAnimator.ofFloat(fab1, "translationY", preY, leftY);
        ObjectAnimator animator2X = ObjectAnimator.ofFloat(fab2, "translationX", preX, leftX);
        ObjectAnimator animator2Y = ObjectAnimator.ofFloat(fab2, "translationY", preY, leftY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();

        AnimatorSet animator1Set = new AnimatorSet();
        animator1Set.playTogether(animator1X, animator1Y);
        animator1Set.setStartDelay(200);
        animator1Set.start();

        AnimatorSet animator2Set = new AnimatorSet();
        animator2Set.playTogether(animator2X, animator2Y);
        animator2Set.setStartDelay(100);
        animator2Set.start();
        preX = leftX;
        preY = leftY;
    }

    @Override
    public void onScrollShow() {
        if (!isOpen)  //当按钮展开时 没有隐藏 也没必要浮出
            startMenuAnim(500, 0, true);
    }

    @Override
    public void onScrollhide() {
        if (fab1.isShown()) {
            fab1.setVisibility(View.GONE);
        }

        if (fab2.isShown()) {
            fab2.setVisibility(View.GONE);
        }

        //当按钮展开时 不做处理
        if (!isOpen) {
            startMenuAnim(0, 500, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (isOpen) {
            buttonAnim(false);
        } else {
            super.onBackPressed();
        }

    }

    /**
     * 开始右下角菜单位置按钮的上下滑动显示隐藏的动画
     *
     * @param fromY
     * @param toY
     * @param isDown true为向下隐去 false为向上浮出
     */
    private void startMenuAnim(float fromY, float toY, final boolean isDown) {
        ObjectAnimator animatorHide = ObjectAnimator.ofFloat(fabMenu, "translationY", fromY, toY);
        animatorHide.setDuration(500);
        animatorHide.setInterpolator(new LinearInterpolator());
        animatorHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isDown) {
                    fabMenu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isDown) {
                    fabMenu.setVisibility(View.GONE);
                }
                fabMenu.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorHide.start();
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

    }

    class PagerChangeListener implements ViewPager.OnPageChangeListener {
        int screenX = DeviceUtils.getScreen().x;
        float preDegree, curDegree;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            curDegree = 720 * positionOffsetPixels / screenX;
            if (preDegree - curDegree > 200)
                return;
            ObjectAnimator cutAnim = ObjectAnimator.ofFloat(fabMenu, "rotation", preDegree,
                    curDegree);
            cutAnim.start();
            if (isOpen) {
                ObjectAnimator addAnim = ObjectAnimator.ofFloat(fabAdd, "rotation", preDegree,
                        curDegree);
                ObjectAnimator editAnim = ObjectAnimator.ofFloat(fabEdit, "rotation", preDegree,
                        curDegree);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(addAnim, editAnim);
                set.start();
            }
            preDegree = curDegree;
        }

        @Override
        public void onPageSelected(int position) {
            Log.i("kachem", position + "");
            fabMenu.clearAnimation();
            preDegree = 0;
            curDegree = 0;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class AnimatorListener implements Animator.AnimatorListener {
        private View view;
        private boolean isShow;

        /**
         * 创建add edit按钮位置的动画监听
         *
         * @param view   指定需要设置动画的控件
         * @param isShow true为向上浮现 false为向下隐藏
         */
        public AnimatorListener(View view, boolean isShow) {
            this.view = view;
            this.isShow = isShow;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (isShow)
                view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!isShow && view.getId() != R.id.fab_menu) {
                view.setVisibility(View.GONE);
            }
            view.clearAnimation();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
