package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Patient {

    @Id
    private String id;

    private String name;
    private String diagnosis;

}
