/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  Button,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

import {XUpdate, InitArgs, UpdateArgs} from 'react-native-xupdate-new';
import AppInfo from './update_custom';

const _updateUrl =
  'https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_test.json';

const _updateUrl2 =
  'https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_forced.json';

const _updateUrl3 =
  'https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_custom.json';

export default class App extends Component<{}> {
  state = {
    _message: '',
  };

  //自定义的异常处理
  errorListener = (error) => {
    console.log(error);
    //下载失败
    if (error.code === 4000) {
      XUpdate.showRetryUpdateTip(
        'Github被墙无法继续下载，是否考虑切换蒲公英下载？',
        'https://www.pgyer.com/flutter_learn',
      );
    }
    this.setState({
      _message: '发送异常：' + JSON.stringify(error),
    });
  };

  componentDidMount() {
    this.initXUpdate();
  }

  initXUpdate() {
    let args = new InitArgs();
    args.debug = true;
    args.isPostJson = false;
    args.timeout = 25000;
    args.isWifiOnly = false;
    args.isAutoMode = false;
    args.supportSilentInstall = false;
    args.enableRetry = false;
    XUpdate.init(args)
      .then((result) => {
        this.setState({
          _message: '初始化成功:' + JSON.stringify(result),
        });
      })
      .catch((error) => {
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

  componentWillUnmount() {
    XUpdate.removeErrorListener(this.errorListener);
  }

  render() {
    return (
      <>
        <StatusBar barStyle="default" />
        <SafeAreaView>
          <ScrollView
            contentInsetAdjustmentBehavior="automatic"
            style={styles.scrollView}>
            <View>
              <View style={styles.sectionContainer}>
                <Text style={styles.sectionTitle}>XUpdate</Text>
              </View>
              <View style={styles.logContainer}>
                <Text style={styles.logStyle}>{this.state._message}</Text>
              </View>
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateDefault}
                title="默认App更新"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateSupportBackground}
                title="默认App更新 + 支持后台更新"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateRatio}
                title="调整宽高比"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateForce}
                title="强制更新"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateAutoMode}
                title="自动模式"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.enableChangeDownLoadType}
                title="下载时点击取消允许切换下载方式"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.showRetryDialogTip}
                title="显示重试提示弹窗"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.customJsonParse}
                title="使用自定义json解析"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.checkUpdateByUpdateEntity}
                title="直接传入UpdateEntity进行更新"
                color="#2196F3"
              />
            </View>

            <View style={styles.buttonContainer}>
              <Button
                onPress={this.customPromptDialog}
                title="自定义更新弹窗样式"
                color="#2196F3"
              />
            </View>

            <View style={{height: 20}} />
          </ScrollView>
        </SafeAreaView>
      </>
    );
  }

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
    XUpdate.showRetryUpdateTip(
      'Github下载速度太慢了，是否考虑切换蒲公英下载？',
      'https://www.pgyer.com/flutter_learn',
    );
  }

  ///使用自定义json解析
  customJsonParse() {
    let args = new UpdateArgs(_updateUrl3);
    args.isCustomParse = true;
    XUpdate.update(args);
  }

  ///直接传入UpdateEntity进行更新提示
  checkUpdateByUpdateEntity() {
    let args = new UpdateArgs();
    args.supportBackgroundUpdate = true;
    XUpdate.updateByInfo(args, {
      hasUpdate: AppInfo.hasUpdate,
      versionCode: AppInfo.versionCode,
      versionName: AppInfo.versionName,
      updateContent: AppInfo.updateLog,
      downloadUrl: AppInfo.apkUrl,
      //选填
      isIgnorable: AppInfo.isIgnorable,
      apkSize: AppInfo.apkSize,
    });
  }

  ///自定义更新弹窗样式
  customPromptDialog() {
    let args = new UpdateArgs(_updateUrl);
    args.themeColor = '#FFFFAC5D';
    args.topImageRes = 'bg_update_top';
    args.buttonTextColor = '#FFFFFFFF';
    XUpdate.update(args);
  }

  //自定义解析
  customParser = (json) => {
    let appInfo = JSON.parse(json);
    return {
      //必填
      hasUpdate: appInfo.hasUpdate,
      versionCode: appInfo.versionCode,
      versionName: appInfo.versionName,
      updateContent: appInfo.updateLog,
      downloadUrl: appInfo.apkUrl,
      //选填
      isIgnorable: appInfo.isIgnorable,
      apkSize: appInfo.apkSize,
    };
  };
}

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.white,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  logContainer: {
    marginTop: 16,
    paddingHorizontal: 24,
  },
  logStyle: {
    padding: 16,
    backgroundColor: '#E0E0E0',
    minHeight: 150,
  },
  buttonContainer: {
    marginTop: 16,
    paddingHorizontal: 24,
  },
});
