import 'package:flutter/material.dart';
import 'package:txqrfincrypt/src/app_state_singleton.dart';
import 'package:txqrfincrypt/src/tx_database.dart';
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
        delegate: SliverChildBuilderDelegate((BuildContext context, int index) {
          return _makeElement(index);
        }),
      );

  Widget txSliverList() => CustomScrollView(
        slivers: <Widget>[
          TxSliverAppBar(),
          pageBody(),
        ],
      );

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<List<Message>>(
      stream: TxQrData().watchMessageEntriesInCategory(0),
      builder:
          (BuildContext context, AsyncSnapshot<List<Message>> dataSnapshot) {
        if (dataSnapshot.hasError) {
          return Text('Error: ${dataSnapshot.error}');
        }
        appData.messagesList = dataSnapshot.data;
        appData.callbacks.add(() {
          setState(() {
            print("b00p");
            appData.messagesList = dataSnapshot.data;
          });
        });
        return txSliverList();
      },
    );
  }

  Widget _makeElement(int index) {
    print(index);
    print("__");
    print(appData.messagesList.length);
    if (index >= appData.messagesList.length) {
      return null;
    }

    return Container(
      padding: EdgeInsets.all(5.0),
      child: SafeArea(
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
                    padding: const EdgeInsets.all(5.0),
                    child: Stack(
                      children: <Widget>[
                        Align(
                          alignment: Alignment.center,
                          child: Text(
                            appData.messagesList[index].title,
                            //result,
                            style: new TextStyle(
                                fontSize: 11.0, fontWeight: FontWeight.w600),
                          ),
                        ),
                        Align(
                          alignment: Alignment.bottomRight,
                          child: ButtonBar(
                            children: <Widget>[
                              FlatButton(
                                child: Text("Open"),
                                onPressed: () {
                                  //TODO open dialog
                                },
                              )
                            ],
                          ),
                        )
                      ],
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
      ),
    );
  }
}
