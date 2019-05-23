# TXQR Messaging

TXQRAndroid is an Android app allowing for file transfer through multiple QR codes, either in a .gif file or as individual printed or displayed images. While 


TXQRAndroid uses a form of forward error correction coding called "fountain coding," specifically an algorithm called the [Luby Transform](https://divan.dev/posts/fountaincodes/). 
This means that TXQR messaging uses more QR codes than strictly necessary to transfer data, but this provides resiliency in that 
specific QR codes need not be scanned in order to decode the message. In fact, QR codes can be arbitrarily skipped over when decoding the message. 
Generally, as long as you scan enough QR codes, no matter the order or specific ones you scan, the message will be decoded. 


TXQRAndroid has built-in facilities for generating these QR code GIFs, but for more fine-grained control over the actual QR encoding itself, 
we provide a Python package called [QRStreamer](https://github.com/ThePlasmaRailgun/QRStreamer) to generate GIFs and images. 

### Usage 

Please build with Flutter and/or Android Studio. Play Store upload will be happening once we get more features integrated and pretty up the UI.

### Credits

Uses a slightly modified port of [anrosent's LT coding library](https://github.com/anrosent/LT-Code) to perform LT coding. Inspired by (but incompatible with) [divan's TXQR for iPhones](https://github.com/divan/txqr)