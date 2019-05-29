# TXQR Messaging

TXQRAndroid is an Android app allowing for file transfer through multiple QR codes, either in a .gif file or as individual printed or displayed images. While 


TXQRAndroid uses a form of forward error correction coding called "fountain coding," specifically an algorithm called the [Luby Transform](https://divan.dev/posts/fountaincodes/). 
This means that TXQR messaging uses more QR codes than strictly necessary to transfer data, but this provides resiliency in that 
specific QR codes need not be scanned in order to decode the message. In fact, QR codes can be arbitrarily skipped over when decoding the message. 
Generally, as long as you scan enough QR codes, no matter the order or specific ones you scan, the message will be decoded. 


TXQRAndroid has built-in facilities for generating these QR code GIFs, but for more fine-grained control over the actual QR encoding itself, 
we provide a Python package called [QRStreamer](https://github.com/ThePlasmaRailgun/QRStreamer) to generate GIFs and images. 

## Usage 

TxQR Messaging is built using the Flutter framework with most of the logic written on android with Kotlin.
Building and running Flutter projects such as TxQR Messaging can be done on Windows, macOS, and Linux.
You can find detailed information about Flutter at [Flutter.dev](https://flutter.dev).
##### Prerequisites
* For Windows:
	1. The latest release of Android Sudio with the Android SDK version 28 or greater
	2. An Android device running API 26(Android Oreo 8.0) or higher *Note:* we develop using API 28 and do not guarntee runtime on API 26
* For OSX
	1. 
* For Linux
	1. 
##### Installing Flutter and setting up development environment
* Windows
	1. Download the [Flutter SDK](https://storage.googleapis.com/flutter_infra/releases/stable/windows/flutter_windows_v1.5.4-hotfix.2-stable.zip)
	2. Extract the file in the ZIP to C:/src/flutter
	3. Start the Flutter console by locating the `flutter_console.bat` and double clicking it
		* Optional, add the path C:/src/flutter to the PATH system environment variable
	4. If Flutter is on your path run `flutter doctor`
	5. Flutter requires an android device to be connected to the system for the app to run, this can be accomplished either by installing and running an [Android Virtual Device](https://flutter.dev/docs/get-started/install/windows#set-up-the-android-emulator), *or* by [Enabling USB Debugging and plugging a physical device into your system](https://flutter.dev/docs/get-started/install/windows#set-up-your-android-device)
	6. Editors: Although Flutter requires a complete Android Studio install, development is supported by [IntellijIdea](https://flutter.dev/docs/get-started/editor?tab=androidstudio) and [Visual Studio Code](https://flutter.dev/docs/get-started/editor?tab=vscode) via thier respective Flutter plugins
	7. From here you can follow the instructions for running in your desired editor or command line here [Flutter.dev](https://flutter.dev/docs/get-started/test-drive?tab=)
		* *Important:* Instead of following the instructions for "Create the app", instead do (your editor may run these commands automatically, but its still a good idea to do them yourself):
		* `flutter packages get`
		* Then you can run normally
* OSX
	1. 
* Linux
	1. 

### Credits

Uses a slightly modified port of [anrosent's LT coding library](https://github.com/anrosent/LT-Code) to perform LT coding. Inspired by (but incompatible with) [divan's TXQR for iPhones](https://github.com/divan/txqr)