package fulleventchannel;

import java.util.HashMap;


public class EventBrokerManager {
private static final EventBrokerManager INSTANCE = new EventBrokerManager();
	
	private HashMap<String, EventBroker> brokers;
	
	private EventBrokerManager() {
		brokers = new HashMap<String, EventBroker>();
	}
	
	public static EventBrokerManager getInstance() {
        return INSTANCE;
    }
	
	
	public void registerBroker(EventBroker broker) {
		
		String name = broker.getName();
		
		if(brokers.containsKey(name)) {
			throw new IllegalStateException("2 brokers are already registered with the same name "+name);
		}
		
		this.brokers.put(name, broker);
	}



	public EventBroker getBroker(String name) {
		EventBroker broker =  brokers.get(name);
		return broker;
	}

}