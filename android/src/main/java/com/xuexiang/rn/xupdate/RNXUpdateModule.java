
package com.xuexiang.rn.xupdate;

import android.app.Application;
import android.graphics.Color;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.xuexiang.xupdate.UpdateManager;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import java.util.HashMap;

/**
 * 版本更新模块
 *
 * @author xuexiang
 * @since 2020/3/15 12:48 AM
 */
public class RNXUpdateModule extends ReactContextBaseJavaModule {

    public static final String KEY_ERROR_EVENT = "XUpdate_Error_Event";
    public static final String KEY_JSON_EVENT = "XUpdate_Json_Event";

    private final ReactApplicationContext mApplication;

    private RNCustomUpdateParser mCustomParser;

    public RNXUpdateModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mApplication = reactContext;
    }

    @Override
    public String getName() {
        return "RNXUpdate";
    }


    /**
     * 初始化XUpdate
     *
     * @param map
     * @param promise
     */
    @ReactMethod
    public void initXUpdate(ReadableMap map, Promise promise) {
        try {
            boolean debug = map.getBoolean("debug");
            boolean isGet = map.getBoolean("isGet");
            boolean isPostJson = map.getBoolean("isPostJson");
            int timeout = map.getInt("timeout");
            boolean isWifiOnly = map.getBoolean("isWifiOnly");
            boolean isAutoMode = map.getBoolean("isAutoMode");
            boolean supportSilentInstall = map.getBoolean("supportSilentInstall");
            boolean enableRetry = map.getBoolean("enableRetry");
            String retryContent = map.getString("retryContent");
            String retryUrl = map.getString("retryUrl");

            XUpdate.get()
                    .debug(debug)
                    //默认设置使用get请求检查版本
                    .isGet(isGet)
                    //默认设置只在wifi下检查版本更新
                    .isWifiOnly(isWifiOnly)
                    //默认设置非自动模式，可根据具体使用配置
                    .isAutoMode(isAutoMode)
                    //是否支持静默安装
                    .supportSilentInstall(supportSilentInstall)
                    .setOnUpdateFailureListener(new OnUpdateFailureListener() {
                        @Override
                        public void onFailure(UpdateError error) {
                            sendErrorEvent(error);
                        }
                    })
                    //设置默认公共请求参数
                    .param("versionCode", UpdateUtils.getVersionCode(mApplication))
                    .param("appKey", mApplication.getPackageName())
                    .setIUpdateDownLoader(new RetryUpdateDownloader(enableRetry, retryContent, retryUrl))
                    //这个必须设置！实现网络请求功能。
                    .setIUpdateHttpService(new OKHttpUpdateHttpService(timeout, isPostJson));

            ReadableMap params = map.getMap("params");
            if (params != null) {
                XUpdate.get().params(params.toHashMap());
            }
            XUpdate.get().init((Application) mApplication.getApplicationContext());

            WritableMap result = Arguments.createMap();
            result.merge(map);
            promise.resolve(result);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }


    /**
     * 版本更新
     *
     * @param map
     */
    @ReactMethod
    public void checkUpdate(ReadableMap map, Promise promise) {
        try {
            if (!mApplication.hasCurrentActivity()) {
                promise.reject("1001", "Not attach a Activity");
            }

            String url = map.getString("url");
            boolean supportBackgroundUpdate = map.getBoolean("supportBackgroundUpdate");
            boolean isAutoMode = map.getBoolean("isAutoMode");
            boolean isCustomParse = map.getBoolean("isCustomParse");
            String themeColor = map.getString("themeColor");
            String topImageRes = map.getString("topImageRes");
            String buttonTextColor = map.getString("buttonTextColor");
            double widthRatio = map.getDouble("widthRatio");
            double heightRatio = map.getDouble("heightRatio");

            boolean overrideGlobalRetryStrategy = map.getBoolean("overrideGlobalRetryStrategy");
            boolean enableRetry = map.getBoolean("enableRetry");
            String retryContent = map.getString("retryContent");
            String retryUrl = map.getString("retryUrl");

            UpdateManager.Builder builder = XUpdate.newBuild(mApplication.getCurrentActivity())
                    .updateUrl(url)
                    .isAutoMode(isAutoMode)
                    .supportBackgroundUpdate(supportBackgroundUpdate);

            ReadableMap params = map.getMap("params");
            if (params != null) {
                builder.params(params.toHashMap());
            }

            if (isCustomParse) {
                if (mCustomParser == null) {
                    mCustomParser = new RNCustomUpdateParser(mApplication);
                }
                builder.updateParser(mCustomParser);
            }

            updatePromptStyle(builder, themeColor, topImageRes, buttonTextColor, widthRatio, heightRatio, overrideGlobalRetryStrategy, enableRetry, retryContent, retryUrl);

            builder.update();

            promise.resolve("start updating...");

        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    /**
     * 直接传入UpdateEntity进行版本更新
     *
     * @param map
     */
    @ReactMethod
    public void updateByUpdateEntity(ReadableMap map, Promise promise) {
        try {
            if (!mApplication.hasCurrentActivity()) {
                promise.reject("1001", "Not attach a Activity");
            }

            ReadableMap entityMap = map.getMap("updateEntity");
            UpdateEntity updateEntity = RNCustomUpdateParser.parseUpdateEntityMap(entityMap);

            boolean supportBackgroundUpdate = map.getBoolean("supportBackgroundUpdate");
            boolean isAutoMode = map.getBoolean("isAutoMode");
            String themeColor = map.getString("themeColor");
            String topImageRes = map.getString("topImageRes");
            String buttonTextColor = map.getString("buttonTextColor");
            double widthRatio = map.getDouble("widthRatio");
            double heightRatio = map.getDouble("heightRatio");

            boolean overrideGlobalRetryStrategy = map.getBoolean("overrideGlobalRetryStrategy");
            boolean enableRetry = map.getBoolean("enableRetry");
            String retryContent = map.getString("retryContent");
            String retryUrl = map.getString("retryUrl");


            UpdateManager.Builder builder = XUpdate.newBuild(mApplication.getCurrentActivity())
                    .isAutoMode(isAutoMode)
                    .supportBackgroundUpdate(supportBackgroundUpdate);

            updatePromptStyle(builder, themeColor, topImageRes, buttonTextColor, widthRatio, heightRatio, overrideGlobalRetryStrategy, enableRetry, retryContent, retryUrl);

            builder.build().update(updateEntity);

            promise.resolve("start updating...");

        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    /**
     * 自定义解析回掉
     *
     * @param map
     */
    @ReactMethod
    public void onCustomUpdateParse(ReadableMap map) {
        if (mCustomParser != null) {
            mCustomParser.handleCustomParseResult(map);
        }
    }

    /**
     * 更新弹窗的样式
     *
     * @param builder
     * @param themeColor                  主题颜色
     * @param topImageRes                 弹窗顶部的图片
     * @param buttonTextColor             按钮文字的颜色
     * @param widthRatio                  版本更新提示器宽度占屏幕的比例
     * @param heightRatio                 版本更新提示器高度占屏幕的比例
     * @param overrideGlobalRetryStrategy
     * @param enableRetry
     * @param retryContent
     * @param retryUrl
     */
    private void updatePromptStyle(UpdateManager.Builder builder, String themeColor, String topImageRes, String buttonTextColor, double widthRatio, double heightRatio, boolean overrideGlobalRetryStrategy, boolean enableRetry, String retryContent, String retryUrl) {
        if (!TextUtils.isEmpty(themeColor)) {
            builder.promptThemeColor(Color.parseColor(themeColor));
        }
        if (!TextUtils.isEmpty(topImageRes)) {
            int topImageResId = mApplication.getCurrentActivity().getResources().getIdentifier(topImageRes, "drawable", mApplication.getCurrentActivity().getPackageName());
            builder.promptTopResId(topImageResId);
        }
        if (!TextUtils.isEmpty(buttonTextColor)) {
            builder.promptButtonTextColor(Color.parseColor(buttonTextColor));
        }
        builder.promptWidthRatio((float) widthRatio);
        builder.promptHeightRatio((float) heightRatio);
        if (overrideGlobalRetryStrategy) {
            builder.updateDownLoader(new RetryUpdateDownloader(enableRetry, retryContent, retryUrl));
        }
    }


    /**
     * 显示重试提示弹窗
     *
     * @param map
     */
    @ReactMethod
    public void showRetryUpdateTipDialog(ReadableMap map) {
        String retryContent = map.getString("retryContent");
        String retryUrl = map.getString("retryUrl");

        RetryUpdateTipDialog.show(retryContent, retryUrl);
    }

    /**
     * 发送错误信息
     *
     * @param error
     */
    private void sendErrorEvent(UpdateError error) {
        if (mApplication != null) {
            WritableMap map = Arguments.createMap();
            map.putInt("code", error.getCode());
            map.putString("message", error.getMessage());
            map.putString("detailMsg", error.getDetailMsg());
            mApplication.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(KEY_ERROR_EVENT, map);
        }
    }


}