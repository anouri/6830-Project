**Cassandra**:

Download the Software

> curl -OL http://downloads.datastax.com/community/dsc.tar.gz

Install Cassandra
> tar -xzf dsc-cassandra-1.2.2-bin.tar.gz

> cd dsc-cassandra-1.2.2/bin

> sudo ./cassandra # this will start cassandra server

> ./csql # if you run the above command as background, or open a different terminal

**How to run the demo**:

The Cassandra folder contains the demo that display how the cassandra works by inserting a column to the table and display it. In order to the code to run successfully, you needs to run the following on the terminal:

*Create name space*

``
create keyspace mykeyspace with replication = {'class':'SimpleStrategy','replication_factor':1};
``

```
CREATE TABLE users (

firstname text,

lastname text,

age int,

email text,

city text,

PRIMARY KEY (lastname));
```
