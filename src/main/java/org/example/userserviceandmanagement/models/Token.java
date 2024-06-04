package org.example.userserviceandmanagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class Token extends BaseModel{

    private String value;
    @ManyToOne
    private User user;
    private Date expirationDate;
    private boolean deletedStatus;
}
