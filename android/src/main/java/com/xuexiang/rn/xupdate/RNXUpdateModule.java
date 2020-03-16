
package com.xuexiang.rn.xupdate;

import android.app.Application;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.xuexiang.xupdate.UpdateManager;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;

import java.util.Map;

/**
 * 版本更新模块
 *
 * @author xuexiang
 * @since 2020/3/15 12:48 AM
 */
public class RNXUpdateModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mApplication;

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
                    //设置默认公共请求参数
                    .param("versionCode", UpdateUtils.getVersionCode(mApplication))
                    .param("appKey", mApplication.getPackageName())
                    .setIUpdateDownLoader(new RetryUpdateDownloader(enableRetry, retryContent, retryUrl))
                    //这个必须设置！实现网络请求功能。
                    .setIUpdateHttpService(new OKHttpUpdateHttpService(isPostJson));

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
            Double widthRatio = map.getDouble("widthRatio");
            Double heightRatio = map.getDouble("heightRatio");

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

//        if (isCustomParse) {
//            builder.updateParser(new FlutterCustomUpdateParser(mMethodChannel));
//        }

            updatePromptStyle(builder, themeColor, topImageRes, widthRatio, heightRatio, overrideGlobalRetryStrategy, enableRetry, retryContent, retryUrl);

            builder.update();

            promise.resolve("start updating...");

        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }


    /**
     * 更新弹窗的样式
     *
     * @param builder
     * @param themeColor                  主题颜色
     * @param topImageRes                 弹窗顶部的图片
     * @param widthRatio                  版本更新提示器宽度占屏幕的比例
     * @param heightRatio                 版本更新提示器高度占屏幕的比例
     * @param overrideGlobalRetryStrategy
     * @param enableRetry
     * @param retryContent
     * @param retryUrl
     */
    private void updatePromptStyle(UpdateManager.Builder builder, String themeColor, String topImageRes, Double widthRatio, Double heightRatio, boolean overrideGlobalRetryStrategy, boolean enableRetry, String retryContent, String retryUrl) {
        if (!TextUtils.isEmpty(themeColor)) {
            builder.promptThemeColor(Color.parseColor(themeColor));
        }
        if (!TextUtils.isEmpty(topImageRes)) {
            int topImageResId = mApplication.getCurrentActivity().getResources().getIdentifier(topImageRes, "drawable", mApplication.getCurrentActivity().getPackageName());
            builder.promptTopResId(topImageResId);
        }
        if (widthRatio != null) {
            builder.promptWidthRatio(widthRatio.floatValue());
        }
        if (heightRatio != null) {
            builder.promptHeightRatio(heightRatio.floatValue());
        }
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


}