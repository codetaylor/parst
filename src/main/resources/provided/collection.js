// collection

(function() {

if (!parsedMeta.type || isCollectionKey) {
    return;
}

var typesToQuote = [
    "string"
];

var requiresQuotes = Util.contains(typesToQuote, parsedMeta.type);

writer.write(newline);

if (isCollectionArray) {

    writer.write("global "
      + collectionName + " as "
      + parsedMeta.type + "[] = [");
    writer.write(newline);

    for (var j = 0; j < recordList.size(); j++) {
        var record = recordList.get(j);
        var element = Util.replaceIfNull(record.get(columnIndex), columnIndex);

        if (requiresQuotes && element != 'null') {
            element = Util.quote(element);
        }

        writer.write("  " + element);

        if (j < recordList.size() - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }

    writer.write("];" + newline);

} else {

    var collectionKeyIndex = Util.getCollectionKeyIndex(columnIndex);

    writer.write("global "
      + collectionName + " as "
      + parsedMeta.type + "["
      + parsedMetaList[collectionKeyIndex].type + "] = {");
    writer.write(newline);

    for (var j = 0; j < recordList.size(); j++) {

        var record = recordList.get(j);
        var element = Util.replaceIfNull(record.get(columnIndex), columnIndex);

        if (requiresQuotes && element != 'null') {
            element = Util.quote(element);
        }

        writer.write("  " + record.get(collectionKeyIndex) + ": " + element);

        if (j < recordList.size() - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }

    writer.write("};" + newline);
}

}());