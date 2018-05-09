// collection

(function() {

if (!parsedMeta.type) {
    return;
}

if (!isCollectionArray && columnIndex == 0) {
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
        var element = record.get(columnIndex);

        if (requiresQuotes) {
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

    writer.write("global "
      + collectionName + " as "
      + parsedMeta.type + "["
      + parsedMetaList[0].type + "] = {");
    writer.write(newline);

    for (var j = 0; j < recordList.size(); j++) {

        var record = recordList.get(j);
        var element = record.get(columnIndex);

        if (requiresQuotes) {
            element = Util.quote(element);
        }

        writer.write("  " + record.get(0) + ": " + element);

        if (j < recordList.size() - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }

    writer.write("};" + newline);
}

}());