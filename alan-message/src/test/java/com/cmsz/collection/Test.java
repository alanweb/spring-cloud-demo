package com.cmsz.collection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author weibin
 * @descript:TOOL
 * @date 2018/6/1
 */
public class Test {
    public static void main1(String[] args) {
        String str1 = "2018-06-01";
        String str2 = "2018-05-20";
        System.out.println(str1.compareTo(str2));
    }

    public static void main(String[] args) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream("D:\\alan\\oc-message\\s_20007_SZS_05001_20180531_00_001.dat.gz"));
        ZipEntry zipEntry;
        while ((zipEntry = zipIn.getNextEntry()) != null) {
            zipIn.closeEntry();
            if (!zipEntry.isDirectory()) {
                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compd = zipEntry.getCompressedSize();
                System.out.printf("%s , size=%d, compressed size=%d\r\n", name, size, compd);
            }
        }
        zipIn.close();
    }
}
