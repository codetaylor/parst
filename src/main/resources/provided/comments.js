// preCollection

(function() {

if (!parsedMeta.type) {
    return;
}

if (!isCollectionArray && columnIndex == 0) {
    return;
}

writer.write(newline);
Util.writeCommentSeparator(writer, collectionName);

}());