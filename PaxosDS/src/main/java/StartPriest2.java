import java.util.LinkedList;

public class StartPriest2 {
    public static void main(String[] args) {
        Priest p = new Priest("localhost", 4002);
        LinkedList<Priest> group = new LinkedList();
        group.add(new Priest("localhost",4000));
        group.add(new Priest("localhost",4001));
        p.setGroup(group);
        p.listen();
        //p.startBallot("terzoDecree");
    }
}
