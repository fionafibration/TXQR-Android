class AppData {
  static final AppData _appData = new AppData._internal();
  
  String text = "default_VALUE:(";
  String result = "Hey there !";
  String path = "";
  String message = 'No Message';
  factory AppData() {
    return _appData;
  }
  AppData._internal();
}
final appData = AppData();