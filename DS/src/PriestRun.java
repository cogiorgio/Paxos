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
            String[] l=s.split("\n");
            if(l[0] == "NextBallot"){
                pr.NextBallot(l[1],l[2],l[3]);
            }
            if(l[0] == "LastVote"){
                pr.LastVoteR(l[1],l[2],l[3],l[4]);
            }
            if(l[0] == "BeginBallot"){
                pr.BeginBallot();
            }
        }catch(Exception e){
            System.out.println("errore nella generazione del socket");
        }

    }

}
