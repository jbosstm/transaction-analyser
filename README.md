TxVis: Transaction Profiling and Visualisation Tool
===================================================

About
-----
TxVis is a prototype transaction profiling and visualisation tool for WildFly / JBoss EAP for highlighting possible performance issues with transactional applications.

TxVis is currently in an early prototype stage and only a small subset of planned features are available and the tool may not be stable.

Dependencies
------------

The following dependencies must be satisfied before running the tool. 

### MySQL

A local MySQL server is required running on `localhost` with the default port of `3306` and needs to be configured with the following database and users:

A user: `txvis` with password: `d8mmANpFJVQUXMtb` which has ALL privileges on a database called `txvis`.

This can be created from the command line like so (note: leave root password blank if you have never set it before):

	>mysql -u root -p <root password>
	mysql> CREATE DATABASE txvis;
	mysql> GRANT ALL PRIVILEGES ON txvis.* TO txvis@localhost IDENTIFIED BY 'd8mmANpFJVQUXMtb';
	
In order for MySQL to work with JBoss you will need to install the Connector/J MySQL JDBC driver this can be achieved by following these steps:

1. Download Connector/J from MySQL at: http://dev.mysql.com/downloads/connector/j/
2. Extract the archive and copy mysql-connector-java-[version]-bin.jar to `$JBOSS_HOME/modules/com/mysql/main` recursively creating the directories if they don't exist.
3. Copy module.xml from `[project-root]/etc` to the same directory as above; NOTE: if you downloaded a version of Connector/J other than 5.1.25 you will need to edit module.xml and update the resource-root path property to point to the correct connector/J jar file.
4. Add the following lines to your JBoss standalone.xml file:
		
		<driver name="MySqlXA" module="com.mysql">
			<xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
		</driver>
    
This should be placed inside the `<datasources><drivers>` tags.

### Maven

Maven 3+ should be installed and present in your OS's PATH environment variable.

### commons-io-2.5-SNAPSHOT.jar

The commons-io 2.5-SNAPSHOT release is required as it contains a bug fix that is essential to the correct running of this tool. The snapshot release is not available from a central maven repository so a compiled jar is included in the [project root]/etc/lib directory. 

This should be installed into your local maven repository using the shell script `installdeps.sh` available in the [project root]/etc/ directory: 

Alternatively this can be installed by running the following maven command from the [project root]/etc/lib directory: 
	mvn install:install-file -Dfile=commons-io-2.5-SNAPSHOT.jar -DgroupId=commons-io -DartifactId=commons-io -Dversion=2.5-SNAPSHOT -Dpackaging=jar
	
Running TxVis
-------------
A shell script is included in the project root folder to deploy and start txvis: `deploy.sh` the corresponding script `undeploy.sh` is included to shut it down.

The user interface can be accessed by pointing a browser at: http://localhost/txvis/
