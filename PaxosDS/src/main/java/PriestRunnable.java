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
            System.out.println("Priest listening on" + pr.getAddress()+ ":" + pr.port);
            while(pr.listening==1) {
               /*double random=Math.random();
                random=random*10000;
                sleep((long) random);*/
                System.out.println("before accept");
                Socket client = s.accept();
                System.out.println("after accept");
                Thread t = new Thread(new PriestRun(client,pr));
                t.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        };
    }
}
