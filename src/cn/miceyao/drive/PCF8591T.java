package cn.miceyao.drive;

import java.io.IOException;

public interface PCF8591T {

    /**
     * PCF8591默认的i2c地址
     * Default i2c PCF8591 address.
     */
    byte DEFAULT_ADDRESS = 0x48;
//
//    /**
//     * 模拟输出命令字节
//     * Analog output active if bit at position 2 in control byte is 1.
//     */
//    public static final byte ANALOGUE_OUTPUT_ENABLE_FLAG = 0x40; // 0100 0000
//
//    /**
//     * 如果设置了自动增量标志，则在每次A / D转换后，通道编号会自动递增。
//     * If the auto-increment flag is set, the channel number is incremented automatically after each A/D conversion.
//     */
//    public static final byte AUTO_INCREMENT_FLAG = 0x04; // 0000 0100

    /**
     * 输入通道
     */
    byte CHANNEL_0 = 0x00;
    byte CHANNEL_1 = 0x01;
    byte CHANNEL_2 = 0x02;
    byte CHANNEL_3 = 0x03;

    /**
     * 存在4种不同的模式来处理四个模拟输入。
     * There exists 4 different modes to deal with the four analog inputs.
     */
    public enum AnalogueInputProgrammingMode {
        /**
         * 四单端输入
         * AIN0,AIN1,AIN2,AIN3 为单端输入端
         * AIN 和 CHANNEL关系为
         * AIN0 --> CHANNEL_0
         * AIN1 --> CHANNEL_1
         * AIN2 --> CHANNEL_2
         * AIN3 --> CHANNEL_3
         * Analogue input programming mode:
         * four single ended inputs.
         */
        FOUR_SINGLE_ENDED_INPUTS(0x00, 3), // 0000 0000

        /**
         * 三分差输入
         * AIN0,AIN1,AIN2 为分差(+)输入端
         * AIN3 为分差(-)公共端
         * AIN 和 CHANNEL关系为
         * AIN0         --> CHANNEL_0
         * AIN1         --> CHANNEL_1
         * AIN2         --> CHANNEL_2
         * Analogue input programming mode:
         * three differential inputs.
         */
        THREE_DIFFERENTIAL_INPUTS(0x10, 2), // 0000 0001

        /**
         * 单端和差分混合输入
         * AIN0, AIN1 为单端
         * AIN2(+), AIN3(-) 为分差
         * AIN 和 CHANNEL关系为
         * AIN0         --> CHANNEL_0
         * AIN1         --> CHANNEL_1
         * AIN2,AIN3    --> CHANNEL_2
         * Analogue input programming mode:
         * single ended and differential mixed.
         */
        SINGLE_ENDED_AND_DIFFERENTIAL_MIXED(0x20, 2), // 0000 0010

        /**
         * 双分差输入
         * AIN0(+), AIN1(-) 为分差
         * AIN2(+), AIN3(-) 为分差
         * AIN 和 CHANNEL关系为
         * AIN0,AIN1    --> CHANNEL_0
         * AIN2,AIN3    --> CHANNEL_1
         * Analogue input programming mode:
         * two differential inputs.
         */
        TWO_DIFFERENTIAL_INPUTS(0x30, 1); // 0000 0011

        byte code;
        byte maxChannel;

        AnalogueInputProgrammingMode(int code, int maxChannel) {
            this.code = (byte) code;
            this.maxChannel = (byte) maxChannel;
        }

        public byte toByte() {
            return code;
        }

        public byte toMaxChannel() {
            return maxChannel;
        }

        ;
    }

    /**
     * 设置模拟输入编程模式
     *
     * @param mod
     */
    void setAnalogueInputProgrammingMode(AnalogueInputProgrammingMode mod);

    /**
     * 获取模拟输入编程模式
     */
    AnalogueInputProgrammingMode getAnalogueInputProgrammingMode();

    /**
     * 设置输出通道电平启用/关闭状态
     *
     * @param outputState
     */
    void setOutputState(boolean outputState) throws IOException;

    /**
     * 获取输出通道电平启用/关闭状态
     *
     * @return
     */
    boolean getOutputState();


    /**
     * This method reads one byte from the i2c device. Result is between 0 and 255 if read operation was successful, else a negative number for an error.
     * 此方法从i2c设备PCF8591T读取一个字节。 如果读取操作成功，则结果在0到255之间，否则为错误的负数。
     *
     * @param channel 要读取的数据通道
     * @return 读取的字节值：如果读取成功，则为正数（或零）到255。 读数失败时为负数。
     */
    int read(byte channel);


    /**
     * 此方法直接从i2c设备PCF8591T读取字节到给定的缓冲区。
     * This method reads bytes directly from the i2c device to given buffer at asked offset.
     *
     * @param channel 要读取的数据通道
     * @param buff    一次性从i2c设备读取的数据缓冲区
     * @return 读取的字节数 number of bytes read
     */
    int read(byte channel, byte[] buff) throws IOException;


    /**
     * 此方法直接从i2c设备PCF8591T读取字节到给定的缓冲区。
     * This method reads bytes directly from the i2c device to given buffer at asked offset.
     *
     * @param channel         要读取的数据通道
     * @param buff            一次性从i2c设备读取的数据缓冲区
     * @param offset          缓冲区中的偏移量
     * @param size            要读取的字节数
     * @param isAutoIncrement 是否启用通道自动递增
     * @return 读取的字节数 number of bytes read
     * @throws IOException
     */
    int read(byte channel, byte[] buff, int offset, int size, boolean isAutoIncrement) throws IOException;


    /**
     * PCF8591T读取所有可用通道当前的值
     *
     * @return
     * @throws IOException
     */
    byte[] readAll() throws IOException;

    /**
     * 设置输出通道电平[0,255]
     *
     * @param value 要设置的电平值 [0,255]
     * @return 输出成功返回 true
     */
    boolean output(int value);


    /**
     * 设置输出通道电平[0,255]
     *
     * @param outputState 输出通道启用状态
     * @param value       要设置的电平值 [0,255]
     * @return 输出成功返回 true
     */
    boolean output(boolean outputState, int value);

}
