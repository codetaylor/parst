// postCollection

(function() {

if (!parsedMeta.ore) {
    return;
}

var oreDictEntry = "<ore:" + parsedMeta.ore + ">";

writer.write(newline);

if (parsedMeta.collection && parsedMeta.collection == 'array') {
    writer.write("for item in " + collectionName + " {" + newline);

} else {
    writer.write("for name, item in " + collectionName + " {" + newline);
}

writer.write("  if (item as bool & !(" + oreDictEntry + " has item)) {" + newline);
writer.write("    " + oreDictEntry + ".add(item);" + newline);
writer.write("  }" + newline);
writer.write("};" + newline);

}());