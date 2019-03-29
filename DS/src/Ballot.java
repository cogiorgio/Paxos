import java.util.List;

public class Ballot {
    private String decree;
    private List<Priest> quorum;
    private List<Priest> voting;
    private int number;

    public String getDecree() {
        return decree;
    }

    public void setDecree(String decree) {
        this.decree = decree;
    }

    public List<Priest> getQuorum() {
        return quorum;
    }

    public void setQuorum(List<Priest> quorum) {
        this.quorum = quorum;
    }

    public List<Priest> getVoting() {
        return voting;
    }

    public void setVoting(List<Priest> voting) {
        this.voting = voting;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //
}
