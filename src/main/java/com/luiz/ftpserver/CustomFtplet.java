package com.luiz.ftpserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;

import java.io.IOException;

@Slf4j
public class CustomFtplet extends ApacheMinaFtplet {

    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        FtpletResult result = super.onUploadEnd(session, request);
        log.info("File Upload Ended");
        return result;
    }
}
