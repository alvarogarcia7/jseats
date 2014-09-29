package org.jseats;

import java.util.Properties;

import org.jseats.model.InmutableTally;
import org.jseats.model.Result;
import org.jseats.model.SeatAllocationException;
import org.jseats.model.methods.SeatAllocationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeatAllocatorProcessor {

	Logger log = LoggerFactory.getLogger(SeatAllocatorProcessor.class);

	Properties properties;

	SeatAllocationMethod method;

	InmutableTally tally;

	public SeatAllocatorProcessor() {
		log.debug("Initializing processor");
		properties = new Properties();
	}

	public InmutableTally getTally() {
		return tally;
	}

	public void setTally(InmutableTally tally) {
		log.debug("Added tally: " + tally);
		this.tally = tally;
	}

	public void setProperty(String key, String value) {
		log.debug("Set property " + key + "=" + value);
		properties.setProperty(key, value);
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public Properties getProperties() {
		return properties;
	}

	public void setMethodByName(String method)
			throws SeatAllocationException {
		
		log.debug("Adding method by name:" + method);
		this.method = SeatAllocationMethod.getByName(method);
	}

	public void setMethodByClass(
			Class<? extends SeatAllocationMethod> clazz)
			throws InstantiationException, IllegalAccessException {
		
		log.debug("Adding method by class:" + clazz);
		method = clazz.newInstance();
	}

	public void reset() {
		log.debug("Resetting processor");
		
		properties.clear();
		method = null;
		tally = null;
	}

	public Result process() throws SeatAllocationException {

		if (tally == null)
			throw new SeatAllocationException(
					"Trying to run processor without providing a tally");

		log.debug("Processing...");
		
		// TODO fix
		Result result = method.process(tally, properties);
		
		log.debug("Processed");
		
		return result;

	}
}
