package hellojpa;

import hellojpa.ch10.Member10;
import hellojpa.ch8.Child;
import hellojpa.ch8.Member_8;
import hellojpa.ch8.Parent;
import hellojpa.ch8.Team_8;
import hellojpa.ch9.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String[] args) {
        //persistenceUnitName 은 persistence.xml에 있는 <persistence-unit name="hello"> 의 설정정보를 읽어서 만든다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        //emf를 만드는 순간 데이터베이스와 연결이 된것이다.
        EntityManager em = emf.createEntityManager();    //이 안에서 db에 데이터를 저장하던지 조회하던지를 하게된다.

        /** EntityManagerFactory는 애플리케이션 로딩 시점에 딱 하나만 만들어놔야 한다.
         *  데이터를 저장하거나 조회할때마다 EntityManager를 만들어줘야 한다.
         * */

        EntityTransaction tx = em.getTransaction();
        tx.begin();     //데이터베이스 트랜잭션 시작.

        try{
            /** 영속성 컨텍스트는 EntityManager가 생성될때 안에 생기게된다. 그리고 1차캐시와 SQL 저장소가 들어있다.
             * 트랜잭션이 커밋되면 영속성 컨텍스트는 삭제된다.
             * */

            /** native query */
            Member10 member = new Member10();
            member.setUsername("member1");
            em.persist(member);     //이시점에 DB에 들어가는게 아니라 커밋해야 들어가지.

            //flush는  언제 호출 되냐면,  1. commit할떄  2. 쿼리 날라갈때.   이때 flush는 기본적으로 동작을 한다.

            //em.flush();
            //dbconn.executeQuery("select * from member");      //얘는 jpa랑 아무 관련이 없어서 플러시 안돼. 그래서 이시점엔 당연히 결과 0, 이런경우는 강제로 flush 해주면 된다.

            List<Member10> resultList = em.createNativeQuery("select MEMBER_ID,USERNAME from MEMBER",Member10.class).getResultList();
            //값을 출력하기 전에 em.createNativeQuery 했을 때 사실 위에서 persist가 Flush가 되고 select 쿼리가 시작된다.
            for (Member10 member10 : resultList) {
                System.out.println("member10 = " + member10);
            }


            /** jPQL*/
            /*String qlString = "select m from Member10 m where m.username like '%kim%'";
            List<Member10> resultList = em.createQuery(
                    qlString, Member10.class).getResultList();//Member10은 테이블이 아닌 엔티티다.
            //엔티티의 매핑정보를 읽어서 적절한 SQL을 실행한다.
            //select m  <-  member10 자체를 가리킨다.

            for (Member10 member10 : resultList) {
                System.out.println("member10 = " + member10);
            }*/


            /** 1:N 으로 변경 후. 즉, 엔티티로 변경 후  이제는 수정을 자유롭게 할 수 있다.  실무에서 이방법을 쓴다.*/
            /*Member_valuetype member = new Member_valuetype();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            //아래 2개는 update문 2개가 발생함.  Member (1) : AddressEntity(N)  외래키가 있는  AddressEntity에 update가 일어난다.( MEMBER_ID set을 해줌.)
            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            em.persist(member);


            em.flush();
            em.clear();

            System.out.println(" ========== start =========");
            Member_valuetype findMember = em.find(Member_valuetype.class, member.getId());

            AddressEntity addressEntity = em.find(AddressEntity.class, 2L);
            Address address = new Address("newCity1", "street", "10000");
            addressEntity.setAddress(address); */     //이렇게 사용하면 된다. update문 나감.



            /** 값 타입 컬렉션 수정. 변경시 싹다 지우고 갈아끼워야함.*/
            /*Member_valuetype member = new Member_valuetype();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);


            em.flush();
            em.clear();

            System.out.println(" ========== start =========");
            Member_valuetype findMember = em.find(Member_valuetype.class, member.getId());
            // homeCity -> newCity
            //findMember.getHomeAddress().setCity("");    //이렇게 하면 안된다.side effect가 생긴다. 값타입은 Immutable 해야된다.
            //이 부분이 em.flush(), em.clear()가 없었다면

            Address old = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity",old.getStreet(),old.getZipcode()));

            //치킨을 한식으로 바꾼다면,
            findMember.getFavoriteFoods().remove("치킨");     //HashSet이라 인덱스로 지울순 없음.
            findMember.getFavoriteFoods().add("한식");
            //컬렉션의 값만 변경해도 실제 DB 쿼리가 날라가면서 뭐가 변경되는지 알고 JPA가 알아서 바꿔준다.


            //주소를  old1 - > new1 바꾸고 싶다.
            findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));      //equalsAndHashcode를 오버라이드 안하면 이렇게 해도 안지워진다.
            findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));*/

            /** 값 타입 수정.*/

            /*Member_valuetype member = new Member_valuetype();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);


            em.flush();
            em.clear();

            System.out.println(" ========== start =========");
            Member_valuetype findMember = em.find(Member_valuetype.class, member.getId());
            // homeCity -> newCity
            //findMember.getHomeAddress().setCity("");    //이렇게 하면 안된다.side effect가 생긴다. 값타입은 Immutable 해야된다.

            Address old = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity",old.getStreet(),old.getZipcode())); */      //이렇게 값타입을 인스턴스 자체를 새로운걸 넣어야 한다.


            /** 값 타입을 컬렉션으로  */
            /*Member_valuetype member = new Member_valuetype();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);
            //지금 값타입 컬렉션들은 persist 하지 않고. member만 persist 했더니, 자동으로 persist가 되었다.
            //컬렉션들이 다른 테이블임에도 불구하고 Member와 같은 라이프사이클을 타고 있다.  즉, 생명주기는 Member에 의존하고 있다.
            //Member_valuetype 클래스의
            //@Column(name = "USERNAME") private String username; 도 값 타입이듯. 컬렉션들도 값타입이다.
            // Member에서 값을 체인지하면 자동으로 업데이트 한다고 보면 된다.

            em.flush();
            em.clear();     //DB에는 데이터가 있는 상태에서 영속성 컨텍스트를 비웠다.

            System.out.println(" ===========start ====== ");
            Member_valuetype findMember = em.find(Member_valuetype.class, member.getId());      //Member 만 가져온다. 컬렉션들은 지연로딩이다. Address는 Member에 소속된 값 타입이라 같이 불러와 진다.
            System.out.println(" ================= ");
            //컬렉션들은 지연로딩이다.
            List<Address> addressHistory = findMember.getAddressHistory();
            System.out.println("addressHistory.getClass() = " + addressHistory.getClass());

            for (Address address : addressHistory) {
                System.out.println("address.getCity() = " + address.getCity());
            }
            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }*/


            /** 불변 immutable객체로 만들자.*/
            /*Address address = new Address("city", "street", "10000");
            Member9 member1 = new Member9();
            member1.setUsername("member1");
            member1.setHomeAddress(address);
            em.persist(member1);

            //Address를 통으로 갈아 끼워라. 완전히 새로 만들었다.  그래서 side-effect가 없다. ( address에 setter메서드를 다 지웠다.)
            Address newAddress = new Address("NewCity", address.getStreet(), address.getZipcode());
            member1.setHomeAddress(newAddress);*/

            /** 임베디드 타입을 공유해서 사용하는 경우 (side-effect 발생한다)*/
            /*Address address = new Address("city", "street", "10000");
            Member9 member1 = new Member9();
            member1.setUsername("member1");
            member1.setHomeAddress(address);
            em.persist(member1);

            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member9 member2 = new Member9();
            member2.setUsername("member2");
            member2.setHomeAddress(copyAddress);
            em.persist(member2);

            Member9 member3 = new Member9();
            member3.setUsername("member3");
            member3.setHomeAddress(address);
            em.persist(member3);*/

            //여기까진 똑같은 주소라 문제가 없다.
            //member1.getHomeAddress().setCity("newCity");        //member1,3의 값이 동일해짐. 같은것을 참조하기 때문에..
            //나는 분명  member1의 주소만 바꾸려는 의도였다. 근데 실행 해보면, 업데이트 쿼리가 2번 나간다.
            //이런 side effect는 진짜 잡기 힘들다.  만약 공유해서 쓰려면,Address를 Entity로 만들어야 한다.
            //그래서 값을 복사해서 사용하자.




            /** 임베디드 타입*/
            /*Member9 member = new Member9();
            member.setUsername("hello");
            member.setHomeAddress(new Address("city", "street", "10000"));
            member.setWorkPeriod(new Period());

            em.persist(member);*/


            /** 고아 객체 + CASCADE
             * Child의 생명주기는 Parent가 관리한다. */
            /*Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent); //child는 persist 안했다. cascade = CascadeType.ALL 옵션이 동작하게 된다.
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);*/
            //이건 orphanRemoval = true 옵션이 동작하게 된다. 근데 orphanRemoval = true 옵션만 있으면 컬렉션에서 빠진 child가 삭제되지 않는다.
            //CascadeType.PERSIST옵션도 같이 사용하자.


            //em.remove(findParent);


            /** 고아 객체 주의  Parent의 cascade = CASCADETYPE.ALL 만 지우고 orphanRemoval = true이다.*/
            /*Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            em.remove(findParent);*/
            // 이렇게 하면 Parent는 지워지고 Parent의 컬렉션에 있던 child까지 다 지워지게 된다.
            //여기다 파라미터로 parent 하게 되면.. 이미 flush로 DB에 쿼리를 반영하고, clear로 1차캐시를 지운 상태라 IllegalArgumentException이 뜸.

            /** 영속성 전이 */
            /*Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0); */       //orphanRemoval = true 이 동작하기 때문에 하나가 지워진다.

            /*em.persist(child1);
            em.persist(child2); */
            //근데 이렇게 하는게 귀찮다.  지금 Parent를 중심으로 코드를 짜고 있다. cascade = CascadeType.ALL 로 parent persist 하면 child까지 다 persist 되도록 옵션 설정하자.



            /** 즉시 로딩, EAGER  */
            /*Team_8 team = new Team_8();
            team.setName("teamA");
            em.persist(team);

            Team_8 teamB = new Team_8();
            teamB.setName("teamB");
            em.persist(teamB);

            Member_8 member1 = new Member_8();
            member1.setName("member1");
            member1.setTeam(team);
            em.persist(member1);

            Member_8 member2 = new Member_8();
            member1.setName("member2");
            member2.setTeam(teamB);
            em.persist(member2);



            em.flush();
            em.clear();*/

            //Member_8 m = em.find(Member_8.class, member1.getId());      // 이렇게 em.find로 Member를 가지고 올때 부터 Team까지 조인해서 같이 가지고 온다.
            //em.find라는 것은 딱 pk를 찍어서 가져오기 때문에 JPA가 내부적으로 최적화 할 수 있다.
            //System.out.println("m.getTeam().getClass() = " + m.getTeam().getClass());

            //List<Member_8> members = em.createQuery("select m from Member_8 m", Member_8.class).getResultList();
            //그런데 JPQL은 select m from Member_8 m 이게 sql로 그대로 번역이 된다. 그러면 당연히 Member만 select한다. 근데 Member를 select해서 가져왔더니
            //team 이 즉시로딩으로 되어있네..? 그럼 team 도 가져와야해.  SQL: select * from Team where Team_id =xxx
            // 즉시로딩이란 말은 가져올때 무조건 값이 다 들어있어야 한다. 그래서, Member 쿼리 나가고 Member의 개수가 10개면(member1, member2 ... )  10개만큼  EAGER를 가져오기 위해 쿼리가 별도로 나간다.
            //즉, 쿼리가 추가쿼리가 나간다 해서 N+1이라 한다.

            //System.out.println(" ====== ");
            //System.out.println("m.getTeam().getName() = " + m.getTeam().getName());
            //System.out.println(" ====== ");

            /** 지연 로딩, LAZY
             *  Member를 가져올땐 질의문에서 Member로만 해서 가져오고, team은 프록시 객체로 가져온다.
             * */
            /*Team_8 team = new Team_8();
            team.setName("teamA");
            em.persist(team);

            Member_8 member1 = new Member_8();
            member1.setName("member1");
            em.persist(member1);


            member1.setTeam(team);
            em.flush();
            em.clear();

            Member_8 m = em.find(Member_8.class, member1.getId());      //조인이 안일어난다.
            System.out.println("m.getTeam().getClass() = " + m.getTeam().getClass());

            System.out.println(" ====== ");
            m.getTeam().getName();      //뭔가 team의 속성을 사용하는 시점에 프록시 객체가 초기화되면서 DB에서 값을 가져온다.
            System.out.println(" ====== ");*/

            /** 준영속 상태에서 프록시 객체 초기화 ? */
            /*Member_5 member1 = new Member_5();
            member1.setUsername("hello");
            em.persist(member1);
            em.flush();
            em.clear();

            Member_5 refMember = em.getReference(Member_5.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass());  // Proxy

            System.out.println(" isLoaded "+ emf.getPersistenceUnitUtil().isLoaded(refMember));
            //refMember가 프록시 초기화가 된 애냐??

            Hibernate.initialize(refMember);        //프록시 강제 초기화  JPA에서는 아래처럼 getUserName()같이 메소드를 호출해줘야함.
            
            em.detach(refMember);
            //em.clear();
            //이런거 만나는 순간 이미 refMember는 영속성 컨텍스트의 도움을 못받는다.

            refMember.getUsername();        //사실 이건 프록시 강제 초기화다.
            //이때 이제 DB에 쿼리가 나가면서 프록시 객체가 초기화 된다.(select 문 나가면서 영속성컨텍스트에 1차캐시에 놓이게 됨)
            //초기화는 영속성 컨텍스트를 통해서 일어난다. 그런데 만약에 영속성 컨테스트를 꺼버리면?? 또는 끄집어 내면?

            //detach 후 , 영속성 컨텍스트에서 해당 객체 관리 안해! 라고 한다.  could not initialize proxy [hellojpa.Member_5#1] - no Session  영속성 컨텍스트에 프록시 없다는 뜻.
            */

            /** 이번엔 둘다 getReference로 가져와 보자.*/
            /*Member_5 member1 = new Member_5();
            member1.setUsername("hello");
            em.persist(member1);
            em.flush();
            em.clear();

            Member_5 refMember = em.getReference(Member_5.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass());      // Proxy
            refMember.getUsername();       //프록시가 초기화 되버린다.

            Member_5 findMember = em.find(Member_5.class, member1.getId());
            System.out.println("findMember.getClass() = " + findMember.getClass());     // 실제 Member가 아닌 프록시가 반환되었다. 프록시로 한번 조회가 되면 em.find에서도 프록시로 반환해준다.
            System.out.println("(refMember==findMember) = " + (refMember == findMember));   //그래서 == 비교가 true가 나왔다.*/

            /** 영속성 컨텍스트에 찾는 엔티티가 이미 있으면.*/
            /*Member_5 member1 = new Member_5();
            member1.setUsername("hello");
            em.persist(member1);

            em.flush();
            em.clear();

            Member_5 m1 = em.find(Member_5.class, member1.getId());
            System.out.println("m1.getClass() = " + m1.getClass());
            //이 상태는 이미 영속성 컨텍스트에 멤버가 올라가 있다. 그러면 reference 해도 프록시가 아니라 실제 엔티티를 가져오게 된다.

            Member_5 reference = em.getReference(Member_5.class, member1.getId());
            System.out.println("reference.getClass() = " + reference.getClass());
            //reference 로 했는데 프록시가 아닌 실제 객체다.

            System.out.println(" a == a: " + (m1 == reference));
            //JPA에서는 한 영속성 컨텍스트 안에서 가져오고 PK가 똑같으면  항상 true를 반환해 줘야한다.*/




            /** 8장 프록시와 연관관계 관리. 프록시 특징.*/
            /*Member_5 member1 = new Member_5();
            member1.setUsername("hello");
            em.persist(member1);

            Member_5 member2 = new Member_5();
            member2.setUsername("member2");
            em.persist(member2);

            em.flush();
            em.clear();
            //Member_5 m1 = em.find(Member_5.class, member1.getId());
            //Member_5 m2 = em.find(Member_5.class, member2.getId());
            //System.out.println("m1.getClass() = " + m1.getClass());
            //System.out.println("m2.getClass() = " + m2.getClass());

            Member_5 m1 = em.find(Member_5.class, member1.getId());
            Member_5 m2 = em.getReference(Member_5.class, member2.getId());
            logic(m1, m2);      //실제로 m1과 m2가 프록시로 넘어올지 실제객체로 넘어올지 모르기 때문에 타입비교를 절대 ==으로 하면 안된다.*/

            /** 8장 프록시와 연관관계 관리. 프록시 특징*/
            /*Member_5 member = new Member_5();
            member.setUsername("hello");
            em.persist(member);

            em.flush();
            em.clear();
            //Member_5 findMember = em.find(Member_5.class, member.getId());        //진짜 객체를 얻어서 바로 밑에서 값을 출력한다.
            //System.out.println("findMember.getId() = " + findMember.getId());
            //System.out.println("findMember.getUsername() = " + findMember.getUsername());   //값이 잘 출력되는걸 볼 수 있다.

            Member_5 findMember = em.getReference(Member_5.class, member.getId());
            //select 쿼리가 안나간다. getReference 호출하는 시점에는 데이터베이스 쿼리를 안한다.
            // 그런데 이 값이 실제 사용되는 시점 ( getUsername() ) 에는 DB에 쿼리를 날려서 findMember에 값을 채운다음에 출력한다.
            // getId()는 이미 getReference()호출시 파라미터로 넣었던 것이라 값이 이미 있어서 DB에 쿼리를 할 필요가 없다.
            System.out.println("findMember = " + findMember.getClass());    //프록시 객체임을 알 수 있다.
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getUsername() = " + findMember.getUsername());   //값이 잘 출력되는걸 볼 수 있다.
            System.out.println("findMember.getUsername() = " + findMember.getUsername());*/



            //Member_5 member = em.find(Member_5.class, 1L);
            //printMember(member);
            //printMemberAndTeam(member);




            /** @MappedSuperclass*/
            /*Member_mapperclass member = new Member_mapperclass();
            member.setCreateBy("KIM");
            member.setCreatedDate(LocalDateTime.now());
            em.persist(member);

            em.flush();
            em.clear();*/


            /** 7장 고급매핑 12페이지 이전*/
            /*Movie movie = new Movie();
            movie.setDirector("aaaa");
            movie.setActor("bbbb");
            movie.setName("바람과함께사라지다");
            movie.setPrice(10000);

            em.persist(movie);      // item을 먼저 인서트 하고   movie 인서트 한다.
            em.flush();
            em.clear();

            //Movie findMovie = em.find(Movie.class, movie.getId());      //movie와 이너조인으로 item을 해서 id를 가져온다.   //단일테이블 전략으로 바꾸면 조인 안한다.
            Item findItem = em.find(Item.class, movie.getId());
            System.out.println("findMovie = " + findItem);*/


            /** Team : Member  1 : N 관계에서 1에다가 연관관계 주인을 설정함. 사실 이 방법은 권장하지 않는다. */
            /*Member_1vN member = new Member_1vN();
            member.setUsername("member1");

            em.persist(member);

            Team_1vN team = new Team_1vN();
            team.setName("teamA");
            //여기 포인트가 좀 애매해진다.
            team.getMembers().add(member);// 외래키가 있는 Member테이블을 업데이트 한다.

            //왜 업데이트 쿼리가 추가로 나가야되냐면, 바로 아래서 팀 엔티티를 저장하는데, 나(team)는 그냥 인서트 치면된다.. 근데 멤버테이블의 team_id를 어떻게 할 수가 없다 그래서 업데이트 쿼리가 한번 더 나간 것.
            em.persist(team);

            em.flush();
            em.clear();
            Team_1vN findTeam = em.find(Team_1vN.class, team.getId());*/
            //Member_1vN member1vN = em.find(Member_1vN.class, member.getId());


            /** 자주하는 연관관계 매핑 실수
             * 1. team을 먼저 세팅(members.add 주석) 후 member에 team 넣으면서 persist  ( 정상 )
             * 2. member 먼저 만들고(team 따로set 안함) team 세팅 하면 MEMBER 테이블에 member1이 있긴 한데,  team_id가 NULL이다.
             * */

            /** 1. 연관관계의 주인에만 값을 넣는다. - 정상
             *  JPA입장에서 아래 team.getMembers().add()를 제외하곤 맞는 코드다. 근데, 객체지향적으로 생각한다면, 양쪽에서 다 넣어주는건 맞다. */
            /*Team_5 team = new Team_5();
            team.setName("TeamA");

            em.persist(team);

            Member_5 member = new Member_5();
            member.setUsername("member1");
            member.setTeam(team);       //연관관계의 주인에만 값을 넣어본다.  이러면 정상적으로 값이 들어간다.
            em.persist(member);*/

            //em.flush();
            //em.clear();

            //team.getMembers().add(member); //어짜피 읽기 전용이라 JPA에서 이 값을 안쓴다.
            //근데 바로 위 코드 주석해도 아래 for문에서 값이 나온다. 왜? Team에서 자기랑 연관된 MEMBER의 FK를 보고 가져온다.
            //Team_5 findTeam = em.find(Team_5.class, team.getId());      //이 코드가 Hibernate: select team 문을 찍는다.

            //근데 위에 em.flush(), em.clear()를 주석처리하면 , em.find()부분은 1차캐시 즉, 메모리에만 올라가 있다. 1차캐시에 있는 값이 그대로 튀어나오는데 컬렉션에 값이 있을까? 없다.!!
            //team.getMembers().add(membeR)를 주석 풀어야, 아래 For문에서 값이 있을 것.
            /*List<Member_5> members = findTeam.getMembers();
            System.out.println(" ========= ");
            for (Member_5 member5 : members) {
                System.out.println("member5.getUsername() = " + member5.getUsername());
            }
            System.out.println(" ========= ");*/


            /** 2. 연관관계 주인이 아닌곳에 값을 넣으면 안된다.*/
            /*Member_5 member = new Member_5();
            member.setUsername("member1");
            em.persist(member);

            Team_5 team = new Team_5();
            team.setName("TeamA");
            team.getMembers().add(member);  //team에 members에서 member를 집어 넣었다.
            em.persist(team);               //이렇게 하면 member 테이블에 member1이 들어갔긴 한데, team_id가 NULL이다.
            //지금 연관관계 주인은 member에 있는 team 필드다.

            em.flush();
            em.clear();*/

            /** 연관관계 매핑시 객체지향 스럽게 매핑.*/
            /*Team_5 team = new Team_5();
            team.setName("TeamA");
            em.persist(team);

            Member_5 member = new Member_5();
            member.setUsername("member1");
            member.setTeam(team);       //이러면 JPA가 알아서 Team에서 pk값을 꺼내서 Insert할때 fk 값으로 사용한다.
            em.persist(member);

            //아래find  -  select하는 쿼리도 보고싶다. 그러면 flush,close해주면 된다.
            em.flush();
            em.clear();

            Member_5 findMember = em.find(Member_5.class, member.getId());
            List<Member_5> members = findMember.getTeam().getMembers();

            for (Member_5 member5 : members) {
                System.out.println("member5.getUsername() = " + member5.getUsername());

            }*/

            //Member의 팀을 바꾸고 싶은 경우 100L 팀이 있다고 가정.
            //Team newTeam = em.find(Team.class, 100L);
            //findMember.setTeam(newTeam);      //팀을 set하면서 외래키 값이 체인지 된다.


            /** 연관관계 매핑시 테이블에 맞춰 매핑한 경우.*/
            /*Team_5 team = new Team_5();
            team.setName("TeamA");
            em.persist(team);       //em.persist 하면 항상 Team의 id값이 들어간다 했지?  pk id값이 세팅이 되고 영속상태가 된다.

            Member_5 member = new Member_5();
            member.setUsername("member1");
            member.setTeamId(team.getId());

            em.persist(member);     //아마도 같은 시퀀스를 써서 첫 데이터 input시  team의 pk는 1 member의 pk는 2일 것.
            //아래는 번잡한 경우다. 객체지향 스럽지 않다.
            Member_5 findMember = em.find(Member_5.class, member.getId());
            Long findTeamId = findMember.getTeamId();
            Team_5 findTeam = em.find(Team_5.class, findTeamId);*/


            /** 시퀀스 전략  IDENTITY 전략과는 다르다.*/
            //영속성 컨텍스트에 넣으려보니 시퀀스 전략이네? DB한테 값 얻어와서 id값을 넣어주고 영속성 컨텍스트에 저장한다.
            // call next value for MEMBER_SEQ <<-
            //아직 DB에 insert 쿼리를 날린것은 아니다.  이제 실제 커밋 시점에 insert 쿼리를 날린다.
            //allocationSize =50 미리 50개를 땡겨와서 메모리에  DB에 미리 50개를 올려놓고 내가 Insert할 때 50이 되면
            //그때 next call을 호출하여 또 50~ 100으로 미리 땡겨놓는다.
            //Hibernate:
            //    call next value for MEMBER_SEQ        // DB SEQ = 1
            //Hibernate:
            //    call next value for MEMBER_SEQ        // DB SEQ = 51
            //

            /*Member_sequence_strategy member1 = new Member_sequence_strategy();
            member1.setUsername("C");
            Member_sequence_strategy member2 = new Member_sequence_strategy();
            member2.setUsername("B");
            Member_sequence_strategy member3 = new Member_sequence_strategy();
            member3.setUsername("A");
            System.out.println("==============");
            em.persist(member1);    // 1, 51
            em.persist(member2);    //  시퀀스를 DB가 아닌 Memory에서 호출한다.
            em.persist(member3);    //  시퀀스를 DB가 아닌 Memory에서 호출한다.
            System.out.println("==============");*/

            /** IDENTITY 전략*/
            /*Member_primary member = new Member_primary();
            member.setUsername("C");
            System.out.println("==============");
            em.persist(member);
            System.out.println("==============");*/

            /*Member member = new Member();
            member.setId(2L);
            member.setUsername("B");
            member.setRoleType(RoleType.ADMIN);
            //USER로 하면 만약 필드에 EnumType.ORDINAL 이면 (기본이 이거임) DB에 0번이 들어가있다
            //ADMIN으로 하면 1번이 DB에 들어가있다.

            em.persist(member);*/


            /** em.close();*/
            /*Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");

            em.close(); // 영속성 컨텍스트를 닫았다. 마찬가지로 변경이 일어나지 않는다.
            Member member2 = em.find(Member.class, 150L);   //다시 DB에서 조회하여 영속성 컨텍스트에 올린다.
            System.out.println("=================");*/

            /** em.clear();*/
            /*Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");

            em.clear(); //완전 영속성 컨텍스트를 초기화 한다. 1차 캐시를 통으로 지운것.
            Member member2 = em.find(Member.class, 150L);   //다시 DB에서 조회하여 영속성 컨텍스트에 올린다.
            System.out.println("=================");*/


            /** em.detach(); */
            /*Member member = em.find(Member.class, 150L);        //DB에서 조회후 영속성 컨텍스트에 등록한다.(1차캐시에 등록)
            member.setName("AAAAA");    // ZZZZZ인 것을 AAAAA로 바꿔보자.
            em.detach(member);//영속성 상태에서 준영속으로 바꾼다 영속상태에서 빠진것이다.. 이제 JPA에서 관리 안한다. 그래서 tx.commit할때 아무일도 일어나지 않는다.
            //member와 관련된 것들이 전부 영속성 컨텍스트에서 빠지게 된다.*/

            /*Member member = new Member(200L, "member200");
            em.persist(member);
            //근데, 내가 미리 쫌 DB에 반영하고싶어, 아니면 쿼리를 좀 미리 보고싶어.
            em.flush();
            System.out.println("=================");*/

            /** dirty checking  변경 감지*/
            /*Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");*/
            //JPA의 목적은 자바 컬렉션 다루듯이 하면 된다. 객체를 찾아오면 같은 주소를 가지게 되고  주소가 가리킨 곳의 값을 변경 한 것이기 때문에 다시 넣을 필요 없다.

            /** 쓰기 지연 SQL 저장소 */
            /*Member member1 = new Member(150L, "A");
            Member member2 = new Member(160L, "B");
            em.persist(member1);
            em.persist(member2);
            System.out.println("=================");*/
            //<property name="hibernate.jdbc.batch_size" value="10"/> 이 size만큼 쿼리를 모아서 DB에 한번에 쫙 넣을 수 있다.
            //persist 순간에 DB에 저장되는게 아니라 영속성 컨텍스트에 차곡차곡 쿼리가 쌓인다. 커밋 시점에 쌓였던 쿼리가 Flush  된다.

            /** 1. DB조회 2. 1차 캐시에서 조회 */
            //101L가 저장되어 있는 상태에서 조회를 해보자. 지금  //비영속 영속상태 이하 주석처리 했다.// 그러면 엔티티매니저 팩토리와 엔티티 매니저가 새로 실행된다.
            //findMember1은 DB에서 조회해야 하고(DB에서 조회 후 영속성 컨텍스트에 무조건 올려놓는다. 그리고 1차캐시에서도 갖고있게 된다), findMember2는 1차 캐시에서 가져오게 될 것.
            /*Member findMember1 = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);
            System.out.println("result =" + (findMember1 == findMember2));  //자바 컬렉션에서 가져온것 처럼. 주소가 같다. 동일성을 보장해준다.*/

            /** 비영속 , 영속 상태. */
            /*Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA");

            //이제부터 영속 상태
            System.out.println("==== BEFORE ====");
            em.persist(member); //이때 DB에 저장되는건 아님  근데 인서트 쿼리가 AFTER 이후에 찍혀있다. " 쿼리가 날라가는 시점은 커밋 바로 직전이다. "
            //em.detach(member);      //영속성 컨텍스트에서 member 객체를 지운다.
            System.out.println("==== AFTER ====");

            //아래 했을 때 조회용 SQL이 나가는지 보자  soutv로 값은 찍는데 DB에 select쿼리가 안나간다?? -> 위에서 저장할 때 1차캐시에 저장한 상태라 캐시에서 가져온 것.
            Member findMember = em.find(Member.class, 101L);
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());*/

            /** 멤버 리스트 조회*/
            /*List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();       //JPA 입장에서는 테이블 대상으로 코드를 짜지 않는다. Member 객체를 대상으로 쿼리,  select m 은 Member객체를 다 가져와!
            for (Member member : result) {
                System.out.println("member.getName() = " + member.getName());
            }
            List<Member> result2 = em.createQuery("select m from Member as m where m.name='HelloB'", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
            for (Member member : result2) {
                System.out.println("member = " + member.getName());
            }*/


            /** 멤버 수정. */
            /*Member findMember = em.find(Member.class, 1L);
            findMember.setName("helloJPA");     //이러고 나서 다시 저장해야될까?  안해도된다 !*/
            //자바 컬렉션을 다루는것과 똑같아서 변경되어 있다 !
            /** JPA를 통해서 이렇게 엔티티를 가져오면, JPA가 관리를 해준다. 변경이 되었는지 트랜잭션 커밋하는 시점에 다 체크한다. 바뀐게 있다면 update 쿼리를 해서 바꿔준다.*/


            /** 멤버 삭제. */
            /*Member findMember = em.find(Member.class, 1L);
            em.remove(findMember);*/

            /** 멤버 조회.*/
            /*Member findMember = em.find(Member.class, 1L);//
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());*/

            /** 멤버 저장.*/
            /*Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");       //2가지 set해도 뭔가 된거같지가 않다. JPA에서는 트랜잭션이라는 단위가 중요하다.*/
            //JPA에서는 모든 데이터에 대한 작업은 트랜잭션에서 해야된다.
            //em.persist(member);

            tx.commit();
        }catch(Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();     //데이터베이스 커넥션을 물고 동작하기 때문에 사용 다하면 닫아줘야한다.
        }

        emf.close();//전체 애플리케이션이 끝나면 emf도 닫아준다.
    }

    private static void logic(Member_5 m1, Member_5 m2) {
        //System.out.println("m1== m2 " + (m1.getClass() == m2.getClass()));
        System.out.println("(m1 instanceof Member_5) = " + (m1 instanceof Member_5));
    }

    private static void printMember(Member_5 member) {
        System.out.println("member.getUsername() = " + member.getUsername());

    }

    private static void printMemberAndTeam(Member_5 member) {
        String username = member.getUsername();
        System.out.println("username = " + username);
        Team_5 team = member.getTeam();
        System.out.println("team.getName() = " + team.getName());

    }
}
