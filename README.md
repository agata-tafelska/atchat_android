# ATChat Android
Simple chat application designed to communicate between users by sending text messages. This application, implemented using gRPC framework, is another type of client for ATChat gRPC server. Project where server and desktop client are implemented can be found [here](https://github.com/agata-tafelska/chat-grpc)..

## Motivation
Project is my first Android application created for getting to know the features of Android framework and practicing Java coding skills as well.

## Requirements
* Android >= 5.0 (API level 21)

## Install
* [APK](https://github.com/agata-tafelska/atchat_android/releases/download/1.0/chat-debug.apk) direkt download  
If you want to host your server, download latest of [server releases](https://github.com/agata-tafelska/chat-grpc/releases)
 from [ATChat repository](https://github.com/agata-tafelska/chat-grpc).

## Build
If you want to build this app yourself, clone this repository:
```
https://github.com/agata-tafelska/atchat_android.git
```
or download [master branch latest archive](https://github.com/agata-tafelska/atchat_android/archive/master.zip).  
Then open/import the project in Android Studio and build it.

## Overview  
### Structure:
**1. Protofile** - file containing app's service definition
* specifies chat's models and methods for communication between client and server
* protocol buffer compiler (protoc) automatically generates data access classes in Java language out of above definition
* project uses exactly the same proto file as server - both depends on it

**2. ChatService** - uses channel specified by server address and port number to create asynchronous stub that makes calls to the server

**3. Activities** - handle inputs provided by the user and pass them to the methods called on ActivitiesCoordinator:
* MainActivity - welcome activity prompting to select preffered way of joining chat
* JoinAsGuestActivity - enables to use chat as guest
* RegisterActivity - enables to create user account
* LoginActivity - enables existing users signing in
* ChatActivity - displays entire conversation and enables to send message and follow current users; holds LiveData observers, which, once notified, immediatelly update UI with latest changes (like new messages and current users list)
  
**4. ActivitiesCoordinator** - holds an instance of ChatService that enables to send requests to the server
* coordinates switching between different activities
* holds LiveData instances observed by ChatActivity

### Screenshots

| Main chat screen | Join as guest screen | Register screen |
| --- | --- | --- |
|![](screenshots/device-2020-04-04-main_screen.png)|![](screenshots/device-2020-04-04-join_as_guest_screen.png)|![](screenshots/device-2020-04-04-register_screen.png)|

| Login screen | Chat screen | Users bottom sheet |
| --- | --- | --- |
![](screenshots/device-2020-04-04-login_screen.png)|![](screenshots/device-2020-04-04-conversation.png)|![](screenshots/device-2020-04-04-users_list.png)|