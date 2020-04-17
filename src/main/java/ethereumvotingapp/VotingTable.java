package ethereumvotingapp;

public class VotingTable {

    Integer id;
    String name;
    Integer votes;

    public VotingTable(Integer id, String name, Integer votes) {
        this.id = id;
        this.name = name;
        this.votes = votes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
