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


## Building from source
If you have a binary release of this tool, you can skip to the 'Deploy to WildFly' and 'Running the Demo Application' sections.

To build the `Transaction Analyser` from source you need Maven 3.0.0+ installed and available on your path. Then run:

    mvn clean install

## Download and Configure WildFly

Currently the `Transaction Analyser` can work with the WildFly 10.0.0.Final:

    wget http://download.jboss.org/wildfly/10.0.0.Final/wildfly-10.0.0.Final.zip
    unzip wildfly-10.0.0.Final.zip
    export WILDFLY_HOME=/path/to/wildfly-10.0.0.Final.zip

## Run Wildfly

    cd $WILDFLY_HOME/bin
    ./standalone.sh -c standalone-full.xml

## Deploy to WildFly
To run the `Transaction Analyzer` you just need to deploy the ear file to WildFly and visit the console. To do this:

    cp nta-dist/target/nta-1.0.0.Alpha3-SNAPSHOT.ear $WILDFLY_HOME/standalone/deployments

Then visit: http://localhost:8080/nta/. By default the `Transaction Analyser` is disabled. To enable it click on the 'start' button at the top-right
of the console's home page. Similarly, you can stop it by clicking the 'stop' button.


## Running the Demo Application
The demo application provides a simple way to play with this tool if you don't have an application with failing transactions to hand. To deploy it:

    cp demo/target/nta-demo-1.0.0.Alpha3-SNAPSHOT.war $WILDFLY_HOME/standalone/deployments

The demo is now available at http://localhost:8080/txdemo. To use the demo, simply click on the button for the scenario you want to run. When the scenario
completes you will be notified of the outcome from the transaction. For most scenarios you will get an error message returned. This is to be expected
as most scenarios result in a failing transaction. After running a scenario, go to the `Transaction Analyser` console and look at the details of the
latest transaction.

NOTE: if you don't see any transactions in the `Transaction Analyser`, you may have forgotten to hit 'start' to enable analysis.
