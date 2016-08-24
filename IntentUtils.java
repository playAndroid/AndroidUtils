package geminihao.com.androidutils.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Intent启动 工具类
 * Created by user on 2016/7/28.
 */
public class IntentUtils {
    /**
     * 浏览手机相册
     *
     * @return
     */
    public static Intent createAlbumIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooseIntent = Intent.createChooser(intent, null);
        return chooseIntent;
    }

    /**
     * 拍照
     *
     * @return
     */
    public static Intent createShotIntent(File tempFile) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri uri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    /**
     * 发短信
     *
     * @param mContext
     * @param phone
     * @param content
     */
    public static void sendSMS(Context mContext, String phone, String content) {
        if (StringUtils.isNullOrEmpty(phone)) {
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                + phone));
        if (mContext.getPackageManager().resolveActivity(sendIntent, 0) == null) {
            Toast.makeText(mContext, "系统不支持此功能", Toast.LENGTH_SHORT).show();
            return;
        }
        sendIntent.putExtra("sms_body", content);
        mContext.startActivity(sendIntent);
    }

    /**
     * 发邮件
     *
     * @param mContext
     * @param mail
     * @param content
     */
    public static void sendEmail(Context mContext, String mail, String content,
                                 String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"
                + mail));

        if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            Toast.makeText(mContext, "系统不支持此功能", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // intent.setType("text/plain");
        mContext.startActivity(intent);
    }

    /**
     * 打电话
     *
     * @param mContext
     * @param phone
     */
    public static void dialPhone(Context mContext, String phone) {
        dialPhone(mContext, phone, true);
    }

    public static void dialPhone(Context mContext, String phone, boolean isShow) {
        String action = Intent.ACTION_CALL;// 在应用中启动一次呼叫有缺陷,不能用在紧急呼叫上
        if (isShow) {
            action = Intent.ACTION_DIAL;// 显示拨号界面
        }
        if (StringUtils.isNullOrEmpty(phone)) {
            return;
        }
        Intent intent = new Intent(action, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mContext.startActivity(intent);
    }

    /**
     * 是否安装了客户端
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstall(Context context, String packageName) {
        PackageManager pckMan;
        pckMan = context.getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (packageName.equals(p.packageName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 安装客户端
     *
     * @param context
     * @param installName
     */
    public static void setupApk(Context context, String installName) {
        String fileName = context.getApplicationContext().getFilesDir() + "/"
                + installName;
        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
