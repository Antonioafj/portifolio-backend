package dev.antonio.portifolio.entities;


import dev.antonio.portifolio.dtos.ValidationEntityDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "validation_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;

    private  String code;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public ValidationEntity(ValidationEntityDto validationEntityDto){
        this.destination = validationEntityDto.destination();
        this.code = validationEntityDto.code();
        this.createdAt = LocalDateTime.now();
    }

}
