package com.ns.marketservice.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="category")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
}
