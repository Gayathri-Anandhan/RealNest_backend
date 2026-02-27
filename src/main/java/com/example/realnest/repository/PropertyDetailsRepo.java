package com.example.realnest.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.realnest.entity.RealNest;

@Repository

public interface PropertyDetailsRepo extends JpaRepository<RealNest, Long> {
    List<RealNest> findByType(String type);
    // List<RealNest> findByFilter(String location,Integer price,String type);
    RealNest findByLocationAndPriceAndType(
        String location,
        Integer price,
        String type
    );
}
