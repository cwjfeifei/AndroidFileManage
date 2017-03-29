package cwj.androidfilemanage.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cwj.androidfilemanage.R;
import cwj.androidfilemanage.utils.FileUtil;
import cwj.androidfilemanage.view.CheckBox;


/**
 * Created by CWJ on 2017/3/20.
 */

public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    public MultipleItemQuickAdapter(List<MultipleItem> data) {
        super(data);
        addItemType(MultipleItem.FOLD, R.layout.item_fold);
        addItemType(MultipleItem.FILE, R.layout.item_file);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        helper.setText(R.id.tv_file_name, item.getData().getFileName());
        if (item.getItemType() == MultipleItem.FOLD) {
//            helper.setImageResource(R.id.iv_file, R.drawable.rc_ad_list_folder_icon);
            Glide.with(mContext).load(R.drawable.rc_ad_list_folder_icon).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
        } else {
            helper.setText(R.id.tv_file_size, FileUtil.FormetFileSize(item.getData().getFileSize()));
            helper.setText(R.id.tv_file_time, item.getData().getTime());
            if (item.getData().getIsCheck()) {
                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, false);
            } else {
                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, false);
            }

            Glide.with(mContext).load(FileUtil.getFileTypeImageId(mContext, item.getData().getFileName())).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
//            helper.setImageResource(R.id.iv_file, FileUtil.getFileTypeImageId(mContext, item.getData().getFileName()));
        }
    }


}
