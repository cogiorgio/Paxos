import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class PriestRunnable implements Runnable {
    private Priest pr;
    public PriestRunnable(Priest pr){
        this.pr=pr;
    }
    public void run() {
        try {
            ServerSocket s = new ServerSocket(pr.port);
            System.out.println("Priest listening on port:" + pr.port);
            while(pr.listening==1) {
               /*double random=Math.random();
                random=random*10000;
                sleep((long) random);*/
                Socket client = s.accept();
                Thread t = new Thread(new PriestRun(client,pr));
                t.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        };
    }
}
