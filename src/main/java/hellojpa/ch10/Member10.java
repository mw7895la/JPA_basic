package hellojpa.ch10;

import hellojpa.Team_5;
import hellojpa.ch9.Address;
import hellojpa.ch9.Period;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER")
public class Member10 {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    //기간 Period
    /*private LocalDateTime startDate;
    private LocalDateTime endDate;*/
    @Embedded
    private Period workPeriod;

    //주소
    /*private String city;
    private String street;
    private String zipcode;*/

    @Embedded
    private Address homeAddress;

   /* @Embedded       //임베디드 어노테이션만 적어주면 타입이 위와 같아서 에러난다.
    @AttributeOverrides({
            @AttributeOverride(name="city",
            column = @Column(name="WORK_CITY")),
            @AttributeOverride(name="street",
            column = @Column(name="WORK_STREET")),
            @AttributeOverride(name="zipcode",
            column = @Column(name="WORK_ZIPCODE"))
    })
    private Address workAddress;*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
