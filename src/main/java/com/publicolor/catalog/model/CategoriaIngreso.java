package com.publicolor.catalog.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "income_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaIngreso implements CatalogoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
