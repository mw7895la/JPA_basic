package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "MEMBER")
public class Member_Locker {


    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

/*    @ManyToMany
    @JoinTable(name = "MEMBER_PRODUCT")
    private List<Product> products = new ArrayList<>();*/

    public Locker getLocker() {

        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
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
}
