package org.example.plccontrol.service.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.example.plccontrol.service.VfdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class VfdServiceImpl implements VfdService {
    private static final Logger logger = LoggerFactory.getLogger(VfdServiceImpl.class);
    private static final String IP_ADDRESS = "192.168.1.111";
    private static final int PORT = 502;
    private static final int SLAVE_ID = 1;
    private static final int BUTTON_ON_COIL = 3072;
    private static final int BUTTON_OFF_COIL = 3073;
    private static final int LAMP_STATUS_REGISTER = 1536;
    private static final int PULSE_DURATION_MS = 100;

    private final TCPMasterConnection connection;
    private final MessageChannel mqttOutboundChannel;

    public VfdServiceImpl(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
        try {
            InetAddress addr = InetAddress.getByName(IP_ADDRESS);
            this.connection = new TCPMasterConnection(addr);
            connection.setPort(PORT);
            connection.connect();
            logger.info("Successfully connected to Modbus device at {}:{}", IP_ADDRESS, PORT);
        } catch (Exception e) {
            logger.error("Failed to initialize Modbus connection", e);
            throw new RuntimeException("Failed to initialize Modbus connection", e);
        }
    }

    @Override
    public void turnOn() throws IOException {
        sendMomentaryPulse(BUTTON_ON_COIL);
        publishStatus();
    }

    @Override
    public void turnOff() throws IOException {
        sendMomentaryPulse(BUTTON_OFF_COIL);
        publishStatus();
    }

    @Override
    public boolean getLampStatus() throws IOException {
        return readCoil(LAMP_STATUS_REGISTER);
    }

    @Override
    public void publishStatus() {
        try {
            boolean status = getLampStatus();
            String payload = status ? "ON" : "OFF";
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(MqttHeaders.TOPIC, "vfd/status")
                    .build();
            mqttOutboundChannel.send(message);
            logger.info("Published VFD status: {}", payload);
        } catch (IOException e) {
            logger.error("Error publishing VFD status", e);
        }
    }

    @Override
    public void handleMqttCommand(String command) {
        try {
            if ("ON".equalsIgnoreCase(command)) {
                turnOn();
            } else if ("OFF".equalsIgnoreCase(command)) {
                turnOff();
            } else {
                logger.warn("Received unknown command: {}", command);
            }
        } catch (IOException e) {
            logger.error("Error handling MQTT command", e);
        }
    }

    private void sendMomentaryPulse(int coil) throws IOException {
        try {
            writeCoil(coil, true);
            Thread.sleep(PULSE_DURATION_MS);
            writeCoil(coil, false);
            logger.info("Successfully sent momentary pulse to coil {}", coil);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while sending momentary pulse to coil {}", coil, e);
            throw new IOException("Failed to send momentary pulse", e);
        }
    }

    private void writeCoil(int coil, boolean state) throws IOException {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            WriteCoilRequest req = new WriteCoilRequest(coil, state);
            req.setUnitID(SLAVE_ID);
            ModbusResponse response = executeTransaction(req);
            if (!(response instanceof WriteCoilResponse)) {
                throw new IOException("Unexpected response type");
            }
            logger.info("Successfully wrote to coil {}: {}", coil, state);
        } catch (Exception e) {
            logger.error("Error writing to coil {}", coil, e);
            throw new IOException("Failed to write to coil", e);
        }
    }

    private boolean readCoil(int coil) throws IOException {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            ReadCoilsRequest req = new ReadCoilsRequest(coil, 1);
            req.setUnitID(SLAVE_ID);
            ModbusResponse response = executeTransaction(req);
            if (response instanceof ReadCoilsResponse) {
                ReadCoilsResponse readResponse = (ReadCoilsResponse) response;
                boolean state = readResponse.getCoilStatus(0);
                logger.info("Successfully read from coil {}: {}", coil, state);
                return state;
            }
            throw new IOException("Unexpected response type");
        } catch (Exception e) {
            logger.error("Error reading from coil {}", coil, e);
            throw new IOException("Failed to read from coil", e);
        }
    }

    private ModbusResponse executeTransaction(ModbusRequest request) throws ModbusException {
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
        return transaction.getResponse();
    }
}