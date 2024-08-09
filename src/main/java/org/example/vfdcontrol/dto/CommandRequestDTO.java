package org.example.vfdcontrol.dto;

public class CommandRequestDTO {
    private int command;

    public CommandRequestDTO() {}

    public CommandRequestDTO(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }
}
