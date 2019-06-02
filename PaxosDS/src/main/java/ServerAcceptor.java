import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

                    } else {
                        out.println("command doesn't exist.");
                    }
                } else if (l.length > 1) {
                    if (l[0].equals("commit")) {
                        System.out.println(input.substring(7));
                        p.startBallot(input.substring(7));
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
