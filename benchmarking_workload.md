**Benchmarking Tests using YCSB**

YCSB (Yahoo Cloud Servicing Benchmark) is a test framework developed by Yahoo. It allows to conduct benchmark tests on storages by creating "work loads". Through this benchmark, the storage most suitable for the service that is to be developed can be selected.
Basic operations are Insert, Update, Read, and Scan. There are basic workload sets that combine the basic operations, but new additional workloads can also be created.
YCBS currently supports Cassandra, HBase, MongoDB, Voldemort and JDBC. If tests on other storages are needed, then use YCBS interfaces to test during the development process.

YCSB comes with 6 out of the box workloads, each testing a different common use case

**Cardinality**

recordcount=1000

operationcount=1000

Default data sizes: 

* 1 GB
* 500 MB
* 100 MB
* 1 MB

Insertion: table

Deleteion: table, predicates where, relation and/or

Update: table, field, predicates where, relation and/or

Select: tables (as join), fields, predicates, relation and/or, groupby (0/1), orderby (0/1) -- asc/desc

Examples: 

**Workload A: Update heavy workload**

Application example: Session store recording recent actions

Read/update ratio: 50/50

readproportion=0.5

updateproportion=0.5

scanproportion=0

insertproportion=0


**Workload B: Read mostly workload**

Application example: photo tagging; add a tag is an update, but most operations are to read tags
                        
Read/update ratio: 95/5

readproportion=0.95

updateproportion=0.05

scanproportion=0

insertproportion=0


**Workload C: Read only**

Application example: user profile cache, where profiles are constructed elsewhere (e.g., Hadoop)
                        
Read/update ratio: 100/0

readproportion=1.0

updateproportion=0

scanproportion=0

insertproportion=0


**Workload D: Read latest workload**

Application example: user status updates; people want to read the latest.

Read/update/insert ratio: 95/0/5

readproportion=0.95

updateproportion=0

scanproportion=0

insertproportion=0.05


**Workload E: Short ranges**

In this workload, short ranges of records are queried, instead of individual records. 

Application example: threaded conversations, where each scan is for the posts in a given thread (assumed to be clustered by thread id).

Scan/insert ratio: 95/5

readproportion=0

updateproportion=0

scanproportion=0.95

insertproportion=0.05


**Workload F: Read-modify-write**

Application example: user database, where user records are read and modified by the user or to record user activity.

Read/read-modify-write ratio: 50/50

readproportion=0.5

updateproportion=0

scanproportion=0

insertproportion=0

readmodifywriteproportion=0.5

**Links**

> https://github.com/brianfrankcooper/YCSB#getting-started

> https://github.com/brianfrankcooper/YCSB/tree/master/workloads


**Resource 2**

> http://www.datastax.com/wp-content/uploads/2013/02/WP-Benchmarking-Top-NoSQL-Databases.pdf


**Resource 3 -- Past paper**

> http://www.cubrid.org/blog/dev-platform/nosql-benchmarking/
