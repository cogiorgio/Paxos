import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StartPriest {
    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String dbName = args[2];
        Priest p = new Priest("ip", port,dbName);
        LinkedList<Priest> group = new LinkedList();
        try (BufferedReader br = new BufferedReader(new FileReader("group.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if(! (ip.equals(values[0])) && !(port == Integer.parseInt(values[1])))
                    group.add(new Priest(values[0],Integer.parseInt(values[1]),values[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("192.168.43.24",4001,"b"));
        group.add(new Priest("192.168.43.13",4002,"c"));
        group.add(new Priest("192.168.43.13",4003,"d"));*/
        p.setGroup(group);
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
