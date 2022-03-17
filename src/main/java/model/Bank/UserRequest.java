package model.Bank;

import model.enums.RequestType;

public class UserRequest {
    private long requestID;
    private long userID;
    private long accountID;
    private RequestType type;

    public UserRequest() {
        requestID = -1;
    }

    public void createAccountUnblockingRequest(long accountID, long userID) {
        this.accountID = accountID;
        this.userID = userID;
        type = RequestType.ACCOUNT;
    }

    public void createUserUnblockingRequest(long userID) {
        this.userID = userID;
        accountID = -1;
        type = RequestType.USER;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getRequestID() {
        return requestID;
    }

    public long getUserID() {
        return userID;
    }

    public long getAccountID() {
        return accountID;
    }

    public RequestType getType() {
        return type;
    }
}
