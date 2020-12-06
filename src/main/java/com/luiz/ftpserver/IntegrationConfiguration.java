package com.luiz.ftpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer;
import org.springframework.integration.ftp.server.ApacheMinaFtpEvent;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
@Slf4j
public class IntegrationConfiguration {

    @Bean
    public ApacheMinaFtplet apacheMinaFtplet() {
        return new CustomFtplet();
    }

    @Bean
    @Primary
    public MessageChannel eventsChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow integrationFlow(MessageChannel messageChannel) {
        return IntegrationFlows.from(messageChannel)
                .handle((GenericHandler<ApacheMinaFtpEvent>) (apacheMinaFtpEvent, messageHeaders) -> {
                    log.info("new event: " + apacheMinaFtpEvent.getClass().getName() + " : " + apacheMinaFtpEvent.getSession());
                    return null;
                })
                .get();
    }

    @Bean
    public ApplicationEventListeningMessageProducer applicationEventListeningMessageProducer(MessageChannel messageChannel) {
        var producer = new ApplicationEventListeningMessageProducer();
        producer.setEventTypes(ApacheMinaFtpEvent.class);
        producer.setOutputChannel(messageChannel);
        return producer;
    }
}
