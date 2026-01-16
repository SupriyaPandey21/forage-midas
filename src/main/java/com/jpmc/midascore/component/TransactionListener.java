package com.jpmc.midascore.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveClient incentiveClient;

    // REQUIRED FOR TASK 3 TESTS
    private final List<TransactionRecord> receivedTransactions = new ArrayList<>();

    // Constructor injection (tests depend on this)
    public TransactionListener(
            UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository,
            IncentiveClient incentiveClient
    ) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveClient = incentiveClient;
    }

    @KafkaListener(
            topics = "${midas.transactions-topic}",
            groupId = "task-two-test-group"
    )
    public void listen(String message) throws Exception {

        // Parse Kafka message
        Transaction transaction =
                objectMapper.readValue(message, Transaction.class);

        Long senderId = transaction.getSenderId();
        Long recipientId = transaction.getRecipientId();
        double amount = transaction.getAmount();

        // Fetch users
        var senderOpt = userRepository.findById(senderId);
        var recipientOpt = userRepository.findById(recipientId);

        // Invalid users → discard
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Insufficient balance → discard
        if (sender.getBalance() < amount) {
            return;
        }

        // ✅ TASK 4: Call Incentive API
        double incentive = incentiveClient.getIncentive(transaction);

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction record WITH incentive
        TransactionRecord record =
                new TransactionRecord(sender, recipient, amount, incentive);

        transactionRecordRepository.save(record);
        receivedTransactions.add(record);
    }
    public List<TransactionRecord> getReceivedTransactions() {
        return receivedTransactions;
    }
}
