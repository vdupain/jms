package com.mycompany.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Producer {

	public static void main(String[] args) {
		Context jndiContext = null;
		ConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		Destination destination = null;
		MessageProducer producer = null;
		String destinationName = null;
		int numMsgs = 1;
		int priority = -1;

		if ((args.length < 1) || (args.length > 3)) {
			System.out
					.println("Usage: java Producer <destination-name> [<number-of-messages>] [<priority-of-messages>]");
			System.exit(1);
		}
		destinationName = args[0];
		System.out.println("Destination name is " + destinationName);
		if (args.length >= 2) {
			numMsgs = Integer.valueOf(args[1]).intValue();
		}
		if (args.length == 3) {
			priority = Integer.valueOf(args[2]).intValue();
		}
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
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(destination);
			final TextMessage message = session.createTextMessage();

			for (int i = 0; i < numMsgs; i++) {
				message.setText("This is message " + i);
				int p = priority;
				if (priority == -1) {
					p = (int) (Math.random() * 10);
				}
				System.out.println("Sending message: " + message.getText()
						+ " with priority: " + p);
				producer.send(message, DeliveryMode.PERSISTENT, p,
						Message.DEFAULT_TIME_TO_LIVE);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			JMSUtils.closeJndiContext(jndiContext);
			JMSUtils.closeMessageProducer(producer);
			JMSUtils.closeConnection(connection);
		}
	}
}
