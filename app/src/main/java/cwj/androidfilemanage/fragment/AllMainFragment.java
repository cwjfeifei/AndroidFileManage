package cwj.androidfilemanage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;

import com.tapadoo.alerter.Alerter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.activity.SDCardActivity;
import cwj.androidfilemanage.base.BaseFragment;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileDao;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.utils.FileUtil;
import cwj.androidfilemanage.utils.SystemUtil;

/**
 * Created by CWJ on 2017/3/28.
 */

public class AllMainFragment extends BaseFragment {
    @Bind(R.id.tv_all_size)
    TextView tv_all_size;
    @Bind(R.id.tv_send)
    TextView tv_send;


    @OnClick(R.id.rl_mobile_memory)
    void rl_mobile_memory() {
        Intent intent = new Intent(getActivity(), SDCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", Environment.getDataDirectory().getParentFile().getAbsolutePath());
        bundle.putString("name", "手机内存");
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @OnClick(R.id.rl_extended_memory)
    void rl_extended_memory() {
        if (checkExtentEnvironment()) {
            Intent intent = new Intent(getActivity(), SDCardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path", FileUtil.getStoragePath(getActivity()));
            bundle.putString("name", "扩展卡内存");
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Alerter.create(getActivity())
                    .setTitle("通知")
                    .setText("您手机没有外置SD卡！")
                    .show();
        }
    }

    @OnClick(R.id.rl_sd_card)
    void rl_sd_card() {

        if (checkSDEnvironment()) {
            Intent intent = new Intent(getActivity(), SDCardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path", Environment.getExternalStorageDirectory().getAbsolutePath());
            bundle.putString("name", "SD卡");
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Alerter.create(getActivity())
                    .setTitle("通知")
                    .setText("您手机没有内置SD卡！")
                    .show();
        }

    }

    private boolean checkSDEnvironment() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        return sdCardExist;

    }

    private boolean checkExtentEnvironment() {
        if (checkSDEnvironment() && TextUtils.isEmpty(FileUtil.getStoragePath(getActivity()))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onEventComming(EventCenter var1) {

    }

    @Override
    public boolean isBindEventBusHere() {
        return false;
    }


    public void updateSizAndCount() {
        List<FileInfo> mList = FileDao.queryAll();
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


    @Override
    public int getLayoutResource() {
        return R.layout.fragment_main_all;
    }

    @Override
    public void initView() {

        tv_all_size.setText(getString(R.string.size, "0B"));
        tv_send.setText(getString(R.string.send, "0"));
        SystemUtil.init(getActivity());
        updateSizAndCount();

    }

}
