package com.luiz.ftpserver;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class FtpServerConfiguration {

    @Bean
    public FileSystemFactory fileSystemFactory() {
        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCreateHome(true);
        fileSystemFactory.setCaseInsensitive(false);
        return fileSystemFactory::createFileSystemView;
    }

    @Bean
    public ListenerFactory listenerFactory(SslConfiguration sslConfiguration) {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2121);
        listenerFactory.setSslConfiguration(sslConfiguration);
        return listenerFactory;
    }

    @Bean
    public SslConfiguration sslConfiguration() {
        SslConfigurationFactory sslConfiguration = new SslConfigurationFactory();
        sslConfiguration.setClientAuthentication("WANT");
        sslConfiguration.setKeystoreFile(new File("KeyStore.jks"));
        sslConfiguration.setKeystorePassword("luiz12");
        return sslConfiguration.createSslConfiguration();
    }

    public Map<String, Ftplet> mapOfFtplets(Ftplet[] ftplets) {
        return Arrays.stream(ftplets).collect(Collectors.toMap(x -> x.getClass().getName(), x -> x));
    }

    @Bean
    public FtpServerFactory ftpServerFactory(Ftplet ftplets, ListenerFactory listenerFactory,
                                             UserManager userManager, FileSystemFactory fileSystemFactory) {
        var ftpletMap = mapOfFtplets(new Ftplet[] { ftplets });
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.setFileSystem(fileSystemFactory);
        ftpServerFactory.setUserManager(userManager);
        ftpServerFactory.setFtplets(ftpletMap);
        ftpServerFactory.addListener("default", listenerFactory.createListener());
        return ftpServerFactory;
    }

    @Bean
    public FtpServer ftpServer(FtpServerFactory ftpServerFactory) {
        return ftpServerFactory.createServer();
    }

    @Bean
    DisposableBean destroysFtpServer(FtpServer ftpServer) {
        return ftpServer::stop;
    }

    @Bean
    InitializingBean startsFtpServer(FtpServer ftpServer) {
        return ftpServer::start;
    }

    @Bean
    public UserManager userManager() throws FtpException {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("myuser.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();
        return um;
    }
}
