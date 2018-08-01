package com.cmsz.collection.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileReader {
    /**
     * 线程数,默认为3
     */
    private int threadNum = 3;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 缓冲区大小,默认为1024
     */
    private int bufSize = 1024;
    /**
     * 数据处理接口
     */
    private DataProcessHandler dataProcessHandler;
    private ExecutorService threadPool;

    public FileReader(String filePath, int bufSize, int threadNum) {
        this.threadNum = threadNum;
        this.bufSize = bufSize;
        this.filePath = filePath;
        this.threadPool = Executors.newFixedThreadPool(threadNum);
    }

    /**
     * 启动多线程读取文件
     */
    public void startRead() {
        FileChannel infile = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "r");
            infile = raf.getChannel();
            long size = infile.size();
            long subSize = size / threadNum;
            for (int i = 0; i < threadNum; i++) {
                long startIndex = i * subSize;
                if (size % threadNum > 0 && i == threadNum - 1) {
                    subSize += size % threadNum;
                }
                RandomAccessFile accessFile = new RandomAccessFile(filePath, "r");
                FileChannel inch = accessFile.getChannel();
                threadPool.execute(new MultiThreadReader(inch, startIndex, subSize));
            }
            threadPool.shutdown();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (infile != null) {
                    infile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册数据处理接口
     *
     * @param dataHandler
     */
    public void registerHandler(DataProcessHandler dataHandler) {
        this.dataProcessHandler = dataHandler;
    }

    /**
     * 多线程按行读取文件具体实现类
     *
     * @author zyh
     */
    class MultiThreadReader implements Runnable {
        private FileChannel channel;
        private long startIndex;
        private long rSize;

        public MultiThreadReader(FileChannel channel, long startIndex, long rSize) {
            this.channel = channel;
            this.startIndex = startIndex > 0 ? startIndex - 1 : startIndex;
            this.rSize = rSize;
        }

        @Override
        public void run() {
            readByLine();
        }

        /**
         * 按行读取文件实现逻辑
         *
         * @return
         */
        public void readByLine() {
            try {
                ByteBuffer rbuf = ByteBuffer.allocate(bufSize);
                //设置读取文件的起始位置
                channel.position(startIndex);
                //读取文件数据的结束位置
                long endIndex = startIndex + rSize;
                //用来缓存上次读取剩下的部分
                byte[] temp = new byte[0];
                //换行符
                int LF = "\n".getBytes()[0];
                //用于判断数据是否读取完
                boolean isEnd = false;
                //用于判断第一行读取到的是否是完整的一行
                boolean isWholeLine = false;
                //行数统计
                long lineCount = 0;
                //当前处理字节所在位置
                long endLineIndex = startIndex;
                while (channel.read(rbuf) != -1 && !isEnd) {
                    int position = rbuf.position();
                    byte[] rbyte = new byte[position];
                    rbuf.flip();
                    rbuf.get(rbyte);
                    //每行的起始位置下标，相对于当前所读取到的byte数组
                    int startNum = 0;
                    //判断是否有换行符
                    //如果读取到最后一行不是完整的一行时，则继续往后读取直至读取到完整的一行才结束
                    for (int i = 0; i < rbyte.length; i++) {
                        endLineIndex++;
                        //若存在换行符
                        if (rbyte[i] == LF) {
                            //若改数据片段第一个字节为换行符，说明第一行读取到的是完整的一行
                            if (channel.position() == startIndex) {
                                isWholeLine = true;
                                startNum = i + 1;
                            } else {
                                byte[] line = new byte[temp.length + i - startNum + 1];
                                System.arraycopy(temp, 0, line, 0, temp.length);
                                System.arraycopy(rbyte, startNum, line, temp.length, i - startNum + 1);
                                startNum = i + 1;
                                lineCount++;
                                temp = new byte[0];
                                //处理数据
                                //如果不是第一个数据段
                                if (startIndex != 0) {
                                    if (lineCount == 1) {
                                        //当且仅当第一行为完整行时才处理
                                        if (isWholeLine) {
                                            dataProcessHandler.process(line);
                                        }
                                    } else {
                                        dataProcessHandler.process(line);
                                    }
                                } else {
                                    dataProcessHandler.process(line);
                                }
                                //结束读取的判断
                                if (endLineIndex >= endIndex) {
                                    isEnd = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isEnd && startNum < rbyte.length) {
                        byte[] temp2 = new byte[temp.length + rbyte.length - startNum];
                        System.arraycopy(temp, 0, temp2, 0, temp.length);
                        System.arraycopy(rbyte, startNum, temp2, temp.length, rbyte.length - startNum);
                        temp = temp2;
                    }
                    rbuf.clear();
                }
                //兼容最后一行没有换行的情况
                if (temp.length > 0) {
                    if (dataProcessHandler != null) {
                        dataProcessHandler.process(temp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getThreadNum() {
        return threadNum;
    }

    public String getFilePath() {
        return filePath;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public int getBufSize() {
        return bufSize;
    }
}