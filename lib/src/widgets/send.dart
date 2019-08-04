import 'package:flutter/material.dart';
import 'package:txqrfincrypt/src/app_state_singleton.dart';
import 'package:txqrfincrypt/src/widgets/slivers/app_bar.dart';

class SendPage extends StatefulWidget {
  @override
  SendPageState createState() => SendPageState();
}

class SendPageState extends State<SendPage> {
  @override
  void initState() {
    super.initState();
  }

  Widget pageBody() => SliverList(
      delegate: SliverChildListDelegate([
        SafeArea(
          //width: 380,
          child: Column(
            children: <Widget>[
              SizedBox(
                height: 20,
                child: Text(appData.path),
              ),
              SizedBox(
                width: double.infinity,
                child: Center(
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(30.0),
                      child: Text(
                        appData.result,
                        //result,
                        style: new TextStyle(
                            fontSize: 11.0, fontWeight: FontWeight.w600),
                      ),
                    ),
                  ),
                ),
              ),
              SizedBox(
                height: 20,
              ),
              SizedBox(
                height: 50,
                child: Text(appData.message),
              ),
            ],
          ),
        )
      ]),
    );

  Widget txSliverList() => CustomScrollView(
        slivers: <Widget>[
          TxSliverAppBar(),
          pageBody(),
        ],
      );

  @override
  Widget build(BuildContext context) {
    return txSliverList();
  }
}
