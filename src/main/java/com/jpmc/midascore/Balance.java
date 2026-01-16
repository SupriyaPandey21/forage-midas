package com.jpmc.midascore;

public class Balance {

    private double balance;

    protected Balance() {
    }

    public Balance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("Balance{balance=%f}", balance);
    }
}

