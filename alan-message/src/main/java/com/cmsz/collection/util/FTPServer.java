package com.cmsz.collection.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FTPServer {
    FTPClient ftpClient = null;

    public FTPServer(String ip, int port, String username, String passWord) {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            if (FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode())) {
                if (this.ftpClient.login(username, passWord)) {
                    this.ftpClient.setControlEncoding("GBK");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * FTP上传单个文件测试
     *
     * @throws IOException
     */
    public int uploadFTP(String filePath, String uploadPath) {
        FileInputStream fis = null;
        String fileName = "";
        if (filePath.lastIndexOf("/") > 0) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        }
        if (filePath.lastIndexOf("\\") > 0) {
            fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
        }
        try {
            File srcFile = new File(filePath);
            fis = new FileInputStream(srcFile);
            //设置上传目录 
            ftpClient.changeWorkingDirectory(uploadPath);
            ftpClient.setBufferSize(4096);
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(fileName, fis);
        } catch (IOException e) {
            e.printStackTrace();
            //失败
            return 0;
        } finally {
            IOUtils.closeQuietly(fis);
            try {
                fis.close();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                //失败
                return 0;
            }
        }

        //上传成功
        return 1;
    }

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    private String listFiles(String pathName) throws IOException {
        StringBuffer filename = new StringBuffer();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.enterLocalPassiveMode();
            FTPFile[] files = ftpClient.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        String n = new String(files[i].getName().getBytes("gbk"), "utf-8");
                        if (i == files.length - 1) {
                            filename.append(n);
                        } else {
                            filename.append(n + ",");
                        }
                    }
                }
            }
        }
        return filename.toString();
    }

    // 删除目录至FTP通用方法
    public int deleteDirectoryFtp(String directory, String pathName) {
        try {
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.enterLocalPassiveMode();
            ftpClient.removeDirectory(pathName);
        } catch (Exception e) {
            System.out.println("删除目录失败！");
            return 0;
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                //失败
                return 0;
            }
        }
        return 1;
    }

    // 删除文件至FTP通用方法
    public int deleteFileFtp(String directory, String fileName) {
        try {
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.enterLocalPassiveMode();
            ftpClient.deleteFile(fileName);
        } catch (Exception e) {
            System.out.println("删除文件失败！");
            return 0;
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                //失败
                return 0;
            }
        }
        return 1;
    }

    /**
     * FTP下载文件
     *
     * @throws IOException
     */
    public List<String> downloadFtpFiles(String reportPath, String dataDate, String downloadPath) {
        List<String> fileList = new ArrayList<>();
        FileOutputStream fos = null;
        try {
            ftpClient.setBufferSize(1024);
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            String filePaths = listFiles(reportPath);
            String[] filePathArr = filePaths.split(",");
            for (String filePath : filePathArr) {
                if (filePath.indexOf(dataDate.replaceAll("-", "")) >= 0) {
                    //匹配到对应的文件
                    String remoteFileName = reportPath + "/" + filePath;
                    remoteFileName.replaceAll("//", "/");
                    String download = downloadPath + File.separator + filePath;
                    File file = new File(download);
                    //如果文件存在不下载
                    if (file.exists()) {
                        continue;
                    }
                    fos = new FileOutputStream(file);
                    ftpClient.retrieveFile(remoteFileName, fos);
                    fileList.add(download);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.closeQuietly(fos);
                if (fos != null) {
                    fos.close();
                }
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileList;
    }

    /**
     * 创建目录
     * 需要手动关闭ftpClient
     *
     * @param path 目录地址
     * @param flag 只创建最后一级 默认true
     * @return
     */
    public int uploadDirectory(String path, boolean flag) {
        try {
            String parentPath = path.substring(0, path.lastIndexOf("/"));
            String directoryName = path.substring(path.lastIndexOf("/") + 1);
            //定位到上级目录
            boolean isExit = ftpClient.changeWorkingDirectory(parentPath);
            //如果不存在并且要创建全部的路径
            if (!isExit && !flag) {
                if (1 == uploadDirectory(parentPath, false)) {
                    isExit = true;
                }
            }
            if (isExit) {
                ftpClient.enterLocalPassiveMode();
                ftpClient.makeDirectory(directoryName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void close() {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String ip = ConfigUtil.getValue("ftp.ip");
        int port = Integer.valueOf(ConfigUtil.getValue("ftp.port"));
        String reportPath = ConfigUtil.getValue("ftp.reportPath");
        String uploadPath = ConfigUtil.getValue("ftp.uploadPath");
        String sysUser = ConfigUtil.getValue("ftp.sysUser");
        String passWord = ConfigUtil.getValue("ftp.passWord");
       // new FTPServer(ip, port, sysUser, passWord).deleteDirectoryFtp(uploadPath, "xxx");
//        new FTPServer(ip, port, sysUser, passWord).deleteFileFtp("", "s_20007_SZS_05001_20180610_00_001.verf");
        // new FTPServer(ip, port, sysUser, passWord).uploadDirectory(reportPath + "/swrz", false);
//        FTPServer ftpServer = new FTPServer(ip, port, sysUser, passWord);
//        ftpServer.uploadDirectory(uploadPath + "/swr2/22/".replaceAll("//", "/"), true);
//        ftpServer.close();
        new FTPServer(ip, port, sysUser, passWord).uploadFTP("D://fs_20007_SZS_05001_20180620_01_001.verf", reportPath);
        //  System.out.println(CheckReportEnum.getEnumByCode("06").getDesc());
    }
}
