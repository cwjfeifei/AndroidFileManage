package cwj.androidfilemanage.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import cwj.androidfilemanage.bean.EventCenter;


/**
 * Created by CWJ on 2016/10/8.
 */
public abstract class BaseFragment extends Fragment {

    protected View rootView;
    /**
     * 控件是否初始化完成
     */
    private boolean isViewCreated;
    /**
     * 数据是否已加载完毕
     */
    private boolean isLoadDataCompleted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, rootView);
        }

        isViewCreated = true;
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    protected abstract boolean isBindEventBusHere();

    protected abstract void onEventComming(EventCenter var1);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        if (getUserVisibleHint()) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    private void loadData() {
        Log.e("BaseFragment", "loadData()");
        initView();
    }

    //获取布局文件
    public abstract int getLayoutResource();

    //初始化view
    public abstract void initView();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        ButterKnife.unbind(this);
        if (this.isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            this.onEventComming(eventCenter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
