# Usage

## Import Service Files
At first, you should import all `.aidl` files and `Device class` java files into your Android application project. `Device class` java files include `RaritanDevice.java`, `VerisDevice.java`, `VeraDevice.java` and java files under the `VeraSensor` directory. These class files have all implemented `Parcelable` interface which can be used for passing objects over Android IPC. **IMPORTANT** These imported files in your application project should have the same package path as they are in the HomeSystem service.

## SensorReportService Interface
This interface will return a `HomeSystem` object to the caller. This object contains all sensor information at home. API functions are listed below:
* HomeSystem reportHomeSensor()

## VeraService Interface


## VerisService Interface


## RaritanService Interface


## Call IPC Methods



