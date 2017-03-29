package cwj.androidfilemanage.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.adapter.MultipleItem;
import cwj.androidfilemanage.adapter.MultipleItemQuickAdapter;
import cwj.androidfilemanage.base.baseActivity;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileDao;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.utils.FileUtil;
import cwj.androidfilemanage.view.CheckBox;
import cwj.androidfilemanage.view.DividerItemDecoration;

import static cwj.androidfilemanage.utils.FileUtil.fileFilter;
import static cwj.androidfilemanage.utils.FileUtil.getFileInfosFromFileArray;

public class SDCardActivity extends baseActivity {
    @Bind(R.id.rlv_sd_card)
    RecyclerView rlv_sd_card;
    @Bind(R.id.tv_path)
    TextView tv_path;
    @Bind(R.id.tv_all_size)
    TextView tv_all_size;
    @Bind(R.id.tv_send)
    TextView tv_send;
    private List<FileInfo> fileInfos = new ArrayList<>();
    private List<MultipleItem> mMultipleItems = new ArrayList<>();
    private MultipleItemQuickAdapter mAdapter;
    private File mCurrentPathFile = null;
    private File mSDCardPath = null;
    private String path;
//    private int mCurrentPosition = 0;

    @OnClick(R.id.iv_title_back)
    void iv_title_back() {
        if (mSDCardPath.getAbsolutePath().equals(mCurrentPathFile.getAbsolutePath())) {
            finish();
        } else {
            mCurrentPathFile = mCurrentPathFile.getParentFile();
            showFiles(mCurrentPathFile);
        }
    }

    @Bind(R.id.tv_title_middle)
    TextView tv_title_middle;

    @Override
    public void onEventComming(EventCenter var1) {

    }

    @Override
    public boolean isBindEventBusHere() {
        return false;
    }

    @Override
    public void initViewAndEvent() {
        tv_all_size.setText(getString(R.string.size, "0B"));
        tv_send.setText(getString(R.string.send, "0"));
        path = getIntent().getStringExtra("path");
        tv_title_middle.setText(getIntent().getStringExtra("name"));
        mSDCardPath = new File(path);
        rlv_sd_card.setLayoutManager(new LinearLayoutManager(this));
        rlv_sd_card.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.drawable.divide_line));
        mAdapter = new MultipleItemQuickAdapter(mMultipleItems);
        rlv_sd_card.setAdapter(mAdapter);
        showFiles(mSDCardPath);
        updateSizAndCount();
        rlv_sd_card.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (adapter.getItemViewType(position) == MultipleItem.FILE) {
                    boolean isCheck = fileInfos.get(position).getIsCheck();
                    fileInfos.get(position).setIsCheck(!isCheck);
                    if (fileInfos.get(position).getIsCheck()) {
                        FileDao.insertFile(fileInfos.get(position));
                        ((CheckBox) view.findViewById(R.id.cb_file)).setChecked(true, true);
                    } else {
                        FileDao.deleteFile(fileInfos.get(position));
                        ((CheckBox) view.findViewById(R.id.cb_file)).setChecked(false, true);
                    }
                    updateSizAndCount();
                } else {
                    showFiles(new File(fileInfos.get(position).getFilePath()));
                }

            }
        });
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
    public void onBackPressed() {
        if (mSDCardPath.getAbsolutePath().equals(mCurrentPathFile.getAbsolutePath())) {
            finish();
        } else {
            mCurrentPathFile = mCurrentPathFile.getParentFile();
            showFiles(mCurrentPathFile);
        }
    }

    private void showFiles(File folder) {
        mMultipleItems.clear();
        tv_path.setText(folder.getAbsolutePath());
        mCurrentPathFile = folder;
        File[] files = fileFilter(folder);
        if (null == files || files.length == 0) {
            mAdapter.setEmptyView(getEmptyView());
            Log.e("files", "files::为空啦");
        } else {
            fileInfos = getFileInfosFromFileArray(files);
            for (int i = 0; i < fileInfos.size(); i++) {
                if (fileInfos.get(i).isDirectory) {
                    mMultipleItems.add(new MultipleItem(MultipleItem.FOLD, fileInfos.get(i)));
                } else {
                    mMultipleItems.add(new MultipleItem(MultipleItem.FILE, fileInfos.get(i)));
                }

            }
            List<FileInfo> mList = FileDao.queryAll();
            for (int i = 0; i < fileInfos.size(); i++) {
                for (FileInfo fileInfo : mList) {
                    if (fileInfo.getFileName().equals(fileInfos.get(i).getFileName())) {
                        fileInfos.get(i).setIsCheck(true);
//                        mCurrentPosition = i;
                    }
                }
            }
//            rlv_sd_card.scrollToPosition(mCurrentPosition);
        }
        mAdapter.notifyDataSetChanged();
    }

    private View getEmptyView() {
        return getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rlv_sd_card.getParent(), false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sdcard;
    }


}
