package Hibernate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

@Entity
@Getter
@Setter
@Table(name = "locations")
public class Place implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "Place")
    private String name;
    @Column(name = "x coordinate")
    private int x;
    @Column(name = "y coordinate")
    private int y;

    @Transient
    public HashMap<Place , Flight> parents = new HashMap<>();
    @Transient
    public HashSet<Flight> flights = new HashSet<>();

    @Override
    public String toString() {
        return String.format("id = %s , Place - %s , x = %d , y = %d", id, name, x, y);
    }
}
