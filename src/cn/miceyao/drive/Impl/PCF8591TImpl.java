package cn.miceyao.drive.Impl;

import cn.miceyao.drive.PCF8591T;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CProviderImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PCF8591TImpl implements PCF8591T {
    /**
     * PCF8591默认的i2c地址
     * Default i2c PCF8591 address.
     */
    public static final int DEFAULT_PCF8591_ADDRESS = 0x48;

    /**
     * 模拟输出命令字节
     * Analog output active if bit at position 2 in control byte is 1.
     */
    protected static final byte ANALOGUE_OUTPUT_ENABLE_FLAG = 0x40; // 0100 0000

    /**
     * 如果设置了自动增量标志，则在每次A / D转换后，通道编号会自动递增。
     * If the auto-increment flag is set, the channel number is incremented automatically after each A/D conversion.
     */
    protected static final byte AUTO_INCREMENT_FLAG = 0x04; // 0000 0100


    private final I2CDevice device; // PCF8591T设备
    private boolean isOutput = false; //模拟输出状态标记符
    // 模拟输入模式
    private AnalogueInputProgrammingMode analogueInputProgrammingMode = AnalogueInputProgrammingMode.FOUR_SINGLE_ENDED_INPUTS;


    /**
     * 创建一个PCF8591T实例
     *
     * @param i2cBus     i2c总线编号
     * @param i2cAddress i2c设备地址
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public PCF8591TImpl(int i2cBus, byte i2cAddress) throws IOException, I2CFactory.UnsupportedBusNumberException {
        this(i2cBus, 60, TimeUnit.SECONDS, i2cAddress);
    }

    /**
     * 创建一个PCF8591T实例
     *
     * @param i2cBus                i2c总线编号
     * @param lockAquireTimeout     获取锁等待时间
     * @param lockAquireTimeoutUnit 获取锁等待时间单位
     * @param i2cAddress            i2c设备地址
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public PCF8591TImpl(int i2cBus, long lockAquireTimeout, TimeUnit lockAquireTimeoutUnit, byte i2cAddress) throws IOException, I2CFactory.UnsupportedBusNumberException {
        device = new I2CProviderImpl().getBus(i2cBus, lockAquireTimeout, lockAquireTimeoutUnit).getDevice(i2cAddress);
    }


    public void setAnalogueInputProgrammingMode(AnalogueInputProgrammingMode mod) {
        analogueInputProgrammingMode = mod;
    }

    public AnalogueInputProgrammingMode getAnalogueInputProgrammingMode() {
        return analogueInputProgrammingMode;
    }

    public void setOutputState(boolean outputState) throws IOException {
        if (isOutput != outputState) {
            device.write(outputState ? ANALOGUE_OUTPUT_ENABLE_FLAG : 0x00);
            isOutput = outputState;
        }
    }

    public boolean getOutputState() {
        return isOutput;
    }

    public int read(byte channel) {
        byte[] buff = new byte[2];
        try {
            read(channel, buff);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return Byte.toUnsignedInt(buff[1]);
    }


    public int read(byte channel, byte[] buff) throws IOException {
        return read(channel, buff, 0, buff.length, false);
    }


    public int read(byte channel, byte[] buff, int offset, int size, boolean isAutoIncrement) throws IOException {
        if (channel < 0 || channel > analogueInputProgrammingMode.toMaxChannel())
            throw new IllegalArgumentException("channel must be in the 0.. " + analogueInputProgrammingMode.toMaxChannel() + " range");
        byte controlByte = (byte) ((isOutput ? ANALOGUE_OUTPUT_ENABLE_FLAG : 0)  //输出标志符
                + (isAutoIncrement ? AUTO_INCREMENT_FLAG : 0) //自动递增标记符
                + analogueInputProgrammingMode.toByte() //模式标记符
                + channel); // 通道
        device.write(controlByte);
        return device.read(buff, offset, size);
    }

    public byte[] readAll() throws IOException {
        byte[] buff = new byte[analogueInputProgrammingMode.toMaxChannel() + 2];
        read(CHANNEL_0, buff, 0, buff.length, true);
        return Arrays.copyOfRange(buff,1,buff.length);
    }

    public boolean output(int value) {
        return output(true, value);
    }

    public boolean output(boolean outputState, int value) {
        if (value < 0 || value > 255)
            throw new IllegalArgumentException("Value must be in the 0..255 range");
        try {
            byte controlByte = outputState ? ANALOGUE_OUTPUT_ENABLE_FLAG : 0; //输出标志符
            byte[] buffer = {controlByte, (byte) value};
            device.write(buffer);
            isOutput = outputState;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
