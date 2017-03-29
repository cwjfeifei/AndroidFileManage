package cwj.androidfilemanage.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.activity.ImagePreviewActivity;
import cwj.androidfilemanage.adapter.TabPagerAdapter;
import cwj.androidfilemanage.base.BaseFragment;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileDao;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.utils.FileUtil;
import cwj.androidfilemanage.utils.SystemUtil;

/**
 * Created by CWJ on 2017/3/28.
 */

public class LocalMainFragment extends BaseFragment {
    @Bind(R.id.tl_myfile)
    TabLayout mTabLayout;
    @Bind(R.id.vp_myfile)
    ViewPager mViewPager;
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    @Bind(R.id.tv_all_size)
    TextView tv_all_size;
    @Bind(R.id.tv_send)
    TextView tv_send;
    @Bind(R.id.tv_preview)
    TextView tv_preview;
    List<FileInfo> mListphoto = new ArrayList<>();

    @OnClick(R.id.tv_preview)
    void tv_preview() {
        if (mListphoto.size() != 0) {
            Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
            intent.putExtra("FileInfo", (ArrayList) mListphoto);
            startActivity(intent);
        }
    }

    @Override
    public void onEventComming(EventCenter var1) {
        switch (var1.getEventCode()) {
            case 1:
                updateSizAndCount();
                break;
            case 2:
                if ((int) var1.getData() == 1) {//当前为相册fragment显示预览
                    tv_preview.setVisibility(View.VISIBLE);
                } else {
                    tv_preview.setVisibility(View.GONE);
                }
                break;
        }


    }

    @Override
    public boolean isBindEventBusHere() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_main_local;
    }

    @Override
    public void initView() {

        tv_all_size.setText(getString(R.string.size, "0B"));
        tv_send.setText(getString(R.string.send, "0"));
        updateSizAndCount();
        mTitleList.add("影音");
        mTitleList.add("图片");
        mTitleList.add("文档");
        mTitleList.add("其他");

        fragments.add(new AVFragment());
        fragments.add(new PhotoFragment());
        fragments.add(new DocFragment());
        fragments.add(new OtherFragment());
        TabPagerAdapter mAdapter = new TabPagerAdapter(getChildFragmentManager(), mTitleList, fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
                EventBus.getDefault().post(new EventCenter<>(2, tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //反射修改宽度
        setUpIndicatorWidth(mTabLayout);
    }

    public void updateSizAndCount() {
        mListphoto.clear();
        List<FileInfo> mList = FileDao.queryAll();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getIsPhoto()) {
                mListphoto.add(mList.get(i));
            }
        }
        if (mListphoto.size() == 0) {
            tv_preview.setBackgroundResource(R.drawable.shape_bt_send);
            tv_preview.setTextColor(getResources().getColor(R.color.md_grey_700));
        } else {
            tv_preview.setBackgroundResource(R.drawable.shape_bt_send_blue);
            tv_preview.setTextColor(getResources().getColor(R.color.md_white_1000));
        }
        if (mList.size() == 0) {
            tv_send.setBackgroundResource(R.drawable.shape_bt_send);
            tv_send.setTextColor(getResources().getColor(R.color.md_grey_700));
            tv_all_size.setText(getString(R.string.size, "0B"));
        } else {
            tv_send.setBackgroundResource(R.drawable.shape_bt_send_blue);
            tv_send.setTextColor(getResources().getColor(R.color.md_white_1000));
            long count = 0L;
            for (int i = 0; i < mList.size(); i++) {
                count = count + mList.get(i).getFileSize();
            }
            tv_all_size.setText(getString(R.string.size, FileUtil.FormetFileSize(count)));
        }
        tv_send.setText(getString(R.string.send, "" + mList.size()));
    }

    private void setUpIndicatorWidth(TabLayout mTabLayout) {
        Class<?> tabLayoutClass = mTabLayout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout layout = null;
        try {
            if (tabStrip != null) {
                layout = (LinearLayout) tabStrip.get(mTabLayout);
            }
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(SystemUtil.dp(30f));
                    params.setMarginEnd(SystemUtil.dp(30f));
                }
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
