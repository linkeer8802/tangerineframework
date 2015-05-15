package org.tangerine.container.impl;

import java.util.HashMap;
import java.util.Map;

import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;

@ComponentDef("routeDictionary")
public class RouteDictionary extends Component {

	private Map<Short,String> routeIdToPathMap;
	private Map<String, Short> routePathToIdMap;
	
	public String getRoutePath(Short routeId) {
		return routeIdToPathMap.get(routeId);
	}
	
	public Short getRouteId(String routePath) {
		return routePathToIdMap.get(routePath);
	}

	@Override
	public void initialize() throws Exception {
		routeIdToPathMap = new HashMap<Short,String>();
		routePathToIdMap = new HashMap<String, Short>();
	}
	
	public void start() {}

	public void stop() {}

}
