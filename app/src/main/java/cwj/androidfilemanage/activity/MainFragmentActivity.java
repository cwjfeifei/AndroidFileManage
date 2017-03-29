package cwj.androidfilemanage.activity;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.adapter.TabPagerAdapter;
import cwj.androidfilemanage.base.baseActivity;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileDao;
import cwj.androidfilemanage.fragment.AllMainFragment;
import cwj.androidfilemanage.fragment.LocalMainFragment;
import cwj.androidfilemanage.utils.FileUtil;

public class MainFragmentActivity extends baseActivity {
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    @Bind(R.id.main_viewpager)
    ViewPager main_viewpager;
    @Bind(R.id.main_top_rg)
    RadioGroup main_top_rg;
    @Bind(R.id.top_rg_a)
    RadioButton top_rg_a;
    @Bind(R.id.top_rg_b)
    RadioButton top_rg_b;

    @Override
    public void onEventComming(EventCenter var1) {
    }

    @Override
    public boolean isBindEventBusHere() {
        return false;
    }

    @Override
    public void initViewAndEvent() {
        //6.0权限适配
        requestReadAndWriteSDPermission(new baseActivity.PermissionHandler() {
            @Override
            public void onGranted() {
                Alerter.create(MainFragmentActivity.this)
                        .setTitle("通知")
                        .setText("谢谢您打开权限！")
                        .show();
            }
        });
        Log.e("cwj", "外置SD卡路径 = " + FileUtil.getStoragePath(this));
        Log.e("cwj", "内置SD卡路径 = " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.e("cwj", "手机内存根目录路径  = " + Environment.getDataDirectory().getParentFile().getAbsolutePath());
        fragments.add(new AllMainFragment());//全部
        fragments.add(new LocalMainFragment());//本机
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), mTitleList, fragments);
        main_viewpager.setAdapter(pagerAdapter);
        main_top_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == top_rg_a.getId()) main_viewpager.setCurrentItem(0);
                else if (checkedId == top_rg_b.getId()) main_viewpager.setCurrentItem(1);
            }
        });

        //设置默认选中页
        main_viewpager.setCurrentItem(0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileDao.deleteAll1();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("提示");
        builder.setMessage("留下再体验一下？");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("不要了", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.create().show();
    }
}
