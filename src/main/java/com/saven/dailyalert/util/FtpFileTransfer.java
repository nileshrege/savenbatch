package com.saven.dailyalert.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpFileTransfer {

    private String dateFormat;

    private Logger logger = Logger.getLogger(FtpFileTransfer.class.getName());

    private AwsS3FileUploader awsS3FileUploader;
    private String server;
    private int port;
    private String user;
    private String password;
    private String directory;
    private String s3bucket;
    private boolean deleteAfterRead;

    private FTPClient connect(String server, int port, String user, String password) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE,  e.getMessage());
            throw e;
        }
        showServerReply(ftp);
        int replyCode = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new RuntimeException("failed to connect ftp server");
        }

        ftp.enterLocalPassiveMode();
        ftp.setRemoteVerificationEnabled(false);
        ftp.setControlKeepAliveTimeout(1000000);
        boolean success = false;
        try {
            success = ftp.login(user, password);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE,  e.getMessage());
            throw e;
        }
        showServerReply(ftp);
        if (!success) {
            throw new RuntimeException("could not login to the server");
        }

        return ftp;
    }

    private boolean changeDirectory(FTPClient ftp, String directory) throws IOException {
        logger.info(ftp.printWorkingDirectory());
        boolean success = ftp.changeWorkingDirectory(directory);
        logger.info(ftp.printWorkingDirectory());
        showServerReply(ftp);
        if (!success) {
            logger.info("failed to change working directory, logging out.");
            logout(ftp);
        }
        logger.info("successfully changed working directory to "+directory);
        return true;
    }

    public void transfer() {
        FTPClient ftp = null;
        try {
            ftp = connect(server, port, user, password);
            changeDirectory(ftp, directory);
            transferFiles(ftp);
        }catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if(ftp != null)
                    logout(ftp);
            }
            catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }


    }

    private void logout(FTPClient ftp) throws IOException {
        ftp.logout();
        ftp.disconnect();
    }

    private void transferFiles(FTPClient ftp) throws IOException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat); //-hh:mma
        String dateTime = LocalDateTime.now().format(formatter);

        awsS3FileUploader.createBucket(s3bucket);
        ftp.setDataTimeout(1000000);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        FTPListParseEngine engine = ftp.initiateListParsing(directory);

        try {
            while (engine.hasNext()) {
                FTPFile[] files = engine.getNext(1440);  // "page size" you want
                if (files != null && files.length > 0) {
                    for (FTPFile file : files) {
                        if (!file.isFile()) {
                            continue;
                        }
                        logger.info("file is " + file.getName());

                        InputStream is = ftp.retrieveFileStream(file.getName());
                        if (is != null) {
                            awsS3FileUploader.upload(is, dateTime+"/"+file.getName(), s3bucket);
                        }
                        //close output stream
                        if (is != null) is.close();

                        ftp.completePendingCommand();

                        //delete the file
                        //                        if(deleteAfterRead) ftp.deleteFile(file.getName());

                    }
                }
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                logger.info("server: " + aReply);
            }
        }
    }

    public AwsS3FileUploader getAwsS3FileUploader() {
        return awsS3FileUploader;
    }

    public void setAwsS3FileUploader(AwsS3FileUploader awsS3FileUploader) {
        this.awsS3FileUploader = awsS3FileUploader;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getS3bucket() {
        return s3bucket;
    }

    public void setS3bucket(String s3bucket) {
        this.s3bucket = s3bucket;
    }

    public boolean isDeleteAfterRead() {
        return deleteAfterRead;
    }

    public void setDeleteAfterRead(boolean deleteAfterRead) {
        this.deleteAfterRead = deleteAfterRead;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}


