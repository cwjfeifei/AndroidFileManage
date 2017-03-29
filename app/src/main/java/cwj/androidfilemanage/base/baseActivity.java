package cwj.androidfilemanage.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.utils.StatusBarUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by CWJ on 2017/3/17.
 */
@RuntimePermissions
public abstract class baseActivity extends AppCompatActivity {
    private PermissionHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        StatusBarUtil.setTranslucentStatus(this, R.color.color_48baf3);
        if (this.isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        this.initViewAndEvent();
    }

    public abstract void onEventComming(EventCenter var1);

    public abstract boolean isBindEventBusHere();

    //初始化view
    public abstract void initViewAndEvent();

    //获取布局文件
    public abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            this.onEventComming(eventCenter);
        }

    }

    /**
     * 权限回调接口
     */
    public abstract class PermissionHandler {
        /**
         * 权限通过
         */
        public abstract void onGranted();

        /**
         * 权限拒绝
         */
        public void onDenied() {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        baseActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    //-----------------------------------------------------------

    /**
     * 请求读写SD卡权限
     *
     * @param permissionHandler
     */
    protected void requestReadAndWriteSDPermission(PermissionHandler permissionHandler) {
        this.mHandler = permissionHandler;
        baseActivityPermissionsDispatcher.handleReadAndWriteSDPermissionWithCheck(this);
    }


    @NeedsPermission(value = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void handleReadAndWriteSDPermission() {
        if (mHandler != null)
            mHandler.onGranted();
    }

    @OnPermissionDenied(value = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void deniedReadAndWriteSDPermission() {
        if (mHandler != null)
            mHandler.onDenied();
    }

    @OnNeverAskAgain(value = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void OnReadAndWriteSDNeverAskAgain() {
        showDialog("[存储空间]");
    }

    public void showDialog(String permission) {
        new AlertDialog.Builder(this).setTitle("权限申请").setMessage("在设置-应用-大众生活商家版-权限中开启" + permission + "权限，以正常使用大众生活商家版功能").setPositiveButton("去开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mHandler != null)
                    mHandler.onDenied();
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }
}
