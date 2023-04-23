package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

//@Entity     //이걸 넣어야 JPA가 처음 로딩될 떄 JPA를 사용하는 애구나 하고 인식 후 관리해준다.
//@Table(name="USER") //실제 Entity와 DB의 테이블 명이 다른경우 이렇게 하면된다 (DB에선 테이블명이 USER일떄) DB의 USER테이블에 저장된다.
public class Member {

    /** 기본키 매핑은 중요하다 !*/
    @Id     //JPA에게 pk가 누군지는 알려줘야돼.
    private Long id;

    @Column(name="name",unique = true,length=10)          //애플리케이션 실행에 영향을 주진 않는다.
    //@Column(insertable=true, updatable=true)  //컬럼을 수정했을때 DB에 인서트할거야 말거야 update문 수행시 업뎃 할거야 말거야?
    private String username;        //객체에는 username이라 쓰고 싶고, DB 컬럼명은 name일 경우.

    @Column
    private Integer age;            //DB에 Integer랑 가장 적절한 숫자 타입이 만들어진다.

    @Enumerated(EnumType.STRING)   //객체에서 Enum 타입을 쓰고 싶다. DB에는 Enum타입은 없다. @Enumerated 어노테이션 쓰면 된다.
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)       //날짜는 @Temporal을 쓰면 된다. DATE는 날짜, TIME은 시간 TIMESTAMP는 날짜+시간
    private Date lastModifiedDate;

    private LocalDate testLocalDate;    //연월
    private LocalDateTime testLocalDateTime;    //연월일

    @Lob                            //varchar를 넘어서는 큰 컨텐츠를 넣고 싶으면  @Lob,  필드가 문자타입이면 clob으로 최종 컬럼이 생성된다.
    private String descritpion;

    @Transient      //temp 필드는 DB랑 아예 관련없게 하고 싶어. 즉, 메모리에서만 temp를 쓰고 싶어.
    private int temp;

    public Member() {

    }
    /** 파라미터 있는 생성자를 만들었더니 클래스에 오류 표시가 난다.  JPA는 내부적으로 리플렉션이나 이런걸 써서 동적으로 객체를 생성하기 때문에 기본생성자가 필요하다.
     * JPA 내 엔티티는 hibernate가 내부적으로 JPA 엔티티를 만들때 Class.newInstance()라는 리플렉션을 이용해 해당 "Entity의 기본 생성자"를 호출해서 객체를 생성한다.*/


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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }
}
