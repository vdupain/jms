# Pour lancer le provider JMS ActiveMQ
mvn activemq:run

# Envoi des messages
mvn clean compile exec:java -Dexec.mainClass=com.mycompany.messaging.Producer -Dexec.args="queue1 10"

# Consommation des messages
mvn clean compile exec:java -Dexec.mainClass=com.mycompany.messaging.Consumer -Dexec.args="queue1"

