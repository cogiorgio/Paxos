import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Paxos {
    //LinkedList<Priest> group=new LinkedList<>();
    //Priest president=null;
    //HashMap<String, Integer> group = new HashMap<>();
    List<Pair<String,Integer>> group = new ArrayList<Pair<String,Integer>>();
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
            Pair pair = new Pair(ip,port);
            group.add(pair);
        }
        //p.listen();
        //president.setGroup(group);
    }
    public void switchPresident(){
        for(Pair<String, Integer> pair:group){
            if(this.presidentPort != pair.port && !this.presidentIp.equals(pair.ip)) {
                this.presidentIp = pair.ip;
                this.presidentPort = pair.port;
                break;
            }
        }
    }

    public String startBallot(String decree,String queryLog) {
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
        }
        else {
            System.out.println("sending startBallot to " + presidentIp + ":" + presidentPort);
            Socket s = null;
            try {
                s = new Socket(presidentIp, presidentPort);
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                out.println("StartBallot/" + decree+"/"+queryLog);
                //System.out.println("Ballot started...");
                //president.startBallot(decree);
                //s.close();
            } catch (IOException e) {
                    this.switchPresident();
                    System.out.println(this.presidentIp + " "+ this.presidentPort);
                    e.printStackTrace();
                    return "Please restart";
            }
        }
        return "Commit completed";
    }
    public String show()  {
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else {
            //TODO: SPAWNA THREAD
            Socket s = null;
            try {
                s = new Socket(presidentIp, presidentPort);
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                System.out.println("sending show  to " + presidentIp + ":" + presidentPort);
                out.println("Show");
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String input =in.readLine();
                System.out.println("SHOW PAXOS: " + input);
                s.close();
                return input;
            } catch (IOException e) {
                this.switchPresident();
                System.out.println(this.presidentIp + " "+ this.presidentPort);
                e.printStackTrace();
                return "Please restart";
            }
        }
    }

    public String reset(){
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else {
            //TODO: SPAWNA THREAD
            Socket s = null;
            try {
                s = new Socket(presidentIp, presidentPort);
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                System.out.println("sending restart  to " + presidentIp + ":" + presidentPort);
                out.println("Restart");
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String input =in.readLine();
                System.out.println("RRESTART PAXOS: " + input);
                s.close();
                return input;
            } catch (IOException e) {
                this.switchPresident();
                System.out.println(this.presidentIp + " "+ this.presidentPort);
                e.printStackTrace();
                return "Please restart";
            }
        }
    }
    public String queryLog(String queryLog)  {
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else {
            Socket s = null;
            try {
                s = new Socket(presidentIp, presidentPort);
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                System.out.println("sending query for " + queryLog + " to " + presidentIp + ":" + presidentPort);
                out.println("Query/"+queryLog);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String input =in.readLine();
                System.out.println("Query: " + input);
                return input;
            } catch (IOException e) {
                this.switchPresident();
                System.out.println(this.presidentIp + " "+ this.presidentPort);
                e.printStackTrace();
                return "Please restart";
            }
        }
    }
    public ArrayList showAll(){
        if (presidentIp==null){
            System.out.println("Inizializzare un database...");
            return null;
        }
        else{
            ArrayList<String> res = new ArrayList<>();
            res.add(show());
            group.forEach(elem ->{
              try {
                  System.out.println(elem.getIp());
                  Socket s = new Socket(elem.getIp(), elem.getPort());
                  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                  System.out.println("sending show  to " + elem.getIp() + ":" + elem.getPort());
                  out.println("Show");
                  BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                  String input =in.readLine();
                  System.out.println("SHOW PAXOS: " + input);
                  res.add(input);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          });
            return res;
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
    class Pair<L,R> {
        private L ip;
        private R port;
        public Pair(L ip, R port){
            this.ip = ip;
            this.port = port;
        }
        public L getIp(){ return ip; }
        public R getPort(){ return port; }
        public void setIp(L ip){ this.ip = ip; }
        public void setPort(R port){ this.port = port; }
    }
}
