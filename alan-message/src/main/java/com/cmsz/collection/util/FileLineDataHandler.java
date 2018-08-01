package com.cmsz.collection.util;

import java.io.UnsupportedEncodingException;


/**
 * @author hp
 */
public class FileLineDataHandler implements DataProcessHandler {
    private String encode = "GBK";

    public FileLineDataHandler() {

    }

    public FileLineDataHandler(String encode) {
        this.encode = encode;
    }

    @Override
    public void process(byte[] data) {
        try {
            System.out.println(new String(data, encode).toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

