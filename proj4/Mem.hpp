/*
 * This files defines the data structure for Memory
 * DO NOT MODIFY THIS FILE
 */
#ifndef _MEM_HPP_
#define _MEM_HPP_

#include <vector>
#include <string>

#include "constants.hpp"
#include "Page.hpp"
#include "Disk.hpp"


using namespace std;

class Mem
{
public:

    Mem();

    ~Mem();

    /* Load specific disk page to specific memory page */
    void loadFromDisk(Disk* d, unsigned int disk_page_id, unsigned int mem_page_id);

    /*
     * Write specific memory page into disk, and reset memory page
     * Return written disk page id
     */
    unsigned int flushToDisk(Disk* d, unsigned int mem_page_id);

    // reset all memory pages
    void reset();

    /* Print all the records info in Memory*/
    void print();

    /* Returns the pointer to the memory page specified by page_id */
    Page* mem_page(unsigned int page_id);

private:

	vector<Page*> pages;

};

#endif