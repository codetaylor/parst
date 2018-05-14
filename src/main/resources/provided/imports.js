// preFile

(function() {

var HashSet = Java.type('java.util.HashSet');

var imports = {
    IOreDictEntry: 'crafttweaker.oredict.IOreDictEntry',
    IIngredient: 'crafttweaker.item.IIngredient',
    IItemStack: 'crafttweaker.item.IItemStack',
    IItemDefinition: 'crafttweaker.item.IItemDefinition',
    ILiquidStack: 'crafttweaker.liquid.ILiquidStack'
};

var ignore = [
    'bool',
    'byte',
    'short',
    'int',
    'long',
    'float',
    'double',
    'string'
];

var importSet = new HashSet();

for each(var collection in collectionList) {
    if (collection.getType()) {
        importSet.add(collection.getType());
    }
}

for each(var i in importSet) {

    if (!imports[i] && !Util.contains(ignore, i)) {
        logger.warn('Missing import transform entry for: ' + i);
        continue;
    }

    if (imports[i]) {
        writer.write('import ' + imports[i] + ';' + System.lineSeparator());
    }
}

}());