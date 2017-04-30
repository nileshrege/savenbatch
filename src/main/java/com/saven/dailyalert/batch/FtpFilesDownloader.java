package com.saven.dailyalert.batch;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class FtpFilesDownloader {

    Logger logger = Logger.getLogger(FtpFilesDownloader.class.getName());

    AwsS3FileUploader awsS3FileUploader;
    private String server;
    private int port;
    private String user;
    private String password;
    private String directory;
    private String s3bucket;
    boolean deleteAfterRead;

    public void download() throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            showServerReply(ftp);

            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.info("failed to connect ftp server");
                return;
            }
            ftp.enterLocalPassiveMode();
            ftp.setRemoteVerificationEnabled(false);
            ftp.setControlKeepAliveTimeout(1000000);
            boolean success = ftp.login(user, password);
            showServerReply(ftp);

            if (!success) {
                logger.info("could not login to the server");
                return;
            }

            logger.info(ftp.printWorkingDirectory());
            success = ftp.changeWorkingDirectory(directory);
            logger.info(ftp.printWorkingDirectory());
            showServerReply(ftp);

            if (!success) {
                logger.info("failed to change working directory, logging out.");
                logout(ftp);
            }

            ftp.setDataTimeout(1000000);
            logger.info("successfully changed working directory.");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy"); //-hh:mma
            String dateTime = LocalDateTime.now().format(formatter);
            awsS3FileUploader.createBucket(s3bucket+"-"+dateTime);

            transferFiles(ftp, dateTime);

        } catch (IOException e) {
            logger.info(e.getMessage());
        } finally {
            logout(ftp);
        }
    }

    private void logout(FTPClient ftp) throws IOException {
        ftp.logout();
        ftp.disconnect();
    }

    private void transferFiles(FTPClient ftp, String dateTime ) throws IOException{
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
                            awsS3FileUploader.upload(is, file.getName(), s3bucket+"-"+dateTime);
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
            e.printStackTrace();
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
}
