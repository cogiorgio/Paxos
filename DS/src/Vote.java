public class Vote {
    private Ballot ballot;
    private String decree;
    private Priest priest;

    public Boolean isBefore(Vote v1){
        return this.ballot.getNumber()<=v1.ballot.getNumber();
    }
}
