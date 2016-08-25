package geminihao.com.androidutils.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SDCard 工具类
 * Created by user on 2016/7/28.
 */
public class SDcardUtils {
    /**
     * 检查SDCard是否存在
     *
     * @return
     */
    public static boolean checkSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;

    }

    /**
     * 检查SDCard是否可读
     *
     * @return
     */
    public static boolean checkSDCardRead() {
        if (checkSDCardPresent())
            return Environment.getExternalStorageDirectory().canRead();
        else
            return false;
    }

    /**
     * 检查SDCard是否可写
     *
     * @return
     */
    public static boolean checkSDCardWriter() {
        if (checkSDCardPresent())
            return Environment.getExternalStorageDirectory().canWrite();
        else
            return false;
    }

    /**
     * 检查sdcard的剩余容量是否超过size
     *
     * @param size
     *            单位是KB
     * @return
     */
    public static boolean checkSDCardCapacity(int size) {
        // 取得sdcard文件路径
        File pathFile = Environment.getExternalStorageDirectory();
        StatFs statfs = new StatFs(pathFile.getPath());
        // 获取SDCard上每个block的SIZE
        long blocSize = statfs.getBlockSize();
        // 获取可供程序使用的Block的数量
        long availaBlock = statfs.getAvailableBlocks();
        if ((availaBlock * blocSize / 1024) > size)
            return true;
        else
            return false;
    }

    /**
     * 检查sdcard中是否存在指定路径的文件
     *
     * @param path
     * @return
     */
    public static boolean checkSDCardFile(String path) {
        if (path == null || "".equals(path.trim()))
            return false;
        File file = new File(path);
        return file.exists();
    }
}
