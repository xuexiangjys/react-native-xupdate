import {NativeModules} from 'react-native';

const {RNXUpdate} = NativeModules;

class InitArgs {

    constructor() {
        ///是否输出日志
        this.debug = false;
        ///是否使用post请求
        this.isPost = false;
        ///post请求是否是上传json
        this.isPostJson = false;
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


const XUpdate = {

    ///初始化
    init: (
        InitArg = new InitArgs(),
    ) => {

        if (Platform.OS === 'ios') {
            return 'IOS端暂不支持';
        }

        return RNXUpdate.initXUpdate({
            'debug': InitArg.debug,
            'isGet': !InitArg.isPost,
            'isPostJson': InitArg.isPostJson,
            'isWifiOnly': InitArg.isWifiOnly,
            'isAutoMode': InitArg.isAutoMode,
            'supportSilentInstall': InitArg.supportSilentInstall,
            'enableRetry': InitArg.enableRetry,
            'retryContent': InitArg.retryContent,
            'retryUrl': InitArg.retryUrl,
            'params': InitArg.params,
        });
    },

    ///版本更新
    checkUpdate : () => {

        if (Platform.OS === 'ios') {
            return 'IOS端暂不支持';
        }
    }
};

export {
    InitArgs,
    XUpdate,
};
