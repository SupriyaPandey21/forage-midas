package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jpmc.midascore.component.TransactionListener;


@SpringBootTest(
        properties = {"midas.transactions-topic=test-topic"}
)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
class TaskTwoTests {

    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private TransactionListener transactionListener;

    @Test
    void task_two_verifier() throws InterruptedException {

        System.out.println("TEST STARTED");

        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");
        System.out.println("FILE LOADED, lines = " + transactionLines.length);

        // send messages asynchronously
        for (String transactionLine : transactionLines) {
            System.out.println("SENDING: " + transactionLine);
            kafkaProducer.send(transactionLine); // remove .get()
        }

        System.out.println("MESSAGES SENT");

        int attempts = 0;
        while (transactionListener.getReceivedTransactions().size() < transactionLines.length && attempts < 10) {
            Thread.sleep(500);
            attempts++;
        }

        System.out.println("Received transaction amounts:");
        transactionListener.getReceivedTransactions()
                .forEach(t -> System.out.println(t.getAmount()));

        // Verify each transaction matches the file
        for (int i = 0; i < transactionLines.length; i++) {
            double expectedAmount = extractAmountFromJson(transactionLines[i]);
            double actualAmount = transactionListener.getReceivedTransactions().get(i).getAmount();
            assertEquals(expectedAmount, actualAmount, 0.001, "Amount mismatch at transaction " + (i + 1));
        }

        // Verify total number of transactions
        assertEquals(transactionLines.length, transactionListener.getReceivedTransactions().size());
    }

    // Helper method for extracting amount from JSON line
    private double extractAmountFromJson(String jsonLine) {
        String amountStr = jsonLine.split("\"amount\":")[1].replaceAll("[^0-9.]", "");
        return Double.parseDouble(amountStr);
    }
}
