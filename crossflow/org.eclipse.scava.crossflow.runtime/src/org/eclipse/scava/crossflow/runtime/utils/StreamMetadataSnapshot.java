package org.eclipse.scava.crossflow.runtime.utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class StreamMetadataSnapshot implements Serializable {

	private static final long serialVersionUID = 4865546388740896468L;
	
	private Set<StreamMetadata> streams = new HashSet<>();

	//

	public boolean addStream(String name, long size, long inFlight, boolean isTopic, long l) {
		return streams.add(new StreamMetadata(name, size, inFlight, isTopic, l));
	}

	public Set<StreamMetadata> getStreams() {
		return streams;
	}

	public StreamMetadata getStream(String name) {
		try {
			return streams.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	public void pruneNames(int length) {
		for (StreamMetadata s : streams) {
			if (s.getName().length() >= length)
				s.setName(s.getName().substring(0, length));
			else if (s.getName().length() < length)
				s.setName(String.format("%-" + length + "s", s.getName()));
		}
	}

	@Override
	public String toString() {
		String ret = "Stream Metadata at epoch: " + System.currentTimeMillis() + "\r\n";
		for (StreamMetadata s : streams)
			ret = ret + s.getName() + "\tsize: " + s.getSize() + "\tinFlight: " + s.getInFlight() + "\tisTopic: " + s.isTopic()
					+ "\tnumberOfSubscribers: " + s.getNumberOfSubscribers() + "\r\n";
		return ret;
	}
}
