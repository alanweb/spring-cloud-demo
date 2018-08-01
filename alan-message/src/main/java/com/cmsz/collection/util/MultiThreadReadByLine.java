package com.cmsz.collection.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MultiThreadReadByLine {
    public static void main1(String[] args) throws IOException {
//        FileReader fileReader = new FileReader("D:\\123.txt", 10, 1);
//        fileReader.registerHandler(new FileLineDataHandler("utf-8"));
//        fileReader.startRead();
        readLine();
    }

    public static void main(String[] args) throws IOException {
        String word2048 = "|0071|20180522|10201805221414114936882568076210|0130222018052214141149377003210|upss.cmcc.com;3673048969;9|20180522141411493|0|0001|20180522|10201805221414114936882568076210|20180522112249180|0071|10|1|100|210|15026662335|0303101020180523173002210|0130222018052317300221002210||20180522|20180522112020|||200001|123456789012345678901210|85|0|123456789012|0|210|01|20180522113021|20180522113021|00||0|10|0|123456|0|2001|2001|zhengchang|0\n";
        RandomAccessFile acf = new RandomAccessFile("D:\\test013.txt", "rw");
        FileChannel fc = acf.getChannel();
        byte[] bs = word2048.getBytes();
        int len = bs.length * 1000;
        long offset = 0;
        int i = 2000;
        int index = 1;
        while (i > 0) {
            MappedByteBuffer mbuf = fc.map(FileChannel.MapMode.READ_WRITE, offset, len);
            for (int j = 0; j < 1000; j++) {
                mbuf.put(bs);
            }
            offset = offset + len;
            i = i - 1000;
        }
        fc.close();
    }

    private static void readLine() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("E:\\soft\\CentOS-7.0-1406-x86_64-DVD.iso", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int bytesRead = channel.read(buffer);
        ByteBuffer stringBuffer = ByteBuffer.allocate(20);
        while (bytesRead != -1) {
            System.out.println("读取字节数：" + bytesRead);
            //之前是写buffer，现在要读buffer
            buffer.flip();// 切换模式，写->读
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10 || b == 13) { // 换行或回车
                    stringBuffer.flip();
                    // 这里就是一个行
                    final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
                    System.out.println(line + "----------");// 解码已经读到的一行所对应的字节
                    stringBuffer.clear();
                } else {
                    if (stringBuffer.hasRemaining()) {
                        stringBuffer.put(b);
                    } else { // 空间不够扩容
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();// 清空,position位置为0，limit=capacity
            //  继续往buffer中写
            bytesRead = channel.read(buffer);
        }
        randomAccessFile.close();
    }

    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }
}