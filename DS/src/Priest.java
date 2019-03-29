import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Priest {
    private int port;
    private int listening=0;
    //Preliminary Protocol
    private Vote last_vote; //LastVote sent by the priest
    private Ballot next_ballot;
    private List<Ballot> ballots;
    private List<Vote> votes;
    //Basic Protocol
    private Ballot lastTried; // last ballot that p tried to initiate.
    private Vote prevVote; //vote cast by p ub the highest ballot in which he voted.
    private Ballot nexBal; //largest value of b for which p has sent a LastVote(b,v) msg.


    //possibile ottimizzazione tra fat and slim per scegliere il quorum

    public Priest(int port, String ip_address) {
        this.port=port;
    }

    public synchronized Vote getPrev_vote() {
        return prev_vote;
    }

    public synchronized Ballot getNext_ballot() {
        return next_ballot;
    }

    public synchronized List<Ballot> getBallots() {
        return ballots;
    }


    public synchronized void setPrev_vote(Vote prev_vote) {
        this.prev_vote = prev_vote;
    }

    public synchronized void setNext_ballot(Ballot next_ballot) {
        this.next_ballot = next_ballot;
    }

    public synchronized void addBallot(Ballot b){
        this.ballots.add(b);
    }

    public void listen(){
        try {
            ServerSocket s = new ServerSocket(port);
            while(listening==1) {
                Socket client = s.accept();
                Thread t=new Thread(new PriestRunnable(client,this));
                t.start();
            }

        }catch(Exception e){};


    }
    //priest starts a ballot choosing its number,decree,quorum;
    public void startBallot(){

    }
    //priest choose a majority for the quorum
    public List<Priest> chooseQuorum(){
        return null;
    }

    //decide the next ballot number (> lastTried) and send it
    public void NextBallot(Ballot b){

    }

    //response to the next ballot message,b must be > nextBal or it is ignored, last vote must be equal prev vote
    public void LastVote(Ballot b,Vote v){

    }

    public void BeginBallot(Ballot b,String decree){

    }
    //sends the vote,condition : b = nextBallot
    public void Voted(Ballot b,Priest q){

    }
    //sends a success message
    public void Success(String decree){

    }

    public synchronized void stopListening(){
        listening=0;
    }
}
