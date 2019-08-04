// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'tx_database.dart';

// **************************************************************************
// MoorGenerator
// **************************************************************************

// ignore_for_file: unnecessary_brace_in_string_interps
class Message extends DataClass implements Insertable<Message> {
  final int id;
  final String metaContent;
  final String mimeType;
  final String title;
  final String content;
  final int category;
  Message(
      {@required this.id,
      this.metaContent,
      @required this.mimeType,
      @required this.title,
      @required this.content,
      @required this.category});
  factory Message.fromData(Map<String, dynamic> data, GeneratedDatabase db,
      {String prefix}) {
    final effectivePrefix = prefix ?? '';
    final intType = db.typeSystem.forDartType<int>();
    final stringType = db.typeSystem.forDartType<String>();
    return Message(
      id: intType.mapFromDatabaseResponse(data['${effectivePrefix}id']),
      metaContent: stringType
          .mapFromDatabaseResponse(data['${effectivePrefix}meta_content']),
      mimeType: stringType
          .mapFromDatabaseResponse(data['${effectivePrefix}mime_type']),
      title:
          stringType.mapFromDatabaseResponse(data['${effectivePrefix}title']),
      content:
          stringType.mapFromDatabaseResponse(data['${effectivePrefix}body']),
      category:
          intType.mapFromDatabaseResponse(data['${effectivePrefix}category']),
    );
  }
  factory Message.fromJson(Map<String, dynamic> json,
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return Message(
      id: serializer.fromJson<int>(json['id']),
      metaContent: serializer.fromJson<String>(json['metaContent']),
      mimeType: serializer.fromJson<String>(json['mimeType']),
      title: serializer.fromJson<String>(json['title']),
      content: serializer.fromJson<String>(json['content']),
      category: serializer.fromJson<int>(json['category']),
    );
  }
  @override
  Map<String, dynamic> toJson(
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return {
      'id': serializer.toJson<int>(id),
      'metaContent': serializer.toJson<String>(metaContent),
      'mimeType': serializer.toJson<String>(mimeType),
      'title': serializer.toJson<String>(title),
      'content': serializer.toJson<String>(content),
      'category': serializer.toJson<int>(category),
    };
  }

  @override
  T createCompanion<T extends UpdateCompanion<Message>>(bool nullToAbsent) {
    return MessagesCompanion(
      id: id == null && nullToAbsent ? const Value.absent() : Value(id),
      metaContent: metaContent == null && nullToAbsent
          ? const Value.absent()
          : Value(metaContent),
      mimeType: mimeType == null && nullToAbsent
          ? const Value.absent()
          : Value(mimeType),
      title:
          title == null && nullToAbsent ? const Value.absent() : Value(title),
      content: content == null && nullToAbsent
          ? const Value.absent()
          : Value(content),
      category: category == null && nullToAbsent
          ? const Value.absent()
          : Value(category),
    ) as T;
  }

  Message copyWith(
          {int id,
          String metaContent,
          String mimeType,
          String title,
          String content,
          int category}) =>
      Message(
        id: id ?? this.id,
        metaContent: metaContent ?? this.metaContent,
        mimeType: mimeType ?? this.mimeType,
        title: title ?? this.title,
        content: content ?? this.content,
        category: category ?? this.category,
      );
  @override
  String toString() {
    return (StringBuffer('Message(')
          ..write('id: $id, ')
          ..write('metaContent: $metaContent, ')
          ..write('mimeType: $mimeType, ')
          ..write('title: $title, ')
          ..write('content: $content, ')
          ..write('category: $category')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => $mrjf($mrjc(
      $mrjc(
          $mrjc(
              $mrjc($mrjc($mrjc(0, id.hashCode), metaContent.hashCode),
                  mimeType.hashCode),
              title.hashCode),
          content.hashCode),
      category.hashCode));
  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Message &&
          other.id == id &&
          other.metaContent == metaContent &&
          other.mimeType == mimeType &&
          other.title == title &&
          other.content == content &&
          other.category == category);
}

class MessagesCompanion extends UpdateCompanion<Message> {
  final Value<int> id;
  final Value<String> metaContent;
  final Value<String> mimeType;
  final Value<String> title;
  final Value<String> content;
  final Value<int> category;
  const MessagesCompanion({
    this.id = const Value.absent(),
    this.metaContent = const Value.absent(),
    this.mimeType = const Value.absent(),
    this.title = const Value.absent(),
    this.content = const Value.absent(),
    this.category = const Value.absent(),
  });
}

class $MessagesTable extends Messages with TableInfo<$MessagesTable, Message> {
  final GeneratedDatabase _db;
  final String _alias;
  $MessagesTable(this._db, [this._alias]);
  final VerificationMeta _idMeta = const VerificationMeta('id');
  GeneratedIntColumn _id;
  @override
  GeneratedIntColumn get id => _id ??= _constructId();
  GeneratedIntColumn _constructId() {
    return GeneratedIntColumn('id', $tableName, false, hasAutoIncrement: true);
  }

  final VerificationMeta _metaContentMeta =
      const VerificationMeta('metaContent');
  GeneratedTextColumn _metaContent;
  @override
  GeneratedTextColumn get metaContent =>
      _metaContent ??= _constructMetaContent();
  GeneratedTextColumn _constructMetaContent() {
    return GeneratedTextColumn(
      'meta_content',
      $tableName,
      true,
    );
  }

  final VerificationMeta _mimeTypeMeta = const VerificationMeta('mimeType');
  GeneratedTextColumn _mimeType;
  @override
  GeneratedTextColumn get mimeType => _mimeType ??= _constructMimeType();
  GeneratedTextColumn _constructMimeType() {
    return GeneratedTextColumn(
      'mime_type',
      $tableName,
      false,
    );
  }

  final VerificationMeta _titleMeta = const VerificationMeta('title');
  GeneratedTextColumn _title;
  @override
  GeneratedTextColumn get title => _title ??= _constructTitle();
  GeneratedTextColumn _constructTitle() {
    return GeneratedTextColumn('title', $tableName, false,
        minTextLength: 6, maxTextLength: 32);
  }

  final VerificationMeta _contentMeta = const VerificationMeta('content');
  GeneratedTextColumn _content;
  @override
  GeneratedTextColumn get content => _content ??= _constructContent();
  GeneratedTextColumn _constructContent() {
    return GeneratedTextColumn(
      'body',
      $tableName,
      false,
    );
  }

  final VerificationMeta _categoryMeta = const VerificationMeta('category');
  GeneratedIntColumn _category;
  @override
  GeneratedIntColumn get category => _category ??= _constructCategory();
  GeneratedIntColumn _constructCategory() {
    return GeneratedIntColumn(
      'category',
      $tableName,
      false,
    );
  }

  @override
  List<GeneratedColumn> get $columns =>
      [id, metaContent, mimeType, title, content, category];
  @override
  $MessagesTable get asDslTable => this;
  @override
  String get $tableName => _alias ?? 'messages';
  @override
  final String actualTableName = 'messages';
  @override
  VerificationContext validateIntegrity(MessagesCompanion d,
      {bool isInserting = false}) {
    final context = VerificationContext();
    if (d.id.present) {
      context.handle(_idMeta, id.isAcceptableValue(d.id.value, _idMeta));
    } else if (id.isRequired && isInserting) {
      context.missing(_idMeta);
    }
    if (d.metaContent.present) {
      context.handle(_metaContentMeta,
          metaContent.isAcceptableValue(d.metaContent.value, _metaContentMeta));
    } else if (metaContent.isRequired && isInserting) {
      context.missing(_metaContentMeta);
    }
    if (d.mimeType.present) {
      context.handle(_mimeTypeMeta,
          mimeType.isAcceptableValue(d.mimeType.value, _mimeTypeMeta));
    } else if (mimeType.isRequired && isInserting) {
      context.missing(_mimeTypeMeta);
    }
    if (d.title.present) {
      context.handle(
          _titleMeta, title.isAcceptableValue(d.title.value, _titleMeta));
    } else if (title.isRequired && isInserting) {
      context.missing(_titleMeta);
    }
    if (d.content.present) {
      context.handle(_contentMeta,
          content.isAcceptableValue(d.content.value, _contentMeta));
    } else if (content.isRequired && isInserting) {
      context.missing(_contentMeta);
    }
    if (d.category.present) {
      context.handle(_categoryMeta,
          category.isAcceptableValue(d.category.value, _categoryMeta));
    } else if (category.isRequired && isInserting) {
      context.missing(_categoryMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  Message map(Map<String, dynamic> data, {String tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : null;
    return Message.fromData(data, _db, prefix: effectivePrefix);
  }

  @override
  Map<String, Variable> entityToSql(MessagesCompanion d) {
    final map = <String, Variable>{};
    if (d.id.present) {
      map['id'] = Variable<int, IntType>(d.id.value);
    }
    if (d.metaContent.present) {
      map['meta_content'] = Variable<String, StringType>(d.metaContent.value);
    }
    if (d.mimeType.present) {
      map['mime_type'] = Variable<String, StringType>(d.mimeType.value);
    }
    if (d.title.present) {
      map['title'] = Variable<String, StringType>(d.title.value);
    }
    if (d.content.present) {
      map['body'] = Variable<String, StringType>(d.content.value);
    }
    if (d.category.present) {
      map['category'] = Variable<int, IntType>(d.category.value);
    }
    return map;
  }

  @override
  $MessagesTable createAlias(String alias) {
    return $MessagesTable(_db, alias);
  }
}

class Code extends DataClass implements Insertable<Code> {
  final int id;
  final String path;
  Code({@required this.id, @required this.path});
  factory Code.fromData(Map<String, dynamic> data, GeneratedDatabase db,
      {String prefix}) {
    final effectivePrefix = prefix ?? '';
    final intType = db.typeSystem.forDartType<int>();
    final stringType = db.typeSystem.forDartType<String>();
    return Code(
      id: intType.mapFromDatabaseResponse(data['${effectivePrefix}id']),
      path: stringType.mapFromDatabaseResponse(data['${effectivePrefix}path']),
    );
  }
  factory Code.fromJson(Map<String, dynamic> json,
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return Code(
      id: serializer.fromJson<int>(json['id']),
      path: serializer.fromJson<String>(json['path']),
    );
  }
  @override
  Map<String, dynamic> toJson(
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return {
      'id': serializer.toJson<int>(id),
      'path': serializer.toJson<String>(path),
    };
  }

  @override
  T createCompanion<T extends UpdateCompanion<Code>>(bool nullToAbsent) {
    return CodesCompanion(
      id: id == null && nullToAbsent ? const Value.absent() : Value(id),
      path: path == null && nullToAbsent ? const Value.absent() : Value(path),
    ) as T;
  }

  Code copyWith({int id, String path}) => Code(
        id: id ?? this.id,
        path: path ?? this.path,
      );
  @override
  String toString() {
    return (StringBuffer('Code(')
          ..write('id: $id, ')
          ..write('path: $path')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => $mrjf($mrjc($mrjc(0, id.hashCode), path.hashCode));
  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Code && other.id == id && other.path == path);
}

class CodesCompanion extends UpdateCompanion<Code> {
  final Value<int> id;
  final Value<String> path;
  const CodesCompanion({
    this.id = const Value.absent(),
    this.path = const Value.absent(),
  });
}

class $CodesTable extends Codes with TableInfo<$CodesTable, Code> {
  final GeneratedDatabase _db;
  final String _alias;
  $CodesTable(this._db, [this._alias]);
  final VerificationMeta _idMeta = const VerificationMeta('id');
  GeneratedIntColumn _id;
  @override
  GeneratedIntColumn get id => _id ??= _constructId();
  GeneratedIntColumn _constructId() {
    return GeneratedIntColumn('id', $tableName, false, hasAutoIncrement: true);
  }

  final VerificationMeta _pathMeta = const VerificationMeta('path');
  GeneratedTextColumn _path;
  @override
  GeneratedTextColumn get path => _path ??= _constructPath();
  GeneratedTextColumn _constructPath() {
    return GeneratedTextColumn(
      'path',
      $tableName,
      false,
    );
  }

  @override
  List<GeneratedColumn> get $columns => [id, path];
  @override
  $CodesTable get asDslTable => this;
  @override
  String get $tableName => _alias ?? 'codes';
  @override
  final String actualTableName = 'codes';
  @override
  VerificationContext validateIntegrity(CodesCompanion d,
      {bool isInserting = false}) {
    final context = VerificationContext();
    if (d.id.present) {
      context.handle(_idMeta, id.isAcceptableValue(d.id.value, _idMeta));
    } else if (id.isRequired && isInserting) {
      context.missing(_idMeta);
    }
    if (d.path.present) {
      context.handle(
          _pathMeta, path.isAcceptableValue(d.path.value, _pathMeta));
    } else if (path.isRequired && isInserting) {
      context.missing(_pathMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  Code map(Map<String, dynamic> data, {String tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : null;
    return Code.fromData(data, _db, prefix: effectivePrefix);
  }

  @override
  Map<String, Variable> entityToSql(CodesCompanion d) {
    final map = <String, Variable>{};
    if (d.id.present) {
      map['id'] = Variable<int, IntType>(d.id.value);
    }
    if (d.path.present) {
      map['path'] = Variable<String, StringType>(d.path.value);
    }
    return map;
  }

  @override
  $CodesTable createAlias(String alias) {
    return $CodesTable(_db, alias);
  }
}

abstract class _$TxQrData extends GeneratedDatabase {
  _$TxQrData(QueryExecutor e) : super(const SqlTypeSystem.withDefaults(), e);
  $MessagesTable _messages;
  $MessagesTable get messages => _messages ??= $MessagesTable(this);
  $CodesTable _codes;
  $CodesTable get codes => _codes ??= $CodesTable(this);
  @override
  List<TableInfo> get allTables => [messages, codes];
}
