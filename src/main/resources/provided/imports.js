// preFile

(function() {

var HashSet = Java.type('java.util.HashSet');

var imports = {
    IOreDictEntry: 'crafttweaker.oredict.IOreDictEntry',
    IIngredient: 'crafttweaker.item.IIngredient',
    IItemStack: 'crafttweaker.item.IItemStack',
    IItemDefinition: 'crafttweaker.item.IItemDefinition',
    ILiquidStack: 'crafttweaker.liquid.ILiquidStack'
}

var importSet = new HashSet();

for each(var meta in metaList) {
    var m = JSON.parse(meta);

    if (m.type) {
        importSet.add(m.type);
    }
}

for each(var i in importSet) {

    if (!imports[i]) {
        logger.warn('Missing import transform entry for: ' + i);
        continue;
    }

    writer.write('import ' + imports[i] + ';' + System.lineSeparator());
}

}());