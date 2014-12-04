**NOTE: DON'T INCLUDE YOUR OWN JAR IF YOU DON' HAVE TOO. PLEASE USE POM.XML TO INCLUDE YOUR DEPENDENCY**

# IDE Setup

**Maven**:
Make sure your mvn --version has java version 1.8 otherwise there will be a minor/major error when you do `mvn clean install`.

This project is managed using maven. In order to have the jar that include all of the dependency, use ``mvn package`` or ``mvn clean install`` or ``mvn -Dmaven.test.skip=true install``. It will generate a uber jar file in target name DatabaseBenchmarking-1.0-SNAPSHOT.jar. You can run any class in the jar with the following command:
> java -cp target/DatabaseBenchmarking-1.0-SNAPSHOT.jar [classname]

For example: > java -cp target/DatabaseBenchmarking-1.0-SNAPSHOT.jar SQLSchemaParser

**Create a user defined library in Eclipse**

1. Right click on the project and select properties
2. Create a Library -> User Library called “xxx”, in this case, can be called "Unity Library"
3. Add in all external libraries necessary
4. Finish


**Unity for Consistent Multi-db Accesses**

**Dependencies**

All of the dependecies can be found in UnityJar folder on root directory

First, nstall unity jdbc by running UnityJDBC_Trial_Install.jar (Shirley signed up for a free trial)

Then, manually add in the following dependencies: *This step is no longer needed*
- unityjdbc.jar
- mongodb_unityjdbc_full.jar
- cassandra-jdbc-2.1.jar

**DB Configurations**

All the db configurations are in:

src/main/java/db.properties

**Configure Cassandra**

In cassandra.yaml (you can find this in the dsc-cassandra-2.1.1/conf directory), change thrift_framed_transport_size_in_mb: 18

Make sure port is set to 9160.

**Configure MySQL**

start mysql client using:

```shell
sudo mysql
```

Then run:
```shell
GRANT ALL PRIVILEGES ON *.* TO '*'@'localhost';
```

**Benchmarking Output Sample**

db is: cassandra run time is: 96

db is: mongo run time is: 107

db is: mysql run time is: 7

#Running Queries and Plotting

**API**

>QueryExecutorAll.java

```java
public static Properties initializeProp()
- Initializes db config defined in db.properties

public static void set_cassandra_keyspace(String keyspaceName)
- Sets Cassandra keyspace

public static void shema_creation_all(String createProcedure)
- creates a specified schema in all supported databases in batch 
- Only supports "PRIMARY KEY" but *NOT* "AUTO_INCREMENT" or "NOT NULL"
- Unique primary key is handled by data generation
- In the case of mongo, the primary key is created as a unique index called "primary_key" --> check using db.getIndexes()
- Sample shema looks like:
                    "DROP TABLE IF EXISTS follower;" +
                    "CREATE TABLE follower (who_id int, whom_id int, PRIMARY KEY (who_id));"+
                    "DROP TABLE IF EXISTS message;" +
                    "CREATE TABLE message (message_id int, text text, PRIMARY KEY (message_id));"

public static void dropTables(String[] tableNames)
- drops the tables in the list in all databases

public static HashMap<String, Long> run_all(String rawSQLQuery)
- Runs a raw SQLQuery on all dbs supported. 
- Currently supports, insert, update, delete, select, join statements
```

For large queries, need to set aside large heap space:
JVM command line parameters: -Xms500m -Xmx500m
These parameters set heap space to 500 MB.  Do not set higher than 80% of physical machine memory size.

>BarChart.java

```java
public BarChart(final String title, HashMap<String, HashMap<String, Long>> results)
- Constructor taking in a hashmap of hashmap
	- outer hashmap: key: query type; value: runtime map
	- inner hashmap: key: db name; value: runtime for that db 
	
public BarChart(final String title)
- Constructor using test data

private CategoryDataset createDataset(HashMap<String, HashMap<String, Long>> results) 
- creates chart with a hashmap of hashmap

private CategoryDataset createDatasetTest()
- creates chart with test data

```

To run chart plotting with test data as included in main in BarChart.java:
```java
		final BarChart demo = new BarChart("Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
```

**Test**

>ApacheCommonsDBCPTest.java

```java
		setup(); // creates all tables and sets keyspaces
    	testInsertionFromJSON(); // inserts form json
    	cleanUpTables(); // deletes all tables
    	testBatchInsertionFromJSON();
    	testDeletion();
    	testUpdate();
    	testJoin();
```

**POOLING**

Pooling is supported by ApacheCommonsDBCP, and libraries are in pom.xml
Why pooling? With connection pooling, a pool of connections can be used over and over again, avoiding the expense of creating a new connection for every database access. 

**Additional Resources**

SQL to NoSQL Translation

http://www.unityjdbc.com/mongojdbc/mongosqltranslate.php

http://www.unityjdbc.com/mongojdbc/code/ExampleMongoTranslate.java


**Batch Queries**

Can run callable stored procedures:

http://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-statements-callable.html

#Testing

Example Input Schema
```shell
DROP TABLE user;
CREATE TABLE user (user_id integer PRIMARY KEY auto_increment,
                   username text NOT NULL, email text NOT NULL,
                    pw_hash text NOT NULL);

DROP TABLE follower;

CREATE TABLE follower (who_id integer,whom_id integer);

DROP TABLE message;

CREATE TABLE message (message_id integer PRIMARY KEY auto_increment,author_id integer NOT NULL,text text NOT NULL,pub_date integer);
```

Example JSON Output for Distribution (Kristin can expect this format to generate data set)
```shell
{ 
  "user" : {
    "cardinality" : 1000, 
    "fields" : [{
      "category": "Integer", 
      "length": 4, 
      "name": "user_id", 
      "distribution": "auto_increment", 
      "distinct": nil, 
      "mean": nil, 
      "stdv": nil, 
      "min": nil, 
      "max": nil
    }, {
      "category": "String", 
      "length": 128, 
      "name": "username", 
      "distribution": "uniform", 
      "distinct": 50, 
      "mean": nil, 
      "stdv": nil, 
      "min": 4, 
      "max": 8
    }, {
      "category": "String", 
      "length": 128, 
      "name": "email", 
      "distribution": "delta", 
      "distinct": 100, 
      "mean": nil, 
      "stdv": nil, 
      "min": 6, 
      "max": 18
    }, {
      "category": "String", 
      "length": 128, 
      "name": "pw_hash", 
      "distribution": "normal", 
      "distinct": nil, 
      "mean": 10, 
      "stdv": 4, 
      "min": nil, 
      "max": nil
    }]
  }, 
  "follower": {
    "cardinality": 2000, 
    "fields": [{
      "category": "Integer", 
      "length": 4, 
      "name": "who_id", 
      "distribution": 
      "normal", 
      "distinct": 10, 
      "mean": 5, 
      "stdv": 1, 
      "min": nil, 
      "max": nil
    }, {
      "category": "Integer", 
      "length": 4, 
      "name": "whom_id", 
      "distribution": "delta", 
      "distinct": 10, 
      "mean": nil, 
      "stdv": nil, 
      "min": 1, 
      "max": 100
    }]
  }, 
  "message": {
    "cardinality": 3000, 
    "fields": [{
      "category": "Integer", 
      "length": 4, 
      "name": "message_id", 
      "distribution": "auto_increment", 
      "distinct": nil, 
      "mean": nil, 
      "stdv": nil, 
      "min": nil, 
      "max": nil
    }, {
      "category": "Integer", 
      "length": 4, 
      "name": "author_id", 
      "distribution": "normal", 
      "distinct": 10, 
      "mean": 2, 
      "stdv": 1, 
      "min": nil, 
      "max": nil
    }, {
      "category": "String", 
      "length": 128, 
      "name": "text", 
      "distribution": "delta",
      "distinct": 20, 
      "mean": nil, 
      "stdv": nil, 
      "min": 10, 
      "max": 40
    }, {
      "category": "Integer", 
      "length": 4, 
      "name": "pub_date", 
      "distribution": "uniform", 
      "distinct": 18, 
      "mean": nil, 
      "stdv": nil, 
      "min": 20, 
      "max": 60
    }]
  }
}
```

Updates to JSON Output to Data Generator:
- distribution can be "uniform", "delta", or "normal" (and for strings, refers to distribution on length of string)
- categories can be "String" or "Integer"
- I'm currently ignoring the length field, so we might not need this
- add two fields "mean" and "stdv"
- if distribution is normal: mean and stdv are non-null; min and max are null
- else: mean and stdv are null; min and max are not
- if the user doesn't know how many distinct values there are, distinct is null and i'll sample from the distribution every time

Output JSON Grammar from Data Generator:
```shell
{
	tableName1: {
		colNames: [colName1, colName2, colName3],
		colData: [[1,2,3],[4,5,6],[7,8,9]]
	},
	tableName2: {
		colNames: [colName4, colName5, colName6],
		colData: [['x','y','z'],['x','y','z'],['x','y','z']]
	}
}
```

#Data Generator

create a new FastDataGenerator with 
```shell
public FastDataGenerator(String jsonString)
```

then use that FastDataGenerator to get subsequent sets of 100 rows (or fewer if the current table is finishing) until all tables are fully generated:
```shell
public String generateMoreData()
```
where output String follows the grammar above.  When data is finished being generated, further calls will return null.

**On Indices and Joins**:
Because allowing the user to specify primary key columns could lead to major bottlenecks in randomly generating those distinct values, we'll automatically include an auto-incrementing primary key ID field as specified in any table the user creates.  We should display this to the user when they create the table in the UI, but they shouldn't be able to change it.  In the UI, this field should not be available for choosing distributions for columns, and should not be passed in the JSON input to the data generator, as its values will automatically be created when each record is inserted.

Since foreign keys must reference a primary key of another table to ensure it exists and is unique, users will only be able to have foreign keys that reference our automatically included ID fields on other tables.  Because we know the min and max values these IDs can have (from 0 to the cardinality of that table), we can automatically assume the distribution on the foreign key column to be {distribution: uniform, min: 0, max: cardinality of other table}, which should be included in the JSON input to the data generator.  We can show this in the UI for choosing distributions as unchangeable, or just not show it there at all.  

Adhering to these assumptions means we'll be able to analyze joins while keeping runtime of data generation reasonable.

# Installing DBs and Rails

**MySQL**

Download:

http://dev.mysql.com/downloads/mysql/

Start/end server:

sudo /usr/local/mysql/support-files/mysql.server start

sudo /usr/local/mysql/support-files/mysql.server stop

**Sybase**

Download the Sybase ASE Enterprise installer pushed in the repo (i signed up for trial, if it doesn't work sign up for an account):

Create /opt/sybase and /var/sybase directories, and make sure the user you are have write permission.

Run the following command to start the installer:

> sudo sh setup.bin LAX_VM /usr/bin/java

Click through to install the free developer version.

MAKE SURE THAT THE INSTALLER SAID INSTALLATION WAS SUCCESSFUL.

Your Sybase is installed here:
ASE-16_0/

Guideline for an earlier version:

http://www.petersap.nl/SybaseWiki/index.php?title=Installation_guidelines_ASE_15.5

**MongoDB**:

*Preferred*: 
If you have homebrew, you can simply use
```shell
brew update (optional)
brew doctor (optional)
brew upgrade (optional)
brew install mongodb 
```
Assume successfully run, you can now use
```shell
mongod
```
this will start the mongodb server.

Download the Software
```shell
curl -O http://downloads.mongodb.org/osx/mongodb-osx-x86_64-2.6.4.tgz
```

Install MongoDB
```shell
tar -zxvf mongodb-osx-x86_64-2.6.4.tgz
```
```shell
mkdir -p mongodb
```
```shell
cp -R -n mongodb-osx-x86_64-2.6.4/ mongodb
```

Replace <mongodb-install-directory> with the path to the extracted MongoDB archive.

```shell
export PATH=<mongodb-install-directory>/bin:$PATH
```

Make a directory for data **AS ROOT**:

```shell
sudo mkdir -p /data/db
```

Check permission on data directory:

```shell
ls -ld /data/db/
```
You should see:

> drwxr-xr-x 4 mongod mongod 4096 Oct 26 10:31 /data/db/

However, if you don't see the right permissions, run:

```shell
sudo chmod 0755 /data/db
```
```shell
sudo chown mongod:mongod /data/db
```
Now, to run without a path:

```shell
mongod
```

Run with path:

```shell
<path to binary>/mongod
```

**Cassandra**:

Download the Software

> curl -OL http://downloads.datastax.com/community/dsc.tar.gz

Install Cassandra
> tar -xzf dsc.tar.gz

> cd dsc-cassandra-1.2.2/bin

> sudo ./cassandra # this will start cassandra server in background, use -f if want foreground

> ./cqlsh # if you run the above command as background, or open a different terminal

*How to run the demo*:

The Cassandra folder contains the demo that display how the cassandra works by inserting a column to the table and display it. In order to the code to run successfully, you needs to run the following on the terminal:

*Create name space*

``
create keyspace mykeyspace with replication = {'class':'SimpleStrategy','replication_factor':1};
``

```
CREATE TABLE users (firstname text,lastname text,age int,email text,city text,PRIMARY KEY (lastname));
```

**Installing Ruby on Rails**

If you already have RoR installed, feel free to start fresh and re-install the newest version. Run the command rvm implode to remove your version. We'll be using JRuby to support Java usage with the web front end.

Install Xcode Command Line Tools

> xcode-select --install

Install RVM (Ruby Version Manager)

> \curl -sSL https://get.rvm.io | bash

Install JRuby

> rvm install jruby

> rvm --default use jruby-1.7.16.1

> Run ruby --version. You should get something like jruby 1.7.16.1 (1.9.3p392) 2014-10-28 4e93f31 on Java HotSpot(TM) 64-Bit Server VM 1.8.0_25-b17 +jit [darwin-x86_64]

Update Gem Manager

> gem update --system

Update Stale Gems

> echo "gem: --no-document" >> ~/.gemrc

> rvm gemset use global

> gem update

Install Nokogiri (dependency for many other gems)

> gem install nokogiri

> If installation fails, check out the Troubleshooting section at http://www.nokogiri.org/tutorials/installing_nokogiri.html

Adding Gemset to Existing Application and Installing Rails

> git pull (to get the WebApp code)

> cd 6830-Project/WebApp

> rvm use jruby-1.7.16.1@WebApp --ruby-version --create

> gem install rails 

> Run rails --version. You should get Rails 4.1.7 (might have to run `bundle install` if this command fails)

> bundle install

*Running the Web Application*

> cd to 6830-Project/WebApp directory

> rake db:migrate

> rails s

> You should see the home page by going to http://localhost:3000 

> If you get the message Illegal key size: possibly you need to install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files for your JRE proceed as follows

> Go to http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

> Download the jce_policy-8.zip. I'm currently using Java 8. If you have Java 7 just google jce7 and download that one, but you should upgrade to Java 8 to keep things uniform

> Extract jce-policy-8.zip. You will see local_policy.jar and US_export_policy.jar in the folder

> Move these two files into $JAVA_HOME/jre/lib/security directory

> For me, I did sudo mv local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/jre/lib/security

