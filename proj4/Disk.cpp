#include "Disk.hpp"

#include <iostream>
#include <algorithm>
#include <string>
#include <fstream>

using namespace std;

Disk::Disk(){}

Disk::~Disk() {
	for (size_t i = 0; i < this->pages.size(); ++i) {
		this->pages[i]->reset();
		delete this->pages[i];
	}
}

unsigned int Disk::diskWrite(Page* p) {
	if (this->pages.size() == DISK_SIZE_IN_PAGE) {
		cerr << "Error: can not write to the disk due to out of disk space." << endl;
		exit(1);
	}
	unsigned int new_disk_page_id = this->pages.size();
	Page *new_disk_page = new Page(*p);
	this->pages.push_back(new_disk_page);
	return new_disk_page_id;
}

Page* Disk::diskRead(unsigned int pos){
	if (pos >= this->pages.size()) {
		cerr << "Error: accessing invalid disk page." << endl;
		exit(1);
	}
	return this->pages[pos];
}

void Disk::print(unsigned int id){
	this->pages[id]->print();
}

void Disk::print(){
	for(unsigned int i = 0; i < this->pages.size(); i++){
		if(this->pages[i]){
			cout << "Disk page id: " << i << endl;
			this->pages[i]->print();			
		}
	}
}

/* Different tables should not mix with each other */
pair<unsigned int, unsigned int> Disk::read_data(const char* file_name) {
	/* Read all the raw data from txt file */
	string str_file_name(file_name);
	ifstream raw_data_file(str_file_name);
	string one_line;
	unsigned int start_page_id = this->pages.size();
	/* Create the first new disk page */
	this->pages.push_back(new Page());
	while (getline(raw_data_file, one_line)) {
		if (this->pages[this->pages.size() - 1]->full()) {
			/* Create a new disk page */
			this->pages.push_back(new Page());
		}
		size_t space_idx = one_line.find(' ');
		string key = one_line.substr(0, space_idx);
		string data = one_line.substr(space_idx + 1);
		this->pages[this->pages.size() - 1]->loadRecord(Record(key, data));
	}
	unsigned int end_page_id = this->pages.size();
	raw_data_file.close();
	return make_pair(start_page_id, end_page_id);
}