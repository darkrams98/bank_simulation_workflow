package com.alireza.shaparakservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.alireza.payment.common.dto");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter,
                                         ShaparakProperties properties) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(properties.getExchange());
        template.setReplyAddress(properties.getExchange() + "/" + properties.getReplyRoutingKey());
        template.setReplyTimeout(properties.getReplyTimeoutMs());
        template.setUseDirectReplyToContainer(false);
        template.setUserCorrelationId(properties.isUserCorrelationId());
        return template;
    }

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer(ConnectionFactory connectionFactory,
                                                                 RabbitTemplate rabbitTemplate,
                                                                 ShaparakProperties properties) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(properties.getReplyQueue());
        container.setMessageListener(rabbitTemplate);
        container.setConcurrentConsumers(2);
        container.setMaxConcurrentConsumers(10);
        return container;
    }
}
