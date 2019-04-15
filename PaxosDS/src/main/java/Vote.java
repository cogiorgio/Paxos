public class Vote {
    //private Ballot ballot;
    private int ballotNumber;
    private String decree;
    //private Priest priest;

    public Vote(int ballotNumber, String decree) {
        this.ballotNumber = ballotNumber;
        this.decree = decree;
    }

    public int getBallotNumber() {
        return ballotNumber;
    }

    public String getDecree() {
        return decree;
    }

    public void setBallotNumber(int ballotNumber) {
        this.ballotNumber = ballotNumber;
    }

    public void setDecree(String decree) {
        this.decree = decree;
    }
}
