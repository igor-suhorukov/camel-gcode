package com.github.igorsuhorukov;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class LinuxCncComponentTest extends CamelTestSupport {

    @Test
    public void testLinuxCnc() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("gcode://foo")
                  .to("gcode://bar")
                  .to("mock:result");
            }
        };
    }
}
