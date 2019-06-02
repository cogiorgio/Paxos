import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Paxos {
    //LinkedList<Priest> group=new LinkedList<>();
    //Priest president=null;
    HashMap<String, Integer> group = new HashMap<>();
    String presidentIp;
    Integer presidentPort;

    public Paxos() {
        this.presidentIp = null;
        this.presidentPort = 0;
    }

    public void addPriest(String ip, int port,String name ){
        //Priest p=new Priest(ip,port,name);
        if(presidentIp==null){
            presidentIp= ip;
            presidentPort = port;
        }
        else {
            group.put(ip,port);
        }
        //p.listen();
        //president.setGroup(group);
    }

    public void startBallot(String decree) throws IOException {
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
        }
        else {
            System.out.println("sending startBallot to " + presidentIp + ":" + presidentPort);
            Socket s = new Socket(presidentIp, presidentPort);
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println("StartBallot/" + decree);
            //System.out.println("Ballot started...");
            //president.startBallot(decree);
        }
    }
    public String show() throws IOException {
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else {
            //TODO: SPAWNA THREAD
            Socket s = new Socket(presidentIp, presidentPort);
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            System.out.println("sending show  to " + presidentIp + ":" + presidentPort);
            out.println("Show");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String input =in.readLine();
            System.out.println("SHOW PAXOS: " + input);
            return input;
        }
    }
    //A COSA SERVE?
    /*public void stopListening(){
        Iterator<Priest> i=group.iterator();
        while(i.hasNext()){
            i.next().stopListening();
        }
        president.stopListening();
    }*/
}
