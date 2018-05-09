// collection

(function() {

if (!parsedMeta.type) {
    return;
}

if (!isCollectionArray && columnIndex == 0) {
    return;
}

writer.write(newline);

if (isCollectionArray) {

    writer.write("global "
      + collectionName + " as "
      + parsedMeta.type + "[] = [");
    writer.write(newline);

    for (var j = 0; j < recordList.size(); j++) {
        var record = recordList.get(j);
        writer.write("  " + record.get(columnIndex));

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
      + JSON.parse(metaList.get(0)).type + "] = {");
    writer.write(newline);

    for (var j = 0; j < recordList.size(); j++) {

        var record = recordList.get(j);
        writer.write("  " + record.get(0) + ": " + record.get(columnIndex));

        if (j < recordList.size() - 1) {
          writer.write(",");
        }
        writer.write(newline);
    }

    writer.write("};" + newline);
}

}());