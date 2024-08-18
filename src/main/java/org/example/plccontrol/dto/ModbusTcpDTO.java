package org.example.plccontrol.dto;

public class ModbusTcpDTO {
    public static class LampStatusResponse {
        private boolean isOn;

        public LampStatusResponse(boolean isOn) {
            this.isOn = isOn;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean on) {
            isOn = on;
        }
    }
}