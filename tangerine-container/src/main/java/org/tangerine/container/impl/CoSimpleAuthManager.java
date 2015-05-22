package org.tangerine.container.impl;

import java.util.HashMap;
import java.util.Map;

import org.tangerine.container.ComponentDef;
import org.tangerine.protocol.model.UserInfo;

@ComponentDef("simpleAuthManager")
public class CoSimpleAuthManager extends CoAuthManager {

	private static Map<String, UserInfo> userInfoMap;
	

	@Override
	protected void initialize() throws Exception {
		userInfoMap = new HashMap<String, UserInfo>();
	}

	@Override
	public void start() throws Exception {
		
	}

	@Override
	public void stop() throws Exception {
		
	}
	
	@Override
	public void addUser(UserInfo userInfo) {
		userInfoMap.put(userInfo.getSessionId(), userInfo);
	}

	@Override
	public void removeUser(String sId) {
		userInfoMap.remove(sId);
	}

	@Override
	public UserInfo getBySId(String sId) {
		return userInfoMap.get(sId);
	}
}
