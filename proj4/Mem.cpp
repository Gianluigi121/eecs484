#include "Mem.hpp"
#include "constants.hpp"
#include <iostream>
#include <algorithm>
#include <string>

using namespace std;

/* RAII paradigm */
Mem::Mem() {
	/* Dynamic memory allocation for memory page */
	for (unsigned int i = 0; i < MEM_SIZE_IN_PAGE; ++i) {
		Page* mem_page = new Page();
		this->pages.push_back(mem_page);
	}
}

Mem::~Mem() {
	for (size_t i = 0; i < this->pages.size(); ++i) {
		this->pages[i]->reset();
		delete this->pages[i];
	}
}

void Mem::loadFromDisk(Disk* d, unsigned int disk_page_id, unsigned int mem_page_id) {
	Page* disk_p = d->diskRead(disk_page_id);
	this->pages[mem_page_id]->loadPage(disk_p);
	return;
}

void Mem::print() {
	for(unsigned int i = 0; i < MEM_SIZE_IN_PAGE; i++){
		cout << "PageID " << i << " in Mem:" << endl;
		if(this->pages[i]){
			this->pages[i]->print();
		}
	}
}

unsigned int Mem::flushToDisk(Disk* d, unsigned int mem_page_id) {
	unsigned int new_disk_page_id = d->diskWrite(this->pages[mem_page_id]);
	this->pages[mem_page_id]->reset();
	return new_disk_page_id;
}

void Mem::reset() {
	for(unsigned int i = 0; i < this->pages.size(); ++i){
		this->pages[i]->reset();
	}
}

Page* Mem::mem_page(unsigned int page_id) {
	return this->pages[page_id];
}
