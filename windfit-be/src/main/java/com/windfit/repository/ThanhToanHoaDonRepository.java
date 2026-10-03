package com.windfit.repository;

import com.windfit.entity.ThanhToanHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanHoaDonRepository extends JpaRepository<ThanhToanHoaDon, Long> {
    List<ThanhToanHoaDon> findByHoaDonId(Long hoaDonId);
}
