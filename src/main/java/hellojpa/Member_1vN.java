package hellojpa;

import javax.persistence.*;

//@Entity
@Table(name = "MEMBER")
public class Member_1vN {

    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    /**강의자료 16장  일대다 양방향 관계를 억지로 만든다. 인서트나 업데이트를 안하는 것이라 읽기만 한다.
     * Team의 members 리스트가 연관관계 주인을 계속 행세하고, Member의 team도 연관관계 주인처럼 만들었지만 읽기전용으로 한 것. - 양방향 매핑처럼 되었다.
     * */
    /*@ManyToOne
    @JoinColumn(name="TEAM_ID",insertable = false,updatable = false)
    private Team_1vN team;*/

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
