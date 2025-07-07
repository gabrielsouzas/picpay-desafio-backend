package br.com.gabrielsouzas.picpay_desafio_backend.notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.gabrielsouzas.picpay_desafio_backend.transaction.Transaction;

@Service
public class NotificationConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

  private RestClient restClient;

  public NotificationConsumer(RestClient.Builder builder) {
    this.restClient = builder
        .baseUrl("https://run.mocky.io/v3") // URL fornecida pelo desafio para o mock de notificação
        .build();
  }

  @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend")
  public void receiveNotification(Transaction transaction) {
    LOGGER.info("Nottifying transaction {}...", transaction);

    var response = restClient.get()
        .retrieve()
        .toEntity(Notification.class);

    if (response.getStatusCode().isError() || !response.getBody().message()) {
      throw new NotificationException("Failed to send notification for transaction: " + transaction.id());
    }

    LOGGER.info("Notification sent successfully {}...", response.getBody());
  }
}
