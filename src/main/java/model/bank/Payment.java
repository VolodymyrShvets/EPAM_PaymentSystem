package model.bank;

import model.enums.PaymentStatus;

import java.time.LocalDate;

/**
 * Class that represents Payment entity in system.
 */
public class Payment {
    private long paymentID = -1;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private BankAccount recipient; // получатель
    private String recipientName;
    private BankAccount sender; // отправитель
    private String senderName;
    private double paymentSum;

    /**
     * Default constructor that creates payment with Prepared status.
     * See also {@see model.enums.PaymentStatus}
     *
     * @param recipient     recipient account {@see model.bank.BankAccount}
     * @param paymentDate   payment date
     * @param recipientName recipient full name
     * @param sender        sender account {@see model.bank.BankAccount}
     * @param senderName    sender full name
     * @param paymentSum    payment sum
     */
    public Payment(BankAccount recipient, LocalDate paymentDate, String recipientName, BankAccount sender, String senderName, double paymentSum) {
        status = PaymentStatus.PREPARED;
        this.paymentDate = paymentDate;
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.sender = sender;
        this.senderName = senderName;
        this.paymentSum = paymentSum;
    }

    /**
     * Another constructor for Payment.
     * Used to create new Payment with specific status.
     * See also {@see model.enums.PaymentStatus}
     *
     * @param status        payment status {@see model.enums.PaymentStatus}
     * @param recipient     account {@see model.bank.BankAccount}
     * @param paymentDate   payment date
     * @param recipientName recipient full name
     * @param sender        sender account {@see model.bank.BankAccount}
     * @param senderName    sender full name
     * @param paymentSum    payment sum
     */
    public Payment(PaymentStatus status, LocalDate paymentDate, BankAccount recipient, String recipientName, BankAccount sender, String senderName, double paymentSum) {
        this.status = status;
        this.paymentDate = paymentDate;
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.sender = sender;
        this.senderName = senderName;
        this.paymentSum = paymentSum;
    }

    /**
     * Yet another Payment constructor.
     * User to represent payment received from Database.
     *
     * @param paymentID     payment ID (created via database)
     * @param status        payment status {@see model.enums.PaymentStatus}
     * @param recipient     account {@see model.bank.BankAccount}
     * @param paymentDate   payment date
     * @param recipientName recipient full name
     * @param sender        sender account {@see model.bank.BankAccount}
     * @param senderName    sender full name
     * @param paymentSum    payment sum
     */
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

    public void updateStatus() {
        status = PaymentStatus.SENT;
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
