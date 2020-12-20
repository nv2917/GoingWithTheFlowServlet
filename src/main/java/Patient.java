
import java.io.Serializable;
import java.sql.Timestamp;

public class Patient implements Serializable {

    private int id;
    private String nameInitials;
    private String currentLocation;
    private String sex;
    private Timestamp arrivalDateTime;
    private String initialDiagnosis;
    private boolean needsSideRoom;
    private boolean acceptedByMedicine;
    private String nextDestination;
    private Timestamp estimatedDateTimeOfNext;
    private boolean ttaSignedOff;
    private boolean suitableForDischargeLounge;
    private String transferRequestStatus;
    private boolean deceased;

    public Patient(String nameInitials,String sex,String initialDiagnosis,boolean needsSideRoom) {
        this.nameInitials = nameInitials;
        this.currentLocation = "A&E";
        this.sex = sex;
        this.initialDiagnosis = initialDiagnosis;
        this.needsSideRoom = needsSideRoom;
    }

    public Patient(int id, String nameInitials,String currentLocation,String sex,Timestamp arrivalTime, String initialDiagnosis,
                   boolean needsSideRoom,boolean acceptedByMedicine,String nextDestination,Timestamp estimatedTimeOfNext,
                   boolean ttaSignedOff,boolean suitableForDischargeLounge,String transferRequestStatus,boolean deceased) {
        this.id = id;
        this.nameInitials = nameInitials;
        this.currentLocation = currentLocation;
        this.sex = sex;
        this.arrivalDateTime = arrivalTime;
        this.initialDiagnosis = initialDiagnosis;
        this.needsSideRoom = needsSideRoom;
        this.acceptedByMedicine = acceptedByMedicine;
        this.nextDestination = nextDestination;
        this.estimatedDateTimeOfNext = estimatedTimeOfNext;
        this.ttaSignedOff = ttaSignedOff;
        this.suitableForDischargeLounge = suitableForDischargeLounge;
        this.transferRequestStatus = transferRequestStatus;
        this.deceased = deceased;
    }

    public int getId() {return id;}
    public String getNameInitials() {return nameInitials;}
    public String getCurrentLocation() {return currentLocation;}
    public String getSex() {return sex;}
    public Timestamp getArrivalDateTime() {return arrivalDateTime;}
    public String getInitialDiagnosis() {return initialDiagnosis;}
    public boolean getNeedsSideRoom() {return needsSideRoom;}
    public boolean getAcceptedByMedicine() {return acceptedByMedicine;}
    public String getNextDestination() {return nextDestination;}
    public Timestamp getEstimatedTimeOfNext() {return estimatedDateTimeOfNext;}
    public boolean getTtaSignedOff() {return ttaSignedOff;}
    public boolean getSuitableForDischargeLounge() {return suitableForDischargeLounge;}
    public String getTransferRequestStatus() {return transferRequestStatus;}
    public boolean getDeceased() {return deceased;}
}
