package br.com.gabrielsouzas.picpay_desafio_backend.authorization;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.gabrielsouzas.picpay_desafio_backend.transaction.Transaction;

@Service
public class AuthorizerService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizerService.class);

  private RestClient restClient;

  public AuthorizerService(RestClient.Builder builder) {
    this.restClient = builder
        .baseUrl("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc")
        .build();
  }

  public void authorize(Transaction transaction) {
    LOGGER.info("Authorizing transaction: {}", transaction);
    var response = restClient.get()
        .retrieve()
        .toEntity(Authorization.class);

    if (response.getStatusCode().isError() || !response.getBody().isAuthorized()) {
      throw new UnauthorizedTransactionException(
          "Unauthorized transaction");

    }
    LOGGER.info("Transaction authorized: {}", transaction);
  }
}
