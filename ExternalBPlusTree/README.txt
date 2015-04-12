ExternalBPlus Tree reads in a transaction file (batch processing) with codes:

SC XXX: for search for country information by code, were XXX is a three digit country code
AC: Search all by code. Displays all country data in aphabetical order by three digit country code

Country data is stored in CountryData.csv with fixed length fields which enables random access. 
CodeIndex.bin is a binary file which contains a B+ tree containg the three digit country code of
each country along with a data record pointer(DRP)which is used to calculate the offset for random
access of the CountryData.csv file.

**Checkout the Log.txt for the final output