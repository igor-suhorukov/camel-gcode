package com.github.igorsuhorukov.gcode;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import java.net.URLDecoder;
import java.util.Map;

/**
 * Represents the component that manages {@link LinuxCncEndpoint}.
 */
public class LinuxCncComponent extends UriEndpointComponent {
    
    public LinuxCncComponent() {
        super(LinuxCncEndpoint.class);
    }

    public LinuxCncComponent(CamelContext context) {
        super(context, LinuxCncEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        LinuxCncEndpoint endpoint = new LinuxCncEndpoint(uri, this);
        setProperties(endpoint, parameters);
        if(remaining!=null && !remaining.isEmpty()) {
            endpoint.setCommand(URLDecoder.decode(remaining, "UTF-8"));
        }
        return endpoint;
    }
}
