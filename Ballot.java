import java.util.LinkedList;
import java.util.List;

public class Ballot {
    private int voted=0;
    private int youngerBallot=0;
    private String decree;
    private LinkedList<Priest> quorum;
    private LinkedList<Priest> voting;
    private int number;


    public Ballot(String decree,LinkedList<Priest> quorum,LinkedList<Priest> voting,int number){
        this.decree=decree;
        this.quorum=quorum;
        this.voting=voting;
        this.number=number;
    }

    public synchronized void addVoting(Priest p){
        voting.add(p);
    }

    public synchronized int getVoted() {
        return voted;
    }

    public synchronized void setVoted(int voted) {
        this.voted = voted;
    }


    public String getDecree() {
        return decree;
    }

    public void setDecree(String decree) {
        this.decree = decree;
    }

    public LinkedList<Priest> getQuorum() {
        return quorum;
    }

    public void setQuorum(LinkedList<Priest> quorum) {
        this.quorum = quorum;
    }

    public LinkedList<Priest> getVoting() {
        return voting;
    }

    public void setVoting(LinkedList<Priest> voting) {
        this.voting = voting;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public int getYoungerBallot() {
        return youngerBallot;
    }

    public void setYoungerBallot(int youngerBallot) {
        this.youngerBallot = youngerBallot;
    }

    //
}
