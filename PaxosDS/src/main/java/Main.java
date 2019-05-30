import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Paxos p=new Paxos();
        String input="";
        String[] l;
        Scanner reader= new Scanner(System.in);
        System.out.println("Commands:\n- createPriest [port] [name]\n -show\n- commit [decree]\n- exit");
        while(input!="exit"){
            sleep(1000);
            System.out.print("Insert a command: ");
            input=reader.nextLine();
            l=input.split(" ");
            if(l.length==1){
                if(l[0].equals("exit")){
                    System.out.println("database offline,goodbye.");
                    p.stopListening();
                    return;
                }
                else if(l[0].equals("show")){
                    p.show();
                }
                else{
                    System.out.println("command doesn't exist.");
                }
            }
            else if(l.length>1){
                if(l[0].equals("createPriest")){
                    p.addPriest(parseInt(l[1]),l[2]);
                }
                else if(l[0].equals("commit")) {
                    p.startBallot(input.substring(7));
                }
                else{
                    System.out.println("command doesn't exist.");
                }
            }

        }
    }
}
