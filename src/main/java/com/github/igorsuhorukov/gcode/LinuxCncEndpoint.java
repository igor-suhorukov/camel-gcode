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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Represents a LinuxCnc endpoint.
 */
@UriEndpoint(scheme = "gcode", title = "LinuxCnc", syntax="gcode:command", consumerClass = LinuxCncConsumer.class, label = "LinuxCnc")
public class LinuxCncEndpoint extends DefaultEndpoint {
    @UriPath(description = "Specify linuxcncrsh command for endpoint in consumer mode only. See http://linuxcnc.org/docs/html/man/man1/linuxcncrsh.1.html")
    private String command;
    @UriParam(description = "Specify the host for linuxcncrsh to listen on. See http://linuxcnc.org/docs/html/man/man1/linuxcncrsh.1.html") @Metadata(required = "true")
    private String host;
    @UriParam(defaultValue = "5007", description = "Specify the port for linuxcncrsh to listen on.")
    private int port = 5007;
    @UriParam(defaultValue = "EMC", description = "Specify the connection password to use during handshaking with a new client.")
    private String password = "EMC";
    @UriParam(defaultValue = "ApacheCamel", description = "Client name is used during handshaking with a new client.")
    private String clientName = "ApacheCamel";
    @UriParam(defaultValue = "1.0", description = "Client version is used during handshaking with a new client.")
    private String clientVersion = "1.0";
    @UriParam(defaultValue = "null", description = "Specify axis count of your CNC. This count is used during autohoming after connection.")
    private Integer autoHomeAxisCount;

    private GCodeClient gCodeClient;

    private static final Logger LOG = LoggerFactory.getLogger(LinuxCncEndpoint.class);

    public LinuxCncEndpoint() {
    }

    public LinuxCncEndpoint(String uri, LinuxCncComponent component) {
        super(uri, component);
    }

    public LinuxCncEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        if(ObjectHelper.isNotEmpty(command)){
            throw new IllegalArgumentException("'command' provided for endpoint in producer mode.");
        }
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
        createGcodeClient();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        gCodeClient.close();
    }

    private synchronized void createGcodeClient() throws IOException {
        gCodeClient = new GCodeClient(host, port);
        gCodeClient.login(password, clientName, clientVersion);
        if (autoHomeAxisCount != null) {
            performAutoHoming();
        }
    }

    private void performAutoHoming() throws IOException {
        gCodeClient.sendCommand("set estop off");
        gCodeClient.sendCommand("set machine on");
        gCodeClient.sendCommand("set mode manual");
        for(int axis=0; axis<autoHomeAxisCount; axis++)
            gCodeClient.sendCommand("set home " + axis);
        String mode = gCodeClient.sendCommand("get mode");
        gCodeClient.sendCommand("set mode mdi");
    }

    public String sendCncCommand(String command) throws IOException {
        try {
            return gCodeClient.sendCommand(command);
        } catch (IOException e){
            LOG.warn("Command '"+command+"' is failed",e);
            reconnect();
            return gCodeClient.sendCommand(command);
        }
    }

    private synchronized void reconnect() throws IOException {
        LOG.info("Try to reconnect");
        IOUtils.closeQuietly(gCodeClient);
        createGcodeClient();
    }
}
