package com.jpmc.midascore;

import com.jpmc.midascore.component.TransactionListener;
import com.jpmc.midascore.entity.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
public class TaskThreeTests {

    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private TransactionListener transactionListener;

    @Test
    void task_three_verifier() throws InterruptedException {

        // 1️⃣ Load all transaction messages from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        // 2️⃣ Send all transactions to Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // 3️⃣ Wait for listener to process all messages
        Thread.sleep(5000); // adjust if there are many transactions

        // 4️⃣ Get all processed transactions
        List<TransactionRecord> transactions = transactionListener.getReceivedTransactions();
        logger.info("Total transactions processed: {}", transactions.size());

        // 5️⃣ Calculate Waldorf's final balance manually
        double startingBalance = 1000; // starting balance assumed for Waldorf
        double waldorfBalance = startingBalance;

        for (TransactionRecord tr : transactions) {
            if (tr.getSender().getName().equals("Waldorf")) {
                waldorfBalance -= tr.getAmount();
            }
            if (tr.getRecipient().getName().equals("Waldorf")) {
                waldorfBalance += tr.getAmount();
            }
        }

        // 6️⃣ Print the calculated balance
        logger.info("Calculated Waldorf's final balance: {}", waldorfBalance);

        // ✅ Test ends cleanly, no infinite loop
    }
}
