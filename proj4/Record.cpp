#include "Record.hpp"
#include "constants.hpp"
#include <iostream>
#include <algorithm>
#include <string>

using namespace std;

#define MODULAR 1000000

Record::Record(string joinKey, string data){
    this->key = joinKey;
    this->data = data;
}

Record::Record(const Record &r2){
	key = r2.key;
	data = r2.data;
}

/* h1 used at partition stage */
unsigned int Record::partition_hash() {
    /* Use stl hash function */
    hash<string> str_hash;
    return str_hash(this->key) % MODULAR;
}

/* h2 different from h1 */
unsigned int Record::probe_hash() {
    /* Use stl hash function */
    hash<string> str_hash;
    return str_hash("key:" + this->key) % MODULAR;
}

/* Equality comparator */
bool Record::operator ==(const Record& r2)const {
    hash<string> str_hash;
    /* If two records have different hash values(h2) of key, exit */
    if ((unsigned int)(str_hash("key:" + this->key) % MODULAR) % (MEM_SIZE_IN_PAGE-2) != 
        (unsigned int)(str_hash("key:" + r2.key) % MODULAR) % (MEM_SIZE_IN_PAGE-2)) {
        cout << "Error: Can not compare two records with different hash values(h2) of key." << endl;
        exit(1);
    }
    if (this->key == r2.key) {
        return true;
    }
    else {
        return false;
    }
}