package com.alan.common.ftp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alan.common.ftp.FtpClientPool;
import com.alan.common.ftp.FtpClientUtil;
import com.alan.common.util.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yaoQingCan
 * @Description: FtpClient工具接口实现类
 * @date 2018年1月10日 上午10:57:23
 */
public class FtpClientUtilImpl implements FtpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClientUtilImpl.class);

    /**
     * ftp client 资源池
     */
    private FtpClientPool pool;

    /**
     * ftp client pool 基本信息
     */
    private FtpClientPoolBaseInfo ftpPoolBaseInfo;

    /**
     * 初始化ftp客户端资源池
     *
     * @throws IOException ftp服务器无法连接，或登录失时异常
     */
    public void init() throws IOException {

        LOGGER.info("poolsize:" + ftpPoolBaseInfo.getPoolsize());
        LOGGER.info("threshold:" + ftpPoolBaseInfo.getThreshold());

        LOGGER.info("Hostname:" + ftpPoolBaseInfo.getHostname());
        LOGGER.info("Port:" + ftpPoolBaseInfo.getPort());
        LOGGER.info("Username:" + ftpPoolBaseInfo.getUsername());
        //LOGGER.info("Password:"+ftpPoolBaseInfo.getPassword());
        LOGGER.info("BufferSize:" + ftpPoolBaseInfo.getBufferSize());
        LOGGER.info("FtpLocalMode:" + ftpPoolBaseInfo.getFtpLocalMode());

        LOGGER.info("defaultTimeout:" + ftpPoolBaseInfo.getDefaultTimeout());
        LOGGER.info("connectTimeout:" + ftpPoolBaseInfo.getConnectTimeout());
        LOGGER.info("dataTimeout:" + ftpPoolBaseInfo.getDataTimeout());


        FtpClientPoolImpl p = new FtpClientPoolImpl();
        p.setFtpPoolBaseInfo(ftpPoolBaseInfo);
        p.init();
        this.pool = p;

    }


    @Override
    public void put(InputStream local, String path, String name, String suffix) throws IOException {
        FTPClient ftp = pool.obtain();

        boolean b = this.accessDir(path, ftp);
        if (b == false) {
            LOGGER.error("can't access path {} on ftp serer", path);

            pool.revert(ftp);
            throw new IOException("can't access path " + path + " on ftp server");
        }

        String fname = name;
        if (suffix != null && suffix.length() > 0) {
            fname = name + suffix;
        }
        LOGGER.debug("begin to upload file to ftp sever, dir : {}, file name : {}", path, fname);
        b = ftp.storeFile(fname, local);
        if (b == false) {
            LOGGER.error("store file to ftp server failed, file name : {}, dir:{}", fname, path);

            pool.revert(ftp);
            throw new IOException("upload file, ftp server store file failed fname:" + fname + ", dir:" + path);
        } else {
            if (!ftp.rename(fname, name)) {
                LOGGER.error("rename file, ftp server rename file failed fname:" + fname + ", dir:" + path + ",name:"
                        + name);
                throw new IOException("rename file, ftp server rename file failed fname:" + fname + ", dir:" + path
                        + ",name:" + name);
            }
        }
        LOGGER.debug("upload file to ftp server success, dir:{}, file name:{}", path, fname);

        pool.revert(ftp);
    }

    @Override
    public void put(File local, String path, String name, String suffix) throws IOException {

        FileInputStream fileInputStream = null;
        BufferedInputStream bin = null;

        try {
            fileInputStream = new FileInputStream(local);
            bin = new BufferedInputStream(fileInputStream);
            this.put(bin, path, name, suffix);
        } catch (IOException e) {
            throw e;
        } finally {
            if (bin != null) {
                bin.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    @Override
    public void put(String local, String path, String name, String suffix) throws IOException {

        FileInputStream fileInputStream = null;
        BufferedInputStream bin = null;

        try {
            fileInputStream = new FileInputStream(local);
            bin = new BufferedInputStream(fileInputStream);
            this.put(bin, path, name, suffix);
        } catch (IOException e) {
            throw e;
        } finally {
            if (bin != null) {
                bin.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    @Override
    public void get(OutputStream local, String path, String name) throws IOException {
        FTPClient ftp = pool.obtain();

        boolean b = this.accessDir(path, ftp);
        if (b == false) {
            LOGGER.error("can't access path {} on ftp serer", path);
            pool.revert(ftp);
            throw new IOException("can't access path " + path + " on ftp server");
        }

        b = ftp.retrieveFile(name, local);
        if (b == false) {
            LOGGER.error("retrieve file from ftp server failed, fname : {}, dir:{}, reply code :{}", name, path,
                    ftp.getReplyCode());
            pool.revert(ftp);
            throw new IOException("ftpclient download file failed form ftp server, fname:" + name + ", dir:" + path
                    + ", replay code:" + ftp.getReplyCode());
        } else {
            LOGGER.debug("download file from ftp server success, path {} , name {}", path, name);
        }
        pool.revert(ftp);
    }


    @Override
    public void get(File local, String path, String name) throws IOException {

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bout = null;
        try {
            fileOutputStream = new FileOutputStream(local);
            bout = new BufferedOutputStream(fileOutputStream);
            this.get(bout, path, name);
            bout.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            if (bout != null) {
                bout.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    @Override
    public void get(String localDir, String path, String name) throws IOException {
        File localFolder = new File(localDir);
        if (!localFolder.exists()) {
            try {
                localFolder.mkdirs();
            } catch (Exception e) {
                LOGGER.error("can't create path {} on ftp server", path);
                throw new IOException(e.getMessage());
            }
        }
        String file = localDir + File.separator + name;
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
        try {
            this.get(bout, path, name);
        } catch (IOException e) {
            throw e;
        } finally {
            bout.flush();
            bout.close();
        }
    }

    @Override
    public void move(String spath, String mpath, String fileName) throws IOException {
        FTPClient ftp = pool.obtain();
        boolean b = this.accessDir(spath, ftp);
        if (b == false) {
            LOGGER.error("can't access path {} on ftp serer", spath);
            pool.revert(ftp);
            throw new IOException("can't access path " + spath + " on ftp server");
        }

        boolean b1 = this.accessDir(mpath, ftp);
        if (b1 == false) {
            LOGGER.error("can't access path {} on ftp serer", mpath);
            pool.revert(ftp);
            throw new IOException("can't access path " + mpath + " on ftp server");
        }

        boolean b2 = ftp.rename(spath + "/" + fileName, mpath + "/" + fileName);
        if (b2 == false) {
            LOGGER.error("copy file to ftp server failed, file name : {}, dir:{}", fileName, spath);
            pool.revert(ftp);
            throw new IOException("copy file, ftp server store file failed fname:" + fileName + ", dir:" + spath);
        } else {
            LOGGER.debug("copy file to ftp server success, sdir:{}, file name:{}, tdir:{}", spath, fileName, mpath);
        }
        pool.revert(ftp);
    }

    @Override
    public void copy(String spath, String cpath, String fileName, String suffix, String rname) throws IOException {
        FTPClient ftp = pool.obtain();
        boolean b = this.accessDir(spath, ftp);
        if (b == false) {
            LOGGER.error("can't access path {} on ftp serer", spath);
            pool.revert(ftp);
            throw new IOException("can't access path " + spath + " on ftp server");
        }
        InputStream inputStream = ftp.retrieveFileStream(spath + "/" + fileName);
        if (!ftp.completePendingCommand()) {
            LOGGER.error("can't completePendingCommand on ftp server");
            pool.revert(ftp);
            throw new IOException("can't completePendingCommand on ftp server");
        }
        boolean b1 = this.accessDir(cpath, ftp);
        if (b1 == false) {
            LOGGER.error("can't access path {} on ftp serer", cpath);
            pool.revert(ftp);
            throw new IOException("can't access path " + cpath + " on ftp server");
        }
        if (inputStream != null) {
            boolean b3 = false;
            try {
                if (!"".equals(rname) && rname != null) {
                    b3 = ftp.storeFile(rname + suffix, inputStream);
                    if (b3) {
                        if (!ftp.rename(rname + suffix, rname)) {
                            LOGGER.error("rename file, ftp server rename file failed fname:" + rname + suffix + ", dir:" + cpath + ",name:"
                                    + rname);
                            throw new IOException("rename file, ftp server rename file failed fname:" + rname + suffix + ", dir:" + cpath + ",name:"
                                    + rname);
                        }
                    }
                } else {
                    b3 = ftp.storeFile(fileName + suffix, inputStream);
                    if (b3) {
                        if (!ftp.rename(fileName + suffix, fileName)) {
                            LOGGER.error("rename file, ftp server rename file failed fname:" + fileName + suffix + ", dir:" + cpath + ",name:"
                                    + fileName);
                            pool.revert(ftp);
                            throw new IOException("rename file, ftp server rename file failed fname:" + fileName + suffix + ", dir:" + cpath + ",name:"
                                    + fileName);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("can't copy file {} on ftp serer", spath + "/" + fileName);
                pool.revert(ftp);
                throw new IOException("can't  copy file " + spath + "/" + fileName + " on ftp server");
            } finally {
                inputStream.close();
            }
            if (b3 == false) {
                LOGGER.error("can't copy file {} on ftp serer", spath + "/" + fileName);
                pool.revert(ftp);
                throw new IOException("can't  copy file " + spath + "/" + fileName + " on ftp server");
            }
            pool.revert(ftp);
        }
    }

    @Override
    public String[] listNames(String pathname) throws IOException {
        FTPClient ftp = this.pool.obtain();
        String[] names = null;
        try {
            names = ftp.listNames(pathname);
        } catch (IOException e) {
            LOGGER.error("ftp client listNames exception happend:", e);
            throw e;
        } finally {
            this.pool.revert(ftp);
        }
        return names;
    }

    @Override
    public FTPFile[] listFiles(String pathname) throws IOException {
        FTPClient ftp = this.pool.obtain();
        FTPFile[] files = null;
        try {
            files = ftp.listFiles(pathname);
        } catch (IOException e) {
            LOGGER.error("ftp client listFiles exception happend:", e);
            throw e;
        } finally {
            this.pool.revert(ftp);
        }
        return files;
    }

    @Override
    public void get(String localDir, String pathname) throws IOException {
        int l = pathname.lastIndexOf("/");
        String path = pathname.substring(0, l);
        String name = pathname.substring(l + 1);
        try {
            this.get(localDir, path, name);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean deleteFile(String pathname) throws IOException {
        FTPClient ftp = this.pool.obtain();
        boolean b = ftp.deleteFile(pathname);
        this.pool.revert(ftp);
        return b;
    }

    @Override
    public List<RemoteFileInfo> getRemoteFile(String path, boolean isdepth) throws Exception {

        List<RemoteFileInfo> ftpFiles = new ArrayList<RemoteFileInfo>();

        FTPClient ftpClient = this.pool.obtain();
        try {

            listFiles(ftpFiles, ftpClient, path, isdepth);

        } catch (IOException e) {
            LOGGER.error("ftp client listFiles exception happend:", e);
            throw e;
        } finally {
            this.pool.revert(ftpClient);
        }
        return ftpFiles;

    }

    /**
     * 递归扫描path下的所有文件
     *
     * @param ftpFiles
     * @param ftpClient 远程ftp
     * @param path      远程路径
     * @param isdepth   是否扫描子文件
     * @throws Exception
     */
    private void listFiles(List<RemoteFileInfo> ftpFiles, FTPClient ftpClient, String path, boolean isdepth)
            throws Exception {

        FTPFile[] remoteFiles = ftpClient.listFiles(path);
        if (remoteFiles != null && remoteFiles.length > 0) {
            for (FTPFile ftpFile : remoteFiles) {
                if (ftpFile.isDirectory() && isdepth) {
                    listFiles(ftpFiles, ftpClient, path + "/" + ftpFile.getName(), isdepth);
                } else {
                    ftpFiles.add(new RemoteFileInfo(ftpFile.getSize(), ftpFile.getName(), ftpFile.isDirectory(), ftpFile.isFile(), ftpFile.isSymbolicLink(), path));
                }
            }
        }
    }

    private boolean accessDir(String path, FTPClient ftp) {
        boolean b;
        try {
            b = ftp.changeWorkingDirectory(path);
            if (b == false) {
                String[] ps = path.split("/");
                String dir = "";
                for (String p : ps) {
                    if (!StringUtils.isBlank(p)) {
                        dir += "/" + p;
                        b = ftp.makeDirectory(dir);
                    }
                }
                if (b == false) {
                    LOGGER.error("can't create path {} on ftp server", path);
                    return false;
                } else {
                    LOGGER.info("mkdir  {} on ftp server, success", path);
                }
                b = ftp.changeWorkingDirectory(path);
            }
        } catch (IOException e) {
            LOGGER.error("can't access path " + path + " on server, exception happened", e);
            return false;
        }

        if (b == false) {
            LOGGER.error("can't access path {} on server", path);
        }
        return b;
    }

    public FtpClientPool getPool() {
        return pool;
    }

    public void setPool(FtpClientPool pool) {
        this.pool = pool;
    }


    public FtpClientPoolBaseInfo getFtpPoolBaseInfo() {
        return ftpPoolBaseInfo;
    }


    public void setFtpPoolBaseInfo(FtpClientPoolBaseInfo ftpPoolBaseInfo) {
        this.ftpPoolBaseInfo = ftpPoolBaseInfo;
    }


}
