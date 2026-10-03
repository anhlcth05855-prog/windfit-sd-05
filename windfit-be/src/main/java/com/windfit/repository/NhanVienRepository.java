package com.windfit.repository;
import com.windfit.entity.NhanVien;
import com.windfit.enums.TrangThaiNhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
public interface NhanVienRepository extends JpaRepository<NhanVien, Long>, JpaSpecificationExecutor<NhanVien> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);
    List<NhanVien> findAllByOrderByIdDesc();
}
