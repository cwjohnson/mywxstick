/bin/sh

VERSION="2.2"
ARTIFACT="rxtx"
GROUP="org.rxtx"

mvn install:install-file -Dfile=`pwd`/RXTXcomm.jar -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=jar
mvn install:install-file -Dfile=`pwd`/rxtx-2.2-bin.so -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=so -Dclassifier=bin
