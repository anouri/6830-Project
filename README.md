**Cassandra**:

Download the Software

> curl -OL http://downloads.datastax.com/community/dsc.tar.gz

Install Cassandra
> tar -xzf dsc-cassandra-1.2.2-bin.tar.gz

> cd dsc-cassandra-1.2.2/bin

> sudo ./cassandra # this will start cassandra server in background, use -f if want foreground

> ./csql # if you run the above command as background, or open a different terminal

*How to run the demo*:

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
***MonetDB***:

Download the Software

> https://www.monetdb.org/downloads/MacOSX/Oct2014/

> Download the file MonetDB-11.19.3-x86_64-Darwin-9-bin.tar.bz2

Install MonetDB
> Extract MonetDB-11.19.3-x86_64-Darwin-9-bin.tar.bz2 

> You will now have a folder called usr

> ln -s usr/local/monetdb /usr/local/monetdb

> Add MonetDB bin directory to path in shell config: export PATH=/usr/local/monetdb/bin:$PATH

> Install Xcode Command Line Tools: xcode-select --install

> Install Homebrew: ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

> brew install monetdb

> monetdbd create /path/to/mydbfarm (just name this path anything)

> monetdbd start /path/to/mydbfarm

> monetdb create voc

> mounted release voc

> mclient -u monetdb -d voc

> password:<monetdb>

> sql>

