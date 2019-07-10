import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import java.util.Scanner;



import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;
import static javax.swing.text.html.HTML.Tag.HEAD;

public class ClientApp {

    public static void main(String[] args) throws InterruptedException {

        try {
            Socket s = new Socket("192.168.43.24", 3999);
            PrintWriter outStream = new PrintWriter(s.getOutputStream(), true);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String input="";
            String[] l;
            Scanner reader= new Scanner(System.in);
            System.out.println("Commands:\n -show\n- commit [decree]\n- query [log]\n- exit");
            while(input!="exit") {
                System.out.println("Sono in attesa di comandi");
                sleep(1000);
                System.out.print("Insert a command: ");
                input = reader.nextLine();
                l = input.split(" ");
                if (l.length == 1) {
                    if (l[0].equals("exit")) {
                        System.out.println("Goodbye.");
                        return;
                    } else if (l[0].equals("show")) {
                        System.out.println("I'm sending show");
                        outStream.println("show");
                        //BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String res =inStream.readLine();
                        System.out.println("DB: " + res);
                    }else if (l[0].equals("restart")) {
                        System.out.println("I'm sending restart");
                        outStream.println("restart");
                        //BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String res =inStream.readLine();
                        System.out.println("DB: " + res);
                    }else if (l[0].equals("showAll")) {
                        System.out.println("I'm sending showAll");
                        outStream.println("showAll");
                        //BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String res =inStream.readLine();
                        System.out.println("DB: " + res);
                    } else {
                        System.out.println("command doesn't exist.");
                    }
                } else if (l.length > 1) {
                    if (l[0].equals("commit")) {
                        System.out.println("I'm sending commit");
                        outStream.println(input);
                        String res =inStream.readLine();
                        System.out.println(res);
                    }else if (l[0].equals("query")) {
                        System.out.println("I'm sending query for log " + l[1]);
                        outStream.println(input);
                        String res =inStream.readLine();
                        System.out.println("Query: " + res);
                    } else {
                        System.out.println("command doesn't exist.");
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}