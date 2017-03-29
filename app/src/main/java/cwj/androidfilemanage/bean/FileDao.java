package cwj.androidfilemanage.bean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cwj.androidfilemanage.base.BaseApplication;

/**
 * Created by CWJ on 2017/3/20.
 */

public class FileDao {
    /**
     * 添加数据，如果有重复则覆盖
     */
    public static void insertFile(FileInfo fileInfo) {
        BaseApplication.getDaoInstant().getFileInfoDao().insertOrReplace(fileInfo);
    }

    /**
     * 删除数据
     */
    public static void deleteFile(FileInfo fileInfo) {
        BaseApplication.getDaoInstant().getFileInfoDao().delete(fileInfo);
    }

    /**
     * 更新数据
     */
    public static void updateFile(FileInfo fileInfo) {
        BaseApplication.getDaoInstant().getFileInfoDao().update(fileInfo);
    }


    /**
     * 查询全部数据
     */
    public static List<FileInfo> queryAll() {
        return BaseApplication.getDaoInstant().getFileInfoDao().loadAll();

    }

    /**
     * 删除全部数据
     */
    public static void deleteAll1() {
        BaseApplication.getDaoInstant().getFileInfoDao().deleteAll();
    }

    public static boolean isContain(String ID) {
        QueryBuilder<FileInfo> qb = BaseApplication.getDaoInstant().getFileInfoDao().queryBuilder();
        qb.where(FileInfoDao.Properties.FileName.eq(ID));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
}
