package cwj.androidfilemanage.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cwj.androidfilemanage.R;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.fragment.ImagePreviewFragment;
import cwj.androidfilemanage.utils.SystemUtil;
import cwj.androidfilemanage.view.CustomViewPager;

/**
 * 图片预览界面
 * Created by CWJ on 2017/3/28.
 */

public class ImagePreviewActivity extends AppCompatActivity {
    private LinearLayout barLayout;
    private boolean isShowBar = true;
    private Toolbar toolbar;
    private CustomViewPager viewPager;
    private int position = 0;
    private List<FileInfo> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_preview);
        initViewAndEvent();
        registerListener();
    }

    public void initViewAndEvent() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().findViewById(android.R.id.content)
                .setPadding(0, 0, 0, SystemUtil.getNavigationBarHeight(this));
        images = (List<FileInfo>) getIntent().getSerializableExtra("FileInfo");
        barLayout = (LinearLayout) findViewById(R.id.bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((position + 1) + "/" + images.size());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        viewPager = (CustomViewPager) findViewById(R.id.preview_pager);
        viewPager.setAdapter(new SimpleFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(position);
        viewPager.setIsPagingEnabled(true);
        registerListener();
    }

    public class SimpleFragmentAdapter extends FragmentPagerAdapter {
        ImagePreviewFragment mFragment;

        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mFragment = ImagePreviewFragment.getInstance(images.get(position).getFilePath());
            return mFragment;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    public void registerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(position + 1 + "/" + images.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     *显示和隐藏标题栏和状态栏
     */
    public void switchBarVisibility() {
        barLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        toolbar.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        if (isShowBar) {
            hideStatusBar();
        } else {
            showStatusBar();
        }
        isShowBar = !isShowBar;
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }
}
