import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PriestRunnable implements Runnable {
    private Priest pr;
    public PriestRunnable(Priest pr){
        this.pr=pr;
    }
    public void run() {
        try {
            ServerSocket s = new ServerSocket(pr.port);
            while(pr.listening==1) {
                Socket client = s.accept();
            }
        }catch(Exception e){
            System.out.println("errore nella generazione del socket");
        };
    }
}
