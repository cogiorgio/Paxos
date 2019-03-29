import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PriestRunnable implements Runnable {
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private Priest pr;
    public PriestRunnable(Socket sock,Priest pr){
      this.sock=sock;
    }
    public void run() {
        try {
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s=in.readLine();
            if(s.charAt(0)=='1'){
                
            }
        }catch(Exception e){
            System.out.println("errore nella generazione del socket");
        };
    }
}
