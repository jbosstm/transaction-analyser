TxVis: Transaction Profiling and Visualisation Tool
===================================================

About
-----
TxVis is a prototype transaction profiling and visualisation tool for WildFly / JBoss EAP for highlighting possible performance issues with transactional applications.

TxVis is currently in an early prototype stage and only a small subset of planned features are available and the tool may not be stable.

Dependencies
------------

The following dependencies must be satisfied before running the tool. 

### WildFly

Currently this works with the latest WildFly master. To build it:

    git clone https://github.com/wildfly/wildfly.git
    ./build.sh clean install -DskipTests

You need to change the logging level for the transaction manager. This is what the profiler reads in order to get its information.

Open standalone/configuration/standalone.xml and change:

            <logger category="com.arjuna">
                <level name="WARN"/>
            </logger>

To:
            <logger category="com.arjuna">
                <level name="TRACE"/>
            </logger>

Also, set the TRACE level on the periodic-rotating-file-handler, by adding the "<level name="TRACE"/>" block. This ensures that the transaction manager's
TRACE logging appears in the standalone/log/server.log file.

            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <level name="TRACE"/>
                <formatter>
                    <named-formatter name="PATTERN"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>


### MySQL

A local MySQL server is required running on `localhost` with the default port of `3306` and needs to be configured with the following database and users:

A user: `txvis` with password: `txvis` which has ALL privileges on a database called `txvis`.

This can be created from the command line like so (note: leave root password blank if you have never set it before):

	>mysql -u root -p <root password>
	mysql> CREATE DATABASE txvis;
	mysql> GRANT ALL PRIVILEGES ON txvis.* TO txvis@localhost IDENTIFIED BY 'txvis';
	
In order for MySQL to work with JBoss you will need to install the Connector/J MySQL JDBC driver this can be achieved by following these steps:

1. Download Connector/J from MySQL at: http://dev.mysql.com/downloads/connector/j/
2. Extract the archive and copy mysql-connector-java-[version]-bin.jar to `$JBOSS_HOME/modules/system/layers/base/com/mysql/main` recursively creating the directories if they don't exist.
3. Copy module.xml from `[project-root]/etc` to the same directory as above; NOTE: if you downloaded a version of Connector/J other than 5.1.26 you will need to edit module.xml and update the resource-root path property to point to the correct connector/J jar file.
4. Add the following lines to your JBoss standalone.xml file:
		
   <driver name="MySqlNonXA" module="com.mysql">
       <datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlDataSource</datasource-class>
   </driver>
    
This should be placed inside the `<datasources><drivers>` tags.

### Maven

Maven 3+ should be installed and present in your OS's PATH environment variable.

### commons-io-2.5-SNAPSHOT.jar

The commons-io 2.5-SNAPSHOT release is required as it contains a bug fix that is essential to the correct running of this tool. The snapshot release is not available from a central maven repository so a compiled jar is included in the [project root]/etc/lib directory. 

This should be installed into your local maven repository using the following commands

    cd ./etc
    ./installdeps.sh

Alternatively this can be installed by running the following maven command from the [project root]/etc/lib directory: 
	mvn install:install-file -Dfile=commons-io-2.5-SNAPSHOT.jar -DgroupId=commons-io -DartifactId=commons-io -Dversion=2.5-SNAPSHOT -Dpackaging=jar
	
Running TxVis
-------------

Start the application server and check it boots without errors.

A shell script is included in the project root folder to deploy and start txvis: `deploy.sh` the corresponding script `undeploy.sh` is included to shut it down.

The user interface can be accessed by pointing a browser at: http://localhost:8080/txvis/


## Profiling a Distributed Transaction

To profile a distributed transaction you will need to make some additional configuration changes:

Enable JTS and set the Node ID, by updating the transactions SubSytem config in standalone/configuration/standalone-full.xml. In particular you are adding the 'node-identifier' attribute
and the 'jts' block.

    <subsystem xmlns="urn:jboss:domain:transactions:1.3">
        <core-environment node-identifier="${jboss.tx.node.id}">
            <process-id>
                <uuid/>
            </process-id>
        </core-environment>
        <recovery-environment socket-binding="txn-recovery-environment" status-socket-binding="txn-status-manager"/>
        <coordinator-environment default-timeout="300"/>
        <jts/>
    </subsystem>

Update the JacOrb service for use by JTS:

    <subsystem xmlns="urn:jboss:domain:jacorb:1.3">
        <orb socket-binding="jacorb" ssl-socket-binding="jacorb-ssl" name="${jboss.node.name}">
            <initializers security="identity" transactions="on"/>
        </orb>
        <naming root-context="${jboss.node.name}/Naming/root"/>
    </subsystem>


Now start each server involved in the transaction. The following commands show how to run three separate copies of WildFly on the same computer. It uses port offsets to ensure each uses
different ports. Also different node names and IDs are needed for each instance:

    ./bin/standalone.sh -Djboss.node.name=alpha -Djboss.tx.node.id=alpha -c standalone-full.xml
    ./bin/standalone.sh -Djboss.node.name=gamma -Djboss.tx.node.id=gamma -c standalone-full.xml -Djboss.socket.binding.port-offset=200
    ./bin/standalone.sh -Djboss.node.name=beta -Djboss.tx.node.id=beta -c standalone-full.xml -Djboss.socket.binding.port-offset=100


### Running an example to create some transactions

Clone the following project. This will allow you to run some successful and failing transactions and observe them in the Transaction Profiler.

    git clone https://github.com/alexcreasy/distributed-test-service.git
    cd distributed-test-service

Add the following driver to standalone/configuration/standalone-full.xml.

    <driver name="MySqlXA" module="com.mysql">
        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
    </driver>

Change driver in the DS of application1, currently it is configured to use Postgres. Edit ./application-component-1/src/main/webapp/WEB-INF/jts-quickstart-ds.xml and set:

    <driver>MySqlXA</driver>

Now create the required database and user:

	>mysql -u root -p <root password>
	mysql> CREATE DATABASE jts-quickstart-database;
	mysql> GRANT ALL PRIVILEGES ON jts-quickstart-database.* TO sa@localhost IDENTIFIED BY 'sa';

Deploy the application to each server:

    mvn package jboss-as:deploy

Copy the 'lite' version of the Transaction Profiler to the 2nd and 3rd servers.

  cp ./tx-profiler-lite/target/tx-profiler-lite.ear <PATH TO DEPLOY DIR ON SERVER2>
  cp ./tx-profiler-lite/target/tx-profiler-lite.ear <PATH TO DEPLOY DIR ON SERVER3>


Visit the Transaction Profiler page: http://localhost:8080/txvis/. This is where the transaction details will apear after they are run.

Run the transactions by visiting here and creating some users: http://localhost:8080/jboss-as-jts-application-component-1/addCustomer.jsf



