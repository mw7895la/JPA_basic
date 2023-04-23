package hellojpa.ch7;

import javax.persistence.*;

//@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn  // 단일 테이블 전략에서는  @DiscriminatorColumn 이 없어도 DTYPE이 생긴다.  strategy = InheritanceType.TABLE_PER_CLASS 인 경우에는 의미 없다.
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
