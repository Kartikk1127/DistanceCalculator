package com.Kartikey.distance.dao;

import com.Kartikey.distance.model.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, String> {
}
