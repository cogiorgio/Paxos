import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StartPriest {
    public static void main(String[] args) {
        Priest p = new Priest("192.168.43.24", 4000,"a");
        LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("192.168.43.24",4001,"b"));
        group.add(new Priest("192.168.43.13",4002,"c"));
        group.add(new Priest("192.168.43.13",4003,"d"));
        /*LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("192.168.43.24",4001,"b"));
        group.add(new Priest("192.168.43.13",4002,"c"));
        group.add(new Priest("192.168.43.13",4003,"d"));*/
        p.setGroup(group);
        p.connect();
        p.restart();
        p.listen();
        while(true);
        /*String input="";
        String[] l;
        Scanner reader= new Scanner(System.in);
        System.out.println("Commands:\n- commit [decree]\n- exit");
        while(input!="exit"){
            System.out.print("Insert a command: ");
            input=reader.nextLine();
            l=input.split(" ");
            System.out.println(l[0]);
            if(l.length==1){
                if(l[0].equals("exit")){
                    System.out.println("database offline,goodbye.");
                    p.stopListening();
                    return;
                }
                else{
                    System.out.println("command doesn't exist.");
                }
            }
            else if(l.length>1){
                if(l[0].equals("commit")) {
                    System.out.println(input.substring(7));
                    p.startBallot(input.substring(7));
                }
                else{
                    System.out.println("command doesn't exists.]");
                }
            }
        }*/
    }
}
