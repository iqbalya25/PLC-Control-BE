package org.example.vfdcontrol.dto;

public class FrequencyRequestDTO {
    private int frequency;

    public FrequencyRequestDTO() {}

    public FrequencyRequestDTO(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
