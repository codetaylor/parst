var Exception = Java.type("java.lang.Exception");
var System = Java.type("java.lang.System");
var newline = System.lineSeparator();
var rowCount = recordList.length;

var collectionList = [];

var quoteByType = [
    'string'
];

// ----------------------------------------------------------------------------
// - Collection
// ----------------------------------------------------------------------------

var Collection = function(index, name, meta) {
    this.index = index;
    this.name = name;
    this.meta = meta;
};

Collection.prototype.getRecordEntry = function(row) {
    var value = recordList[row].get(this.index);
    if (value == 'null') {
        value = this.getNullReplacement();
        if (value != 'null' && Util.contains(quoteByType, this.getType())) {
            return "\"" + value + "\"";
        }
        return value;
    }
    if (Util.contains(quoteByType, this.getType())) {
        return "\"" + value + "\"";
    }
    return value;
};

Collection.prototype.getType = function() {
    return this.meta.type;
};

Collection.prototype.getCollectionType = function() {
    return this.meta.collection;
};

Collection.prototype.isKey = function() {
    return this.getCollectionType() == 'key';
};

Collection.prototype.isArray = function() {
    return this.getCollectionType() == 'array';
};

Collection.prototype.isMap = function() {
    return !this.isKey() && !this.isArray();
};

Collection.prototype.getKeyIndex = function() {
    if (this.isArray()) {
        return -1;
    }
    if ('key' in this.meta) {
        for (var i = 0; i < collectionList.length; i++) {
            var collection = collectionList[i];
            if (collection.isKey() && collection.getCollectionKeyId() == this.meta.key) {
                return i;
            }
        }
    }
    return 0;
};

Collection.prototype.getKeyType = function() {
    return !this.isArray() ? collectionList[this.getKeyIndex()].getType() : null;
};

Collection.prototype.getCollectionKeyId = function() {
    if (this.isKey()) {
        var entry = this.meta.collection.split(':');
        if (entry && entry.length > 1) {
            return entry[1];
        }
    }
    return null;
};

Collection.prototype.getDisplayName = function() {
    var collectionName;
    if (this.isArray()) {
        if (this.name) {
            collectionName = this.name.toUpperCase();
        }
    } else {
        if (collectionList[0].name) {
            collectionName = collectionList[0].name.toUpperCase();
        }
        if (this.name) {
            if (collectionName) {
                collectionName = collectionName + '_' + this.name.toUpperCase();
            } else {
                collectionName = this.name.toUpperCase();
            }
        }
    }
    if (!collectionName) {
        throw new Exception('Missing name for column: ' + this.index);
    }
    return collectionName;
};

Collection.prototype.hasNullReplacement = function() {
    return 'nullValue' in this.meta;
};

Collection.prototype.getNullReplacement = function() {
    return (this.hasNullReplacement()) ? this.meta.nullValue : 'null';
};

Collection.prototype.allowNulls = function() {
    return 'allowNulls' in this.meta && this.meta.allowNulls;
};

// ----------------------------------------------------------------------------

for (var i = 0; i < nameList.length; i++) {
    collectionList.push(new Collection(i, nameList[i], JSON.parse(metaList[i])));
}

// ----------------------------------------------------------------------------

var Util = {};

Util.writeCommentSeparator = function(writer, text) {
    writer.write("// -----------------------------------------------------------------------------" + newline);
    writer.write("// - " + text + newline);
    writer.write("// -----------------------------------------------------------------------------" + newline);
};

Util.contains = function(arr, element) {
    return arr.indexOf(element) > -1;
};