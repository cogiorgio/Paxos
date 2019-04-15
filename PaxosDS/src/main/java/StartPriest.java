import java.util.LinkedList;

public class StartPriest {
    public static void main(String[] args) {
        Priest p = new Priest("localhost", 4001);
        LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("localhost",4000));
        group.add(new Priest("localhost",4002));
        p.setGroup(group);
        p.listen();
        try{
            System.in.read();
        }
        catch (Exception e){}

        //p.startBallot("secondoDecree");
    }
}
