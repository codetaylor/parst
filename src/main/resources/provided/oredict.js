// postCollection

(function() {

if (!collection.meta.ore) {
    return;
}

var oreDictEntry = "<ore:" + collection.meta.ore + ">";

writer.write(newline);

if (collection.isArray()) {
    writer.write("for item in " + collection.getDisplayName() + " {" + newline);

} else {
    writer.write("for name, item in " + collection.getDisplayName() + " {" + newline);
}

writer.write("  if (item as bool & !(" + oreDictEntry + " has item)) {" + newline);
writer.write("    " + oreDictEntry + ".add(item);" + newline);
writer.write("  }" + newline);
writer.write("}" + newline);

}());