import {NativeEventEmitter, NativeModules} from 'react-native';

const {RNXUpdate} = NativeModules;

///XUpdate初始化参数
class InitArgs {
  constructor() {
    ///是否输出日志
    this.debug = false;
    ///是否使用post请求
    this.isPost = false;
    ///post请求是否是上传json
    this.isPostJson = false;
    ///请求响应超时时间
    this.timeout = 20000;
    ///是否只在wifi下才能进行更新
    this.isWifiOnly = true;
    ///是否开启自动模式
    this.isAutoMode = false;
    ///是否支持静默安装，这个需要设备有root权限
    this.supportSilentInstall = false;
    ///在下载过程中，如果点击了取消的话，是否弹出切换下载方式的重试提示弹窗
    this.enableRetry = false;
    ///重试提示弹窗的提示内容
    this.retryContent = '';
    ///重试提示弹窗点击后跳转的url
    this.retryUrl = '';
    ///需要设置的公共参数
    this.params = {};
  }
}

const KEY_ERROR_EVENT = 'XUpdate_Error_Event';
const KEY_JSON_EVENT = 'XUpdate_Json_Event';

///版本更新参数
class UpdateArgs {
  constructor(url: string) {
    ///版本检查的地址
    this.url = url;
    ///传递的参数
    this.params = {};
    ///是否支持后台更新
    this.supportBackgroundUpdate = false;
    ///是否开启自动模式
    this.isAutoMode = false;
    ///是否是自定义解析协议
    this.isCustomParse = false;
    ///应用弹窗的主题色
    this.themeColor = '';
    ///应用弹窗的顶部图片资源名
    this.topImageRes = '';
    ///按钮文字的颜色
    this.buttonTextColor = '';
    ///版本更新提示器宽度占屏幕的比例, 不设置的话不做约束
    this.widthRatio = -1;
    ///版本更新提示器高度占屏幕的比例, 不设置的话不做约束
    this.heightRatio = -1;
    ///是否覆盖全局的重试策略
    this.overrideGlobalRetryStrategy = false;
    ///在下载过程中，如果点击了取消的话，是否弹出切换下载方式的重试提示弹窗
    this.enableRetry = false;
    ///重试提示弹窗的提示内容
    this.retryContent = '';
    ///重试提示弹窗点击后跳转的url
    this.retryUrl = '';
  }
}

const EventEmitter = new NativeEventEmitter(RNXUpdate);

class UpdateParser {
  parseJson: (json: string) => UpdateEntity;

  constructor(parser) {
    this.parseJson = parser;
  }
}

///版本更新信息
class UpdateEntity {
  ///是否有新版本
  hasUpdate: boolean;
  ///是否强制安装：不安装无法使用app
  isForce: boolean;
  ///是否可忽略该版本
  isIgnorable: boolean;

  //===========升级的信息=============//
  ///版本号
  versionCode: number;
  ///版本名称
  versionName: string;
  ///更新内容
  updateContent: string;
  ///下载地址
  downloadUrl: string;
  ///apk的大小
  apkSize: number;
  ///apk文件的加密值（这里默认是md5值）
  apkMd5: string;

  //这5个值必须传
  constructor(hasUpdate, versionCode, versionName, updateContent, downloadUrl) {
    this.hasUpdate = hasUpdate;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.updateContent = updateContent;
    this.downloadUrl = downloadUrl;
  }
}

const XUpdate = {
  ///初始化
  init: (initArg = new InitArgs()) => {
    if (Platform.OS === 'ios') {
      return 'IOS端暂不支持';
    }

    return RNXUpdate.initXUpdate({
      debug: initArg.debug,
      isGet: !initArg.isPost,
      isPostJson: initArg.isPostJson,
      timeout: initArg.timeout,
      isWifiOnly: initArg.isWifiOnly,
      isAutoMode: initArg.isAutoMode,
      supportSilentInstall: initArg.supportSilentInstall,
      enableRetry: initArg.enableRetry,
      retryContent: initArg.retryContent,
      retryUrl: initArg.retryUrl,
      params: initArg.params,
    });
  },

  setCustomParser: (parser: UpdateParser) => {
    EventEmitter.addListener(KEY_JSON_EVENT, (json) => {
      if (parser !== null) {
        RNXUpdate.onCustomUpdateParse(parser.parseJson(json));
      }
    });
  },

  addErrorListener: (listener: Function) => {
    EventEmitter.addListener(KEY_ERROR_EVENT, listener);
  },

  removeErrorListener: (listener: Function) => {
    EventEmitter.removeListener(KEY_ERROR_EVENT, listener);
  },

  ///版本更新
  update: (updateArgs = new UpdateArgs()) => {
    if (Platform.OS === 'ios') {
      return 'IOS端暂不支持';
    }

    if (
      updateArgs.url === null ||
      updateArgs.url === undefined ||
      updateArgs.url === ''
    ) {
      return 'url can not be null or empty！';
    }

    return RNXUpdate.checkUpdate({
      url: updateArgs.url,
      params: updateArgs.params,
      supportBackgroundUpdate: updateArgs.supportBackgroundUpdate,
      isAutoMode: updateArgs.isAutoMode,
      isCustomParse: updateArgs.isCustomParse,
      themeColor: updateArgs.themeColor,
      topImageRes: updateArgs.topImageRes,
      buttonTextColor: updateArgs.buttonTextColor,
      widthRatio: updateArgs.widthRatio,
      heightRatio: updateArgs.heightRatio,
      overrideGlobalRetryStrategy: updateArgs.overrideGlobalRetryStrategy,
      enableRetry: updateArgs.enableRetry,
      retryContent: updateArgs.retryContent,
      retryUrl: updateArgs.retryUrl,
    });
  },

  ///直接传入UpdateEntity进行版本更新
  updateByInfo: (updateArgs = new UpdateArgs(), updateEntity: UpdateEntity) => {
    if (Platform.OS === 'ios') {
      return 'IOS端暂不支持';
    }

    return RNXUpdate.updateByUpdateEntity({
      updateEntity: updateEntity,
      supportBackgroundUpdate: updateArgs.supportBackgroundUpdate,
      isAutoMode: updateArgs.isAutoMode,
      themeColor: updateArgs.themeColor,
      topImageRes: updateArgs.topImageRes,
      buttonTextColor: updateArgs.buttonTextColor,
      widthRatio: updateArgs.widthRatio,
      heightRatio: updateArgs.heightRatio,
      overrideGlobalRetryStrategy: updateArgs.overrideGlobalRetryStrategy,
      enableRetry: updateArgs.enableRetry,
      retryContent: updateArgs.retryContent,
      retryUrl: updateArgs.retryUrl,
    });
  },

  showRetryUpdateTip: (retryContent, retryUrl) => {
    if (Platform.OS === 'ios') {
      return;
    }

    RNXUpdate.showRetryUpdateTipDialog({
      retryContent: retryContent,
      retryUrl: retryUrl,
    });
  },
};

export {InitArgs, UpdateArgs, UpdateEntity, UpdateParser, XUpdate};
