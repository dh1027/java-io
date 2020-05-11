package com.heyuanjun;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Test003 {
    @Test
    // 使用直接缓冲区完成文件的复制(內存映射文件) //428、357
    public void test2() throws IOException {
        long startTime = System.currentTimeMillis();
        FileChannel inChannel = FileChannel.open(Paths.get("f://1.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("f://2.mp4"), StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
        // 映射文件
        MappedByteBuffer inMapperBuff = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapperBuff = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        // 直接对缓冲区进行数据读写操作
        byte[] dst = new byte[inMapperBuff.limit()];
        inMapperBuff.get(dst);
        outMapperBuff.put(dst);
        outChannel.close();
        inChannel.close();
        long endTime = System.currentTimeMillis();
        System.out.println("内存映射文件耗时:" + (endTime - startTime));
    }

    @Test
    // 1.利用通道完成文件复制(非直接缓冲区)
    public void test1() throws IOException {  //11953 、3207、3337
        long startTime = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream("f://1.mp4");
        FileOutputStream fos = new FileOutputStream("f://2.mp4");
        // ①获取到通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // ②分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (inChannel.read(buf) != -1) {
            buf.flip();// 切换到读取模式
            outChannel.write(buf);
            buf.clear();// 清空缓冲区
        }
        // 关闭连接
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
        long endTime = System.currentTimeMillis();
        System.out.println("非缓冲区:" + (endTime - startTime));
    }
}
