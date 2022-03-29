package model.bank;

import model.enums.PaymentStatus;

import java.time.LocalDate;

public class Payment {
    private long paymentID = -1;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private BankAccount recipient; // получатель
    private String recipientName;
    private BankAccount sender; // отправитель
    private String senderName;
    private double paymentSum;

    public Payment(PaymentStatus status, BankAccount recipient, String recipientName, BankAccount sender, String senderName, double paymentSum) {
        this.status = status;
        paymentDate = LocalDate.now();
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.sender = sender;
        this.senderName = senderName;
        this.paymentSum = paymentSum;
    }

    public Payment(PaymentStatus status, LocalDate paymentDate, BankAccount recipient, String recipientName, BankAccount sender, String senderName, double paymentSum) {
        this.status = status;
        this.paymentDate = paymentDate;
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.sender = sender;
        this.senderName = senderName;
        this.paymentSum = paymentSum;
    }

    public Payment(long paymentID, PaymentStatus status, LocalDate paymentDate, BankAccount recipient, String recipientName, BankAccount sender, String senderName, double paymentSum) {
        this.paymentID = paymentID;
        this.status = status;
        this.paymentDate = paymentDate;
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.sender = sender;
        this.senderName = senderName;
        this.paymentSum = paymentSum;
    }

    public long getPaymentID() {
        return paymentID;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public long getRecipient() {
        return recipient.getAccountID();
    }

    public String getRecipientName() {
        return recipientName;
    }

    public long getSender() {
        return sender.getAccountID();
    }

    public String getSenderName() {
        return senderName;
    }

    public double getPaymentSum() {
        return paymentSum;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", recipient=" + recipient.getAccountID() +
                ", recipientName='" + recipientName + '\'' +
                ", sender=" + sender.getAccountID() +
                ", senderName='" + senderName + '\'' +
                ", paymentSum=" + paymentSum +
                '}';
    }
}
