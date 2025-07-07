package br.com.gabrielsouzas.picpay_desafio_backend.transaction;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gabrielsouzas.picpay_desafio_backend.authorization.AuthorizerService;
import br.com.gabrielsouzas.picpay_desafio_backend.notification.NotificationService;
import br.com.gabrielsouzas.picpay_desafio_backend.wallet.Wallet;
import br.com.gabrielsouzas.picpay_desafio_backend.wallet.WalletRepository;
import br.com.gabrielsouzas.picpay_desafio_backend.wallet.WalletType;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final WalletRepository walletRepository;
  private final AuthorizerService authorizerService;
  private final NotificationService notificationService;

  public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository,
      AuthorizerService authorizerService, NotificationService notificationService) {
    this.transactionRepository = transactionRepository;
    this.walletRepository = walletRepository;
    this.authorizerService = authorizerService;
    this.notificationService = notificationService;
  }

  @Transactional
  public Transaction create(Transaction transaction) {
    // 1 - validar
    validate(transaction);

    // 2 - criar a tyransação
    var newTransaction = transactionRepository.save(transaction);
    // 3 - debitar da carteira do pagador e creditar na carteira do recebedor
    var walletPayer = walletRepository.findById(transaction.payer()).get();
    var walletPayee = walletRepository.findById(transaction.payee()).get();
    walletRepository.save(walletPayer.debit(transaction.value()));
    walletRepository.save(walletPayee.credit(transaction.value()));

    // 4 - chamar serviços externos
    // authorize transaction
    authorizerService.authorize(transaction);

    // notificação
    notificationService.notify(transaction);

    return newTransaction;
  }

  /*
   * - the payer has a common wallet
   * - the payer must have enough balance to pay the transaction
   * - the payer must be different from the payee
   */
  private void validate(Transaction transaction) {
    walletRepository.findById(transaction.payee())
        .map(payee -> walletRepository.findById(transaction.payer())
            .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
            .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
        .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
  }

  private boolean isTransactionValid(Transaction transaction, Wallet payer) {
    return payer.type() == WalletType.COMUM.getValue() &&
        payer.balance().compareTo(transaction.value()) >= 0 &&
        !payer.id().equals(transaction.payee());
  }

  public List<Transaction> list() {
    return transactionRepository.findAll();
  }
}
