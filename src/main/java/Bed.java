import java.io.Serializable;

public class Bed implements Serializable {

    private int bedId;
    private int wardId;
    private String status;
    private String forSex;
    private boolean hasSideRoom;

    public Bed(int bedId,int wardId,String status,String forSex,boolean hasSideRoom) {
        this.bedId=bedId;
        this.wardId=wardId;
        this.status=status;
        this.forSex=forSex;
        this.hasSideRoom=hasSideRoom;
    }

    public int getBedId(){return bedId;}
    public int getWardId(){return wardId;}
    public String getStatus(){return status;}
    public String getForSex(){return forSex;}
    public boolean getHasSideRoom(){return hasSideRoom;}
}
