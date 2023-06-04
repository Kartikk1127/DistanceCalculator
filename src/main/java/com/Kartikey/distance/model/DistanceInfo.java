package com.Kartikey.distance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="distance_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="from_pincode")
    private String fromPincode;

    @Column(name="to_pincode")
    private String toPincode;

    @Column(name="distance")
    private double distance;

    @Column(name="duration")
    private double duration;

    @Column(name="route")
    private String route;

}
