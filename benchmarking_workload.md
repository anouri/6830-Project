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

INSERT INTO chartitems VALUES (?, ?, ?, ?);

DELETE FROM chartitems WHERE chartitems.itemid = ? OR chartitems.category = ?;

UPDATE chartitems SET chartitems.label = ?, chartitems.category = ?, chartitems.description = ? WHERE chartitems.itemid = ?;

SELECT patients.subject_id, chartitems.itemid FROM patients, chartitems WHERE patients.subject_id = chartitems.category GROUP BY patients.dod ORDER BY chartitems.category ASC;

**Workload A: Update heavy workload**

Application example: Session store recording recent actions

Read/update ratio: 50/50

readproportion=0.5 = 500

Table: medevents, fields: [subject_id, charttime, site], where: (subject_id < 500 and itemid > 20) --> 200 times

Table: labevents, fields: [labevent_id, labvalue], where: icustay_id == 2, orderby charttime asc --> 200 times

Table: chartevents, fields: [subject_id, elemid, annotation], where: icustay_id not in [1, 3, 5] or itemid == 10 --> 100 times

updateproportion=0.5

Table: patients, fields: [dod, hospital_expire_flg], where: (subject_id == 100) --> 200 times

Table: icustayevents, fields: [outtime, intime], where: (icustay_id >= 400) --> 300 times

scanproportion=0

insertproportion=0

**Workload B: Read mostly workload**

Application example: photo tagging; add a tag is an update, but most operations are to read tags
                        
Read/update ratio: 95/5

readproportion=0.95

Table: medevents, fields: [subject_id, charttime, site], where: (subject_id < 500 and itemid > 20) --> 150 times

Table: labevents, fields: [labevent_id, labvalue], where: icustay_id == 2, orderby charttime asc --> 400 times

Table: chartevents, fields: [subject_id, elemid, annotation], where: icustay_id not in [1, 3, 5] or itemid == 10 --> 400 times

updateproportion=0.05

Table: patients, fields: [dod, hospital_expire_flg], where: (subject_id == 100) --> 20 times

Table: icustayevents, fields: [outtime, intime], where: (icustay_id >= 400) --> 30 times

scanproportion=0

insertproportion=0

MySQL: 272406 milliseconds

MongoDB: 125597 milliseconds


**Workload C: Read only**

Application example: user profile cache, where profiles are constructed elsewhere (e.g., Hadoop)
                        
Read/update ratio: 100/0

readproportion=1.0

Table: medevents, fields: [subject_id, charttime, site], where: (subject_id < 500 and itemid > 20) --> 300 times

Table: labevents, fields: [labevent_id, labvalue], where: icustay_id == 2, orderby charttime asc --> 300 times

Table: chartevents, fields: [subject_id, elemid, annotation], where: icustay_id not in [1, 3, 5] or itemid == 10 --> 400 times

updateproportion=0

scanproportion=0

insertproportion=0

MySQL: 188062 milliseconds

MongoDB: 14703 milliseconds

**Workload D: Read latest workload**

Application example: user status updates; people want to read the latest.

Read/update/insert ratio: 95/0/5

readproportion=0.95

Table: medevents, fields: [subject_id, charttime, site], where: (subject_id < 500 and itemid > 20) --> 150 times

Table: labevents, fields: [labevent_id, labvalue], where: icustay_id == 2, orderby charttime asc --> 400 times

Table: chartevents, fields: [subject_id, elemid, annotation], where: (icustay_id !=20) or itemid == 10 --> 400 times

updateproportion=0

scanproportion=0

insertproportion=0.05

Table: medevents --> 30

Table: meddurations --> 20

MySQL: 181164 milliseconds

MongoDB: 20025 milliseconds

**Workload E: Short ranges**

In this workload, short ranges of records are queried, instead of individual records. 

Application example: threaded conversations, where each scan is for the posts in a given thread (assumed to be clustered by thread id).

Scan/insert ratio: 95/5

readproportion=0

updateproportion=0

scanproportion=0.95

Table: medevents, fields: *, where: (subject_id < 500 and itemid > 20) --> 150 times

Table: labevents, fields: *, where: icustay_id == 2, orderby charttime asc --> 400 times

Table: chartevents, fields: *, where: icustay_id not in [1, 3, 5] or itemid == 10 --> 400 times

insertproportion=0.05

Table: medevents --> 30

Table: meddurations --> 20

MySQL: 166806 milliseconds

MongoDB: 36186 milliseconds

**Workload F: Read-modify-write**

Application example: user database, where user records are read and modified by the user or to record user activity.

Read/read-modify-write ratio: 50/50

readproportion=0.5

Table: medevents, fields: [subject_id, charttime, site], where: (subject_id < 500 and itemid > 20) --> 200 times

Table: labevents, fields: [labevent_id, labvalue], where: icustay_id == 2, orderby charttime asc --> 200 times

Table: chartevents, fields: [subject_id, elemid, annotation], where: icustay_id not in [1, 3, 5] or itemid == 10 --> 100 times

updateproportion=0

scanproportion=0

insertproportion=0

readmodifywriteproportion=0.5

1. 250 times

Select

- Table: patients, fields: [subject_id, sex], where: (subject_id < 100) 

Update

- Table: patients, fields: [dod, hospital_expire_flg], where: (subject_id == 100)

2. 250 times

Select

- Table: icustayevents, fields:*, where: (subject_id < 150) 

Update

- Table: icustayevents, fields: [outtime, intime], where: (icustay_id >= 400)

MySQL: 836876 milliseconds

MongoDB: 314502 milliseconds


**Links**

> https://github.com/brianfrankcooper/YCSB#getting-started

> https://github.com/brianfrankcooper/YCSB/tree/master/workloads


**Resource 2**

> http://www.datastax.com/wp-content/uploads/2013/02/WP-Benchmarking-Top-NoSQL-Databases.pdf


**Resource 3 -- Past paper**

> http://www.cubrid.org/blog/dev-platform/nosql-benchmarking/
