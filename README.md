
# react-native-xupdate

A React-Native plugin for XUpdate(Android Version Update Library).

## Getting started

`$ npm install react-native-xupdate --save`

### Mostly automatic installation

`$ react-native link react-native-xupdate`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-xupdate` and add `RNXUpdate.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNXUpdate.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.xuexiang.xupdate.RNXUpdatePackage;` to the imports at the top of the file
  - Add `new RNXUpdatePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-xupdate'
  	project(':react-native-xupdate').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-xupdate/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-xupdate')
  	```


## Usage
```javascript
import {XUpdate} from 'react-native-xupdate';

XUpdate;
```
