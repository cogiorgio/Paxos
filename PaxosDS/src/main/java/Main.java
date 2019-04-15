import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        Priest p = new Priest("localhost", 4000);
        LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("localhost",4001));
        group.add(new Priest("localhost",4002));
        p.setGroup(group);
        p.listen();
        p.startBallot("primoDecree");
    }
}
