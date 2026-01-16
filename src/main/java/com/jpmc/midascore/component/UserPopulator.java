package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {

    private final UserRepository userRepository;

    public UserPopulator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void populate() {
        userRepository.save(new UserRecord(0L, "waldorf", 0.0));
        userRepository.save(new UserRecord(1L, "statler", 0.0));
        userRepository.save(new UserRecord(2L, "kermit", 0.0));
        userRepository.save(new UserRecord(3L, "fozzie", 0.0));
        userRepository.save(new UserRecord(4L, "miss_piggy", 0.0));
        userRepository.save(new UserRecord(5L, "gonzo", 0.0));
        userRepository.save(new UserRecord(6L, "rowlf", 0.0));
        userRepository.save(new UserRecord(7L, "scooter", 0.0));
        userRepository.save(new UserRecord(8L, "animal", 0.0));
        userRepository.save(new UserRecord(9L, "beaker", 0.0));
        userRepository.save(new UserRecord(10L, "wilbur", 0.0));
        userRepository.save(new UserRecord(11L, "user11", 0.0));
        userRepository.save(new UserRecord(12L, "user12", 0.0));
    }
}
