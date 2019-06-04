# cordova-plugin-gpsmockchecker

This is a cordova plugin to avoid mock locations.

This plugin get mock location in Android api <= 22 AND api > 22

You can choose a white list of packages.

## Supported Platforms

- Android API all versions

## Installation

```bash
cordova plugin add cordova-plugin-gpsmockchecker
```

## Usage in typescript / ionic

```js
    this.platform.ready().then(() => {

      var whiteList = ['com.package.example','com.package.example2'];

      (<any>window).gpsmockchecker.check(whiteList, (result) => {
        
        console.log(result);

        if(result.isMock){
            console.log("DANGER!! Mock is in use");
            console.log("Apps that use gps mock: ");
            console.log(result.mocks);
        }
        else
          console.log("All its ok");
  
  
      }, (error) => console.log(error));
      
    });
```


## Usage in javascript

```js

document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {  
  var whiteList = ['com.package.example','com.package.example2'];
  mockchecker.check(whiteList, successCallback, errorCallback);
}

function successCallback(mockStatus) {
  console.log(mockStatus);
}

function errorCallback(error) {
  console.log(error);
}
```

## mockStatus

Contains mock status :

### properties

- isMock : (boolean) true if device mock, false if no mock behavior detected.
- mocks : (array) return APPs using mock. 

