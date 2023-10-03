# GazeTurner
## An eReading application controlled using your eyes
GazeTurner is designed for people who struggle with using touchscreens, such as those afflicted with muscle spasticity or poor muscle control. 

![alt text](https://github.com/raymondb-dev/eyeturner-backup/blob/main/images/app.jpg?raw=true)

### Features
- [x] Hands-free navigation
- [x] Control the turning of pages with your eyes
- [x] Add books to your library
- [x] Customisable font size
- [x] Support for .epub files in your local storage 
- [ ] Support for PDF and other formats
- [ ] Support for Dark Mode

## How to install
**Prerequisites:**
- Android Studio
- A real Android device on Android 11 or higher
- SeeSo SDK License (see below)

**Get SeeSo SDK licence (for 30 days):**
1. Go to https://seeso.io/
2. Click on 'Free Trial'
3. On the following page, click "License Keys" on the sidebar.
4. Sign up for the free trial using your email account. Fill out the details accordingly.
5. Copy the generated access key to your clipboard.

**Install GazeTurner on your Android phone:**
1. Open the 'EyeTurner' folder in Android Studio
2. Open the file titled 'MainActivity.java'
3. Using your SeeSo SDK key, paste the key into the variable "yourDevKey" under the onCreate function.
4. Select the connected device from the menu
5. Run the app.
6. Enjoy!

## How to use
![alt text](https://github.com/raymondb-dev/eyeturner-backup/blob/main/images/gestures.jpg?raw=true)

### Add Books
To add new eBooks to your library, select "Add Books" on the main page to bring up the Android file picker. By default it opens on your Download folder, so please place your files there for convenience. You can only select files with the '.epub' file extension. 

### Select Books
Perform "LOOK LEFT" or "LOOK RIGHT" gestures to scroll backwards and forwards through your library of books. Perform a "LOOK UP" gesture to open the selected book. 

### Read Books
On the reading view, perform "LOOK LEFT" or "LOOK RIGHT" gestures to turn the pages of your select book. 

### Change Font Size
On the main page, perform a "LOOK DOWN" gesture to open settings. On this page, perform "LOOK LEFT" or "LOOK RIGHT" gestures to change the font size of the application. 




