package hellojpa;

import javax.persistence.*;

//@Entity
@Table(name = "MEMBER")
public class Member_5 {

    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    /*@Column(name="TEAM_ID")
    private Long teamId;        //이렇게 teamId 즉, 테이블에 맞춰서 외래키 값을 그대로 가지고 있는 것.
*/
    @ManyToOne              //Member 입장에서 적는다. 나는 Many , Team 너는 One
    @JoinColumn(name = "TEAM_ID")       //관계를 할 때 Join해야 되는 컬럼이 뭐냐?
    private Team_5 team;    //어노테이션 없이 선언만 하면 에러난다. JPA에게 관계를 알려줘야 한다.
    //Member와 Team중에서 Member가 N이고  Team이 1이다.
    //team필드가 연관관계의 주인이다.
    /** @ManyToOne은 데이터베이스와 객체를 맵핑하기 위해 사용하는 애노테이션입니다. 이해하신대로 테이블에서 Member가 N, Team이 1인 것임을 알려주는 동시에 Member 테이블의 외래키(Member.TEAM_ID)와 Member 객체의 Team을 맵핑하는 역할을 합니다. */

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

    public Team_5 getTeam() {
        return team;
    }

    public void setTeam(Team_5 team) {
        this.team = team;
        team.getMembers().add(this);        //나 자신 인스턴스 Member를 넣어주고.
    }

/*    @Override
    public String toString() {
        return "Member_5{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", team=" + team +
                '}';
    }*/     // team.toString을 호출한다는 건데. Team 가서도 members안에 하나하나 있는 toString을 호출하게 된다. 즉 양쪽으로 무한 호출하게 된다. --> StackOverFlow 발생
}
