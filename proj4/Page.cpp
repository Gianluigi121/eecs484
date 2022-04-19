#include "Page.hpp"
#include "constants.hpp"

#include <iostream>
#include <algorithm>
#include <string>

using namespace std;

Page::Page(){}

void Page::loadPage(const Page *p2) {
	this->reset();
	for(size_t i = 0; i < p2->records.size(); i++){
		this->records.push_back(Record(p2->records[i]));
	}
}

Page::Page(const Page &p2){
	this->loadPage(&p2);
}

void Page::print(){
	for (size_t i = 0; i < this->records.size(); ++i) {
		this->records[i].print();
	}
}

void Page::loadRecord(Record r){
	if (this->records.size() < RECORDS_PER_PAGE) {
		this->records.push_back(Record(r));
	}
	else {
		cout << "Error: Can not add record into full page." << endl;
		exit(1);
	}
}

bool Page::full() {
	if (this->records.size() == RECORDS_PER_PAGE) {
		return true;
	}
	else {
		return false;
	}
}

void Page::reset() {
	this->records.clear();
}

unsigned int Page::size() {
	return this->records.size();
}

Record Page::get_record(unsigned int record_id) {
	return this->records[record_id];
}

// load 2 matching record into a page
// records per page will always be even number
void Page::loadPair(Record left_r, Record right_r) {
	if (this->records.size() < (RECORDS_PER_PAGE - 1)) {
		this->records.push_back(left_r);
		this->records.push_back(right_r);
	}
	else {
		cout << "Error: Can not add record into full page." << endl;
		exit(1);
	}
}