[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1419527.svg)](https://doi.org/10.5281/zenodo.1419527)
---
# Project Title

SENDA(Sensor Data Streamer) is an android application to stream real time sensor reading using LSL (Lab Streaming Layer). Along with the sensors it streams real time audio as well. Following sensors are included: 

- Accelerometer
- Light
- Proximity
- Gravity
- Linear Acceleration
- Rotation Vector
- Step Count

## Getting Started
---
#### Development:

In order to start with development you need to follow these steps: 

- Clone this repository
- Open project with Android Studio

If you get errors related to native, built in functions such as fgetpos and fsetpos in cstdio, this problem arises beacuse of the version of the ndk that you are using. Follow this link https://developer.android.com/ndk/downloads/older_releases.html to download Revision 14b. Now to update the ndk in android studio go to File -> Project Structure -> SDK Location -> Android NDK location and set the path to point at android-ndk-r14b.

#### Usage: 

Install this application and start streaming data by clicking on Start LSL button. You can record this data on PC using Lab Recoder from https://github.com/sccn/labstreaminglayer/tree/master/Apps/LabRecorder. 

## Built With

* [Android Studio](https://developer.android.com/studio/) - Android development framework
* [LSL ](https://github.com/sccn/labstreaminglayer) - Lab Streaming Layer library

## Contributing

Please feel free to contribute to this project by creating an issue first and then sending a pull respectively. 

## Authors

* **Ali Ayub Khan** - [AliAyub007](https://github.com/AliAyub007)


## License

This project is licensed under GNU General Public License License - see the [LICENSE.md](https://github.com/AliAyub007/SENDA/blob/master/LICENSE) file for details

## Acknowledgments

[liblsl Library](https://github.com/sccn/labstreaminglayer/tree/master/LSL): I used this library to develop this application. 

## Cite As: 
You can cite this work in your research using text below:

    Ali Ayub Khan. (2018, September 15). AliAyub007/SENDA: First Release (Version v1.0.0). Zenodo. http://doi.org/10.5281/zenodo.1419527

