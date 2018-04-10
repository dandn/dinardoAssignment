#!/bin/bash

# specify directory where jmeter binary is installed
JMETER_INSTALLDIR=/Users/ddinardo/Documents/apache-jmeter-4.0/bin

# specify location of jmx file containing performance tests
JMX_FILE=REST_API_Performance_Tests.jmx

# specify target directory for resuling jtl files
JTL_TARGETDIR=perfresults

# specify ipaddress of pet store webapp
IPADDRESS=localhost

# specify port of pet store webapp
PORT=3000

# specify the duration of each test (seconds)
DURATION=120

# specify number of times to repeat tests - an average of test runs might give us a better perspective of system behaviour
MAXREPEAT=1



mkdir -p ${JTL_TARGETDIR}

for repeatCount in `seq 1 ${MAXREPEAT}`; do

  # get system time
  DATE=`date +%Y%m%d%H%M%S`

  for threadCount in 1 10; do
    # initialize db contents
    mvn -Dtest=InitializeTests#testMediumDb test

    # run the performance tests for Thread Group 1 - this thread more accurately represents the current operation of the Petstore webapp: 'getAllPets' is called after every post/put operation
    ${JMETER_INSTALLDIR}/jmeter -n -t ${JMX_FILE} -l ${JTL_TARGETDIR}/results_g1_${threadCount}threads-${DATE}.jtl \
      -Jthreads1=$threadCount -Jthreads2=0 -Jduration=${DURATION} -Jrampup=10 -Jipaddress=${IPADDRESS} -Jport=${PORT}

    # initialize db contents
    mvn -Dtest=InitializeTests#testMediumDb test

    # run the performance tests for Thread Group 2 - this thread represents a potentially better approach: call 'getPetById' to refresh only the implicated fields on the screen
    ${JMETER_INSTALLDIR}/jmeter -n -t ${JMX_FILE} -l ${JTL_TARGETDIR}/results_g2_${threadCount}threads-${DATE}.jtl \
      -Jthreads2=$threadCount -Jthreads1=0 -Jduration=${DURATION} -Jrampup=10 -Jipaddress=${IPADDRESS} -Jport=${PORT}
  done

done
