rmiregistry 2002 &
export CLASSPATH="$CLASSPATH:./../Server/:./../lib/*"
echo $CLASSPATH
rmic -classpath $CLASSPATH -d ./../Server client.Client
sleep 5
java -classpath $CLASSPATH -Dlog4j.configuration="file:./../log4j.properties" -Djava.rmi.server.codebase=file:./../Server/ client.ClientDriver 2000 2002 Client2
sleep 50