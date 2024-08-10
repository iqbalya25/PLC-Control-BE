package org.example.vfdcontrol.service;

import java.io.IOException;

public interface VfdService {
    void setFrequency(int freq) throws IOException, IllegalArgumentException;
    void sendCommand(int command) throws IOException, IllegalArgumentException;
    int readFrequency() throws IOException;
    boolean isConnected();
    void connect() throws IOException;
    void disconnect() throws IOException;
}