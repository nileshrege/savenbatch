package com.saven.dailyalert.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class JobNotificationListener extends JobExecutionListenerSupport {

    Logger logger = Logger.getLogger(JobNotificationListener.class.getName());

    FtpFilesDownloader ftpFilesDownloader;

    @Autowired
    MultiResourceItemReader multiResourceItemReader;

    @Autowired
    S3ResourceLoader s3ResourceLoader;

    @Override
    public void beforeJob(JobExecution jobExecution){
        try {
            ftpFilesDownloader.download();
            multiResourceItemReader.setResources(s3ResourceLoader.getResources());
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public FtpFilesDownloader getFtpFilesDownloader() {
        return ftpFilesDownloader;
    }

    public void setFtpFilesDownloader(FtpFilesDownloader ftpFilesDownloader) {
        this.ftpFilesDownloader = ftpFilesDownloader;
    }
}
