package com.windfit.repository;

import com.windfit.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    Optional<DanhMuc> findByMaDanhMuc(String maDanhMuc);
    List<DanhMuc> findByTrangThaiOrderByTenDanhMucAsc(Integer trangThai);
    boolean existsByMaDanhMuc(String maDanhMuc);
}
