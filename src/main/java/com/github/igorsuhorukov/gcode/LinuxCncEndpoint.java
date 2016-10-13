package com.github.igorsuhorukov.gcode;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.ObjectHelper;

import java.io.IOException;

/**
 * Represents a LinuxCnc endpoint.
 */
@UriEndpoint(scheme = "gcode", title = "LinuxCnc", syntax="gcode:command", consumerClass = LinuxCncConsumer.class, label = "LinuxCnc")
public class LinuxCncEndpoint extends DefaultEndpoint {
    @UriPath
    private String command;
    @UriParam @Metadata(required = "true")
    private String host;
    @UriParam(defaultValue = "5007")
    private int port = 5007;
    @UriParam(defaultValue = "EMC")
    private String password = "EMC";
    @UriParam(defaultValue = "ApacheCamel")
    private String clientName = "ApacheCamel";
    @UriParam(defaultValue = "1.0")
    private String clientVersion = "1.0";
    @UriParam(defaultValue = "null")
    private Integer autoHomeAxisCount;

    private GCodeClient gCodeClient;

    public LinuxCncEndpoint() {
    }

    public LinuxCncEndpoint(String uri, LinuxCncComponent component) {
        super(uri, component);
    }

    public LinuxCncEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new LinuxCncProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        ObjectHelper.notEmpty(command, "command");
        return new LinuxCncConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        ObjectHelper.notEmpty(command, "command");
        this.command = command;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        ObjectHelper.notEmpty(host, "host");
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Integer getAutoHomeAxisCount() {
        return autoHomeAxisCount;
    }

    public void setAutoHomeAxisCount(Integer autoHomeAxisCount) {
        this.autoHomeAxisCount = autoHomeAxisCount;
    }

    public GCodeClient getgCodeClient() {
        return gCodeClient;
    }

    public void setgCodeClient(GCodeClient gCodeClient) {
        this.gCodeClient = gCodeClient;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        gCodeClient = new GCodeClient(host, port);
        gCodeClient.login(password, clientName, clientVersion);
        if(autoHomeAxisCount!=null){
            performAutoHoming();
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        gCodeClient.close();
    }

    private void performAutoHoming() throws IOException {
        gCodeClient.sendCommand("set mode manual");
        gCodeClient.sendCommand("set estop off");
        gCodeClient.sendCommand("set machine on");
        for(int axis=0; axis<autoHomeAxisCount; axis++)
            gCodeClient.sendCommand("set home " + axis);
        gCodeClient.sendCommand("set mode mdi");
    }

    public GCodeClient getGCodeClient() {
        return gCodeClient;
    }
}
