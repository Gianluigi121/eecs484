#include "Join.hpp"
#include <functional>
#include <vector>

using namespace std;

/*
 * TODO: Student implementation
 * Input: Disk, Memory, Disk page ids for left relation, Disk page ids for right relation
 * Output: Vector of Buckets of size (MEM_SIZE_IN_PAGE - 1) after partition
 */
vector<Bucket> partition(
    Disk* disk, 
    Mem* mem, 
    pair<unsigned int, unsigned int> left_rel, 
    pair<unsigned int, unsigned int> right_rel) {
    // Create output vector
    vector<Bucket> output;
    for (unsigned int i = 0; i < MEM_SIZE_IN_PAGE - 1; ++i)    {
        output.emplace_back(disk);
    }

    // Load each page from left relation (from disk)
    for (unsigned int disk_page_id = left_rel.first; disk_page_id != left_rel.second; ++disk_page_id)    {
        mem->loadFromDisk(disk, disk_page_id, MEM_SIZE_IN_PAGE - 1);
        Page* input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);
        // Compute hash for each record in page and load into hash table (mem)
        for (unsigned int record_id = 0; record_id < input_page->size(); ++record_id)   {
            Record data_record = input_page->get_record(record_id);
            unsigned int hash = data_record.partition_hash() % (MEM_SIZE_IN_PAGE - 1);

            // Flush to disk and reset bucket if full
            if (mem->mem_page(hash)->full())   {
                output[hash].add_left_rel_page(mem->flushToDisk(disk, hash));
                //mem->mem_page(hash)->reset();
            }
            (mem->mem_page(hash))->loadRecord(data_record);
        }
    }
    
    // Flush output buffers to disk
    for (unsigned int i = 0; i < MEM_SIZE_IN_PAGE - 1; ++i)  {
        if (mem->mem_page(i)->size() > 0) {
            output[i].add_left_rel_page(mem->flushToDisk(disk, i));
            //mem->mem_page(i)->reset();
        }
    }

    // Load each page from right relation (from disk)
    for (unsigned int disk_page_id = right_rel.first; disk_page_id != right_rel.second; ++disk_page_id)    {
        mem->loadFromDisk(disk, disk_page_id, MEM_SIZE_IN_PAGE - 1);
        Page* input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);

        // Compute hash for each record in page and load into hash table (mem)
        for (unsigned int record_id = 0; record_id < input_page->size(); ++record_id) {
            Record data_record = input_page->get_record(record_id);
            unsigned int hash = data_record.partition_hash() % (MEM_SIZE_IN_PAGE - 1);

            // Flush to disk and reset bucket if full
            if (mem->mem_page(hash)->full()) {
                output[hash].add_right_rel_page(mem->flushToDisk(disk, hash));
                //mem->mem_page(hash)->reset();
            }
            (mem->mem_page(hash))->loadRecord(data_record);
        }
    }

    // Flush all non-empty mem buffers to disk
    for (unsigned int i = 0; i < MEM_SIZE_IN_PAGE - 1; ++i) {
        if (mem->mem_page(i)->size() > 0) {
            output[i].add_right_rel_page(mem->flushToDisk(disk, i));
            //mem->mem_page(i)->reset();
        }
    }

    // Reset input buffer
    mem->mem_page(MEM_SIZE_IN_PAGE - 1)->reset();

    return output;
}

/*
 * TODO: Student implementation
 * Input: Disk, Memory, Vector of Buckets after partition
 * Output: Vector of disk page ids for join result
 */
vector<unsigned int> probe(Disk* disk, Mem* mem, vector<Bucket>& partitions) {
    vector<unsigned int> result;
    Page* output_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);

    // Iterate thru each partition
    for (Bucket partition : partitions) {
        vector<unsigned int> smaller_pages;
        vector<unsigned int> larger_pages;

        // Determine which rel is smaller
        if (partition.num_left_rel_record <= partition.num_right_rel_record) {
            smaller_pages = partition.get_left_rel();
            larger_pages = partition.get_right_rel();
        }
        else {
            smaller_pages = partition.get_right_rel();
            larger_pages = partition.get_left_rel();
        }
        
        // Load each page from smaller rel of partition
        for (unsigned int disk_page_id : smaller_pages) {
            mem->loadFromDisk(disk, disk_page_id, MEM_SIZE_IN_PAGE - 2);
            Page* input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 2);

            // Rehash each record in page and load into hash table (mem)
            for (unsigned int record_id = 0; record_id < input_page->size(); ++record_id) {
                Record data_record = input_page->get_record(record_id);
                unsigned int hash = data_record.probe_hash() % (MEM_SIZE_IN_PAGE - 2);
                (mem->mem_page(hash))->loadRecord(data_record);
            }
        }
        
        // Load each page from larger rel of partition
        for (unsigned int disk_page_id : larger_pages) {
            mem->loadFromDisk(disk, disk_page_id, MEM_SIZE_IN_PAGE - 2);
            Page* input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 2);

            // Rehash each record in page
            for (unsigned int record_id = 0; record_id < input_page->size(); ++record_id) {
                Record data_record = input_page->get_record(record_id);
                unsigned int hash = data_record.probe_hash() % (MEM_SIZE_IN_PAGE - 2);
                Page* match_page = mem->mem_page(hash);

                // Compare rehashed record from larger rel with each record in hash table (from smaller rel) 
                for (unsigned int match_id = 0; match_id < match_page->size(); ++match_id) {
                    Record match_record = match_page->get_record(match_id);
                    if (data_record == match_record) {

                        // Flush output page to disk if full and reset
                        if (output_page->full()) {
                            result.push_back(mem->flushToDisk(disk, MEM_SIZE_IN_PAGE - 1));
                            //output_page->reset();
                        }

                        // Load pair into output buffer if matching
                        if (partition.num_left_rel_record <= partition.num_right_rel_record) {
                            output_page->loadPair(match_record, data_record);
                        }
                        else {
                            output_page->loadPair(data_record, match_record);
                        }
                    }
                }
            }
        }
        
        // Clean buffer pages
        for (unsigned int i = 0; i < MEM_SIZE_IN_PAGE - 1; ++i) {
            (mem->mem_page(i))->reset();
        }
        
    }

    // Flush remaining data to results
    if (output_page->size() > 0) {
        result.push_back(mem->flushToDisk(disk, MEM_SIZE_IN_PAGE - 1));
    }

    return result;
}