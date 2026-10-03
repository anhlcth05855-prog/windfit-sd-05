package com.windfit.repository;

import com.windfit.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Long> {
    List<LichSuHoaDon> findByHoaDonIdOrderByNgayTaoAsc(Long hoaDonId);
}
