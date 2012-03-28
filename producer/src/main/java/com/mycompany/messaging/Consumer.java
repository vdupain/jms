package com.mycompany.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Consumer {

	public static void main(String[] args) {
		Context jndiContext = null;
		ConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		Destination destination = null;
		MessageConsumer consumer = null;
		String destinationName = null;

		if ((args.length < 1) || (args.length > 1)) {
			System.out.println("Usage: java Consumer <destination-name>");
			System.exit(1);
		}
		destinationName = args[0];
		System.out.println("Destination name is " + destinationName);

		try {
			jndiContext = new InitialContext();
			connectionFactory = (ConnectionFactory) jndiContext
					.lookup("ConnectionFactory");
			destination = (Destination) jndiContext.lookup(destinationName);
		} catch (NamingException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			connection = connectionFactory.createConnection();
			ConnectionMetaData metaData = connection.getMetaData();
			String info = "Connected to " + metaData.getJMSProviderName()
					+ " version " + metaData.getProviderVersion() + " ("
					+ metaData.getProviderMajorVersion() + "."
					+ metaData.getProviderMinorVersion() + ")";
			System.out.println(info);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(destination);

			while (true) {
				Message message = consumer.receive(2000);
				if (message == null) {
					System.out.println("waiting...");
					continue;
				}
				if (message instanceof TextMessage) {
					TextMessage txtMessage = (TextMessage) message;
					System.out.println("Message received: "
							+ txtMessage.getText() + ", priority:"
							+ txtMessage.getJMSPriority());
				} else {
					System.out.println("Invalid message received.");
				}
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JMSUtils.closeJndiContext(jndiContext);
			JMSUtils.closeMessageConsumer(consumer);
			JMSUtils.closeConnection(connection);
		}

	}
}
