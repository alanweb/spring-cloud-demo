/**
 *
 */
package com.alan.cloud;

import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author liaoruiyi
 */
public class Des {

    /**
     * 定义 加密算法,可用 DES,DESede,Blowfish
     */
    private static final String ALGORITHM = "DES";

    private static final int TWO = 2;

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


    /**
     * @param keybyte 加密密钥，长度为24字节
     * @param src     加密后的缓冲区
     * @return
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author yqc
     * @date 2018年1月10日 上午10:49:51
     */
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
     * @param b
     * @return
     * @Description: 转换成十六进制字符串
     * @author yqc
     * @date 2018年1月10日 上午10:50:21
     */
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

    /**
     * @param hex
     * @return
     * @throws IllegalArgumentException
     * @Description: 16 进制 转 2 进制
     * @author yqc
     * @date 2018年1月10日 上午10:55:39
     */
    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % TWO != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    @SuppressWarnings("unused")
    private static byte[] hex2byte(byte[] b) {
        if ((b.length % TWO) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += TWO) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * @param str
     * @param key
     * @return
     * @Description: 加密
     * @author yqc
     * @date 2018年1月10日 上午10:50:38
     */
    public static String encrypt(String str, byte[] key) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        byte[] encrypt = encryptMode(key, str.getBytes());
        System.out.println("Cipher加密后的  ：" + Arrays.toString(encrypt));
        return byte2hex(encrypt);
    }

    /**
     * @param src
     * @param key
     * @return
     * @Description: 加密
     * @author yqc
     * @date 2018年1月10日 上午10:50:49
     */
    public static byte[] encryptRetByte(byte[] src, byte[] key) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        byte[] encrypt = encryptMode(key, src);
        return encrypt;
    }

    /**
     * @param str
     * @param key
     * @return
     * @Description:解密
     * @author yqc
     * @date 2018年1月10日 上午10:51:01
     */
    public static String decrypt(String str, byte[] key) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        byte[] decrypt = decryptMode(key, hex2byte(str));
        System.out.println("Cipher解密后的  ：" + Arrays.toString(decrypt));
        return new String(decrypt);
    }

    public static void main(String[] args) {

        /*111111111111111111111111 1000 0000        1 1000 0000
         * 减一  111111111111111111111111 0 111 1111  1 0111 1111
         *     正数 100 0000       1
         *     1000 0000                         0000000 1 000 0000
         *
         *     1000 0011    1000 0010  0111 1101   1+4+8+16+32+64
         *     A 0000 1010    （A＆B），得到12，即
         *     B 0000 1011   0000 1100
         *     -12 0000 1100  1111 0011  1111 0100
         *      0xFF    -127    0000 0000   0111 1111       1000 0001  1111 1111 1000 0000
         */

        CmupEncryptor cmup = new CmupEncryptor();
        cmup.setEncry(true);
        cmup.setSecurityKey("0002000200020002");
        String message = "张三";
        //9B53C1953748DAAA
        System.out.println(cmup.encrypt(message));
        System.out.println(cmup.decrypt("9B53C1953748DAAA"));


        byte[] str = new byte[]{-27, -68, -96, -28, -72, -119};
        byte [] a =  "张三".getBytes() ;
        System.out.println();
      //  System.out.println(new String(str));

    }
}
