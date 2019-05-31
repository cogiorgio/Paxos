import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class Paxos {
    LinkedList<Priest> group=new LinkedList<>();
    Priest president=null;

    public Paxos() {
    }

    public void addPriest(String ip, int port,String name ){
        Priest p=new Priest(ip,port,name);
        if(president==null){
            president=p;
        }
        else {
            group.add(p);
        }
        //p.listen();
        president.setGroup(group);
    }

    public void startBallot(String decree) throws IOException {
        if (president==null){
            System.out.println("Inizializzare un database...");
        }
        else {
            Socket s = new Socket(president.getAddress(), president.getPort());
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println("StartBallot/" + decree);
            //System.out.println("Ballot started...");
            //president.startBallot(decree);
        }
    }
    public String show() throws IOException {
        LinkedList<String> ret=null;
        if (president==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else {
            //TODO: SPAWNA THREAD
            Socket s = new Socket(president.getAddress(), president.getPort());
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println("Show");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String input =in.readLine();
            System.out.println("SHOW PAXOS: " + input);
            return input;
        }
    }
    public void stopListening(){
        Iterator<Priest> i=group.iterator();
        while(i.hasNext()){
            i.next().stopListening();
        }
        president.stopListening();
    }
}
