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


