package ch.exense.util;

import java.util.Hashtable;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXConnectionTest implements NotificationListener  {

	public static void main(String... args){
		
		if(args.length < 2 || args.length > 4) {
			System.out.println("syntax= java -jar debugJMXConnection.jar <hostname> <port> [optional: <user> <password>]");
			System.exit(0);
		}
		try {
			new JMXConnectionTest().connect_(args[0], args[1], args.length > 2 ? args[2] : null, args.length > 3 ? args[3] : null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    protected synchronized void connect_(String host, String port, String username, String password) throws Exception {

        System.out.println("Creating JMX connection to " + host + ":" + port);

        String urlPath = "/jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL url = new JMXServiceURL("rmi", "", 0, urlPath);

        Hashtable<String, String[]> h = new Hashtable<String, String[]>();
        if (username != null) {
            String[] credentials = new String[]{username, password};
            h.put("jmx.remote.credentials", credentials);
        }

        JMXConnector connector = JMXConnectorFactory.connect(url, h);
        
        System.out.println("JMX connection successful!");
    }

	@Override
	public void handleNotification(Notification notification, Object handback) {
		System.out.println(notification);
		System.out.println(handback);
		
	}
}
