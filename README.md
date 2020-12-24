
# react-native-xupdate

[![Version](https://img.shields.io/badge/version-1.0.2-blue.svg)](https://www.npmjs.com/package/react-native-xupdate-new)
[![Issue](https://img.shields.io/github/issues/xuexiangjys/react-native-xupdate.svg)](https://github.com/xuexiangjys/react-native-xupdate/issues)
[![Star](https://img.shields.io/github/stars/xuexiangjys/react-native-xupdate.svg)](https://github.com/xuexiangjys/react-native-xupdate)

A React-Native plugin for XUpdate(Android Version Update Library).

## Getting started

`$ npm install react-native-xupdate-new --save`

### Mostly automatic installation

`$ react-native link react-native-xupdate-new`

### Manual installation


#### iOS（Temporarily not supported）

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-xupdate-new` and add `RNXUpdate.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNXUpdate.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.xuexiang.xupdate.RNXUpdatePackage;` to the imports at the top of the file
  - Add `new RNXUpdatePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-xupdate-new'
  	project(':react-native-xupdate-new').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-xupdate-new/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-xupdate-new')
  	```


## Usage

### Initialization

```
import {XUpdate} from 'react-native-xupdate-new';

///初始化
initXUpdate() {
    ///设置初始化参数
    let args = new InitArgs();
    ///是否输出日志
    args.debug = true;
    ///post请求是否是上传json
    args.isPostJson = false;
    ///是否只在wifi下才能进行更新
    args.isWifiOnly = false;
    ///是否开启自动模式
    args.isAutoMode = false;
    ///是否支持静默安装，这个需要设备有root权限
    args.supportSilentInstall = false;
    ///在下载过程中，如果点击了取消的话，是否弹出切换下载方式的重试提示弹窗
    args.enableRetry = false;
    
    ///初始化SDK
    XUpdate.init(args).then(result => {
        this.setState({
            _message: '初始化成功:' + JSON.stringify(result),
        });
    }).catch(error => {
        console.log(error);
        this.setState({
            _message: '初始化失败:' + error,
        });
    });

    //设置自定义解析
    XUpdate.setCustomParser({parseJson: this.customParser});
    //设置错误监听
    XUpdate.addErrorListener(this.errorListener);
}
```

### JSON Format

```
{
  "Code": 0, //0代表请求成功，非0代表失败
  "Msg": "", //请求出错的信息
  "UpdateStatus": 1, //0代表不更新，1代表有版本更新，不需要强制升级，2代表有版本更新，需要强制升级
  "VersionCode": 3,
  "VersionName": "1.0.2",
  "ModifyContent": "1、优化api接口。\r\n2、添加使用demo演示。\r\n3、新增自定义更新服务API接口。\r\n4、优化更新提示界面。",
  "DownloadUrl": "https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk",
  "ApkSize": 2048
  "ApkMd5": "..."  //md5值没有的话，就无法保证apk是否完整，每次都会重新下载。框架默认使用的是md5加密。
}
```

### CheckUpdate

```
    ///默认App更新
    checkUpdateDefault() {
        let args = new UpdateArgs(_updateUrl);
        XUpdate.update(args);
    }

    ///默认App更新 + 支持后台更新
    checkUpdateSupportBackground() {
        let args = new UpdateArgs(_updateUrl);
        args.supportBackgroundUpdate = true;
        XUpdate.update(args);
    }

    ///调整宽高比
    checkUpdateRatio() {
        let args = new UpdateArgs(_updateUrl);
        args.widthRatio = 0.6;
        XUpdate.update(args);
    }

    ///强制更新
    checkUpdateForce() {
        let args = new UpdateArgs(_updateUrl2);
        XUpdate.update(args);
    }

    ///自动模式, 如果需要完全无人干预，自动更新，需要root权限【静默安装需要】
    checkUpdateAutoMode() {
        let args = new UpdateArgs(_updateUrl);
        args.isAutoMode = true;
        XUpdate.update(args);
    }

    ///下载时点击取消允许切换下载方式
    enableChangeDownLoadType() {
        let args = new UpdateArgs(_updateUrl);
        args.overrideGlobalRetryStrategy = true;
        args.enableRetry = true;
        args.retryContent = 'Github下载速度太慢了，是否考虑切换蒲公英下载？';
        args.retryUrl = 'https://www.pgyer.com/flutter_learn';
        XUpdate.update(args);
    }

    ///显示重试提示弹窗
    showRetryDialogTip() {
        XUpdate.showRetryUpdateTip('Github下载速度太慢了，是否考虑切换蒲公英下载？', 'https://www.pgyer.com/flutter_learn');
    }
```



### Custom JSON Format

1.Setting up a custom update parser

```
//设置自定义解析
XUpdate.setCustomParser({parseJson: this.customParser});

///Resolve the custom JSON content to the UpdateEntity entity class
customParser = (json) => {
    let appInfo = JSON.parse(json);
    return {
        //必填
        hasUpdate: appInfo['hasUpdate'],
        versionCode: appInfo['versionCode'],
        versionName: appInfo['versionName'],
        updateContent: appInfo['updateLog'],
        downloadUrl: appInfo['apkUrl'],
        //选填
        isIgnorable: appInfo['isIgnorable'],
        apkSize: appInfo['apkSize'],
    };
};
```

2.Set the parameter `isCustomParse` to true

```
///使用自定义json解析
customJsonParse() {
    let args = new UpdateArgs(_updateUrl3);
    args.isCustomParse = true;
    XUpdate.update(args);
}
```

### Update By UpdateEntity Directly

```
///直接传入UpdateEntity进行更新提示
checkUpdateByUpdateEntity() {
    let args = new UpdateArgs();
    args.supportBackgroundUpdate = true;
    XUpdate.updateByInfo(args, {
        hasUpdate: AppInfo['hasUpdate'],
        versionCode: AppInfo['versionCode'],
        versionName: AppInfo['versionName'],
        updateContent: AppInfo['updateLog'],
        downloadUrl: AppInfo['apkUrl'],
        //选填
        isIgnorable: AppInfo['isIgnorable'],
        apkSize: AppInfo['apkSize'],
    });
}
```

### Custom Update Prompt Style

> Currently, only theme color and top picture customization are supported!

1.Configure top picture, Path: `android/app/src/main/res/values/drawable`, For example:

![](https://github.com/xuexiangjys/flutter_xupdate/blob/master/example/art/6.png)

2.Set the parameter `themeColor` and `topImageRes`

```
///自定义更新弹窗样式
customPromptDialog() {
    let args = new UpdateArgs(_updateUrl);
    args.themeColor = '#FFFFAC5D';
    args.topImageRes = 'bg_update_top';
    XUpdate.update(args);
}
```

## Property value

### Initialization

Name | Type | Default | Description
:-|:-:|:-:|:-
debug | bool | false | Whether Output log
isPost | bool | false | Whether use post request
isPostJson | bool | false | Whether post request upload json format
timeout | int | 20000(ms) | Request response timeout
isWifiOnly | bool | true | Whether update only under WiFi
isAutoMode | bool | false | Whether to turn on automatic mode
supportSilentInstall | bool | false | Whether to support silent installation requires that the device has root permission
enableRetry | bool | false | In the process of downloading, if you click Cancel, whether the pop-up window for retrying to switch the download mode will pop up
retryContent | String | '' | Try the prompt content of the prompt pop-up window again
retryUrl | String | '' | Retrying prompt pop-up URL to jump after clicking
params | Map | / | Public parameters to be set

### CheckUpdate

Name | Type | Default | Description
:-|:-:|:-:|:-
url | String | / | URL of version check
params | Map | / | Parameters
supportBackgroundUpdate | bool | false | Whether to support background updates
isAutoMode | bool | false | Whether to turn on automatic mode
isCustomParse | bool | false | Is it a custom resolution protocol
themeColor | String | '' | Apply pop-up theme color
topImageRes | String | '' | The name of the top picture resource in the pop-up window
buttonTextColor | String | '' | The color of the button text
widthRatio | double | / | Proportion of version update Prompter width to screen
heightRatio | double | / | Proportion of version update Prompter height to screen
overrideGlobalRetryStrategy | bool | false | Whether to override the global retry policy
enableRetry | bool | false | In the process of downloading, if you click Cancel, whether the pop-up window for retrying to switch the download mode will pop up
retryContent | String | '' | Try the prompt content of the prompt pop-up window again
retryUrl | String | '' | Retrying prompt pop-up URL to jump after clicking
