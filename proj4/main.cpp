#include <iostream>
#include <string>
#include <functional>
#include <fstream>
#include "Join.hpp"
#include "Record.hpp"
#include "Page.hpp"
#include "Disk.hpp"
#include "Mem.hpp"
#include "Bucket.hpp"
#include "constants.hpp"
#include <algorithm>
using namespace std;

void print(vector<unsigned int> &join_res, Disk* disk) {
	cout << "Size of GHJ result: " << join_res.size() << " pages" << endl;
	for (unsigned int i = 0; i < join_res.size(); ++i) {
		Page *join_page = disk->diskRead(join_res[i]);
		cout << "Page " << i << " with disk id = " << join_res[i] << endl;
		join_page->print();
	}
}


int main(int argc, char* argv[]) {
	/* Parse cmd arguments */
	if (argc != 3) {
		cerr << "Error: Wrong command line usage." << endl;
		cerr << "Usage: ./GHJ left_rel.txt right_rel.txt" << endl;
		exit(1);
	}

	/* Variable initialization */
	Disk disk = Disk();
	Mem mem = Mem();
	pair<unsigned int, unsigned int> left_rel = disk.read_data(argv[1]);
	pair<unsigned int, unsigned int> right_rel = disk.read_data(argv[2]);
	
	/* Grace Hash Join Partition Phase */
	vector<Bucket> res = partition(&disk, &mem, left_rel, right_rel);

	/* Grace Hash Join Probe Phase */
	vector<unsigned int> join_res = probe(&disk, &mem, res);

	/* Print the result */
	print(join_res, &disk);
	return 0;
}