// preCollection

(function() {

if (!collection.getType() || collection.isKey()) {
    return;
}

writer.write(newline);
Util.writeCommentSeparator(writer, collection.getDisplayName());

}());