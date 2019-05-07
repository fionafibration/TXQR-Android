import 'dart:async';

import 'package:flutter/material.dart';
import 'package:barcode_scan/barcode_scan.dart';
import 'package:flutter/services.dart';
import 'package:share/share.dart';

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
  String result = "Hey there !";
  static const platform = const MethodChannel('tx.novalogic.dev/fincrypt');
  static final _notDone = 'DECODE.NOT_DONE';
  // Get the message
  String _message = 'No Message';

  Future<void> _getMessage(String msg) async {
    String message;
    try {
      final String result = await platform.invokeMethod('decodeMessage', msg);
      message = result;
    } on PlatformException catch (e) {
      message = 'Failed to retrieve message ${e.message}';
    }

    setState(() {
      _message = message; 
    });
  }

  Future _scanQR() async {
    try {
      String qrResult = await BarcodeScanner.scan();
      setState(() async {
        await _getMessage(qrResult);
        if (_message == _notDone) {
          _scanQR();
        } else {
          result = _message;
        }
      });
    } on PlatformException catch (ex) {
      if (ex.code == BarcodeScanner.CameraAccessDenied) {
        setState(() {
          result = "Camera permission was denied";
        });
      } else {
        setState(() {
          result = "Unknown Error $ex";
        });
      }
    } on FormatException {
      setState(() {
        result = "You pressed the back button before scanning anything";
      });
    } catch (ex) {
      setState(() {
        result = "Unknown Error $ex";
      });
    }
  }

  Widget txAppBar() => SliverAppBar(
        backgroundColor: Colors.black,
        pinned: true,
        elevation: 10.0,
        forceElevated: true,
        expandedHeight: 150.0,
        flexibleSpace: FlexibleSpaceBar(
          centerTitle: false,
          background: Container(
            decoration: BoxDecoration(
                gradient: LinearGradient(
                    colors: ([
              Colors.blueGrey[800],
              Colors.black87,
            ]))),
          ),
          title: Row(
            children: <Widget>[
              FlutterLogo(
                colors: Colors.blueGrey,
                textColor: Colors.white,
              ),
              SizedBox(
                width: 10.0,
              ),
              Text("TxQr Fincrypt"),
              SizedBox(width: 20,),
              IconButton(
                icon: Icon(Icons.share),
                color: Colors.blueGrey[600],
                tooltip: 'Share',
                onPressed: () {
                  Share.share(result);
                },
              ),
              IconButton(
                icon: Icon(Icons.file_upload),
                color: Colors.green[300],
                tooltip: 'test',
                onPressed: () {
                  _getMessage("oza");
                },
              ),
            ],
          ),
        ),
      );

  Widget txBody() => SliverList(
        delegate: SliverChildListDelegate([
          SizedBox(
            width: 380,
            child: Column(
              children: <Widget>[
                SizedBox(
                  height: 20,
                ),
                SizedBox(
                  width: 360,
                  child: Center(
                    child: Text(
                      result,
                      style: new TextStyle(
                          fontSize: 30.0, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
                SizedBox(
                  height: 20,
                ),
                Text(_message),
              ],
            ),
          )
        ]),
      );

  Widget txSliverList() => CustomScrollView(
        slivers: <Widget>[
          txAppBar(),
          txBody(),
        ],
      );

  Widget txDefaultAppBar() => AppBar(
        title: Text("QR Scanner"),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.share),
            tooltip: 'Search',
            onPressed: () {
              Share.share(result);
            },
          ),
        ],
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: txSliverList(),
      floatingActionButton: FloatingActionButton.extended(
        icon: Icon(Icons.camera_alt),
        label: Text("Scan"),
        onPressed: _scanQR,
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
    );
  }
}
