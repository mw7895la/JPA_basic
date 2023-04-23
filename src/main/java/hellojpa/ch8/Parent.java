package hellojpa.ch8;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Parent {
    @Id
    @GeneratedValue
    @Column(name = "parent_id")
    private Long id;

    private String name;

    /** Parent 를 persist할때 밑에 있는 컬렉션 안에 있는 애들을 persist 다 날려줄거야 라고 하는게 cascade
     *  orphanRemoval = true - Parent가 현재 컬렉션을 관리하고 있는데 child가 컬렉션에서 빠지면 빠진애는 delete 된다.
     *  orphanRemoval = true 만 해놓은 상태에서 em.remove(parent)를 하게 되면, parent는 지워지고 안에 있던 childList의 child들 까지 delete 된다.
     *  cascade = CascadeType.ALL 만 해놓은 상태에서.  em.remove(findParent) 만 지우게 되면  delete가 전파가 돼서 child 다 지우고 parent 지운다.
     * */
    @OneToMany(mappedBy = "parent", /*cascade = CascadeType.ALL,*/orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

    public void addChild(Child child){
        childList.add(child);
        child.setParent(this);
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
