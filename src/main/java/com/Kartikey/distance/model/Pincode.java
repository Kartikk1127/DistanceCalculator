package com.Kartikey.distance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="pincode")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pincode {

    @Id
    @Column(name="id")
    private String pincode;

    @Column(name="latitude")
    private double latitude;

    @Column(name="longitude")
    private double longitude;
}
