.PHONY: compile clean query-all time-all query0 query1 query2 query3 query4 query5 query6 query7 query8 query9 time0 time1 time2 time3 time4 time5 time6 time7 time8 time9

COMP = javac
PACKAGE = project2
MAIN = FakebookOracleMain
FILES = $(PACKAGE)/PublicFakebookOracleConstants.java
FILES += $(PACKAGE)/FakebookOracleUtilities.java
FILES += $(PACKAGE)/FakebookOracleDataStructures.java
FILES += $(PACKAGE)/FakebookOracle.java
FILES += $(PACKAGE)/StudentFakebookOracle.java
FILES += $(PACKAGE)/$(MAIN).java
EXEC = timeout 120 java -Xmx64M -cp "$(PACKAGE)/ojdbc6.jar:." $(PACKAGE)/$(MAIN)

default: compile

compile: $(FILES)
	@$(MAKE) -s clean
	@$(COMP) $(FILES)
	
query-all: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) all p || true
	
query0: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 0 p || true

query1: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 1 p || true

query2: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 2 p || true

query3: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 3 p || true

query4: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 4 p || true

query5: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 5 p || true

query6: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 6 p || true

query7: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 7 p || true

query8: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 8 p || true

query9: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 9 p || true

time-all: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) all t || true
	
time0: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 0 t || true

time1: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 1 t || true

time2: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 2 t || true

time3: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 3 t || true

time4: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 4 t || true

time5: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 5 t || true

time6: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 6 t || true

time7: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 7 t || true

time8: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 8 t || true

time9: $(FILES)
	@$(MAKE) -s compile
	@$(EXEC) 9 t || true

clean:
	@rm -f $(PACKAGE)/*.class
