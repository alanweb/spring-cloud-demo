package com.alan.elk.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author weiBin
 * @date 2018/7/31
 */
public class Test {
    public static void main(String[] args) {
        Path path = Paths.get("D:\\doc\\xxx");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
