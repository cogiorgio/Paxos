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
    private int currentLog;
    private String dbName;
    private MongoClient mongoClient;
    //possibile ottimizzazione tra fat and slim per scegliere il quorum

    public Priest(String ip_address,int port,String dbName) {
        //TODO
        this.address = ip_address;
        this.port=port;
        nextBal = -1;
        prevVote = new Vote(0,"infinity");
        this.dbName=dbName;
        currentLog=-1;
        mongoClient = new MongoClient("localhost", 27017);
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
    public synchronized void startBallot(String decree){
        //ALLORA,all'inizio metto currentLog a -1 così posso distinguere
        //i database che non hanno mai fatto ballots
        //dobbiamo modificare queste modifiche e mettere un metodo synch per la modifica di current
        if(currentLog==-1){
            currentLog=0;
        }
        DB database = mongoClient.getDB(dbName);
        database.createCollection("LOG", null);
        mongoClient.getDatabaseNames().forEach(System.out::println);
        DBCollection collection = database.getCollection("LOG");
        String StringBallotNumber;
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("log",currentLog);
        DBCursor cursor=collection.find(whereQuery);
        //quì vedo se esiste già un log al currentLog
        //NOTA questo fatto si verifica perche alcuni database potrebbero essere più avanti ed hanno già iniziato ballot a log successivi
        //la consistenza è garantita dall'algo
        DBObject dbo=null;
        if(cursor.hasNext()){
            dbo=cursor.next();
            if(!(dbo.get("decree").toString()).equals("infinity")){
                //se troviamo il log al current log che esiste ed ha decree già fissato, aumentiamo current log e invitiamo a riprovare
                currentLog+=1;
                System.out.println("log già occupato,ritenta..");
                return;
            }
        }
        //se arriviamo quì significa che il current log a cui ci troviamo o ancora non esiste,oppure esiste
        //ma ancora non è stato scelto il decree
        if (lastTried==null && dbo==null){
            //caso in cui non esiste
            StringBallotNumber =port+ "" +0;
            //saving lastTried number into DB
            BasicDBObject document = new BasicDBObject();
            document.put("id", ""+this.port);
            document.put("log",currentLog+"");
            document.put("lastTried", StringBallotNumber);
            document.put("nextBallot",-1+"" );
            document.put("decree", "infinity");
            document.put("lastVotedBallot", "0");
            document.put("lastVotedDecree", "infinity");
            collection.insert(document);
            System.out.println(document);
        }else if(lastTried==null && dbo!=null){
            //caso in cui esiste ma ancora non ho iniziato ballot
            //è stato inizializzato da altri database ed ha solo il campo nextBallot rilevante
            //quindi lo aggiorno con le info necessarie per iniziare il ballot
            StringBallotNumber =port+""+0;
            //updating lastTried number into DB
            BasicDBObject query = new BasicDBObject();
            query.put("id", ""+this.port);
            query.put("log",currentLog);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("lastTried", StringBallotNumber);
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            System.out.println(collection.update(query, updateObject));
        }
        else {
            //caso in cui abbiamo già iniziato il ballot,e aumentiamo solo il numero di ballot
            StringBallotNumber =(lastTried.getNumber() +""+ 0);
            //updating lastTried number into DB
            BasicDBObject query = new BasicDBObject();
            query.put("id", ""+this.port);
            query.put("log",currentLog);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("lastTried",StringBallotNumber);
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            System.out.println(collection.update(query, updateObject));
        }
        //aggiorno last tried dopo aver aggiornato il db
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
                out.println("NextBallot/"+currentLog+"/"+lastTried.getNumber()+"/"+this.address+"/"+this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //decide the next ballot number (> lastTried) and send it
    public void NextBallot(String log,String ballot_num, String pAddress, String pPort){
        //controllo se il log è uguale a quello corrente,in tal caso svolgo normalmente
        //il fatto che sia uguale a quello corrente e last tried != null,mi da
        //la certezza che ho già l'oggetto log con un ballot avviato
        if(Integer.parseInt(log)==currentLog && lastTried!=null) {
            //svolgimento del log corrente
            if (parseInt(ballot_num) <= nextBal)
                //message ignored
                return;
            else {
                this.nextBal = (parseInt(ballot_num));
                PrintWriter out;
                try {
                    System.out.println("I'm " + this.port + "connecting to " + pPort);
                    Socket s = new Socket(pAddress, parseInt(pPort));
                    System.out.println("connected to " + pAddress + ":" + pPort);
                    out = new PrintWriter(s.getOutputStream(), true);
                    out.println("LastVote/" +log+"/"+ ballot_num + "/" + this.prevVote.getDecree() + "/" + this.prevVote.getBallotNumber() +
                            "/" + this.address + "/" + this.port);
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //in caso contrario o è un log passato oppure un log che ancora non esiste
        else{
            DB database = mongoClient.getDB(dbName);
            DBCollection collection = database.getCollection("LOG");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("log",log);
            DBCursor cursor=collection.find(whereQuery);
            DBObject dbo=null;
            //caso log precedente,do le informazioni richieste per poter aggiornare gli altri
            System.out.println(cursor);
            //se trova has next significa che è un log passato e dobbiamo solo aggiornare next bal
            //questa operazione serve ad aggiornare gli altri database e basta
            if(cursor.hasNext()) {
                dbo = cursor.next();
                System.out.println(dbo);
                if (parseInt(ballot_num) <= parseInt(dbo.get("nextBallot").toString())) {
                    //message ignored
                    return;
                } else {
                    //aggiorno il log
                    whereQuery.put("id", "" + this.port);
                    BasicDBObject newDocument = new BasicDBObject();
                    newDocument.put("nextBallot", ballot_num);
                    BasicDBObject updateObject = new BasicDBObject();
                    updateObject.put("$set", newDocument);
                    System.out.println(collection.update(whereQuery, updateObject));
                }
            }
            //questo è il caso in cui non esiste,equivale al caso in cui posso accettare tutto
            //anche se il database in questione è ancora indietro con i log,quando arriverà a questo log andrà avanti
            //l'ordine è mantenuto dall'algo
            else{
                    BasicDBObject document = new BasicDBObject();
                    document.put("id", ""+this.port);
                    document.put("log",log);
                    document.put("lastTried", "-1");
                    document.put("nextBallot",ballot_num);
                    document.put("decree", "infinity");
                    document.put("lastVotedBallot", "0");
                    document.put("lastVotedDecree", "infinity");
                    collection.insert(document);
                    System.out.println(document);
                    dbo=document;
                }
            PrintWriter out;
            try {
                System.out.println("I'm " + this.port + "connecting to " + pPort);
                Socket s = new Socket(pAddress, parseInt(pPort));
                System.out.println("connected to " + pAddress + ":" + pPort);
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("LastVote/"+log+"/" + ballot_num + "/" + dbo.get("lastVotedDecree").toString() + "/" + dbo.get("lastVotedBallot").toString() +
                        "/" + this.address + "/" + this.port);
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    //response to the next ballot message,b must be > nextBal or it is ignored, last vote must be equal prev vote
    public synchronized void LastVote(String log,String ballotNumber, String decree, String ballotVote, String address,String port){

        //NOTA->qui controllo che si tratti del mio current log altrimenti accanno
        //questa funzione è rimasta invariata perche agisce localmente sul currentLog
        if(Integer.parseInt(log)!=currentLog){
            return;
        }
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
                out.println("BeginBallot/" +log+"/"+ lastTried.getNumber() + "/" + lastTried.getDecree() + "/" + this.address
                        + "/" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void BeginBallot(String log,String ballotNumber, String decree, String address, String port){
        //qui ricevo il begin ballot,devo andare a prendere il log corrispondente ed eseguire l'algoritmo su quel log
        DB database = mongoClient.getDB(dbName);
        DBCollection collection = database.getCollection("LOG");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("log",log);
        DBCursor cursor=collection.find(whereQuery);
        DBObject dbo=cursor.next();
        //se il ballot è diverso dalla promessa del corrispettivo log accanno
        if(parseInt(ballotNumber) != Integer.parseInt(dbo.get("nextBallot").toString()))
            return;
        else{
            //aggiorno last voted
            whereQuery.put("id", "" + this.port);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("lastVotedBallot",ballotNumber );
            newDocument.put("lastVotedDecree",decree );
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            System.out.println(collection.update(whereQuery, updateObject));
            PrintWriter out;
            try {
                Socket s = new Socket(address, parseInt(port));
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("Voted/" +log+"/"+ ballotNumber + "/" + this.address + "/" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    //sends the vote,condition : b = nextBallot
    public void Voted(String log,String ballotNumber, String address, String port){
        if(Integer.parseInt(log)!=currentLog){
            return;
        }
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
        //se ho raggiunto il quorum mando un messaggio di success ed aggiorno il database
        System.out.println("ALL VOTED");
        DB database = mongoClient.getDB(dbName);
        DBCollection collection = database.getCollection("LOG");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("log",log);
        DBCursor cursor=collection.find(whereQuery);
        DBObject dbo=cursor.next();
        whereQuery.put("id", "" + this.port);
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("decree",lastTried.getDecree());
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);
        System.out.println(collection.update(whereQuery, updateObject));
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
                out.println("Success/" +log+"/"+ lastTried.getDecree() + "/" + this.address + "/" + this.port);
                s.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        lastTried=null;
        //NOTA-> aumento current log poichè ormai questo è finito
        currentLog+=1;

    }
    //sends a success message
    public void Success(String log,String decree){
        //ricevuto il messaggio success aggiorno il decree del log corrispondente
        System.out.println(decree);
        //commit to database
        DB database = mongoClient.getDB(dbName);
        DBCollection collection = database.getCollection("LOG");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("log",log);
        DBCursor cursor=collection.find(whereQuery);
        DBObject dbo=cursor.next();
        whereQuery.put("id", "" + this.port);
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("decree",decree);
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);
        System.out.println(collection.update(whereQuery, updateObject));
    }

    public synchronized void stopListening(){
        listening=0;
    }
}