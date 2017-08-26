package com.saven.dailyalert.batch;

import com.saven.dailyalert.util.FtpFileTransfer;
import com.saven.dailyalert.util.S3ResourceLoader;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class JobNotificationListener extends JobExecutionListenerSupport {

    Logger logger = Logger.getLogger(JobNotificationListener.class.getName());

    FtpFileTransfer ftpFileTransfer;

    boolean skipUpload;

    @Autowired
    MultiResourceItemReader multiResourceItemReader;

    @Autowired
    S3ResourceLoader s3ResourceLoader;

    @Override
    public void beforeJob(JobExecution jobExecution){
        try {
            if(!skipUpload) {
                ftpFileTransfer.transfer();
            }

            multiResourceItemReader.setResources(s3ResourceLoader.getResources());
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public FtpFileTransfer getFtpFileTransfer() {
        return ftpFileTransfer;
    }

    public void setFtpFileTransfer(FtpFileTransfer ftpFileTransfer) {
        this.ftpFileTransfer = ftpFileTransfer;
    }

    public boolean isSkipUpload() {
        return skipUpload;
    }

    public void setSkipUpload(boolean skipUpload) {
        this.skipUpload = skipUpload;
    }
}
