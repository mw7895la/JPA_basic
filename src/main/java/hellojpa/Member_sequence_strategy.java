package hellojpa;

import javax.persistence.*;

//@Entity
/*@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50)      //TABLE 전략의 allocationSize도 동작 방식이 마찬가지다.*/
//@Table(name = "MEMBER")
public class Member_sequence_strategy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name = "name")
    private String username;

    public Member_sequence_strategy() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
