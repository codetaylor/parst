var Exception = Java.type("java.lang.Exception");
var System = Java.type("java.lang.System");
var newline = System.lineSeparator();

var parsedMetaList = [];

for each(var meta in metaList) {
    parsedMetaList.push(JSON.parse(meta));
}

var Util = {};

Util.writeCommentSeparator = function(writer, text) {
    writer.write("// -----------------------------------------------------------------------------" + newline);
    writer.write("// - " + text + newline);
    writer.write("// -----------------------------------------------------------------------------" + newline);
};

Util.quote = function(text) {
    return "\"" + text + "\"";
};

Util.contains = function(arr, element) {
    return arr.indexOf(element) > -1;
};

Util.isCollectionKey = function(columnIndex) {
    return parsedMetaList[columnIndex].collection && parsedMetaList[columnIndex].collection == 'key';
};

Util.isCollectionArray = function(columnIndex) {
    return parsedMetaList[columnIndex].collection && parsedMetaList[columnIndex].collection == 'array';
};

Util.isCollectionMap = function(columnIndex) {
    return !this.isCollectionKey(columnIndex) && !this.isCollectionArray(columnIndex);
};

Util.getCollectionKeyIndex = function(columnIndex) {

    if (this.isCollectionArray(columnIndex)) {
        return -1;
    }

    if ("key" in parsedMetaList[columnIndex]) {
        var key = parsedMetaList[columnIndex].key;

        for (var i = 0; i < parsedMetaList.length; i++) {
            if (this.isCollectionKey(i) && this.getCollectionKeyId(i) == key) {
                return i;
            }
        }
    }

    return 0;
};

Util.getCollectionKeyId = function(columnIndex) {
    if (this.isCollectionKey(columnIndex)) {
        var entry = parsedMetaList[columnIndex].collection.split(':');

        if (entry && entry.length > 1) {
            return entry[1];
        }
    }
    return null;
}

Util.getCollectionType = function(columnIndex) {
    return parsedMetaList[columnIndex].type;
};

Util.getCollectionName = function(columnIndex) {

    if (this.isCollectionArray(columnIndex)) {

        if (nameList.get(columnIndex)) {
            return nameList.get(columnIndex).toUpperCase();
        }

        throw new Exception("Missing name for column: " + columnIndex);

    } else if (columnIndex > 0) {

        var collectionName;

        if (nameList.get(0)) {
            collectionName = nameList.get(0).toUpperCase();
        }

        if (nameList.get(columnIndex)) {

            if (collectionName) {
                collectionName = collectionName + "_" + nameList.get(columnIndex).toUpperCase();

            } else {
                collectionName = nameList.get(columnIndex).toUpperCase();
            }
        }

        if (!collectionName) {
            throw new Exception("Missing name for column: " + columnIndex);
        }

        return collectionName;
    }
};