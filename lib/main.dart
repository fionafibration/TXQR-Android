import 'dart:async';

import 'package:bottom_navy_bar/bottom_navy_bar.dart';
import 'package:flutter/material.dart';

//import 'package:barcode_scan/barcode_scan.dart';
//import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter/services.dart';
import 'package:share/share.dart';
import 'package:txqrfincrypt/src/app_state_singleton.dart';
import 'package:txqrfincrypt/src/widgets/recieve.dart';
import 'package:txqrfincrypt/src/widgets/send.dart';

void main() => runApp(MaterialApp(
      theme: ThemeData(primaryColor: Colors.black, primarySwatch: Colors.blue),
      debugShowCheckedModeBanner: false,
      home: HomePage(),
    ));

class HomePage extends StatefulWidget {
  @override
  HomePageState createState() {
    return new HomePageState();
  }
}

class HomePageState extends State<HomePage> {
  int _selectedIndex = 0;

  static const platform = const MethodChannel('tx.novalogic.dev/fincrypt');
  final txPageController = PageController(
    initialPage: 0,
  );

  //static final _notDone = 'DECODE.NOT_DONE';
  // Get the message
  Future<void> _scan(String methodToInvoke) async {
    try {
      String result = await platform.invokeMethod(methodToInvoke);
      setState(() {
        result = result;
        appData.message = "A Message was decoded YAY";
      });
    } on PlatformException catch (ex) {
      if (ex.code == "PERMISSION_NOT_GRANTED") {
        setState(() {
          appData.result = "Camera permission was denied";
        });
      } else {
        setState(() {
          appData.result = "Unknown Error $ex";
        });
      }
    } on FormatException {
      setState(() {
        appData.result = "You pressed the back button before scanning anything";
      });
    } catch (ex) {
      setState(() {
        appData.result = "Unknown Error $ex";
      });
    }
  }

  void _showModalBottomSheet(BuildContext context) {
    showModalBottomSheet(
        context: context,
        builder: (context) => Material(
            clipBehavior: Clip.antiAlias,
            color: Colors.white,
            child: Container(
              height: 220,
              child: Column(
                //mainAxisAlignment: MainAxisAlignment.spaceAround,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  bottomMenuHeader(),
                  Expanded(
                    child: SizedBox(
                      height: 40,
                      child: GridView.count(
                        crossAxisSpacing: 2,
                        crossAxisCount: 4,
                        children: <Widget>[
                          IconButton(
                            iconSize: 40,
                            icon: Icon(Icons.share),
                            color: Colors.blueGrey[600],
                            tooltip: 'Share',
                            onPressed: () {
                              Share.share(appData.result);
                            },
                          ),
                          IconButton(
                            iconSize: 40,
                            icon: Icon(Icons.camera_alt),
                            color: Colors.blueGrey[600],
                            tooltip: 'Scan a TxQR',
                            onPressed: () {
                              _scan('scan');
                            },
                          ),
                          IconButton(
                            iconSize: 40,
                            icon: Icon(Icons.camera),
                            color: Colors.blueGrey[600],
                            tooltip: 'Alternate Scanner',
                            onPressed: () {
                              _scan('altScan');
                            },
                          ),
                          IconButton(
                            iconSize: 40,
                            icon: Icon(Icons.code),
                            color: Colors.blueGrey[600],
                            tooltip: 'OutputQR v1',
                            onPressed: () async {
                              String path =
                                  await platform.invokeMethod('makeQR', appData.result);
                              setState(() {
                                appData.path = path;
                              });
                            },
                          )
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            )));
  }

  Widget bottomMenuHeader() => Ink(
        decoration: BoxDecoration(
            gradient: LinearGradient(colors: [Colors.black, Colors.blueGrey])),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  // crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    Text(
                      "TxQr Fincrypt",
                      style: TextStyle(
                          fontSize: 20.0,
                          fontWeight: FontWeight.w700,
                          color: Colors.green),
                    ),
                    SizedBox(
                      height: 5.0,
                    ),
                    Text(
                      "Things To Do",
                      style: TextStyle(
                          fontSize: 15.0,
                          fontWeight: FontWeight.normal,
                          color: Colors.blueAccent),
                    ),
                  ],
                ),
              )
            ],
          ),
        ),
      );
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      //body: txSliverList(),
      body: PageView(
        controller: txPageController,
        children: <Widget>[SendPage(), RecievePage()],
        onPageChanged: (int newPageIndex) => {
          setState(() {
            _selectedIndex = newPageIndex;
          })
        },
      ),
      bottomNavigationBar: BottomAppBar(
        shape: CircularNotchedRectangle(),
        notchMargin: 4,
        child: Container(
          height: 50.0,
          child: BottomNavyBar(
            selectedIndex: _selectedIndex,
            showElevation: true, // use this to remove appBar's elevation
            onItemSelected: (index) => setState(() {
              _selectedIndex = index;
              txPageController.animateToPage(index,
                  duration: Duration(milliseconds: 300), curve: Curves.ease);
            }),
            items: [
              BottomNavyBarItem(
                  icon: Icon(Icons.file_upload),
                  title: Text('Send'),
                  activeColor: Colors.blueGrey),
              BottomNavyBarItem(
                  icon: Icon(Icons.file_download),
                  title: Text('Recieve'),
                  activeColor: Colors.blueGrey),
            ],
          ),
        ),
      ),
      floatingActionButton: Builder(builder: (BuildContext myContext) {
        return FloatingActionButton.extended(
            icon: Icon(Icons.adb),
            backgroundColor: Colors.blueGrey[900],
            label: Text("Go"),
            onPressed: () async {
              //_scanQR(myContext);
              //_scan();
              _showModalBottomSheet(context);
            });
      }),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
    );
  }
}
