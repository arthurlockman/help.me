Help.me
=======

Teammates:
* [Arthur Lockman](https://github.com/arthurlockman)
* [Aditya Nivarthi](https://github.com/SIZMW)
* [Joey Perez](https://github.com/Perezjo94)
* [Tommy Trieu](https://github.com/ttrieu9)

## Description
This application is used to help students find other students in their vicinity that are able to assist them with topics, course work, or projects that they may be working on.

The application allows users to sign in and authenticate with a Google account, in order to store their preferences and information.

Users can add topics to their profile. These topics are topics or content that they have a deep understanding of and are topics they could help others understand.

Users may send requests out when they are in need of assistance as well. These requests will have information about the user, the topics that are pertinent to the help request, and the duration of the request. This request is then sent out and pushed to all other users in the vicinity of the requester, should they be a proper fit to help.

### Backend
This repository contains the frontend Android application for this project. The backend repository is located [here](https://github.com/arthurlockman/help.me-backend).

### Data Storage
The application relies on a [Firebase](https://firebase.google.com/) database for the majority of data storage, including user profiles, help requests, and chat logs. See [here](https://cloud.google.com/solutions/mobile/mobile-firebase-app-engine-flexible) for information to setup a basic Firebase instance.

## Build
Use `gradle` to build this application. Additionally, the project can be imported into [Android Studio](https://developer.android.com/studio/index.html) and build from there.

## Usage
To use this application. open the project in Android Studio and deploy the application on a Android device. This will generate the approrpiate APK and load it onto the device.
