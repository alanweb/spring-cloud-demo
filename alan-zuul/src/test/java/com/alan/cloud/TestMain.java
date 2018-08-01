package com.alan.cloud;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author alan
 * @date 2018/7/6
 */
public class TestMain {
    private static String ALGORITHM = "DES";

    public static void main(String[] args) {
        byte a = -101;
        int b = a;
        System.out.println(b);

        // -100 byte -128 ~  127
        int c = a & 0xff;


        int c1 = a > 0 ? a : 256 - a;



        int d = c1 - 256;




        // 155 129 ->byte  X0FF
        System.out.println(c);




/*
        //用key生成密文
        byte[] encryptBytes = encryptMode("zhangsan".getBytes(), "lisi".getBytes());
        byte2hex(encryptBytes);
        System.out.println(new String(encryptBytes));
        //用key 解析密文
        byte[] decryptBytes = decryptMode("zhangsan".getBytes(), encryptBytes);
        System.out.println(new String(decryptBytes));*/
    }


    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            //生成密钥       keybyte:加密秘钥                                                                   DES
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            //解密                                                                             DES
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            //
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * src为被加密的数据缓冲区（源）
     *
     * @param keybyte
     * @param src
     * @return
     * @Description: src为被加密的数据缓冲区（源）
     * @author yqc
     * @date 2018年1月10日 上午10:49:35
     */
    public static byte[] encryptMode(final byte[] keybyte, final byte[] src) {
        try {
            //生成密钥   keybyte：是盐                                src：是加密的字符串         ALGORITHM：DES
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            //加密                                                                               DES
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            //             encrypt_mode
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        //  例如：b数组 [-101, 83, -63, -107, 55, 72, -38, -86]
        StringBuilder stringBuilder = new StringBuilder();
        //String hs="";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            System.out.println(n + " " + stmp);
            if (stmp.length() == 1) {
                //hs=hs+"0"+stmp;
                stringBuilder.append("0").append(stmp);
            } else {
                //hs=hs+stmp;
                stringBuilder.append(stmp);
            }
            if (n < b.length - 1) {
                //hs=hs+"";
                stringBuilder.append("");
            }
        }
        //return hs.toUpperCase();
        return stringBuilder.toString().toUpperCase();
    }
}
