package hellojpa.ch9;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Entity
public class Member_valuetype {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;
    @Embedded
    private Address homeAddress;

    @ElementCollection      /**이 어노테이션의 기본이 Fetch Lazy 전략이다.*/
    @CollectionTable(name = "FAVORITE_FOOD",joinColumns = @JoinColumn(name = "MEMBER_ID"))    //DB에 만들때 테이블 명
    @Column(name ="FOOD_NAME")      // 값이 하나고. 내가 정의한게 아니라서 이렇게 해도 테이블 만들때 이 이름으로 해준다. 아래 Address는 클래스가 따로 있으니. AttributeOverride하거나.. 클래스 가서 컬럼명 해주거나.
    private Set<String> favoriteFoods = new HashSet<>();

    //AddressEntity 생성 후 아래를 값타입으로 매핑하는게 아닌 Entity로 매핑한다.
    /*@ElementCollection
    @CollectionTable(name="ADDRESS",joinColumns = @JoinColumn(name = "MEMBER_ID"))      //외래키로 MEMBER_ID를 잡자.
    private List<Address> addressHistory = new ArrayList<>();*/

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)      //cascade = all  연관된 엔티티까지 영속성 전이 , 고아=true -> addressHistory 컬렉션으로 하나 지우면 실제 AddressEntity테이블에서 delete 발생.
    @JoinColumn(name = "MEMBER_ID")     //1:N 단방향 매핑
    private List<AddressEntity> addressHistory = new ArrayList<>();

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

/*    public List<Address> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<Address> addressHistory) {
        this.addressHistory = addressHistory;
    }*/

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
