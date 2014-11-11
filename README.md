**Maven**:
This project is managed using maven. In order to have the jar that include all of the dependency, use ``mvn package`` or ``mvn clean install``. It will generate a uber jar file in target name DatabaseBenchmarking-1.0-SNAPSHOT.jar. You can run any class in the jar with the following command:
> java -cp target/DatabaseBenchmarking-1.0-SNAPSHOT.jar [classname]

For example: > java -cp target/DatabaseBenchmarking-1.0-SNAPSHOT.jar SQLSchemaParser

**Dataset For Test**:
The smallest version that contains the context and the freebase id is ~5GB in size and since github has a limit on file size, I only included the first 5 out of 109 files in here for reference. 

To download the entire 5GB dataset, run the following command:
```shell
for (( i=1; i<110; i++)) do echo "Downloading file $i of 109"; f=`printf "%03d" $i` ; wget http://iesl.cs.umass.edu/downloads/wiki-link/context-only/$f.gz ; done ; echo "Downloaded all files, verifying MD5 checksums (might take some time)" ; diff --brief <(wget -q -O - http://iesl.cs.umass.edu/downloads/wiki-link/context-only/md5sum) <(md5sum *.gz) ; if [ $? -eq 1 ] ; then echo "ERROR: Download incorrect\!" ; else echo "Download correct" ; fi
```

**Example for Testing**

Example Input Schema
```shell
drop table user;create table user (user_id integer primary key autoincrement,username text not null,email text not null,pw_hash text not null);drop table follower;create table follower (who_id integer,whom_id integer);drop table message;create table message (message_id integer primary key autoincrement,author_id integer not null,text text not null,pub_date integer);
```

Example JSON Output for Distribution (Kristin can expect this format to generate data set)
```shell
{"follower"=>[{"category"=>"Integer", "length"=>4, "name"=>"who_id", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}, 
								{"category"=>"Integer", "length"=>4, "name"=>"whom_id", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}], 
		"message"=>[{"category"=>"Integer", "length"=>4, "name"=>"message_id", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}, 
								{"category"=>"Integer", "length"=>4, "name"=>"author_id", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}, 
								{"category"=>"String", "length"=>128, "name"=>"text", "distribution"=>"uniform", "distinct"=>1, "min"=>nil, "max"=>nil}, 
								{"category"=>"Integer", "length"=>4, "name"=>"pub_date", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}], 
		"user"=>[{"category"=>"Integer", "length"=>4, "name"=>"user_id", "distribution"=>"uniform", "distinct"=>1, "min"=>1, "max"=>1}, 
						 {"category"=>"String", "length"=>128, "name"=>"username", "distribution"=>"uniform", "distinct"=>1, "min"=>nil, "max"=>nil}, 
						 {"category"=>"String", "length"=>128, "name"=>"email", "distribution"=>"uniform", "distinct"=>1, "min"=>nil, "max"=>nil}, 
						 {"category"=>"String", "length"=>128, "name"=>"pw_hash", "distribution"=>"uniform", "distinct"=>1, "min"=>nil, "max"=>nil}]
	}
```

**MongoDB**:

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
CREATE TABLE users (firstname text,lastname text,age int,email text,city text,PRIMARY KEY (lastname));
```
**MonetDB**:

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

*Create a test database and table on terminal then see java example code*
> monetdbd create /path/to/mydbfarm (just name this path anything)

> monetdbd start /path/to/mydbfarm

> monetdb create voc

> monetdb release voc

> mclient -u monetdb -d voc

> password:monetdb

> sql> CREATE TABLE test (id int, data varchar(30));

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

> Run rails --version. You should get Rails 4.1.7

> bundle install

*Running the Web Application*

> In the 6830-Project/WebApp directory, run rails s

> You should see a page with your Java runtime version number by going to http://localhost:3000 

> If you get the message Illegal key size: possibly you need to install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files for your JRE proceed as follows

> Go to http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

> Download the jce_policy-8.zip. I'm currently using Java 8. If you have Java 7 just google jce7 and download that one, but you should upgrade to Java 8 to keep things uniform

> Extract jce-policy-8.zip. You will see local_policy.jar and US_export_policy.jar in the folder

> Move these two files into $JAVA_HOME/jre/lib/security directory

> For me, I did sudo mv local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/jre/lib/security


