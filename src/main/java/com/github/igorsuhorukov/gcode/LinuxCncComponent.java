package com.github.igorsuhorukov.gcode;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

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
        Endpoint endpoint = new LinuxCncEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
}
