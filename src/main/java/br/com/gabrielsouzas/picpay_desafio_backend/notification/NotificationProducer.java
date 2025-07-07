package br.com.gabrielsouzas.picpay_desafio_backend.notification;

import org.springframework.kafka.core.KafkaTemplate;

import br.com.gabrielsouzas.picpay_desafio_backend.transaction.Transaction;

public class NotificationProducer {
  private final KafkaTemplate<String, Transaction> kafkaTemplate;

  public NotificationProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendNotification(Transaction transaction) {
    kafkaTemplate.send("transaction-notification", transaction);
  }
}
