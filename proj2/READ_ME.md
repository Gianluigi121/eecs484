# EECS 484: Project 2
### Fakebook Oracle JDBC

The java and jar files are in the project2 folder.

Before compiling and running, change the username and password in 
FakebookOracleMain.java to your Oracle username and password. 

To compile on Linux, do: 

make

To execute:

make query0

To time:

time make query0

Examine the Makefile for other commands you can run or look at the specs.




Execution on a personal laptop: (unsupported)
----------------------------------------------

This code should run on your personal laptop in a Linux VM with Java 
installed. But, we recommend periodically copying your files to CAEN and testing from there. You do need to be on the UM network to connect to the Oracle server. You can do that by either connecting to MWireless or by joining the UM VPN. UM ITD provides instructions for joining the UM VPN (you should be able to Google for it.)

Mac Compilation (unsupported)
------------------------------

This code should compile on a Mac with Java installed.

On Mac, the timeout command is not avaialble. Edit the Makefile and remove timeout 120 in order to run the command. 

Timing commands
---------------

To get an approximate time for a command to execute on Linux/Macs, you can do:

time command

