package com.jpmc.midascore.controller;

import com.jpmc.midascore.Balance;          // ✅ THIS WAS MISSING
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam String userId) {

        return userRepository.findByName(userId)
                .map(user -> new Balance(user.getBalance()))
                .orElse(new Balance(0.0));
    }
}
