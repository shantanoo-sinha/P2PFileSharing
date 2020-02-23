set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
start rmiregistry 2003
rmic client.Client
timeout /t 5
java -classpath "F:\Workspace\P2PFileSharing\target\P2PFileSharing\Server;F:\Workspace\P2PFileSharing\target\P2PFileSharing\lib\*" -Dlog4j.configuration=file:///f:/Workspace/P2PFileSharing/target/P2PFileSharing/log4j.properties -Djava.rmi.server.codebase=file:F:\Workspace\P2PFileSharing\target\P2PFileSharing\Server client.ClientDriver 2000 2003 Client3
timeout /t 50