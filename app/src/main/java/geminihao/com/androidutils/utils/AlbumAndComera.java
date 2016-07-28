package geminihao.com.androidutils.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 相册和拍照
 */
public class AlbumAndComera {
    public static String imagePath = "";

    public static String getComeraPath(Context mContext, File tempFile) {
        imagePath = "";
        Bitmap bitmap = null;
        try {
            if (tempFile.length() > 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = fitSizeImg(tempFile.length());
                if (options.inSampleSize == 1) {
                    bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                } else {
                    bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
                }

                if (bitmap.getWidth() > 640 && bitmap.getHeight() > 960) {
                    bitmap = ImageOperate.zoomImage(bitmap);
                }
                bitmap.compress(CompressFormat.JPEG, 80, new FileOutputStream(tempFile));
                imagePath = tempFile.getAbsolutePath();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "图片路径不正确";
        } catch (Exception e) {
            e.printStackTrace();
            return "空间不足！";
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return imagePath;
    }

    public static String getAlbumPath(Context mContext, Intent data) {
        imagePath = "";
        if (data == null) {
            return imagePath;
        }
        Uri uri = data.getData();
        if (!isImage(uri.toString())) {
            return "图片格式不正确";
        }
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            is = contentResolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            String filePath = convertStream2File(is);
            options.inSampleSize = fitSizeImg(new File(filePath).length());
            if (options.inSampleSize == 1) {
                bitmap = BitmapFactory.decodeFile(filePath);
            } else {
                bitmap = BitmapFactory.decodeFile(filePath, options);
            }
            if (bitmap.getWidth() > 640 && bitmap.getHeight() > 960) {
                bitmap = ImageOperate.zoomImage(bitmap);
            }
            bitmap.compress(CompressFormat.JPEG, 80, new FileOutputStream(filePath));
            imagePath = filePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            imagePath = "图片路径不正确";
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            imagePath = "图片尺寸太大";
        } catch (Exception e) {
            e.printStackTrace();
            return "空间不足！";
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    /**
     * 将inputstream保存至文件中
     *
     * @param is
     * @return返回文件路径
     */
    public static String convertStream2File(InputStream is) {
        File file = getTempPath();
        final String filePath = file.getAbsolutePath();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            CopyStream(is, fos);
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static boolean isImage(String url) {
        if (StringUtils.isNullOrEmpty(url)) {
            return false;
        }
        if (url.indexOf("images") > 0) {
            return true;
        } else {
            if (url.endsWith(".jpg") || url.endsWith(".png")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取路径
     *
     * @return
     */
    public static File getTempPath() {
        File tempFile = null;
        if (checkSDCard()) {
            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/app/cache/pic_cache");
            tempFile = new File(dirFile, System.currentTimeMillis() + ".jpg");
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
        }
        return tempFile;
    }

    /**
     * 获取图片路径
     *
     * @param data
     * @param file
     * @return
     */
    public static String ImagePathResult(Intent data, File file) {
        imagePath = "";
        FileOutputStream fos = null;
        Bitmap b = null;
        try {
            Bundle extras = data.getExtras();
            if (extras != null) {
                b = extras.getParcelable("data");
            }
            fos = new FileOutputStream(file);
            if (fos != null) {
                b.compress(CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePath = file.getAbsolutePath();
        return imagePath;
    }

    /**
     * 检查sd卡
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 按图片大小(字节大小)压缩图片
     *
     * @param length
     * @return
     */
    public static int fitSizeImg(long length) {
        // 数字越大读出的图片占用的heap越小 不然总是溢出
        if (length < 1024 * 600) { // 0-50k
            return 1;
        } else {
            return (int) (length / (1024 * 600)) + 1;
        }
    }
    public static final int CHOOSE_CUT = 106;// 剪切
    /**
     * 裁剪图片
     *
     * @param uri
     */
    public static void getImageClipIntent(Uri uri, Activity activity) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面
            intent.putExtra("outputX", 225);//设置为225*225
            intent.putExtra("outputY", 300);
            intent.putExtra("aspectX", 3);// 这两项为裁剪框的比例
            intent.putExtra("aspectY", 4);// x:y=1:1
            // 如果不设置aspectX和aspectY，则可以以任意比例缩放
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
            intent.putExtra("noFaceDetection", true);// 是否开启人脸识别功能

            activity.startActivityForResult(intent, CHOOSE_CUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 剪切图片 剪辑比例(调用系统)
     *
     * @param uri
     * @param activity
     * @param x
     * @param y
     */
    public static void getImageClipIntentBySystem(Uri uri, Activity activity,
                                                  int x, int y) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面
            intent.putExtra("outputX", 225);// 设置为225*225
            intent.putExtra("outputY", 300);
            intent.putExtra("aspectX", x);// 这两项为裁剪框的比例
            intent.putExtra("aspectY", y);// x:y=1:1
            // 如果不设置aspectX和aspectY，则可以以任意比例缩放
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
            intent.putExtra("noFaceDetection", true);// 是否开启人脸识别功能
            activity.startActivityForResult(intent, CHOOSE_CUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 剪切图片 剪辑比例(调用系统)
     *
     * @param uri
     * @param activity
     * @param x
     * @param y
     * @param is       系统为19 以上并且图片路径以 "content:" 开头的情况,需要对图片路径进行单独处理时候 is 为true
     *                 其他情况下 is 均为false(代码已经进行处理is可以不需要赋值)
     */
    public static void getImageClipIntentBySystem1(Uri uri, Activity activity,
                                                   int x, int y, int px, int py, boolean is) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            boolean isKitKat = Build.VERSION.SDK_INT >= 19;
            Uri pathUri = null;
            if (uri.getPath().startsWith("content")) {
                is = true;
            }
            if (isKitKat && is) {
                String path = ImageCutUtils.getPath(activity, uri);
                if (StringUtils.isNullOrEmpty(path)) {
                    return;
                }
                pathUri = Uri.fromFile(new File(path));
                intent.setDataAndType(pathUri, "image/*");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
            intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面
            intent.putExtra("outputX", px);// 设置为225*225
            intent.putExtra("outputY", py);
            intent.putExtra("aspectX", x);// 这两项为裁剪框的比例
            intent.putExtra("aspectY", y);// x:y=1:1
            // 如果不设置aspectX和aspectY，则可以以任意比例缩放
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);// 部分机型没有设置该参数截图会有黑边
            intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
            intent.putExtra("noFaceDetection", true);// 是否开启人脸识别功能
            if (pathUri != null) {// 裁剪后图片存放的
                intent.putExtra(MediaStore.EXTRA_OUTPUT, pathUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            activity.startActivityForResult(intent, CHOOSE_CUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述: 裁剪方法</br>
     * 创建时间：2016年3月16日</br>
     *
     * @param activity
     * @param cropInPath  裁剪的图片地址
     * @param cropOutPath 裁剪完成后输出的图片地址
     * @param code        onActivityResult的请求码
     */
    public static void crop(Activity activity, String cropInPath, String cropOutPath, int code) {
        File file = new File(cropInPath);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(cropInPath, options);
            float longer = options.outHeight;
            float shorter = options.outWidth;
            if (options.outHeight < options.outWidth) {
                longer = options.outWidth;
                shorter = options.outHeight;
            }
            float scaleL = longer / 900f;
            float scaleS = shorter / 600f;
            float scale = scaleL < scaleS ? scaleL : scaleS;

            int height = (int) (900L * scale);
            int width = (int) (600L * scale);
            if (options.outHeight < options.outWidth) {
                width = (int) (900L * scale);
                height = (int) (600L * scale);
            }
            File outFile = new File(cropOutPath);
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("output", Uri.fromFile(outFile));
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
            intent.putExtra("return-data", false);
            activity.startActivityForResult(intent, code); // 剪裁完会调用onActivityResult,在onActivityResult里面上code里处理图片
        }
    }

    /**
     * 描述: 图片处理成900*600的方法(最好是放在线程里头处理)</br>
     * 同时压缩图片大小<=800kb
     * 创建时间：2016年3月16日</br>
     *
     * @param cropOutPath 裁剪输出的路径
     * @return
     */
    public static Bitmap deal(String cropOutPath) {
        File file = new File(cropOutPath);
        long maxLenth = 800 * 1024L;
        if (file.exists()) {
            long fileLength = file.length();
            int inSampleSize = (int) (fileLength / maxLenth);
            inSampleSize = inSampleSize < 1 ? 1 : inSampleSize;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            Bitmap tempBitmap = BitmapFactory.decodeFile(cropOutPath, options);
            int width = tempBitmap.getWidth();
            int height = tempBitmap.getHeight();
            int longer = height > width ? height : width;
            float scale = 900f / longer;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(tempBitmap, 0, 0, width, height, matrix, true);
            if (bitmap != tempBitmap) {
                tempBitmap.recycle();
            }
            return bitmap;
        }
        return null;
    }

    /**
     * 拍照后照片旋转90度的，转正后，再放回原处
     *
     * @param imagePath
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void rotate90(String imagePath) throws IOException,
            FileNotFoundException {
        ExifInterface exifInterface = new ExifInterface(imagePath);
        String orientation = exifInterface
                .getAttribute(ExifInterface.TAG_ORIENTATION);
        if ("6".equals(orientation)) { // 拍照后照片旋转了90度
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            File file = new File(imagePath);
            // 数字越大读出的图片占用的heap越小 不然总是溢出
            if (file.length() < 20480) { // 0-20k
                options.inSampleSize = 1;
            } else if (file.length() < 51200) { // 20-50k
                options.inSampleSize = 1;
            } else if (file.length() < 307200) { // 50-300k
                options.inSampleSize = 1;
            } else if (file.length() < 819200) { // 300-800k
                options.inSampleSize = 2;
            } else if (file.length() < 1048576) { // 800-1024k
                options.inSampleSize = 3;
            } else {
                options.inSampleSize = 4;
            }
            Bitmap res = BitmapFactory.decodeFile(imagePath, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bitmap = Bitmap.createBitmap(res, 0, 0, res.getWidth(),
                    res.getHeight(), matrix, true);
            bitmap.compress(CompressFormat.JPEG, 100,
                    new FileOutputStream(new File(imagePath)));
        }
    }

    /**
     * 上传图片前对进行压缩和旋转处理 手机相机配置高,图片质量好,图片过大,导致上传速度过慢,再保证图片不失真和清晰度的情况下对图片进行压缩处理
     * 压缩图片大小 长边限制840,短边限制480,在这左右浮动
     *
     * @param imagePath 图片地址
     * @author fanqi.meng
     */
    public static void compressForupload(String imagePath) throws IOException,
            FileNotFoundException {
        ExifInterface exifInterface = new ExifInterface(imagePath);
        String orientation = exifInterface
                .getAttribute(ExifInterface.TAG_ORIENTATION);
        Bitmap bitmap = null;
        Bitmap newBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置为true,实际不会产生Bitmap,但是能得到图片高宽,减少内存开销
        BitmapFactory.decodeFile(imagePath, options);
        int bitmapHeight;
        int bitmapWidth;
        if (options.outWidth > options.outHeight) { // 判断图片的长和宽到底哪个长度大
            bitmapHeight = options.outWidth;
            bitmapWidth = options.outHeight;
        } else {
            bitmapHeight = options.outHeight;
            bitmapWidth = options.outWidth;
        }
        if (bitmapHeight > 840 || bitmapWidth > 480) {
            float scaleX = (float) bitmapHeight / 840;
            float scaleY = (float) bitmapWidth / 480;
            int sizeX = (int) scaleX;
            int sizeY = (int) scaleY;
            if (scaleX - sizeX > 0.5f) {
                sizeX = sizeX + 1;
            }
            if (scaleY - sizeY > 0.5f) {
                sizeY = sizeY + 1;
            }
            int size = sizeX < sizeY ? sizeX : sizeY; // 取最小缩放值
            options.inJustDecodeBounds = false; // 允许生成Bitmap
            options.inSampleSize = size; // 图片缩放的倍数
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
        } else {
            bitmap = BitmapFactory.decodeFile(imagePath);
        }
        if (bitmap != null) {
            if ("6".equals(orientation)) { // 旋转90度的图片
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
            } else {
                newBitmap = bitmap;
            }
            if (newBitmap != null) {
                newBitmap.compress(CompressFormat.JPEG, 80,
                        new FileOutputStream(new File(imagePath))); // 把图片质量压缩80%,存放在原处
                newBitmap.recycle();
            }
        }
    }

}
