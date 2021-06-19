# RoomExampleAdvanced

Before you can run the app, please apply api keys
(1) Google MAP api key: after applying, type the key into the AndroidManifest.xml
(2) Taiwan CWB open data api key: after applying, type the key into RetrofitService.kt
(3) Enable Firebase: Firestore, Storage and download google-services.json into the app folder

The recyclerView supports the following touch events:
(1) click to view the details
(2) long-click to edit the data
(3) swipe-left or swipe-right to delete the data

Support bottom navigation
(1) Favorite: selected scenes from the public area and add new scene that can be
    uploaded to the public area
(2) Discovery: public shared area with data uploaded by registered users
