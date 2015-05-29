package org.tangerine.net.conn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;

@ComponentDef("connectionManager")
public class ConnectionManager extends Component {

	private static final Log log = LogFactory.getLog(ConnectionManager.class);
	
	private static Map<Integer, Connection> connections;
	
	private AtomicInteger idGenerator;
	
	@Override
	protected void initialize() throws Exception {
		idGenerator = new AtomicInteger(0);
		connections = new ConcurrentHashMap<Integer, Connection>();
	}

	public Connection get(long id) {
		return connections.get(id);
	}
	
	public synchronized Connection addNew(Connection conn) {
		conn.setConnId(idGenerator.incrementAndGet());
		connections.put(conn.getConnId(), conn);
		
		log.info("addNew Connection[id=" + conn.getConnId() + "]");
		
		return conn;
	}
	
	public Connection remove(Integer id) {
		log.info("remove Connection[id=" + id + "]");
		idGenerator.decrementAndGet();
		return connections.remove(id);
	}
	
	public int size() {
		return connections.size();
	}
	
	@Override
	public void start() throws Exception {
		
	}

	@Override
	public void stop() throws Exception {
		
	}
}
