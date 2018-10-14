var getInfo = require('./googleTest.js');

exports.SearchMall = async function (lat, lng, radius) {
    var jsonTest = await getInfo.getInfoByCategory(lat,lng, radius,"shopping_mall");
    var jsonObject = JSON.parse(jsonTest);
    
    while(JSON.stringify(jsonObject.Info) == "[]") {
        var jsonTest = await getInfo.getInfoByCategory(lat,lng, radius,"shopping_mall");
        var jsonObject = JSON.parse(jsonTest);
    }
    
   return jsonObject;
}

exports.SearchStore = async function (lat, lng, radius) {
    var jsonTest = await getInfo.getInfoByCategory(lat,lng, radius,"department_store");
    var jsonObject = JSON.parse(jsonTest);
    
    while(JSON.stringify(jsonObject.Info) == "[]") {
        var jsonTest = await getInfo.getInfoByCategory(lat,lng, radius,"department_store");
        var jsonObject = JSON.parse(jsonTest);
    }
    
   return jsonObject;
}
