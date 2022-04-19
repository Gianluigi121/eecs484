#include "Bucket.hpp"

Bucket::Bucket(Disk* disk) {
    this->disk = disk;
}

void Bucket::add_left_rel_page(unsigned int page_id) {
    this->left_rel.push_back(page_id);
    /* Maintain num_left_rel_record */
    this->num_left_rel_record += this->disk->diskRead(page_id)->size();
}

void Bucket::add_right_rel_page(unsigned int page_id) {
    this->right_rel.push_back(page_id);
    /* Maintain num_right_rel_record */
    this->num_right_rel_record += this->disk->diskRead(page_id)->size();
}

vector<unsigned int> Bucket::get_left_rel() {
    return this->left_rel;
}

vector<unsigned int> Bucket::get_right_rel() {
    return this->right_rel;
}