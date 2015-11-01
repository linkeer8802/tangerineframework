package org.tangerine.im;

import java.util.HashMap;
import java.util.Map;

public class RouteDictionary {

	private Map<Short,String> routeIdToPathDictionarys = new HashMap<Short,String>();
	
	private Map<String, Short> routePathToIdDictionarys = new HashMap<String, Short>();
	
	public String getRoutePath(Short routeId) {
		return routeIdToPathDictionarys.get(routeId);
	}
	
	public Short getRouteId(String routePath) {
		return routePathToIdDictionarys.get(routePath);
	}
	
	
	public void init() {
		
	}
}
