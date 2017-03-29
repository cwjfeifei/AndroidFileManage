package cwj.androidfilemanage.fragment;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cwj.androidfilemanage.R;
import cwj.androidfilemanage.adapter.ExpandableItemAdapter;
import cwj.androidfilemanage.base.BaseFragment;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.bean.SubItem;
import cwj.androidfilemanage.utils.FileUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CWJ on 2017/3/21.
 */

public class OtherFragment extends BaseFragment {
    RecyclerView rlv_other;
    private List<FileInfo> fileInfos = new ArrayList<>();
    ExpandableItemAdapter mExpandableItemAdapter;
    private ArrayList<MultiItemEntity> mEntityArrayList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    public boolean isBindEventBusHere() {
        return true;
    }

    @Override
    public void onEventComming(EventCenter var1) {

    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_other;
    }

    @Override
    public void initView() {
        rlv_other = (RecyclerView) getActivity().findViewById(R.id.rlv_other);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("数据加载中");
        progressDialog.setCancelable(false);
        progressDialog.show();  //将进度条显示出来
        ReadOtherFile();
        rlv_other.setLayoutManager(new LinearLayoutManager(getActivity()));
        mExpandableItemAdapter = new ExpandableItemAdapter(mEntityArrayList, false);
        rlv_other.setAdapter(mExpandableItemAdapter);
    }

    private void ReadOtherFile() {
        List<File> m = new ArrayList<>();
        m.add(new File(Environment.getExternalStorageDirectory() + "/tencent/"));//微信QQ
        m.add(new File(Environment.getExternalStorageDirectory() + "/dzsh/"));//自定义
        Observable.from(m)
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return listFiles(file);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<File>() {
                            @Override
                            public void onCompleted() {
                                progressDialog.dismiss();
                                if (fileInfos.size() > 0) {
                                    SubItem ZipItem = new SubItem("ZIP文件");
                                    SubItem APPItem = new SubItem("APP文件");
                                    for (int j = 0; j < fileInfos.size(); j++) {
                                        if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"zip"})) {
                                            ZipItem.addSubItem(fileInfos.get(j));
                                        } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"apk"})) {
                                            APPItem.addSubItem(fileInfos.get(j));
                                        }
                                    }

                                    mEntityArrayList.add(ZipItem);
                                    mEntityArrayList.add(APPItem);
                                    mExpandableItemAdapter.setNewData(mEntityArrayList);
                                    mExpandableItemAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getActivity(), "sorry,没有读取到文件!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onNext(File file) {
                                FileInfo fileInfo = FileUtil.getFileInfoFromFile(file);
                                Log.e("文件路径", "文件路径：：：" + fileInfo.getFilePath());
                                fileInfos.add(fileInfo);

                            }
                        }
                );
    }

    /**
     * rxjava递归查询内存中的视频文件
     *
     * @param f
     * @return
     */
    public static Observable<File> listFiles(final File f) {
        if (f.isDirectory()) {
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**filter操作符过滤视频文件,是视频文件就通知观察者**/
            return Observable.just(f).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    return f.exists() && f.canRead() && FileUtil.checkSuffix(f.getAbsolutePath(), new String[]{"zip", "apk"});
                }
            });
        }
    }
}
