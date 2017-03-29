package cwj.androidfilemanage.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cwj.androidfilemanage.R;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileDao;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.bean.SubItem;
import cwj.androidfilemanage.utils.FileUtil;
import cwj.androidfilemanage.view.CheckBox;


/**
 * Created by CWJ on 2017/3/22.
 */

public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int HEAD = 0;
    public static final int CONTENT = 1;
    private boolean isPhoto = false;

    public ExpandableItemAdapter(List<MultiItemEntity> data, boolean isPhoto) {
        super(data);
        this.isPhoto = isPhoto;
        addItemType(HEAD, R.layout.item_head);
        if (isPhoto) {
            addItemType(CONTENT, R.layout.item_content_photo);
        } else {
            addItemType(CONTENT, R.layout.item_content);
        }

    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case HEAD:
                final SubItem subItem = (SubItem) item;
                if (null == subItem.getSubItems() || subItem.getSubItems().size() == 0) {
                    helper.setText(R.id.tv_count, mContext.getString(R.string.count, "" + 0));
                } else {
                    helper.setText(R.id.tv_count, mContext.getString(R.string.count, "" + subItem.getSubItems().size()));
                }

                helper.setText(R.id.tv_title, subItem.getTitle());
                helper.setImageResource(R.id.expanded_menu, subItem.isExpanded() ? R.drawable.ic_arrow_drop_down_grey_700_24dp : R.drawable.ic_arrow_drop_up_grey_700_24dp);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (subItem.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case CONTENT:
                final FileInfo f = (FileInfo) item;
                helper.setText(R.id.tv_content, f.getFileName())
                        .setText(R.id.tv_size, FileUtil.FormetFileSize(f.getFileSize()))
                        .setText(R.id.tv_time, f.getTime());
                if (isPhoto) {
                    Glide.with(mContext).load(f.getFilePath()).into((ImageView) helper.getView(R.id.iv_cover));
                } else {
                    Glide.with(mContext).load(FileUtil.getFileTypeImageId(mContext, f.getFilePath())).fitCenter().into((ImageView) helper.getView(R.id.iv_cover));
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPhoto) {
                            boolean IsPhoto = f.getIsPhoto();
                            f.setIsPhoto(!IsPhoto);
                        } else {
                            f.setIsPhoto(false);
                        }

                        boolean isCheck = f.getIsCheck();
                        f.setIsCheck(!isCheck);
                        if (f.getIsCheck()) {
                            FileDao.insertFile(f);
                            ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, true);
                        } else {
                            FileDao.deleteFile(f);
                            ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, true);
                        }
                        EventBus.getDefault().post(new EventCenter<>(1, isPhoto));
                    }
                });
                break;
        }
    }
}
