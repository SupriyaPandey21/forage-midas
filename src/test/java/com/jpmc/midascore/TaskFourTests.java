package com.jpmc.midascore;

import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import com.jpmc.midascore.TaskFour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskFourTests {

    @Autowired
    private TaskFour taskFour;

    @Autowired
    private UserRepository userRepository;

    @Test
    void task_four_placeholder() throws InterruptedException {

        taskFour.run();

        userRepository.findAll()
                .forEach(u -> System.out.println(u.getName() + " -> " + u.getBalance()));

        // ⏳ Give Kafka listeners + incentive API time to process all transactions
        Thread.sleep(10_000);

        // 🔍 DEBUG HERE
        // Put a breakpoint on the line below
        userRepository.findByName("wilbur");
    }
}
