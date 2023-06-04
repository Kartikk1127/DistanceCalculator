package com.Kartikey.distance.controller;

import com.Kartikey.distance.model.DistanceInfo;
import com.Kartikey.distance.model.DistanceResponse;
import com.Kartikey.distance.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distance")
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @GetMapping
    public ResponseEntity<DistanceResponse> getDistance(@RequestParam("fromPincode") String fromPincode,
                                                        @RequestParam("toPincode") String toPincode) {
        DistanceInfo distanceInfo = distanceService.calculateDistance(fromPincode, toPincode);
        if (distanceInfo != null) {
            DistanceResponse response = new DistanceResponse(distanceInfo.getDistance(), distanceInfo.getDuration(), distanceInfo.getRoute());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
