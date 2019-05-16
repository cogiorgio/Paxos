import com.google.gson.Gson;
import com.mongodb.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
    private int trust=0;
    //possibile ottimizzazione tra fat and slim per scegliere il quorum

    public Priest(String ip_address,int port) {
        //TODO
        this.address = ip_address;
        this.port=port;
        nextBal = -1;
        prevVote = new Vote(0,"infinity");
    }

    public synchronized int getTrust() {
        return trust;
    }

    public synchronized void addTrust(){
        this.trust+=1;
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
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("PriestDB");
        database.createCollection("priests", null);
        mongoClient.getDatabaseNames().forEach(System.out::println);
        DBCollection collection = database.getCollection("priests");
        String StringBallotNumber;
        if (lastTried==null){
            StringBallotNumber = port + "0";
            //saving lastTried number into DB
            BasicDBObject document = new BasicDBObject();
            document.put("id", ""+this.port);
            document.put("lastTried", StringBallotNumber);
            collection.insert(document);
        }else {
            StringBallotNumber = ""+(lastTried.getNumber() + 1);
            //updating lastTried number into DB
            BasicDBObject query = new BasicDBObject();
            query.put("id", ""+this.port);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("lastTried", StringBallotNumber);
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            collection.update(query, updateObject);
        }


        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("id", ""+this.port);
        DBCursor cursor = collection.find(searchQuery);

        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
        int BallotNumber = parseInt(StringBallotNumber);
        Ballot b = new Ballot(decree,new LinkedList<Priest>(),BallotNumber);
        this.lastTried=b;
        Iterator<Priest> i = this.group.iterator();
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
        ArrayList<Integer> ord=new ArrayList<>();
        int c=0;
        int length=0;
        while(i.hasNext()){
            ord.add(i.next().getTrust());
            length+=1;
        }
        Collections.sort(ord);
        int left;
        if(length%2==0){
            left=length/2-1;
        }
        else left=length/2;
        List<Integer> ordered=ord.subList(left,length);
        i = group.iterator();
        while(i.hasNext()){
            Priest p=i.next();
            if(ordered.contains(p.getTrust())){
                quorum.add(p);
                ordered.remove(new Integer(p.getTrust()));
                length-=1;
            }
            if(ordered.isEmpty()){
                return quorum;
            }
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
        Iterator b=lastTried.getQuorum().iterator();
        Priest p;
        while(b.hasNext()){
            p=(Priest)b.next();
            if(p.address==address && p.port == parseInt(port)){
                return;
            }
        }
        for(Priest priest : this.group) {
            if (priest.address.equals(address) && priest.port == parseInt(port)) {
                lastTried.addQuorum(priest);
                priest.addTrust();
            }
        }
        //the decree must be the youngest one between priests that already voted B3 condition
        //usa vote.ballot come ultimo voto per scegliere il decree
        if(!decree.equals("infinity")){
            if (lastTried.getQuorum().isEmpty()) {
                    lastTried.setDecree(decree);
                    lastTried.setYoungerBallot(parseInt(ballotVote));
                } else {
                    if (parseInt(ballotNumber) > lastTried.getYoungerBallot()) {
                        lastTried.setYoungerBallot(parseInt(ballotVote));
                        lastTried.setDecree(decree);
                    }
            }

        }
        if(lastTried.getQuorum().size()<this.group.size()/2+1){
            return;
        }
        //if it arrives here it means that we can send our beginBallot and end this step
        lastTried.setVoted(1);
        //this also empty group, why??
        //LinkedList<Priest> temp = (LinkedList<Priest>) this.group.clone();
        //lastTried.emptyVoting();
        //this.group = temp;
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
