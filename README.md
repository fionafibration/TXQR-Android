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
	1. *coming soon*
* For Linux
	1. *coming soon*
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
	1. *coming soon*
* Linux
	1. *coming soon*

### Why We Made TxQR
We took Divan's weekend coding project to create a transfer protocol that used Gifs of QR codes aslo known as TXQR as a challenge. This project began as a simple objective, to port the GO implementation into an all in one android app capable of sending and recieving the protocols "packets."

As we soon realized that interacting with GO from within an android application was too difficult to be reasonable, Fin decided to implement the fundamental design of TXQR in python. This has two parts; An Encoder and a Decoder. The TxQR design uses gifs to transfer encoded data, because a QR reader can take time to scan a code and restart there is a high rate of data loss or disorginization as it is problematic to scan all codes in order quickly. To get around this Divan found that a method of [Forward Error Correction(FEC)](https://en.wikipedia.org/wiki/Forward_error_correction) could be used to recover lost QRCode data. In particular the FEC of choice was a specification known as [Foutain Coding](https://en.wikipedia.org/wiki/Fountain_code) and more specifically an implementation called [Luby Transform](https://en.wikipedia.org/wiki/Luby_transform_code). I wont bother explaning FEC, Foutain Coding and Luby Tranform in depth, but simply put the idea is that by taking the data we want to send and adding extra data, we can then use some mathematic trickery to find out what the original data was from a subset of the total data (the original message and the extra).

Our original python implementation was two projects, LtErrorcorrection was the decoder and QrStreamer was the encoder. Using a SDK plugin for using Python in Android known as ChaquoPy we implemented a QRReader that was specifically designed to use LtErrorcorrection to decode the gifs created by QrStreamer. The original TxQR Messaging (V1.x) was deveoped using the Android platform and appcompat activities which meant that it lacked a certain UI modernity that Nova found particulary annoying.

In order to continue development, the decision was made to migrate the application to Flutter which is a cross platfrom application development SDK written in Dart by Google. This had many benefits, the UI could be prototyped quickly thanks to the lack of XML layouts and Hot Reloading. The use of method channels to platfrom implementations meant that the heavy logic we needed to implement for Encoding and Decoding could be done in Android just like before. But of course as with any platfrom change there were some issues. Firstly ChaquoPy would not function correctly in our Android module and as such the entirity of LtErrorcorrection had to be migrated from Python to Kotlin.

The migration of LtErrorcorrection and then subsequent implementation of QrStreamer has be fraught with problems. Due to Fins inexperience with Kotlin the whole implementation is and has served as a learning experience for him and as only natural there are issues that popup for weird reasons due to the difference between ways to poblem solve in Kotlin and Python.

As of now, the decoding implementation works with the Original python QRStreamer package and the encoding implementation does not produce decodable data, but it is being worked on activly.

### Future Plans
* V2.1.2
	* Encoded Gif Storage and viewing
* V2.2
	* Card based message storage and sending system
		1. After decoding a message a collapsible card will be added to the home page and will contain the message.
		2. Each card can be deleted, edited or archived or reencoded
		3. Cards persist so long as the app remains installed
		4. Creating a new card to send data is possible
		5. There will be enhcancements to the Encoded management system
* V2.3-pre(tentative)
    * Website with support for sending and receiving messages on the desktop
* V2.3
	* The Errorcorrection system will be upgraded to include implementations of [RaporQ](https://openrq-team.github.io/openrq/) (this is not 100% going to happen)
		* Upgraded QrStreamer package for desktop use
	* A revamped encoder system will also include ability to upload and send files(Size constrains dependant on factors)
	* TxQR will be registered as an android share action handler so other apps can send it data to be encoded

### Credits

Uses a slightly modified port of [anrosent's LT coding library](https://github.com/anrosent/LT-Code) to perform Luby Transform Foutain coding. Inspired by (but incompatible with) [divan's TXQR for iPhones](https://github.com/divan/txqr)