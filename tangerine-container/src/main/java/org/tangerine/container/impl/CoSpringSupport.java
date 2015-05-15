package org.tangerine.container.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tangerine.common.ConfigUtil;
import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;

@ComponentDef("spring")
public class CoSpringSupport extends Component{

    private static final Log log = LogFactory.getLog(CoSpringSupport.class);

    public static final String DEFAULT_SPRING_CONFIG = "classpath*:conf/spring/*.xml";

    private ClassPathXmlApplicationContext context;
    
    public ClassPathXmlApplicationContext getContext() {
		return context;
	}
    
    @Override
    public void initialize() throws Exception {}

	public void start() {
        String configPath = ConfigUtil.get("tangerine.spring.config", DEFAULT_SPRING_CONFIG);
        context = new ClassPathXmlApplicationContext(configPath.split("[,\\s]+"));
        context.start();
    }
	
    public void stop() {
        try {
            if (context != null) {
                context.stop();
                context.close();
                context = null;
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
