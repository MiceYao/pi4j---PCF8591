# pi4j-PCF8591

一个基于pi4j开发的PCF8591驱动程序

简单例子:
```java
public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
     // 创建一个pcf8591T实例
     PCF8591T pcf8591T = new PCF8591TImpl(I2CBus.BUS_1, PCF8591T.DEFAULT_ADDRESS);
     //例子1: 读取模拟输入通道AIN0的值
     int read = pcf8591T.read(PCF8591T.CHANNEL_0);
     System.out.println("模拟输入通道AINO的值为: " + read);
     //例子2: 读取所有可用通道的值
     byte[] bytes = pcf8591T.readAll();
     System.out.println("所有可用通道的值依次为: " + Arrays.toString(bytes));
     //例子3: 设置模拟输出的电平值
     boolean output = pcf8591T.output(255);
     System.out.println("设置模拟输出的电平值是否成功: " + output);
     // ... 更多详情请浏览 interface
 }
```

  
