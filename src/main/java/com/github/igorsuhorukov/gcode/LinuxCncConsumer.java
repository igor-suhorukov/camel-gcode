package com.github.igorsuhorukov.gcode;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LinuxCnc consumer.
 */
public class LinuxCncConsumer extends ScheduledPollConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(LinuxCncConsumer.class);
    private final LinuxCncEndpoint endpoint;

    public LinuxCncConsumer(LinuxCncEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        ObjectHelper.notEmpty(endpoint.getCommand(), "command");
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();

        try {
            String result = endpoint.sendCncCommand(endpoint.getCommand());
            exchange.getIn().setBody(result);
        } catch (Exception e) {
            exchange.setException(e);
        }

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
