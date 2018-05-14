// gather all groups

var groups = {};
var columnIndex = 0;

for each(var collection in collectionList) {
    var meta = collection.meta;

    if (meta.group) {
        var group = meta.group.split('.');
        var groupType = collection.getType();
        var groupKeyType = collection.getKeyType();
        var groupName = group[0].toUpperCase();
        var elementId = group[1];

        if (!groups[groupName]) {
            groups[groupName] = {
                type: groupType,
                keyType: groupKeyType,
                isArray: collection.isArray(),
                elements: {}
            };
        }

        if (groups[groupName].isArray != collection.isArray()) {
            throw new Exception("Can't group array collections with non-array collections");
        }

        if (groups[groupName].type != groupType) {
            throw new Exception("Mismatched group types: " + groups[groupName].type + " != " + groupType);
        }

        if (groups[groupName].keyType != groupKeyType) {
            throw new Exception("Mismatched group key types: " + groups[groupName].keyType + " != " + groupKeyType);
        }

        groups[groupName].elements[group[1]] = collection.getDisplayName();
    }

    columnIndex += 1;
}

for (var name in groups) {
    var group = groups[name];

    writer.write(newline);
    Util.writeCommentSeparator(writer, "Group: " + name);
    writer.write(newline);

    if (group.isArray) {
        // TODO

    } else {
        writer.write("global " + name + " as " + group.type + "[" + group.keyType + "][string] = {" + newline);

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