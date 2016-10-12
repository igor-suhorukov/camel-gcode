package com.github.igorsuhorukov.gcode;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LinuxCnc producer.
 */
public class LinuxCncProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(LinuxCncProducer.class);
    private LinuxCncEndpoint endpoint;

    public LinuxCncProducer(LinuxCncEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
