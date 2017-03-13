package com.saven.dailyalert.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    AwsS3FileUploader fileUploader;

    String folderName;

    String prefix;

    String extension;

    boolean skipUpload;

    @Override
    public void afterJob(JobExecution jobExecution) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy"); //-hh:mma
        String dateTime = LocalDateTime.now().format(formatter);

        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null && !skipUpload) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    File file = listOfFiles[i];
                    if (file.exists() && !file.isDirectory()) {
                        fileUploader.upload(file, prefix+dateTime+extension);
                    }
                }
            }
        }
    }

    public AwsS3FileUploader getFileUploader() {
        return fileUploader;
    }

    public void setFileUploader(AwsS3FileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isSkipUpload() {
        return skipUpload;
    }

    public void setSkipUpload(boolean skipUpload) {
        this.skipUpload = skipUpload;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
