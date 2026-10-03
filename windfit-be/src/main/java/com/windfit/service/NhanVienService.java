package com.windfit.service;
import com.windfit.entity.NhanVien;
import com.windfit.enums.TrangThaiNhanVien;
import com.windfit.repository.NhanVienRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
public class NhanVienService {
    private final NhanVienRepository repo;
    public NhanVienService(NhanVienRepository repo) { this.repo = repo; }
    public List<NhanVien> search(String keyword, String phongBan, String chucVu, TrangThaiNhanVien status) {
        Specification<NhanVien> spec = Specification.where(null);
        if (keyword != null && !keyword.isBlank()) {
            String k = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((r,q,cb) -> cb.or(cb.like(cb.lower(r.get("hoTen")),k), cb.like(cb.lower(r.get("email")),k), cb.like(cb.lower(r.get("soDienThoai")),k)));
        }
        if (phongBan != null && !phongBan.isBlank()) spec = spec.and((r,q,cb)->cb.equal(r.get("phongBan"), phongBan));
        if (chucVu != null && !chucVu.isBlank()) spec = spec.and((r,q,cb)->cb.equal(r.get("chucVu"), chucVu));
        if (status != null) spec = spec.and((r,q,cb)->cb.equal(r.get("trangThai"), status));
        return repo.findAll(spec);
    }
    public NhanVien get(Long id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên")); }
    @Transactional public void save(NhanVien nv) {
        String email = nv.getEmail() == null ? "" : nv.getEmail().trim();
        if (email.isBlank()) throw new IllegalArgumentException("Email không được để trống");
        boolean dup = nv.getId() == null ? repo.existsByEmailIgnoreCase(email) : repo.existsByEmailIgnoreCaseAndIdNot(email, nv.getId());
        if (dup) throw new IllegalArgumentException("Email đã tồn tại");
        String phone = nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai().trim();
        boolean dupPhone = nv.getId() == null ? repo.existsBySoDienThoai(phone) : repo.existsBySoDienThoaiAndIdNot(phone, nv.getId());
        if (dupPhone) throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        if (nv.getHoTen() == null || nv.getHoTen().isBlank()) throw new IllegalArgumentException("Họ tên không được để trống");
        if (nv.getSoDienThoai() == null || nv.getSoDienThoai().isBlank()) throw new IllegalArgumentException("Số điện thoại không được để trống");
        if (nv.getChucVu() == null || nv.getChucVu().isBlank()) throw new IllegalArgumentException("Chức vụ không được để trống");
        if (nv.getPhongBan() == null || nv.getPhongBan().isBlank()) throw new IllegalArgumentException("Phòng ban không được để trống");
        if (nv.getNgayVaoLam() == null) throw new IllegalArgumentException("Ngày vào làm không được để trống");
        if (nv.getLuongCoBan() == null) throw new IllegalArgumentException("Lương cơ bản không được để trống");
        if (nv.getLuongCoBan().signum() < 0) throw new IllegalArgumentException("Lương cơ bản không được nhỏ hơn 0");
        if (nv.getTrangThai() == null) throw new IllegalArgumentException("Trạng thái không được để trống");
        nv.setSoDienThoai(phone);
        nv.setEmail(email); repo.save(nv);
    }
    @Transactional public void softDelete(Long id) { NhanVien nv = get(id); nv.setTrangThai(TrangThaiNhanVien.TERMINATED); repo.save(nv); }
}
