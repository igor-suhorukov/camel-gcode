package com.github.igorsuhorukov.gcode;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.*;

/**
 */
public class GCodeClient implements Closeable{

    private TelnetClient client;
    private OutputStream outputStream;
    private BufferedReader response;

    public GCodeClient(String hostname, int port) throws IOException {
        client = new TelnetClient();
        client.connect(hostname, port);
        outputStream = client.getOutputStream();
        response = getResponseReader(client);
    }

    public String login(String password, String clientName, String clientVersion) throws IOException {
        String command = String.format("hello %s %s %s", password, clientName, clientVersion);
        String loginResponse = sendCommand(command);
        if(loginResponse==null || loginResponse.startsWith("HELLO NAK")){
            throw new IllegalArgumentException("Invalid password");
        }
        sendCommand("set enable EMCTOO");
        sendCommand("set_wait done");
        sendCommand("set set_wait done");

        return loginResponse;
    }

    public synchronized String sendCommand(String command) throws IOException {
        writeCommand(command);
        String responseStr = response.readLine();
        System.out.println("CMD " + command);
        System.out.println(responseStr);
        validateResponseErr(responseStr);
        if(command.toLowerCase().startsWith("get ")){
            String next = response.readLine();
            System.out.println(next);
            return next;
        }
        return responseStr;
    }

    private void validateResponseErr(String responseStr) {
        if(responseStr.contains(" NAK")){
            throw new RuntimeException(responseStr);
        }
    }

    private static BufferedReader getResponseReader(TelnetClient cncClient) {
        return new BufferedReader(new InputStreamReader(cncClient.getInputStream()));
    }

    private void writeCommand(String command) throws IOException {
        outputStream.write((String.format("%s\r\n", command)).getBytes());
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if(outputStream!=null) IOUtils.closeQuietly(outputStream);
        if(response!=null) IOUtils.closeQuietly(response);
        if(client!=null) client.disconnect();
    }
}
