package hellojpa;

import javax.persistence.*;

//@Entity
//@Table(name = "MEMBER")
//@SequenceGenerator(name="member_seq_generator", sequenceName = "member_seq")
public class Member_primary {
    /** 기본 키 매핑 어노테이션 용 클래스. 값을 자동으로 할당하는 어노테이션 @GeneratedValue*/
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // int는 애매하고 Integer를 쓰는데 이것도 애매하다 10억넘으면 한바퀴 돌기떄문에.. 그래서 Long을 쓰자.
    //GenerationType.AUTO DB 방언에 맞춰서 자동으로 생성
    //GenerationType.IDENTITY - MySQL에서 주로 사용 기본키 생성을 데이터베이스에 위임하는 것. 애플리케이션에서 우리가 값을 넣으면 안돼.
    //GenerationType.SEQUENCE SEQUENCE 오브젝트를 생성하고 그걸 통해서 자동 생성  (오라클)

    @Column(name="name",nullable = false)
    private String username;

    public Member_primary() {
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
