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

public class DocFragment extends BaseFragment {
    private RecyclerView rlv_doc;
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
        return R.layout.fragment_doc;
    }

    @Override
    public void initView() {
        rlv_doc = (RecyclerView) getActivity().findViewById(R.id.rlv_doc);
        fileInfos.clear();
        mEntityArrayList.clear();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("数据加载中");
        progressDialog.setCancelable(false);
        progressDialog.show();  //将进度条显示出来
//        List<Uri> m = new ArrayList<>();
//        m.add(MediaStore.Files.getContentUri("external"));
//        List<String> mList = new ArrayList<>();
//        mList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/QQfile_recv/");
//        fileInfos = FileUtil.getFilesInfo(mList, getActivity());/*FileUtil.queryFilerInfo(getActivity(), m, null, null);*/
//
//        SubItem wordItem = new SubItem("WORD");
//        SubItem excelItem = new SubItem("EXCEL");
//        SubItem pdfItem = new SubItem("PDF");
//        SubItem PPTItem = new SubItem("PPT");
//        SubItem textItem = new SubItem("TXT");
//        for (int j = 0; j < fileInfos.size(); j++) {
//
//            if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"doc", "docx", "dot"})) {
//                Log.e("SubItem", "SubItem::name::" + fileInfos.get(j).getFileName());
//                wordItem.addSubItem(fileInfos.get(j));
//            } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"xls"})) {
//                excelItem.addSubItem(fileInfos.get(j));
//            } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"pdf"})) {
//                pdfItem.addSubItem(fileInfos.get(j));
//            } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"ppt","pptx"})) {
//                PPTItem.addSubItem(fileInfos.get(j));
//            } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"txt"})) {
//                textItem.addSubItem(fileInfos.get(j));
//            }
//        }
//
//        mEntityArrayList.add(wordItem);
//        mEntityArrayList.add(excelItem);
//        mEntityArrayList.add(pdfItem);
//        mEntityArrayList.add(PPTItem);
//        mEntityArrayList.add(textItem);
        ReadDOCFile();
        rlv_doc.setLayoutManager(new LinearLayoutManager(getActivity()));
        mExpandableItemAdapter = new ExpandableItemAdapter(mEntityArrayList, false);
        rlv_doc.setAdapter(mExpandableItemAdapter);
    }

    private void ReadDOCFile() {
        List<File> m = new ArrayList<>();
        m.add(new File(Environment.getExternalStorageDirectory() + "/tencent/"));
        m.add(new File(Environment.getExternalStorageDirectory() + "/dzsh/"));
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
                                    SubItem wordItem = new SubItem("WORD");
                                    SubItem excelItem = new SubItem("EXCEL");
                                    SubItem pdfItem = new SubItem("PDF");
                                    SubItem PPTItem = new SubItem("PPT");
                                    SubItem textItem = new SubItem("TXT");
                                    for (int j = 0; j < fileInfos.size(); j++) {
                                        if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"doc", "docx", "dot"})) {
                                            wordItem.addSubItem(fileInfos.get(j));
                                        } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"xls"})) {
                                            excelItem.addSubItem(fileInfos.get(j));
                                        } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"pdf"})) {
                                            pdfItem.addSubItem(fileInfos.get(j));
                                        } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"ppt", "pptx"})) {
                                            PPTItem.addSubItem(fileInfos.get(j));
                                        } else if (FileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"txt"})) {
                                            textItem.addSubItem(fileInfos.get(j));
                                        }
                                    }

                                    mEntityArrayList.add(wordItem);
                                    mEntityArrayList.add(excelItem);
                                    mEntityArrayList.add(pdfItem);
                                    mEntityArrayList.add(PPTItem);
                                    mEntityArrayList.add(textItem);
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

    public static Observable<File> listFiles(final File f) {
        if (f.isDirectory()) {
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    return listFiles(file);
                }
            });
        } else {
            return Observable.just(f).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    return f.exists() && f.canRead() && FileUtil.checkSuffix(f.getAbsolutePath(), new String[]{"doc", "docx", "dot", "xls", "pdf", "ppt", "pptx", "txt"});
                }
            });
        }
    }
}
