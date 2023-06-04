package com.Kartikey.distance.dao;

import com.Kartikey.distance.model.DistanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceInfoRepository extends JpaRepository<DistanceInfo, Long> {
    DistanceInfo findByFromPincodeAndToPincode(String fromPincode, String toPincode);
}
