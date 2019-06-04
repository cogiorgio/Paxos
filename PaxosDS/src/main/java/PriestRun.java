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
            System.out.println("waiting for msg");
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s=in.readLine();
            String[] l=s.split("/");
            System.out.println("ho letto " + s);
            if(l[0].equals("StartBallot")){
                System.out.println("Received " + l);
                pr.startBallot(l[1]);
            }
            if(l[0].equals("NextBallot")){
                System.out.println("Nextballot");
                pr.NextBallot(l[1],l[2],l[3],l[4]);
            }
            if(l[0].equals("LastVote")){
                pr.LastVote(l[1],l[2],l[3],l[4],l[5],l[6]);
            }
            if(l[0].equals("BeginBallot")){
                pr.BeginBallot(l[1],l[2],l[3],l[4],l[5]);
            }
            if(l[0].equals("Voted")){
                pr.Voted(l[1],l[2],l[3],l[4]);
            }
            if(l[0].equals("Success")){
                pr.Success(l[1],l[2]);
            }
            if(l[0].equals("Show")){
                LinkedList<String> res = pr.show();
                out.println(res.toString());
            }
            if(l[0].equals("Query")){
                String res = pr.queryLog(l[1]);
                out.println(res);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
