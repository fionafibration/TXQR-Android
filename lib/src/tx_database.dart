import 'package:moor_flutter/moor_flutter.dart';
import 'package:txqrfincrypt/src/app_state_singleton.dart';
export 'package:moor_flutter/moor_flutter.dart' show Value;

/*
Generating the code #
Moor integrates with the dart build system, so you can
generate all the code needed with:

flutter packages pub run build_runner build

If you want to continuously
rebuild the generated code whenever you change your
code, run:

flutter packages pub run build_runner watch
*/

// assuming that your file is called filename.dart. This will give an error at first,
// but it's needed for moor to know about the generated code
part 'tx_database.g.dart';

// this will generate a table called "todos" for us. The rows of that table will
// be represented by a class called "Todo".
class Messages extends Table {
  IntColumn get id => integer().autoIncrement()();

  //This will tell us stuff about the content except its mimetype
  TextColumn get metaContent => text().nullable()();

  //The mimeType is something like 	text/plain which will inform a builder on how to handle the bodies content
  TextColumn get mimeType => text()();

  //Makes messages easier to recognize in lists
  TextColumn get title => text().withLength(min: 6, max: 32)();

  //The message content
  TextColumn get content => text().named('body')();

  //The id of the Codes entry where a realted code can be found, if the item is of category 0
  IntColumn get codeLocationId => integer().nullable()();

  //Category 0,1 where 0 is page 0 and 1 is page 1
  IntColumn get category => integer()();
}

class Codes extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get path => text()();
}

// this annotation tells moor to prepare a database class that uses both of the
// tables we just defined. We'll see how to use that database class in a moment.
@UseMoor(tables: [Messages, Codes])
class TxQrData extends _$TxQrData {
  // we tell the database where to store the data with this constructor
  TxQrData()
      : super(FlutterQueryExecutor.inDatabaseFolder(path: 'txqrdb.sqlite'));

  //bump this number whenever you change or add a table definition
  @override
  int get schemaVersion => 5;

  // returns the generated id
  Future<int> addNewMessageEntry(MessagesCompanion entry) {
    appData.onDataInsert();
    return into(messages).insert(entry);
  }

  // loads all message entries
  Future<List<Message>> get allMessageEntries => select(messages).get();

  // delete the item by its id
  Future deleteMessageById(int id) {
    return (delete(messages)..where((item) => item.id.equals(id))).go();
  }

  // delete all items
  Future deleteMessage(int howMany) {
    return (delete(messages)
          ..where((item) => item.id.isSmallerThanValue(howMany)))
        .go();
  }

  //isSmallerThanValue(10)

  // loads all messages sorted alphabetically
  Future<List<Message>> sortMessageEntriesAlphabetically() {
    return (select(messages)
          ..orderBy([(t) => OrderingTerm(expression: t.title)]))
        .get();
  }

  // watches all mesaage entries in a given category. The stream will automatically
  // emit new items whenever the underlying data changes.
  Stream<List<Message>> watchMessageEntriesInCategory(int c) {
    return (select(messages)..where((t) => t.category.equals(c))).watch();
  }
}
