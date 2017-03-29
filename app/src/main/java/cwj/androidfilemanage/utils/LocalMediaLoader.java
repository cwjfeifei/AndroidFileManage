package cwj.androidfilemanage.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.bean.FolderInfo;

/**
 * Created by CWJ on 2017/3/27.
 */

public class LocalMediaLoader {
    public static final int TYPE_IMAGE = 1;
    private int type = TYPE_IMAGE;
    private FragmentActivity activity;
    private final static String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
    };

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }


    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == TYPE_IMAGE) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?" + " or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png", "image/gif"}, IMAGE_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                try {
                    ArrayList<FolderInfo> imageFolders = new ArrayList<FolderInfo>();
                    if (data != null) {
                        int count = data.getCount();
                        if (count > 0) {
                            data.moveToFirst();
                            do {
                                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                                if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                                    continue;
                                }
                                FileInfo fileInfo = FileUtil.getFileInfoFromFile(new File(path));
                                FolderInfo folder = getImageFolder(path, imageFolders);
                                folder.getImages().add(fileInfo);
                            } while (data.moveToNext());
                            Collections.sort(imageFolders, new FileNameComparator());
                            imageLoadListener.loadComplete(imageFolders);
                            data.close();
                        } else {
                            // 如果没有相册
                            imageLoadListener.loadComplete(imageFolders);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    /**
     * 根据文件名进行比较排序
     */
    public static class FileNameComparator implements Comparator<FolderInfo> {
        @Override
        public int compare(FolderInfo lhs, FolderInfo rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    private FolderInfo getImageFolder(String path, List<FolderInfo> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (FolderInfo folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        FolderInfo newFolder = new FolderInfo();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        imageFolders.add(newFolder);
        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<FolderInfo> folders);
    }
}
