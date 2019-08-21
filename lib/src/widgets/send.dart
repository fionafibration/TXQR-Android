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
        child: Card(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            mainAxisSize: MainAxisSize.max,
            children: <Widget>[
              Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  SizedBox(
                    width: 120,
                  )
                ],
              ),
              Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  Padding(
                    padding: EdgeInsets.all(10),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      mainAxisSize: MainAxisSize.max,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: <Widget>[
                        Text(
                          appData.messagesList[index].title,
                          style: TextStyle(
                              fontSize: 24, fontWeight: FontWeight.w400),
                        )
                      ],
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    mainAxisSize: MainAxisSize.max,
                    children: <Widget>[
                      FlatButton(
                        child: Text(
                          "Open",
                          style: TextStyle(
                            fontSize: 20,
                          ),
                        ),
                        onPressed: () {
                          //TODO open dialog
                        },
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
