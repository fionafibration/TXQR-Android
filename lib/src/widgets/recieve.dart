import 'package:flutter/material.dart';
import 'package:txqrfincrypt/src/app_state_singleton.dart';
import 'package:txqrfincrypt/src/tx_database.dart';

class RecievePage extends StatefulWidget {
  @override
  RecievePageState createState() => RecievePageState();
}

class RecievePageState extends State<RecievePage> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<List<Message>>(
      stream: TxQrData().watchMessageEntriesInCategory(1),
      builder: (BuildContext context, AsyncSnapshot<List<Message>> dataSnapshot) {
        if (dataSnapshot.hasError) {
          return (Text('Error: ${dataSnapshot.error}'));
        }
        appData.incomingMessagesList = dataSnapshot.data;
        appData.callbacks.add( () {
          setState(() {
           print("b00p01");
           appData.incomingMessagesList = dataSnapshot.data; 
          });
        });
        return Text("0");
      },
    );
  }
}
