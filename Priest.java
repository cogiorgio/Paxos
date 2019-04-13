import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Priest {
    private LinkedList<Priest> group;
    public int port;
    private String address;
    public int listening=0;
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
        return prevVote;
    }

    public synchronized Ballot getNext_ballot() {
        return next_ballot;
    }

    public synchronized List<Ballot> getBallots() {
        return ballots;
    }


    public synchronized void setPrev_vote(Vote prev_vote) {
        this.prevVote = prev_vote;
    }

    public synchronized void setNext_ballot(Ballot next_ballot) {
        this.next_ballot = next_ballot;
    }

    public synchronized void addBallot(Ballot b){
        this.ballots.add(b);
    }

    public void listen(){
                Thread t=new Thread(new PriestRunnable(this));
                t.start();
    }
    //priest starts a ballot choosing its number,decree,quorum;
    public void startBallot(String decree){
        int BallotNumber;
        if (lastTried==null){
            BallotNumber=0;
        }else{
            BallotNumber=+ lastTried.getNumber();
        }
        LinkedList<Priest> quorum=chooseQuorum();
        Ballot b=new Ballot(decree,quorum,this.group,BallotNumber);
        this.lastTried=b;
        Iterator<Priest> i=quorum.iterator();
        PrintWriter out;
        while(i.hasNext()){
            Priest p=i.next();
            try {
                Socket s = new Socket(p.address, this.port);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("NextBallot\n"+lastTried.getNumber()+"\n"+this.address+"\n"+this.port);
                s.close();
            }catch(Exception e){}
        }


    }
    //priest choose a majority for the quorum
    public LinkedList<Priest> chooseQuorum(){
        LinkedList<Priest> quorum=new LinkedList<Priest>();
        Iterator<Priest> i= group.iterator();
        int c=0;
        while(i.hasNext()){
            if(c%2==0){
                quorum.add(i.next());
            }
        }
        return quorum;
    }

    //decide the next ballot number (> lastTried) and send it
    public void NextBallot(Ballot b){

    }

    //response to the next ballot message,b must be > nextBal or it is ignored, last vote must be equal prev vote
    public void LastVote(Ballot b,Vote v){

    }
    public synchronized void LastVoteR(String decree, String ballotNumber,String address,String port){

        //check if it is an older ballot
        if(parseInt(ballotNumber)!=lastTried.getNumber()){
            return;
        }
        //check if quorum is already been reached
        if(this.lastTried.getVoted()==1){
            return;
        }
        //check if it already voted
        Iterator b=lastTried.getVoting().iterator();
        Priest p;
        while(b.hasNext()){
            p=(Priest)b.next();
            if(p.address==address && p.port==p.port){
                return;
            }
        }
        //the decree must be the youngest one between priests that already voted
        if(decree!=null){
            if(lastTried.getVoting().isEmpty()){
                lastTried.setDecree(decree);
                lastTried.setYoungerBallot(parseInt(ballotNumber));
            }
            else {
                if(parseInt(ballotNumber)<lastTried.getYoungerBallot()){
                    lastTried.setYoungerBallot(parseInt(ballotNumber));
                }
            }

        }
        //add new priest to voting ones
        lastTried.addVoting(new Priest(parseInt(port),address));

        //check if all quorum voted,if true send beginBallot
        LinkedList<Priest> l=lastTried.getVoting();
        b=lastTried.getQuorum().iterator();
        while(b.hasNext()){
            p=(Priest)b.next();
            if(!l.contains(p)){
                return;
            }
        }

        //if it arrives here it means that we can send our beginBallot and end this step
        lastTried.setVoted(1);
        b=lastTried.getQuorum().iterator();
        PrintWriter out;
        while(b.hasNext()){
            p=(Priest)b.next();
            try {
                Socket s = new Socket(p.address, this.port);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("BeginBallot\n"+lastTried.getNumber()+"\n"+lastTried.getDecree()+"\n"+this.address+"\n"+this.port);
                s.close();
            }catch(Exception e){}
        }




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
