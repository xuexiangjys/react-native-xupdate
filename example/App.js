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

import {
    Colors,
} from 'react-native/Libraries/NewAppScreen';

import {XUpdate, InitArgs} from "./xupdate";

export default class App extends Component<{}> {

    state = {
        _message: '',
    };

    componentDidMount() {
        let args = new InitArgs();
        args.debug = true;
        args.isPostJson = false;
        args.isWifiOnly = false;
        args.isAutoMode = false;
        args.supportSilentInstall = false;
        args.enableRetry = false;

        XUpdate.init(args).then(result => {
            this.setState({
                _message: '初始化成功:' + JSON.stringify(result)
            })
        }).catch(error => {
            console.log(error);
            this.setState({
                _message: '初始化失败:' + error
            })
        })
    }


    render() {
        return (
            <>
                <StatusBar barStyle="default"/>
                <SafeAreaView>
                    <ScrollView
                        contentInsetAdjustmentBehavior="automatic"
                        style={styles.scrollView}>
                        <View>
                            <View style={styles.sectionContainer}>
                                <Text style={styles.sectionTitle}>XUpdate</Text>
                            </View>
                            <View style={styles.logContainer}>
                                <Text style={styles.logStyle}>
                                    {this.state._message}
                                </Text>
                            </View>
                        </View>

                        <View style={styles.buttonContainer}>
                            <Button
                                onPress={checkUpdateDefault}
                                title="默认App更新"
                                color="#2196F3"
                            />
                        </View>

                        <View style={styles.buttonContainer}>
                            <Button
                                onPress={checkUpdateSupportBackground}
                                title="默认App更新 + 支持后台更新"
                                color="#2196F3"
                            />
                        </View>
                    </ScrollView>
                </SafeAreaView>
            </>
        );
    }
}

function checkUpdateDefault() {



}

function checkUpdateSupportBackground() {

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
