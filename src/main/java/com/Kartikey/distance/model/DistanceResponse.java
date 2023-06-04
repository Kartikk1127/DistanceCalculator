package com.Kartikey.distance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

//@Entity
//@Table(name="distance_response")
@Data
@NoArgsConstructor
public class DistanceResponse {

    private double distance;

    private double duration;

    private String route;

    public DistanceResponse(double distance, double duration, String route) {
        this.distance=distance;
        this.duration = duration;
        this.route=route;
    }
}
