package com.windfit.repository;

import com.windfit.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Long> {
    Optional<MauSac> findByMaMau(String maMau);
    List<MauSac> findByTrangThaiOrderByTenMauAsc(Integer trangThai);
    boolean existsByMaMau(String maMau);
}
