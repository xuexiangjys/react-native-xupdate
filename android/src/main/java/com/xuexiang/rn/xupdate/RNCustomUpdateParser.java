package com.xuexiang.rn.xupdate;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;

import java.lang.ref.WeakReference;

import static com.xuexiang.rn.xupdate.RNXUpdateModule.KEY_JSON_EVENT;

/**
 * Flutter端自定义版本更新解析器
 *
 * @author xuexiang
 * @since 2020-02-15 15:21
 */
public class RNCustomUpdateParser implements IUpdateParser {

    private WeakReference<ReactApplicationContext> mContext;

    private IUpdateParseCallback mParseCallback;

    public RNCustomUpdateParser(ReactApplicationContext context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public UpdateEntity parseJson(String json) throws Exception {
        return null;
    }

    @Override
    public void parseJson(String json, final IUpdateParseCallback callback) throws Exception {
        mParseCallback = callback;
        if (mContext != null && mContext.get() != null) {
            mContext.get().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(KEY_JSON_EVENT, json);
        }
    }

    /**
     * 处理flutter端自定义处理的json解析
     *
     * @param result
     */
    public void handleCustomParseResult(ReadableMap result) {
        if (mParseCallback != null) {
            mParseCallback.onParseResult(parseUpdateEntityMap(result));
            mParseCallback = null;
        }
    }

    /**
     * 解析Flutter传过来的UpdateEntity Map
     *
     * @param map
     * @return
     */
    public static UpdateEntity parseUpdateEntityMap(ReadableMap map) {
        //必填项
        boolean hasUpdate = map.getBoolean("hasUpdate");
        int versionCode = map.getInt("versionCode");
        String versionName = map.getString("versionName");
        String updateContent = map.getString("updateContent");
        String downloadUrl = map.getString("downloadUrl");

        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.setHasUpdate(hasUpdate)
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setUpdateContent(updateContent)
                .setDownloadUrl(downloadUrl);

        if (map.hasKey("isForce")) {
            updateEntity.setForce(map.getBoolean("isForce"));
        }
        if (map.hasKey("isIgnorable")) {
            updateEntity.setIsIgnorable(map.getBoolean("isIgnorable"));
        }
        if (map.hasKey("apkSize")) {
            updateEntity.setSize(map.getInt("apkSize"));
        }
        if (map.hasKey("apkMd5")) {
            updateEntity.setMd5(map.getString("apkMd5"));
        }
        return updateEntity;
    }

    @Override
    public boolean isAsyncParser() {
        return true;
    }
}
