package cwj.androidfilemanage.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import cwj.androidfilemanage.bean.FileInfo;


/**
 * Created by CWJ on 2017/3/20.
 */

public class MultipleItem implements MultiItemEntity {
    public static final int FOLD = 1;
    public static final int FILE = 2;
    private int itemType;
    private FileInfo data ;

    public MultipleItem(int itemType, FileInfo data) {
        this.data = data;
        this.itemType = itemType;
    }

    public FileInfo getData() {
        return data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
