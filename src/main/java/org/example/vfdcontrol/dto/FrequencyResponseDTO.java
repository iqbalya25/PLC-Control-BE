package org.example.vfdcontrol.dto;

public class FrequencyResponseDTO {
    private int frequency;

    public FrequencyResponseDTO() {}

    public FrequencyResponseDTO(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
