package hellojpa.ch10;

import hellojpa.Member_5;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Table(name = "TEAM")
public class Team10 {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    //mappedBy="team"  team은  Member_5의 Team타입의 필드 명이다. Member_5의 team 필드와 같이 걸려있다는 뜻. 난 team 필드와 매핑이 되어있다.
    @OneToMany(mappedBy="team")//아까 Member에서Team으로 가는건 N:1이다. 그러면 반대로 Team에서 Member로 가는건 1:N이다.
    private List<Member10> members = new ArrayList<>();   //초기화를 해둔다. 이러면 add할때 NPE가 뜨지 않는다.

    //즉, 나는(members) 필드 team에 의해서 관리가 된다. 연관관계의 주인은 Member_5 클래스의 team 필드다.


    //연관관계 편의 메서드를 이번엔 Team_5에 넣었다. 그러면 Member에 있던건 지워줘야 한다. 편한대로 해라.
/*    public void addMember(Member_5 member){
        member.setTeam(this);
        members.add(member);
    }*/

    public List<Member10> getMembers() {
        return members;
    }

    public void setMembers(List<Member10> members) {
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
