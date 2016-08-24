package geminihao.com.androidutils.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * 判断网络工具类
 * Created by user on 2016/7/28.
 */
public class NetWorkUtils {
    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检测wifi是否连接
     *
     * @return
     */
    public boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测3G是否连接
     *
     * @return
     */
    public boolean is3gConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> accessibleProviders = lm.getProviders(true);
        for (String name : accessibleProviders) {
            if ("gps".equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取网络连接类型
     *
     * @return -1表示没有网络
     */
    public static final int TYPE_WIFI = 0;
    public static final int TYPE_3G = 1;
    public static final int TYPE_GPRS = 2;

    public static final int getNetWorkType(Context c) {
        ConnectivityManager conn = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return -1;
        }
        NetworkInfo info = conn.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return -1;
        }

        int type = info.getType(); // MOBILE（GPRS）;WIFI
        if (type == ConnectivityManager.TYPE_WIFI) {
            return TYPE_WIFI;
        } else {
            TelephonyManager tm = (TelephonyManager) c
                    .getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return TYPE_GPRS;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return TYPE_GPRS;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return TYPE_GPRS;
                default:
                    return TYPE_3G;
            }
        }
    }
}
