package geminihao.com.androidutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ShareUtils {
    /**
     * share封装
     */

    Context mContext;
    SharedPreferences sharedPreferences;

    public ShareUtils(Context mContext) {
        this.mContext = mContext;

    }

    /**
     * 通过share获取对象
     *
     * @param shareName 配置名称
     * @param c         反射获取的对象
     * @return 传入的对象实例
     */
    public <T> T getEntryForShare(String shareName, Class<T> c) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        T t = null;
        try {
            Map<String, ?> map = sharedPreferences.getAll();
            List<Field> list = getObjFieldWithExtends(c);
            if (list != null) {
                if (map.size() > 0 && list.size() > 0) {
                    t = c.newInstance();
                    for (Entry<String, ?> et : map.entrySet()) {
                        String key = et.getKey();
                        Object value = et.getValue();
                        for (Field filed : list) {
                            String name = filed.getName();
                            if (name.equalsIgnoreCase(key)) {
                                filed.set(t, value);
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 把对象放进share里
     *
     * @param shareName 配置名称
     * @param o Object
     */
    public void setShareForEntry(String shareName, Object o) {
        try {
            sharedPreferences = mContext.getSharedPreferences(shareName,
                    Activity.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();

            List<Field> list = getObjFieldWithExtends(o.getClass());
            for (Field f : list) {
                String name = f.getName();
                try {
                    Object rtcs = f.get(o);
                    if (rtcs instanceof String) {
                        editor.putString(name, (String) rtcs);
                    } else if (rtcs instanceof Boolean) {
                        editor.putBoolean(name, (Boolean) rtcs);
                    } else if (rtcs instanceof Float) {
                        editor.putFloat(name, (Float) rtcs);
                    } else if (rtcs instanceof Integer) {
                        editor.putInt(name, (Integer) rtcs);
                    } else if (rtcs instanceof Long) {
                        editor.putLong(name, (Long) rtcs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            editor.commit();
        } catch (Exception e) {
        }

    }

    /**
     * 返回share文件一个节点的值boolean类型
     *
     * @param shareName  配置名称
     * @param key 节点key
     * @param value 设置的值
     */
    public void setBooleanForShare(String shareName, String key, Boolean value) {
        Editor editor = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanForShare(String shareName, String key) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    /**
     * 获取对象里边的public属性
     *
     * @param cls
     * @return
     */
    public static List<Field> getObjFieldWithExtends(Class<?> cls) {
        if (cls.equals(Object.class)) {
            return null;
        }
        List<Field> list = new ArrayList<Field>();
        Field[] mds = cls.getFields();
        for (int i = 0; i < mds.length; i++) {
            list.add(mds[i]);
        }
        return list;
    }

    /**
     * 返回share文件一个节点的值
     *
     * @param shareName
     * @param key
     * @return
     */
    public String getStringForShare(String shareName, String key) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public void setStringForShare(String shareName, String key, String value) {
        Editor editor = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * 返回share文件一个节点的值 Long
     *
     * @param shareName
     * @param key
     * @return
     */
    public Long getLongForShare(String shareName, String key) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

    public void setlongForShare(String shareName, String key, Long value) {
        Editor editor = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }


    /**
     * 把map对象存进share
     *
     * @param shareName
     * @param map
     */
    public void setShareForMap(String shareName, HashMap<String, String> map) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        for (Entry<String, String> et : map.entrySet()) {
            editor.putString(et.getKey(), et.getValue());
        }
        editor.commit();
    }

    /**
     * 清空share
     *
     * @param shareName
     */
    public void clearShare(String shareName) {
        sharedPreferences = mContext.getSharedPreferences(shareName,
                Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
