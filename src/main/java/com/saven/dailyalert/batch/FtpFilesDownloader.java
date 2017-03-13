package com.saven.dailyalert.batch;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FtpFilesDownloader {

    String serverAddress;
    String userId;
    String password;
    String remoteDirectory;
    String localDirectory;
    boolean deleteFileAfterRead;

    public boolean download() throws IOException {
        //new ftp client
        FTPClient ftp = new FTPClient();
        //try to connect
        ftp.connect(serverAddress);
        //login to server
        if(!ftp.login(userId, password))
        {
            ftp.logout();
            return false;
        }
        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes.
        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftp.disconnect();
            return false;
        }

        //enter passive mode
        ftp.enterLocalPassiveMode();
        //get system name
        System.out.println("Remote system is " + ftp.getSystemType());
        //change current directory
        ftp.changeWorkingDirectory(remoteDirectory);
        System.out.println("Current directory is " + ftp.printWorkingDirectory());

        //get list of filenames
        FTPFile[] ftpFiles = ftp.listFiles();

        if (ftpFiles != null && ftpFiles.length > 0) {
            //loop through files
            for (FTPFile file : ftpFiles) {
                if (!file.isFile()) {
                    continue;
                }
                System.out.println("File is " + file.getName());

                //get output stream
                OutputStream output;
                output = new FileOutputStream(localDirectory + "/" + file.getName());

                //get the file from the remote system
                ftp.retrieveFile(file.getName(), output);

                //close output stream
                output.close();

                //delete the file
                if(deleteFileAfterRead) {
                    ftp.deleteFile(file.getName());
                }
            }
        }
        ftp.logout();
        ftp.disconnect();
        return true;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    public boolean isDeleteFileAfterRead() {
        return deleteFileAfterRead;
    }

    public void setDeleteFileAfterRead(boolean deleteFileAfterRead) {
        this.deleteFileAfterRead = deleteFileAfterRead;
    }
}
