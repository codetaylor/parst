// collection

(function() {

if (!collection.getType() || collection.isKey()) {
    return;
}

writer.write(newline);

if (collection.isArray()) {

    writer.write("global "
      + collection.getDisplayName() + " as "
      + collection.getType() + "[] = [");
    writer.write(newline);

    for (var row = 0; row < rowCount; row++) {
        var entry = collection.getRecordEntry(row);

        if (entry == 'null' && !collection.allowNulls()) {
            continue;
        }

        writer.write("  " + entry);
        if (row < rowCount - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }
    writer.write("];" + newline);

} else {

    var keyCollection = collectionList[collection.getKeyIndex()];

    writer.write("global "
      + collection.getDisplayName() + " as "
      + collection.getType() + "["
      + collection.getKeyType() + "] = {");
    writer.write(newline);

    for (var row = 0; row < rowCount; row++) {
        var entry = collection.getRecordEntry(row);

        if (entry == 'null' && !collection.allowNulls()) {
            continue;
        }

        writer.write("  " + keyCollection.getRecordEntry(row) + ": " + entry);
        if (row < rowCount - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }
    writer.write("};" + newline);
}

}());