import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ServerAcceptor implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Paxos p;

    public ServerAcceptor(Socket socket, Paxos p) {
        this.socket = socket;
        this.p = p;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input="";
            String[] l;
            //Scanner reader= new Scanner(in);
            //out.println("Commands:\n- createPriest [port] [name]\n -show\n- commit [decree]\n- exit");
            sleep(1000);
            //out.print("Insert a command: ");
            while(true) {
                input = in.readLine();
                l = input.split(" ");
                System.out.println("ho letto " + input + " e " + l);
                if (l.length == 1) {
                    if (l[0].equals("show")) {
                        System.out.println("received show");
                        String res = p.show();
                        System.out.println("RESSSS" + res);
                        out.println(res);
<<<<<<< HEAD

                    }if (l[0].equals("restart")) {
                        System.out.println("received restart");
                        String res = p.show();
                        System.out.println("RESSSS" + res);
                        out.println(res);

                    } else {
=======
                    }
                    else if(l[0].equals("showAll")) {
                        System.out.println("received showAll");
                        ArrayList<String> res =  p.showAll();
                        System.out.println("RESSSS" + res.toString());
                        out.println(res.toString());
                    }
                    else {
>>>>>>> 2ebe8ce1f7207a101aee56366045d4a082332c7e
                        out.println("command doesn't exist.");
                    }
                } else if (l.length > 1) {
                    if (l[0].equals("commit")) {
                        System.out.println(input.substring(7));
                        p.startBallot(input.substring(7),"-1");
                    }
                    else if (l[0].equals("query")) {
                        System.out.println(l[0]);
                        String res = p.queryLog(l[1]);
                        System.out.println("Query: " + res);
                        out.println(res);
                    } else {
                        out.println("command doesn't exist.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
