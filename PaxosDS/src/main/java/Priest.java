import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    //Basic Protocol
    private Ballot lastTried; // last ballot that p tried to initiate.
    private Vote prevVote; //vote cast by p on the highest ballot in which he voted.
    private int nextBal; //largest value of b for which p has sent a LastVote(b,v) msg.

    //possibile ottimizzazione tra fat and slim per scegliere il quorum

    public Priest(String ip_address,int port) {
        //TODO
        this.address = ip_address;
        this.port=port;
        nextBal = -1;
        prevVote = new Vote(-1,"infinity");
    }

    public void setGroup(LinkedList<Priest> group) {
        this.group = group;
    }

    public synchronized Vote getPrev_vote() {
        return prevVote;
    }

    public synchronized void setPrev_vote(Vote prev_vote) {
        this.prevVote = prev_vote;
    }

    public void listen(){
        this.listening = 1;
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
        LinkedList<Priest> quorum = chooseQuorum();
        Ballot b = new Ballot(decree,quorum,this.group,BallotNumber);
        this.lastTried=b;
        Iterator<Priest> i = quorum.iterator();
        PrintWriter out;
        while(i.hasNext()){
            Priest p = i.next();
            System.out.println("Priest " + p.address + ":" + p.port);
            try {
                Socket s = new Socket(p.address, p.port);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("NextBallot-"+lastTried.getNumber()+"-"+this.address+"-"+this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    //TODO
    //priest choose a majority for the quorum
    public LinkedList<Priest> chooseQuorum(){
        LinkedList<Priest> quorum = new LinkedList<Priest>();
        Iterator<Priest> i = group.iterator();
        int c=0;
        while(i.hasNext()){
            if(c%2==0){
                quorum.add(i.next());
            }
            c++;
        }
        return quorum;
    }

    //decide the next ballot number (> lastTried) and send it
    public void NextBallot(String ballot_num, String pAddress, String pPort){
        if(parseInt(ballot_num) <= nextBal)
            //message ignored
            return;
        else{
            this.nextBal = (parseInt(ballot_num));
            PrintWriter out;
            try {
                System.out.println("I'm " + this.port + "connecting to " + pPort);
                Socket s = new Socket(pAddress, parseInt(pPort));
                System.out.println("connected to " + pAddress + ":" + pPort);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("LastVote-" + ballot_num + "-" + this.prevVote.getDecree() + "-" + this.prevVote.getBallotNumber() +
                                "-" + this.address + "-" + this.port);
                s.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    //response to the next ballot message,b must be > nextBal or it is ignored, last vote must be equal prev vote
    public synchronized void LastVote(String ballotNumber, String decree, String ballotVote, String address,String port){

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
            if(p.address==address && p.port == parseInt(port)){
                return;
            }
        }
        //the decree must be the youngest one between priests that already voted B3 condition
        //usa vote.ballot come ultimo voto per scegliere il decree
        if(decree!=null){
            if(lastTried.getVoting().isEmpty()){
                lastTried.setDecree(decree);
                lastTried.setYoungerBallot(parseInt(ballotVote));
            }
            else {
                if(parseInt(ballotNumber)<lastTried.getYoungerBallot()){
                    lastTried.setYoungerBallot(parseInt(ballotVote));
                }
            }
        }
        //add new priest to voting ones
        for(Priest priest : this.group){
            if (priest.address.equals(address) && priest.port == parseInt(port))
                lastTried.addVoting(priest);
        }
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
        //this also empty group, why??
        LinkedList<Priest> temp = (LinkedList<Priest>) this.group.clone();
        lastTried.emptyVoting();
        this.group = temp;
        b=lastTried.getQuorum().iterator();
        PrintWriter out;
        while(b.hasNext()){
            p=(Priest)b.next();
            try {
                Socket s = new Socket(p.address, p.port);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("BeginBallot-" + lastTried.getNumber() + "-" + lastTried.getDecree() + "-" + this.address
                            + "-" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void BeginBallot(String ballotNumber, String decree, String address, String port){
        if(parseInt(ballotNumber) != nextBal)
            return;
        else{
            this.prevVote = new Vote(parseInt(ballotNumber), decree);
            PrintWriter out;
            try {
                Socket s = new Socket(address, parseInt(port));
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("Voted-" + ballotNumber + "-" + this.address + "-" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    //sends the vote,condition : b = nextBallot
    public void Voted(String ballotNumber, String address, String port){
        //check if it is an older ballot
        if(parseInt(ballotNumber)!=lastTried.getNumber()){
            return;
        }
        System.out.println("NOT OLD BALLOT");

        //check if it already voted
        Iterator b=lastTried.getVoting().iterator();
        Priest p;
        while(b.hasNext()){
            p=(Priest)b.next();
            if(p.address==address && p.port== parseInt(port)){
                return;
            }
        }
        System.out.println("NOT ALREADY VOTED");

        //add new priest to voting ones
        //group cosa sarebbe? se metto lastTried.voting = a this.group è giusto?
        for(Priest priest : this.group){
            if (priest.address.equals(address) && priest.port == parseInt(port))
                lastTried.addVoting(priest);
        }

        //check if all quorum voted,if true send beginBallot
        LinkedList<Priest> l=lastTried.getVoting();
        b=lastTried.getQuorum().iterator();
        while(b.hasNext()){
            p=(Priest)b.next();
            if(!l.contains(p)){
                return;
            }
        }
        System.out.println("ALL VOTED");

        //if it arrives here it means that we can send our Success and end this step
        lastTried.setVoted(1);
        lastTried.emptyVoting();
        b=lastTried.getQuorum().iterator();
        PrintWriter out;
        while(b.hasNext()){
            p=(Priest)b.next();
            try {
                Socket s = new Socket(p.address, p.port);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("Success-" + lastTried.getDecree() + "-" + this.address + "-" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    //sends a success message
    public void Success(String decree){
        System.out.println(decree);
        //commit to database
    }

    public synchronized void stopListening(){
        listening=0;
    }
}
