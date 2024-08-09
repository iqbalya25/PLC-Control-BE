package org.example.vfdcontrol.service;

public interface VfdService {
    void setFrequency(int freq) throws Exception;
    void sendCommand(int command) throws Exception;
    int readFrequency() throws Exception;
}
