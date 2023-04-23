package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Table(name = "TEAM")
public class Team_1vN {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    /** 이제 앞서 우리가 했던 N에 연관관계 주인이 아닌  1에 연관관계 주인으로 해줬다.*/
    @OneToMany
    @JoinColumn(name = "TEAM_ID"/*referenceColumnName =""*/)
    private List<Member_1vN> members = new ArrayList<>();
    /** 일대다 단방향의 경우에는 예외적으로 @JoinColumn이 자신이 아니라 대상 테이블에 있기 때문에 대상 테이블의 FK가 PK로 참조해야 하는 곳, 그러니까 결과적으로 자기 엔티티의 PK를 바라보게 됩니다.
     * referenceColumnName는  Team의 PK가 되겠지요*/

    public Team_1vN() {
    }

    public List<Member_1vN> getMembers() {
        return members;
    }

    public void setMembers(List<Member_1vN> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
