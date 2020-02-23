set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
start rmiregistry 2000
rmic server.Server
java -classpath "F:\Workspace\P2PFileSharing\target\P2PFileSharing\Server;F:\Workspace\P2PFileSharing\target\P2PFileSharing\lib\*" -Dlog4j.configuration=file:///f:/Workspace/P2PFileSharing/target/P2PFileSharing/log4j.properties -Djava.rmi.server.codebase=file:F:\Workspace\P2PFileSharing\target\P2PFileSharing\Server server.ServerDriver 2000
timeout /t 500