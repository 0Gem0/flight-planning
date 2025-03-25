package Hibernate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "flights")
public class Flight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String From;
    private int Price;
    private String To;

    @Transient
    public Place flightDest;
    @Transient
    public int returnPrice(String from,String to){
        if (Objects.equals(this.To, to) && Objects.equals(this.From, from)){
            return this.getPrice();
        }
        return 0;
    }
}
