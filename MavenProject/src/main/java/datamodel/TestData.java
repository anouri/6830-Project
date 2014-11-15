package datamodel;
import javax.persistence.*;
  
@Entity
@Table
public class TestData {
    @Id
    @GeneratedValue
    private Long id;
 
    private String dataPoint;
     
//    @ManyToOne
//    private <some class object here>
 
    public TestData() {}
 
    public TestData(String dataPoint) {
        this.dataPoint = dataPoint;
    }
     
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getData() {
        return dataPoint;
    }
 
    public void setData(String dataPoint) {
        this.dataPoint = dataPoint;
    }
 
 
    @Override
    public String toString() {
        return "Datapoint [id=" + id + ", data=" + dataPoint + "]";
    }
 
}