package hellojpa.ch8;

import javax.persistence.*;

//@Entity
public class Member_8 {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)      //이렇게 하면 team을 프록시 객체로 조회한다는 것. 이말은 Member 클래스만 DB에서 조회한다는 것.
    @JoinColumn(name = "TEAM_ID")
    private Team_8 team;

    public Team_8 getTeam() {
        return team;
    }

    public void setTeam(Team_8 team) {
        this.team = team;
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
