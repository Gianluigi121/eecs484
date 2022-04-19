/*
 * This file defines the data structure for Page
 * DO NOT MODIFY THIS FILE
 */

#ifndef _PAGE_HPP_
#define _PAGE_HPP_

#include <vector>
#include <string>

#include "constants.hpp"
#include "Record.hpp"

using namespace std;

class Page {
public:

    Page();

    /* Copy constructor */
    Page(const Page &p2);

    /* 
     * Load all data from page p2 
     * Copy by value
     */
    void loadPage(const Page *p2);

    /* Load single record into the page */
    void loadRecord(Record r);

    /* Print all the records info */
    void print();

    /* Return true if this page is full of data records */
    bool full();

    /* Clear all the records in this page */
    void reset();

    /* Return number of records in this page */
    unsigned int size();

    /* Get the specific record in this->records at position record_id */
    Record get_record(unsigned int record_id);

    /* Load a pair of matching records in to this pages, consume 2 record spaces in this page*/
    void loadPair(Record left_r, Record right_r);

private:
	vector<Record> records;

};

#endif