*Current version*:
=================

* Schema parser (DONE) -> 
* Column distribution view (DONE) ->
* Data generator -> 
* NOT COMPLETE (insert the data generator output using record i.e parsing batch) : calling the batch data retrieval and keep inserting until receive done signal (remember some of the initial data value for query generator) -> 
* Feed the query generator the value depending on column and table 
* Dumping all of the query to the random generator 
* From the random generator, get next query to be inserted into unity 
* Running each query and get result 
* Sending the result back to the front end for displaying bar graph.

*TODOs for data generator*:
==========================
Restore oldest version with distinct option for integer types and for string types; on that version, include error checking for too many distinct values given distribtuion, and a timeout when generating distinct values based on cardinality; create scalable version just creating batch of 100 rows and maintaining state in json output to know when done (with no distinct params). SENDING MIN(100, CARDINALITY) PER EACH TABLE INITIAL CALL.

*2 points to optimize:*
======================
* support large data 
* generate automatic query generator 
* make sure your part working reliably so that people don't have to debug your code when run (everyone).
  
*Tasks left*:
===================
*  Wiring (Tony, Tran)
*  Supply next max id when doing insertion (Tran)
*  Schema + fast data generator + Test (Kristin)
*  Create_table_all() + Steps for table creation, insertion, plotting etc + API (Shirley; done)
*  generate automatic query generator: think of the different work load to feed into the random generator.
*  

*Final Report*:
======================
*Introduction
* Motivation
* Related Work
* Components
* Implementation
* Results
* Discussion
* Conclusion
