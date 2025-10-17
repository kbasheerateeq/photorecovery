# Photo Recovery App

An Android application for recovering and managing photos with delete functionality.

## Features
- Scan and display photos from device storage
- Grid view of recovered photos
- Delete individual photos with confirmation
- Material Design UI
- Runtime permissions handling

## Setup for Codespace
1. Open the project in Android Studio or build directly in Codespace
2. Sync Gradle dependencies
3. Build and run the app

## Permissions
The app requires:
- READ_EXTERNAL_STORAGE
- WRITE_EXTERNAL_STORAGE

## Building
Use `./gradlew assembleDebug` to build the APK

## Notes
- This app scans common directories for image files
- Uses Glide for efficient image loading
- Implements proper background threading for file operations
