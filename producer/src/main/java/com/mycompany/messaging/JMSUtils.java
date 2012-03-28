package com.mycompany.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.naming.Context;
import javax.naming.NamingException;

public class JMSUtils {

	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeMessageConsumer(MessageConsumer consumer) {
		if (consumer != null) {
			try {
				consumer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeMessageProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeJndiContext(Context jndiContext) {
		if (jndiContext != null) {
			try {
				jndiContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

}
