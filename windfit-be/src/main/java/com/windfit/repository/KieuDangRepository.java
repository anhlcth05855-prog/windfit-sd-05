package com.windfit.repository;

import com.windfit.entity.KieuDang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KieuDangRepository extends JpaRepository<KieuDang, Long> {
    Optional<KieuDang> findByMaKieuDang(String maKieuDang);
    List<KieuDang> findByTrangThaiOrderByTenKieuDangAsc(Integer trangThai);
    boolean existsByMaKieuDang(String maKieuDang);
}
