package com.mycompany.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReDispatchConsumer {

	public static void main(String[] args) {
		Context jndiContext = null;
		ConnectionFactory connectionFactory = null;
		Connection requestConnection = null;
		Connection responseConnection = null;
		Session requestSession = null;
		Session responseSession = null;
		Destination requestDestination = null;
		Destination responseDestination = null;
		MessageConsumer consumer = null;
		MessageProducer producer = null;
		String requestDestinationName = null;
		String responseDestinationName = null;

		if ((args.length < 2) || (args.length > 2)) {
			System.out
					.println("Usage: java ReDispatchConsumer <destination1-name> <destination2-name>");
			System.exit(1);
		}
		requestDestinationName = args[0];
		responseDestinationName = args[1];
		System.out.println("Request destination name is "
				+ requestDestinationName);
		System.out.println("Response destination name is "
				+ responseDestinationName);

		try {
			jndiContext = new InitialContext();
			connectionFactory = (ConnectionFactory) jndiContext
					.lookup("ConnectionFactory");
			requestDestination = (Destination) jndiContext
					.lookup(requestDestinationName);
			responseDestination = (Destination) jndiContext
					.lookup(responseDestinationName);
		} catch (NamingException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			requestConnection = connectionFactory.createConnection();
			responseConnection = connectionFactory.createConnection();
			requestConnection.start();
			responseConnection.start();
			requestSession = requestConnection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			responseSession = requestConnection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			consumer = requestSession.createConsumer(requestDestination);
			producer = responseSession.createProducer(responseDestination);

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
					Message responseMessage = responseSession
							.createTextMessage(txtMessage.getJMSPriority()
									+ "::" + txtMessage.getText());
					producer.send(responseMessage);

				} else {
					System.out.println("Invalid message received.");
				}
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JMSUtils.closeJndiContext(jndiContext);
			JMSUtils.closeConnection(responseConnection);
			JMSUtils.closeConnection(requestConnection);
			JMSUtils.closeMessageConsumer(consumer);
			JMSUtils.closeMessageProducer(producer);
		}

	}
}
