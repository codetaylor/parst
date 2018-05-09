// gather all groups

var groups = {};
var columnIndex = 0;

for each(var meta in parsedMetaList) {

    if (meta.group) {
        var group = meta.group.split('.');
        var groupType = Util.getCollectionType(columnIndex);
        var groupName = group[0].toUpperCase();
        var elementId = group[1];

        if (!groups[groupName]) {
            groups[groupName] = {
                type: groupType,
                isArray: Util.isCollectionArray(columnIndex),
                elements: {}
            };
        }

        if (groups[groupName].isArray != Util.isCollectionArray(columnIndex)) {
            throw new Exception("Can't group array collections with non-array collections");
        }

        if (groups[groupName].type != groupType) {
            throw new Exception("Mismatched group types: " + groups[groupName].type + " != " + groupType);
        }

        groups[groupName].elements[group[1]] = Util.getCollectionName(columnIndex);
    }

    columnIndex += 1;
}

for (var name in groups) {
    var group = groups[name];

    if (group.isArray) {

    } else {
        var keyType = Util.getCollectionType(0);

        writer.write(newline);
        Util.writeCommentSeparator(writer, "Group: " + name);
        writer.write(newline);
        writer.write("global " + name + " as " + group.type + "[" + keyType + "][string] = {" + newline);

        var elementIndex = 0;
        var lastIndex = Object.keys(group.elements).length - 1;

        for (var key in group.elements) {
            writer.write("  " + key + ": " + group.elements[key]);

            if (elementIndex < lastIndex) {
                writer.write(",");
            }

            writer.write(newline);
            elementIndex += 1;
        }

        writer.write("};" + newline);
    }
}

//logger.info(JSON.stringify(groups));