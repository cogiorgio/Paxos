import java.util.LinkedList;
import java.util.Scanner;

public class StartPriest3 {
        public static void main(String[] args) {
            Priest p = new Priest("172.20.10.10", 4002,"c");
            LinkedList<Priest> group = new LinkedList();
            group.add(new Priest("172.20.10.5",4000,"a"));
            group.add(new Priest("172.20.10.5",4001,"b"));
            group.add(new Priest("172.20.10.10",4003,"d"));
            p.setGroup(group);
            p.connect();
            p.listen();
            while(true);
            /*String input="";
            String[] l;
            Scanner reader= new Scanner(System.in);
            System.out.println("Commands:\n- commit [decree]\n- exit \n-show");
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
