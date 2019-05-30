import java.util.Iterator;
import java.util.LinkedList;

public class Paxos {
    LinkedList<Priest> group=new LinkedList<>();
    Priest president=null;

    public Paxos() {
    }

    public void addPriest(int port,String name ){
        Priest p=new Priest("localhost",port,name);
        if(president==null){
            president=p;
        }
        else {
            group.add(p);
        }
        p.listen();
        president.setGroup(group);
    }

    public void startBallot(String decree){
        if (president==null){
            System.out.println("Inizializzare un database...");
        }
        else {
            System.out.println("Ballot started...");
            president.startBallot(decree);
        }
    }
    public void show(){
        LinkedList<String> ret=null;
        if (president==null){
            System.out.println("Inizializzare un database...");
        }
        else {
            System.out.println( president.show());
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
