package com.windfit.repository;

import com.windfit.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Long>, JpaSpecificationExecutor<NhanVien> {
    Optional<NhanVien> findByEmail(String email);

    Optional<NhanVien> findByMaNhanVien(String maNhanVien);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);

    List<NhanVien> findAllByOrderByIdDesc();
}
