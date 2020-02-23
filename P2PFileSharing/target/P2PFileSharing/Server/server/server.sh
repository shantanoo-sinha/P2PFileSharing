rmiregistry 2000 &
export CLASSPATH="$CLASSPATH:./../lib/*"
echo $CLASSPATH
rmic -classpath $CLASSPATH server.Server
java -classpath $CLASSPATH -Dlog4j.configuration="file:./../log4j.properties" -Djava.rmi.server.codebase="file:./../Server" server.ServerDriver 2000
sleep 500