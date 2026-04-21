package candyenk.android.tools;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import androidx.annotation.RequiresApi;

import java.util.Map;

/**
 * 封装内容提供器
 * 重写该类并添加清单内文件
 *
 */
public abstract class CP extends ContentProvider {
    
    
    /**
     * 创建消息发送机
     *
     * @param context     上下文
     * @param authorities 需要访问的应用清单信息（一般是包名）
     * @return 消息发送机
     */
    public static CPT CREATE(Context context, String authorities) {
        return new CPT(context, authorities);
    }
    
    /**
     * 处理客户端set的请求
     *
     * @param data 接收的数据
     * @return 返回状态码
     */
    public abstract int set(ContentValues data);
    
    
    /**
     * 处理客户端get的请求
     *
     * @param key 客户端需要的数据标识符
     * @return 返回给客户端的数据
     */
    public abstract ContentValues get(String key);
    
    /**
     * 转换成map
     */
    public static Map<String, Object> toMap(ContentValues cv) {
        Map<String, Object> map = new ArrayMap<>(cv.size());
        cv.keySet().forEach(s -> map.put(s, cv.get(s)));
        return map;
    }
    
    /**
     * 转换成ContentValues
     */
    public static ContentValues toCV(Map<String, Object> map) {
        ContentValues cv = new ContentValues(map.size());
        map.forEach((s, o) -> {
            switch (o) {
                case Integer i -> cv.put(s, i);
                case Short i -> cv.put(s, i);
                case Long i -> cv.put(s, i);
                case Float i -> cv.put(s, i);
                case Double i -> cv.put(s, i);
                case Byte i -> cv.put(s, i);
                case Boolean i -> cv.put(s, i);
                case String i -> cv.put(s, i);
                case byte[] i -> cv.put(s, i);
                default -> throw new IllegalStateException("不支持的类型: " + o);
            }
        });
        return cv;
    }
    /***************************************************************************************/
    /***************************************************************************************/
    /***************************************************************************************/
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new DataCursor(get(selection));
    }
    
    @Override
    public final String getType(Uri uri) {
        return "";
    }
    
    
    @Override
    public final Uri insert(Uri uri, ContentValues values) {
        return Uri.parse("content://" + uri.getAuthority() + "/" + Uri.encode(String.valueOf(set(values))));
    }
    
    @Override
    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    
    @Override
    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
    
    /**
     * 进程间通信客户端
     */
    public static class CPT {
        private final ContentResolver cr;
        private final Uri uri;
        
        private CPT(Context context, String authorities) {
            cr = context.getContentResolver();
            uri = Uri.parse("content://" + authorities + "/cdk");
        }
        
        /**
         * 发送数据集
         * 支持基本数据类型和byte[]其他的会报错
         *
         * @return 返回状态
         */
        public int set(ContentValues data) {
            return Integer.parseInt(Uri.decode(cr.insert(uri, data).getLastPathSegment()));
        }
        
        
        /**
         * 获取数据集
         * TODO：仅支持API33？？？
         *
         * @param key 标识符
         * @return 数据集
         */
        
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        public ContentValues get(String key) {
            try (Cursor c = cr.query(uri, null, key, null, null)) {
                if (c != null && c.moveToFirst()) return c.getExtras().getParcelable("data", ContentValues.class);
            }
            return null;
        }
    }
    
    /**
     * 专用Cursor
     */
    public static final class DataCursor extends AbstractCursor {
        
        public DataCursor(ContentValues cv) {
            Bundle b = new Bundle();
            b.putParcelable("data", cv);
            super.setExtras(b);
        }
        
        @Override
        public int getCount() {
            return 1;
        }
        
        @Override
        public String[] getColumnNames() {
            return new String[0];
        }
        
        @Override
        public String getString(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public short getShort(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public int getInt(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public long getLong(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public float getFloat(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public double getDouble(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
        
        @Override
        public boolean isNull(int columnIndex) {
            throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
