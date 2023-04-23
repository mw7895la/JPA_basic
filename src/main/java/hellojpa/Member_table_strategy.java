package hellojpa;

import javax.persistence.*;

//@Entity
//@Table(name = "MEMBER")
/*@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table ="MY_SEQUENCES",
        pkColumnValue="MEMBER_SEQ", allocationSize = 1
)*/
public class Member_table_strategy {
    //매핑 전략을 TABLE로 했다. MY_SEQUENCES 이름으로 테이블이 생성되어있음.
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name ="name", nullable = false)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
