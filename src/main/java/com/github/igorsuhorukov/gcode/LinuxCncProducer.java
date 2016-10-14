package com.github.igorsuhorukov.gcode;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
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
        String command = exchange.getIn().getBody(String.class);
        ObjectHelper.notEmpty(command, "command");
        String response = endpoint.getGCodeClient().sendCommand(command);
        exchange.getOut().setBody(response, String.class);
    }
}
