package org.example.account.api;

import org.example.account.api.dto.AccountDto;
import org.example.account.repository.model.Account;
import org.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("accounts")
public class AccountRestController {

    @Autowired
    private AccountService accountService;

    @GetMapping(produces = "application/json")
    public @ResponseBody
    List<AccountDto> getAllAccounts() {
        return accountService.getAccounts().stream()
                .map(in -> AccountDto.builder()
                        .id(in.getAccountId())
                        .clientName(in.getClientName())
                        .amount(in.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public @ResponseBody
    AccountDto getAccount(@PathVariable("id") long id) {
        Optional<Account> account = accountService.getAccount(id);
        if (account.isPresent()) {
            return AccountDto.builder()
                    .id(account.get().getAccountId())
                    .clientName(account.get().getClientName())
                    .amount(account.get().getAmount())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}
