import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Paxos p=new Paxos();
        for(int i = 0; i < 4; i++){
            int port = 4000+i;
            String namedb = "p" + i;
            p.addPriest("localhost", port, namedb);
        }
        try {
            ServerSocket serverSocket = new ServerSocket(3999);
            //TODO: togli while true
            while(true){
                Socket s = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                String input="";
                String[] l;
                Scanner reader= new Scanner(in);
                //out.println("Commands:\n- createPriest [port] [name]\n -show\n- commit [decree]\n- exit");
                sleep(1000);
                //out.print("Insert a command: ");
                input=reader.nextLine();
                l=input.split(" ");
                if(l.length==1){
                    if(l[0].equals("show")){
                        String res = p.show();
                        System.out.println("RESSSS" + res);
                        out.println(res);

                    }
                    else{
                        out.println("command doesn't exist.");
                    }
                }
                else if(l.length>1){
                    if(l[0].equals("commit")) {
                        p.startBallot(input.substring(7));
                    }
                    else{
                        out.println("command doesn't exist.");
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
