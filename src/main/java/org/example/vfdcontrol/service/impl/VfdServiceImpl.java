package org.example.vfdcontrol.service.impl;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.example.vfdcontrol.service.VfdService;
import org.springframework.stereotype.Service;

@Service
public class VfdServiceImpl implements VfdService {
    private SerialConnection connection;

    public VfdServiceImpl() {
        SerialParameters params = new SerialParameters();
        params.setPortName("COM8");
        params.setBaudRate(9600);
        params.setDatabits(8);
        params.setParity(2);
        params.setStopbits(1);
        params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        params.setEcho(false);

        connection = new SerialConnection(params);
        try {
            connection.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFrequency(int frequency) throws Exception {
        int registerAddress = 14; // Frequency control register
        ModbusRequest request = new WriteSingleRegisterRequest(registerAddress, new SimpleRegister(frequency));
        ModbusSerialTransaction trans = new ModbusSerialTransaction(connection);
        trans.setRequest(request);
        trans.execute();
    }

    @Override
    public void sendCommand(int command) throws Exception {
        int registerAddress = 8; // Command control register
        ModbusRequest request = new WriteSingleRegisterRequest(registerAddress, new SimpleRegister(command));
        ModbusSerialTransaction trans = new ModbusSerialTransaction(connection);
        trans.setRequest(request);
        trans.execute();
    }

    @Override
    public int readFrequency() throws Exception {
        int registerAddress = 200; // Frequency control register
        ModbusRequest request = new ReadMultipleRegistersRequest(registerAddress, 1);
        ModbusSerialTransaction trans = new ModbusSerialTransaction(connection);
        trans.setRequest(request);
        trans.execute();
        ModbusResponse response = trans.getResponse();
        if (response instanceof ReadMultipleRegistersResponse) {
            ReadMultipleRegistersResponse readResponse = (ReadMultipleRegistersResponse) response;
            return readResponse.getRegisterValue(0);  // Get the value of the first register
        }
        return -1;  // Adjust this depending on how you want to interpret the response
    }
}
