# Narayana Transaction Analyser

The `Narayana Transaction Analyser` is a tool for helping to diagnose issues with JTA and JTS transactions in WildFly
and JBoss EAP 6. Once deployed, the tool provides a list of all transactions ran within the application server. Detailed
information is available for each transaction; such as what participants where involved and how they behaved. This makes
it easier to find out what went wrong in the case of a transaction rollback.

The `Transaction Analyser` also supports distributed transactions that use JTS. Here the tool will present a diagram of
all the servers involved, including which participants were enlisted with each application server and how they behaved. The distributed
case is covered in README-jts.md. This document just covers the single application server case.

Under normal circumstances your application is unlikely to see transaction related issues. For this reason, the tool ships
with a demo application allowing you to trigger failing transactions on-demand. You can then use the tool to see how
these issues would be reported.


## Building and deploying

The following steps explain how to build and deploy the `Transaction Analyser` from source.

## Build and Configure WildFly

Currently the `Transaction Analyser` only works with the latest WildFly master. To build it:

    git clone https://github.com/wildfly/wildfly.git
    ./build.sh clean install -DskipTests

You need to change the logging level for the transaction manager. This is what the tool reads in order to get its information. To do this:

Start WildFly

    cd ./build/target/wildfly-8.0.0.Beta2-SNAPSHOT

Connect to the server via jboss-cli and run the commands to update the logging configuration:

    ./bin/jboss-cli.sh --connect --command="/subsystem=logging/periodic-rotating-file-handler=FILE:write-attribute(name=level,value=TRACE)"
    ./bin/jboss-cli.sh --connect --command="/subsystem=logging/logger=com.arjuna:write-attribute(name=level,value=TRACE)"


## Maven

Maven 3+ should be installed and present in your OS's PATH environment variable.

## commons-io-2.5-SNAPSHOT.jar

The commons-io 2.5-SNAPSHOT release is required as it contains a bug fix that is essential to the correct running of this tool.
The snapshot release is not available from a central maven repository so a compiled jar is included in the [project root]/etc/lib directory.

This should be installed into your local maven repository using the following command:

    mvn install:install-file -Dfile=./etc/lib/commons-io-2.5-SNAPSHOT.jar -DgroupId=commons-io -DartifactId=commons-io -Dversion=2.5-SNAPSHOT -Dpackaging=jar

## Build and Deploy the Transaction Analyser
Ensure WildFly is running, then run:

    mvn clean package -DskipTests jboss-as:deploy

The user interface can now be accessed by pointing a browser at: http://localhost:8080/txvis/


## Running the Demo Application
The demo application provides a simple way to play with this tool if you don't have an application with failing transactions to hand. To deploy it:

    cp demo/target/demo.war $WILDFLY_HOME/standalone/deployments

The demo is now available at http://localhost:8080/txdemo. To use the demo, simply click on the button for the scenario you want to run. When the scenario
completes you will be notified of the outcome from the transaction. For most scenarios you will get an error message returned. This is to be expected
as most scenarios result in a failing transaction. After running a scenario, go to the `Transaction Analyser` console and look at the details of the
latest transaction.

NOTE: if you don't see any transactions in the `Transaction Analyser`, you may have miss-configured the logging. See above for how to correctly configure the logging.
This is essential as the `Transaction Analyser` reads the server.log file to figure out what happened during the transaction.