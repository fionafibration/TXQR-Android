import 'package:flutter/material.dart';
import 'package:qr_flutter/qr_flutter.dart';

class TxSliverAppBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
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
            title: LimitedBox(
              child: Row(
                children: <Widget>[
                  QrImage(
                    data: "v2.1.2 alpha",
                    size: 60,
                    foregroundColor: Colors.black,
                    backgroundColor: Colors.white,
                    version: 1,
                  ),
                  SizedBox(
                    width: 10.0,
                  ),
                  SizedBox(
                    height: 49,
                    child: Column(
                      children: <Widget>[
                        Text("TxQr Messaging"),
                        Text(
                          "Written by:",
                          style: TextStyle(
                              fontSize: 11.0, fontWeight: FontWeight.w200),
                        ),
                        Text(
                          "Max Paulson & Finian Blackett",
                          style: TextStyle(
                              fontSize: 11.0, fontWeight: FontWeight.w200),
                        ),
                      ],
                    ),
                  ),
                  SizedBox(
                    width: 20,
                  ),
                ],
              ),
            )),
      );
  }
}