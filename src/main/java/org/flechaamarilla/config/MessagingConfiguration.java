package org.flechaamarilla.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.rabbitmq.RabbitMQOptions;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;

/**
 * Configuration for RabbitMQ connection
 */
@ApplicationScoped
public class MessagingConfiguration {

    @Inject
    @ConfigProperty(name = "rabbitmq.host", defaultValue = "localhost")
    String host;

    @Inject
    @ConfigProperty(name = "rabbitmq.port", defaultValue = "5672")
    int port;

    @Inject
    @ConfigProperty(name = "rabbitmq.username", defaultValue = "guest")
    String username;

    @Inject
    @ConfigProperty(name = "rabbitmq.password", defaultValue = "guest")
    String password;

    /**
     * Configures the RabbitMQ client with connection settings
     *
     * @return Configured RabbitMQ options
     */
    @Produces
    @Connector("smallrye-rabbitmq")
    public RabbitMQOptions rabbitMQOptions() {
        return new RabbitMQOptions()
                .setHost(host)
                .setPort(port)
                .setUser(username)
                .setPassword(password)
                .setAutomaticRecoveryEnabled(true);
    }
}