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

for each(var meta in metaList) {
    var m = JSON.parse(meta);

    if (m.type) {
        importSet.add(m.type);
    }
}

for each(var i in importSet) {

    if (!imports[i] && ignore.indexOf(i) == -1) {
        logger.warn('Missing import transform entry for: ' + i);
        continue;
    }

    if (imports[i]) {
        writer.write('import ' + imports[i] + ';' + System.lineSeparator());
    }
}

}());