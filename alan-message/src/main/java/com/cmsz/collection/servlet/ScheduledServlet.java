package com.cmsz.collection.servlet;

import com.cmsz.collection.bean.BossMessage;
import com.cmsz.collection.cache.MessageCache;
import com.cmsz.collection.util.DateUtil;
import com.cmsz.collection.util.FTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Calendar;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 定时任务执行服务
 *
 * @author weibin
 * @date 2018/6/1
 */
@Component
public class ScheduledServlet {
    private Logger logger = LoggerFactory.getLogger(ScheduledServlet.class);
    @Value("${ftp.tmpPath}")
    private String tmpPath;
    @Value("${ftp.ip}")
    private String ip;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.uploadPath}")
    private String uploadPath;
    @Value("${ftp.sysUser}")
    private String sysUser;
    @Value("${ftp.passWord}")
    private String passWord;

    @Scheduled(cron = "${ftp.cron}")
    public void pushDataScheduled() {
        //1、验证是否存在需要处理的报文Message
        String yesterday = DateUtil.getDateyyyyMMdd(1);
        Set<String> set = MessageCache.dateTradeSessionMap.get(yesterday);
        if (set == null || set.isEmpty() || MessageCache.bossMessageMap.isEmpty()) {
            logger.info("{}没有报文消息", yesterday);
            //没有则不处理
            return;
        }
        //判断tmpPath是否存在 不存在则创建
        File filePath = new File(tmpPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        //2、对报文Message进行封装处理后生成xxx.dat.gz 文件并上传ftp服务器
        // 声明压缩流对象
        ZipOutputStream zipOut = null;
        ZipInputStream zipIn = null;
        try {
            String lineStr = System.getProperty("line.separator");
            //文件格式 s_20007_SZS_05001_yyyyMMdd_XX_XXX.dat
            String fileName = "s_20007_SZS_05001_" + yesterday.replaceAll("-", "") + "_00_001.dat";
            //创建压缩文件
            File file = new File(tmpPath + File.separator + fileName + ".gz");
            zipOut = new ZipOutputStream(new FileOutputStream(file));
            // 设置ZipEntry对象
            zipOut.putNextEntry(new ZipEntry(fileName));
            //行数
            long len = set.size();
            for (String key : set) {
                //对报文Message对象解析封装 写入压缩流
                BossMessage bossMessage = MessageCache.bossMessageMap.get(key);
                zipOut.write(bossMessage.joinAttribute("|").getBytes());
                zipOut.write(lineStr.getBytes());
            }
            //关闭写入连接
            zipOut.close();
            logger.info("{}压缩完成", tmpPath + File.separator + fileName + ".gz");
            //文件大小
            long fileSize = 0L;
            //压缩后文件大小
            long fileCompdSize = 0L;
            //读取压缩文件
            zipIn = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                zipIn.closeEntry();
                if (!zipEntry.isDirectory()) {
                    fileSize = zipEntry.getSize();
                    fileCompdSize = zipEntry.getCompressedSize();
                    break;
                }
            }
            //关闭读取连接
            zipIn.close();
            //封装s_2001220007_ITSSZS_0101105001_yyyymmdd_XX.verf 内容
            String verfName = "s_20007_SZS_05001_" + yesterday.replaceAll("-", "") + "_00_001.verf";
            long time = file.lastModified();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            String createTime = DateUtil.formatDate(cal.getTime(), "yyyyMMddHHmmss");
            StringBuffer sb = new StringBuffer();
            sb.append(fileName + ".gz").append("|");
            sb.append(fileCompdSize).append("|");
            sb.append(len).append("|");
            sb.append(yesterday.replaceAll("-", "")).append("|");
            sb.append(createTime).append("|");
            sb.append(fileSize);
            //输出verf 文件
            OutputStream os = new FileOutputStream(tmpPath + File.separator + verfName);
            os.write(sb.toString().getBytes());
            os.close();
            logger.info("{} 校验文件完成", verfName);

            //上传到ftp服务器
            FTPServer ftpServer = new FTPServer(ip, port, sysUser, passWord);
            int i = ftpServer.uploadFTP(tmpPath + File.separator + fileName + ".gz", uploadPath);
            logger.info("上传文件{}到ftp服务器{}{}.", fileName, ip, i == 1 ? "成功" : "失败");
            //3、如果上传成功 移除已经处理完成的报文Message 释放缓存空间
            if (i == 1) {
                for (String key : set) {
                    MessageCache.bossMessageMap.remove(key);
                }
                //4、上传校验文件成功
                i = ftpServer.uploadFTP(tmpPath + File.separator + verfName, uploadPath);
                logger.info("上传文件{}到ftp服务器{}{}.", verfName, ip, i == 1 ? "成功" : "失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipIn != null) {
                try {
                    zipIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
