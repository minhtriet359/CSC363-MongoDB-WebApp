//lab 21

//Pharmacy collection  insert data
// Each pharmacy document has attributes:id(auto), name, address, phone,
// a list of drug names and costs.

db.pharmacy.drop();
let doc1 = {_id:1, name:'cvs', address:'123 main', phone:'813-774-1200', drugCosts: [ { drugName: 'lisinopril', cost: 7.5 }]};
let doc2 = {_id:2, name:'rite-aid', address:'456 central', phone:'714-778-5000', drugCosts: [ { drugName: 'advil', cost: 1.5 }]};
let doc3 = {_id:3, name:'wal-mart', address:'789 pinewood dr', phone:'714-990-4000', drugCosts: [ { drugName: 'xanax', cost: 2.75 }]};
db.pharmacy.insertOne(doc1);
db.pharmacy.insertOne(doc2);
db.pharmacy.insertOne(doc3);

print("Pharmacy with data: ");
let result = db.pharmacy.find();
result.forEach(printjson);

//Drug collection insert data
// Each drug document has attributes:id(auto), name
db.drug.drop();
doc1 = { _id: 1, name: 'lisinopril' };
doc2 = { _id: 2, name: 'advil' };
doc3 = { _id: 3, name: 'xanax' }
db.drug.insertOne(doc1);
db.drug.insertOne(doc2);
db.drug.insertOne(doc3);
print("Drug with data: ");
result = db.drug.find();
result.forEach(printjson);