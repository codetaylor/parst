var parsedMeta = JSON.parse(meta);

var collectionName;

if (parsedMeta.collection && parsedMeta.collection == 'array') {

    if (nameList.get(columnIndex)) {
        collectionName = nameList.get(columnIndex).toUpperCase();
    }

    if (!collectionName) {
        throw new Exception("Missing name for column: " + columnIndex);
    }

} else {

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
}