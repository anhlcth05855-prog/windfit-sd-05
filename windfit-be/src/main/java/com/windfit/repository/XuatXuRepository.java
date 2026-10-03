package com.windfit.repository;

import com.windfit.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface XuatXuRepository extends JpaRepository<XuatXu, Long> {
    Optional<XuatXu> findByMaXuatXu(String maXuatXu);
    List<XuatXu> findByTrangThaiOrderByTenXuatXuAsc(Integer trangThai);
    boolean existsByMaXuatXu(String maXuatXu);
}
