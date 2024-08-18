package org.example.plccontrol.mqtt;

import org.example.plccontrol.service.VfdService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageHandler {

    private final VfdService vfdService;

    public MqttMessageHandler(VfdService vfdService) {
        this.vfdService = vfdService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<String> message) {
        String payload = message.getPayload();
        vfdService.handleMqttCommand(payload);
    }
}