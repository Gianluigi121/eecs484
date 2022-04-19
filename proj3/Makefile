uniqname = cktran
password = CKTran45

compile:
	javac -Xlint:-unchecked -cp "ojdbc6.jar:json-20151123.jar:json_simple-1.1.jar:" Main.java GetData.java

run:
	@echo "Running the Java program to create the JSON file. "
	@echo "You must be on the university VPN or network. "
	@echo "Also check your username and password is correct in Main.java"
	@echo ""
	java -cp "ojdbc6.jar:json-20151123.jar:json_simple-1.1.jar:" Main
	@echo "An output file output.json should be created if everything ran fine."

loginmongo:
	@echo "You must edit the uniqname and password in Makefile"
	@echo "You may need to run 'module load mongodb' as well on CAEN."
	mongo $(uniqname) --host eecs484.eecs.umich.edu -u $(uniqname) -p $(password)

setupsampledb:
	@echo "You must edit the uniqname and password in Makefile"
	@echo "You may need to run 'module load mongodb' as well on CAEN."
	mongoimport --host eecs484.eecs.umich.edu --username $(uniqname) --password $(password) --collection users --db $(uniqname) --file  sample.json --jsonArray

setupmydb:
	@echo "You must edit the uniqname and password in Makefile"
	@echo "You may need to run 'module load mongodb' as well on CAEN."
	mongoimport --host eecs484.eecs.umich.edu --username $(uniqname) --password $(password)  --collection users --db $(uniqname) --file  output.json --jsonArray


mongotest:
	@echo "Running test.js using the database. Run make setupsampledb or make setupmydb before this."
	@echo "You must edit the uniqname and password in Makefile"
	@echo "You may need to run 'module load mongodb' as well on CAEN."
	mongo $(uniqname) -u $(uniqname) -p $(password) --host eecs484.eecs.umich.edu --eval "db.users.drop(); db.flat_users.drop(); db.cities.drop();"
	mongoimport --host eecs484.eecs.umich.edu --username $(uniqname) --password $(password)  --collection users --db $(uniqname) --file  output.json --jsonArray
	mongo $(uniqname) -u $(uniqname) -p $(password) --host eecs484.eecs.umich.edu < test.js
	@echo "Local tests in test.js have been run."
	@echo "Optional: Do make dropdatabase to remove the mongo database."

dropdb:
	@echo "You must edit the uniqname and password in Makefile"
	@echo "You may need to run 'module load mongodb' as well on CAEN."
	mongo $(uniqname) -u $(uniqname) -p $(password) --host eecs484.eecs.umich.edu --eval "db.users.drop(); db.flat_users.drop(); db.cities.drop();"
	