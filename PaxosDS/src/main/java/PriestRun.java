import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class PriestRun implements Runnable {
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private Priest pr;
    public PriestRun(Socket sock,Priest pr){
        this.sock=sock;
        this.pr=pr;
    }
    public void run(){
        try {
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s=in.readLine();
            System.out.println("String read:" + s);
            String[] l=s.split("-");
            if(l[0].equals("NextBallot")){
                System.out.println("I'm "+ pr.port+  " NextBallot n "+ l[1] + " from " + l[2] + ":" + l[3] );
                pr.NextBallot(l[1],l[2],l[3]);
            }
            if(l[0].equals("LastVote")){
                System.out.println("LastVote from " + l[4] + ":" + l[5]);
                pr.LastVote(l[1],l[2],l[3],l[4],l[5]);
            }
            if(l[0].equals("BeginBallot")){
                pr.BeginBallot(l[1],l[2],l[3],l[4]);
            }
            if(l[0].equals("Voted")){
                pr.Voted(l[1],l[2],l[3]);
            }
            if(l[0].equals("Success")){
                pr.Success(l[1]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
