package org.example.account.service;

import org.example.account.repository.AccountRepository;
import org.example.account.repository.model.Account;
import org.example.account.service.exception.AccountException;
import org.example.account.service.exception.AccountNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void addAccounts() {
        accountRepository.save(Account.builder()
                .clientName("Nik")
                .amount(Double.valueOf(1000))
                .build());

        accountRepository.save(Account.builder()
                .clientName("Peter")
                .amount(Double.valueOf(5000))
                .build());

        accountRepository.save(Account.builder()
                .clientName("Olek")
                .amount(Double.valueOf(8000))
                .build());
    }

    public List<Account> getAccounts() {
        List<Account> result = new ArrayList<>();
        for (Account account : accountRepository.findAll()) {
            result.add(account);
        }

        return result;
    }

    public Optional<Account> getAccount(long id) {
        return accountRepository.findById(id);
    }

    @Transactional
    public void debit(Long accountId, Double price) throws AccountException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with id %d not found", accountId));
        }

        Account accountEntity = account.get();

        if (accountEntity.getAmount() <= 0) {
            throw new AccountException(String.format("Account with id %d has zero amount", accountId));
        }

        if (price > accountEntity.getAmount()) {
            throw new AccountException(String.format("Account with id %d does not have enough balance to perform transaction", accountId));
        }

        accountEntity.setAmount(accountEntity.getAmount() - price);
        accountRepository.save(accountEntity);
    }
}
