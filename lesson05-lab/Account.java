public final class Account {

    private long balance;

    public Account(long balance) {
        this.balance = balance;
    }

    public final long getBalance() {
        return balance;
    }

    public final void setBalance(long balance) {
        this.balance = balance;
    }
}