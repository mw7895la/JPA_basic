package hellojpa.ch8;

import hellojpa.Member_5;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Team_8 {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    //mappedBy="team"  team은  Member_5의 Team타입의 필드 명이다. Member_5의 team 필드와 같이 걸려있다는 뜻. 난 team 필드와 매핑이 되어있다.
    @OneToMany(mappedBy="team")//아까 Member에서Team으로 가는건 N:1이다. 그러면 반대로 Team에서 Member로 가는건 1:N이다.
    private List<Member_8> members = new ArrayList<>();   //초기화를 해둔다. 이러면 add할때 NPE가 뜨지 않는다.

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

    public List<Member_8> getMembers() {
        return members;
    }

    public void setMembers(List<Member_8> members) {
        this.members = members;
    }
}
