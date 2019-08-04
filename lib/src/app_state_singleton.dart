import 'dart:async';
import 'package:txqrfincrypt/src/tx_database.dart';

class AppData {
  static final AppData _appData = new AppData._internal();
  List<Message> messagesList = [];

  List<Function> callbacks = [];

  String text = "default_VALUE:(";
  String result = "Hey there !";
  String path = "";
  String message = 'No Message';
  factory AppData() {
    return _appData;
  }
  AppData._internal();

  void onDataInsert() async {
    print("B00p");
    for (Function f in callbacks) {
      f();
    }
    messagesList = await TxQrData().allMessageEntries;
  }
}

final appData = AppData();
