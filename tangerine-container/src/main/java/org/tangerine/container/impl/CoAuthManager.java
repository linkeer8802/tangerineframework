package org.tangerine.container.impl;

import org.tangerine.container.Component;
import org.tangerine.protocol.model.UserInfo;

public abstract class CoAuthManager extends Component {

	public abstract void addUser(UserInfo userInfo);
	
	public abstract void removeUser(String sId);
	
	public abstract UserInfo getBySId(String sId);  
}
