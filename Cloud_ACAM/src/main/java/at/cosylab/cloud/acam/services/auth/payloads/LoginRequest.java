package at.cosylab.cloud.acam.services.auth.payloads;

public class LoginRequest {
    private String accountName;
    private String password;

    public LoginRequest(String accountName, String password) {
        this.accountName = accountName;
        this.password = password;
    }

    public LoginRequest() {
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
