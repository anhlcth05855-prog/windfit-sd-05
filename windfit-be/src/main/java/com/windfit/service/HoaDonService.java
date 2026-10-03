package com.windfit.service;

import com.windfit.dto.*;
import com.windfit.entity.*;
import com.windfit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface HoaDonService {

    Page<HoaDonResponseDTO> layDanhSachHoaDon(
            String keyword,
            Integer trangThai,
            String kenhBan,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Pageable pageable
    );

    HoaDonResponseDTO layChiTietHoaDon(Long id);

    HoaDonResponseDTO layChiTietTheoMa(String maHoaDon);

    HoaDonResponseDTO taoHoaDon(TaoHoaDonRequest request);

    HoaDonResponseDTO capNhatTrangThai(Long id, CapNhatTrangThaiRequest request);

    void huyHoaDon(Long id, String lyDoHuy);

    List<HoaDonResponseDTO> layHoaDonChoTaiQuay();

    HoaDonResponseDTO yeuCauHoanHang(Long id, YeuCauHoanHangRequest request);

    HoaDonResponseDTO xacNhanHoanHang(Long id, Long idNhanVien);

    HoaDonResponseDTO tuChoiHoanHang(Long id, String lyDoTuChoi, Long idNhanVien);
}

@Service
@RequiredArgsConstructor
class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final ThanhToanHoaDonRepository thanhToanHoaDonRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<HoaDonResponseDTO> layDanhSachHoaDon(
            String keyword,
            Integer trangThai,
            String kenhBan,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Pageable pageable) {

        String trangThaiStr = null;
        if (trangThai != null) {
            trangThaiStr = switch (trangThai) {
                case 0 -> "CHO_THANH_TOAN";
                case 1 -> "CHO_XAC_NHAN";
                case 2 -> "DANG_GIAO";
                case 3 -> "HOAN_THANH";
                case 4 -> "DA_HUY";
                case 5 -> "YEU_CAU_HOAN";
                case 6 -> "DA_HOAN";
                default -> null;
            };
        }

        Page<HoaDon> pageResult = hoaDonRepository.timKiemHoaDon(keyword, trangThaiStr, kenhBan, tuNgay, denNgay, pageable);
        return pageResult.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonResponseDTO layChiTietHoaDon(Long id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));
        return mapToDTO(hd);
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonResponseDTO layChiTietTheoMa(String maHoaDon) {
        HoaDon hd = hoaDonRepository.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn mã: " + maHoaDon));
        return mapToDTO(hd);
    }

    @Override
    @Transactional
    public HoaDonResponseDTO taoHoaDon(TaoHoaDonRequest request) {
        String kenh = (request.getKenhBan() != null && !request.getKenhBan().isBlank()) ? request.getKenhBan().toUpperCase() : "TAI_QUAY";
        boolean isPos = kenh.equalsIgnoreCase("TAI_QUAY");
        String maHoaDon = "HD-" + (isPos ? "POS" : "ONL") + "-" + System.currentTimeMillis() % 1000000;

        KhachHang kh = null;
        if (request.getIdKhachHang() != null) {
            kh = khachHangRepository.findById(request.getIdKhachHang()).orElse(null);
        }

        NhanVien nv = null;
        if (request.getIdNhanVien() != null) {
            nv = nhanVienRepository.findById(request.getIdNhanVien()).orElse(null);
        }

        PhieuGiamGia pgg = null;
        if (request.getIdPhieuGiamGia() != null) {
            pgg = phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia()).orElse(null);
        }

        BigDecimal tongTienHang = BigDecimal.ZERO;
        List<ChiTietHoaDon> chiTiets = new ArrayList<>();

        for (TaoHoaDonRequest.ItemRequest item : request.getItems()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findByIdWithLock(item.getIdSanPhamChiTiet())
                    .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại ID: " + item.getIdSanPhamChiTiet()));

            if (spct.getSoLuongTon() < item.getSoLuong()) {
                throw new RuntimeException("Sản phẩm '" + (spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : spct.getMaSpct())
                        + "' không đủ tồn kho (Còn " + spct.getSoLuongTon() + ", yêu cầu " + item.getSoLuong() + ")");
            }

            spct.setSoLuongTon(spct.getSoLuongTon() - item.getSoLuong());
            spct.setNgaySua(LocalDateTime.now());
            sanPhamChiTietRepository.save(spct);

            BigDecimal gia = item.getDonGia() != null ? item.getDonGia() : spct.getGiaBan();
            BigDecimal tt = gia.multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTienHang = tongTienHang.add(tt);

            ChiTietHoaDon cthd = ChiTietHoaDon.builder()
                    .sanPhamChiTiet(spct)
                    .soLuong(item.getSoLuong())
                    .donGia(gia)
                    .thanhTien(tt)
                    .ngayTao(LocalDateTime.now())
                    .trangThai(1)
                    .build();

            chiTiets.add(cthd);
        }

        BigDecimal tienGiam = BigDecimal.ZERO;
        if (pgg != null && pgg.getTrangThai() == 1) {
            if ("PHAN_TRAM".equalsIgnoreCase(pgg.getLoaiGiamGia())) {
                tienGiam = tongTienHang.multiply(pgg.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
                if (pgg.getGiamToiDa() != null && tienGiam.compareTo(pgg.getGiamToiDa()) > 0) {
                    tienGiam = pgg.getGiamToiDa();
                }
            } else {
                tienGiam = pgg.getGiaTriGiam();
            }

            pgg.setSoLuongDaDung(pgg.getSoLuongDaDung() + 1);
            phieuGiamGiaRepository.save(pgg);

            if (kh != null) {
                Optional<KhachHangPhieuGiamGia> khpgg = khachHangPhieuGiamGiaRepository
                        .findByKhachHangIdAndPhieuGiamGiaId(kh.getId(), pgg.getId());
                if (khpgg.isPresent()) {
                    KhachHangPhieuGiamGia k = khpgg.get();
                    k.setTrangThai(0);
                    k.setNgaySuDung(LocalDateTime.now());
                    khachHangPhieuGiamGiaRepository.save(k);
                }
            }
        }

        BigDecimal thanhTien = tongTienHang.subtract(tienGiam);
        if (thanhTien.compareTo(BigDecimal.ZERO) < 0) thanhTien = BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();
        HoaDon hoaDon = HoaDon.builder()
                .maHoaDon(maHoaDon)
                .loaiHoaDon(kenh)
                .khachHang(kh)
                .nhanVien(nv)
                .phieuGiamGia(pgg)
                .tenNguoiNhan(request.getTenNguoiNhan() != null ? request.getTenNguoiNhan() : (kh != null ? kh.getHoTen() : "Khách lẻ tại quầy"))
                .sdtNguoiNhan(request.getSdtNguoiNhan() != null ? request.getSdtNguoiNhan() : (kh != null ? kh.getSoDienThoai() : ""))
                .diaChiNhan(request.getDiaChiGiao() != null ? request.getDiaChiGiao() : "Mua tại quầy WindFit Store")
                .tongTienHang(tongTienHang)
                .tienGiam(tienGiam)
                .phiShip(BigDecimal.ZERO)
                .tongTienThanhToan(thanhTien)
                .ghiChu(request.getGhiChu())
                .ngayTao(now)
                .ngayCapNhat(now)
                .ngayNhanHang(isPos ? now : null)
                .ngayThanhToan(isPos ? now : null)
                .trangThaiHoaDon(isPos ? "HOAN_THANH" : "CHO_XAC_NHAN")
                .trangThaiThanhToan(isPos ? "DA_THANH_TOAN" : "CHUA_THANH_TOAN")
                .build();

        HoaDon saved = hoaDonRepository.save(hoaDon);

        for (ChiTietHoaDon c : chiTiets) {
            c.setHoaDon(saved);
            chiTietHoaDonRepository.save(c);
        }

        if (request.getHinhThucThanhToan() != null) {
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findAll().stream().findFirst().orElse(null);
            ThanhToanHoaDon tthd = ThanhToanHoaDon.builder()
                    .hoaDon(saved)
                    .phuongThucThanhToan(pttt)
                    .soTien(thanhTien)
                    .maGiaoDich("TXN-" + System.currentTimeMillis())
                    .ghiChu(request.getHinhThucThanhToan())
                    .ngayThanhToan(now)
                    .trangThai(1)
                    .build();
            thanhToanHoaDonRepository.save(tthd);
        }

        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(saved)
                .nhanVien(nv)
                .hanhDong("Tạo hóa đơn (" + (isPos ? "Tại quầy POS" : "Online Web") + ")")
                .ghiChu("Khởi tạo đơn hàng với " + chiTiets.size() + " sản phẩm")
                .ngayTao(now)
                .build();
        lichSuHoaDonRepository.save(log);

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HoaDonResponseDTO capNhatTrangThai(Long id, CapNhatTrangThaiRequest request) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        String newStatus = switch (request.getTrangThaiMoi()) {
            case 0 -> "CHO_THANH_TOAN";
            case 1 -> "CHO_XAC_NHAN";
            case 2 -> "DANG_GIAO";
            case 3 -> "HOAN_THANH";
            case 4 -> "DA_HUY";
            case 5 -> "YEU_CAU_HOAN";
            case 6 -> "DA_HOAN";
            default -> hd.getTrangThaiHoaDon();
        };

        if ("DA_HUY".equals(newStatus) && !"DA_HUY".equals(hd.getTrangThaiHoaDon())) {
            revertStockAndVoucher(hd, request.getGhiChu());
        }

        if ("HOAN_THANH".equals(newStatus) && hd.getNgayNhanHang() == null) {
            hd.setNgayNhanHang(LocalDateTime.now());
            hd.setNgayGiaoHang(LocalDateTime.now());
            hd.setTrangThaiThanhToan("DA_THANH_TOAN");
        }

        hd.setTrangThaiHoaDon(newStatus);
        hd.setNgayCapNhat(LocalDateTime.now());
        if (request.getGhiChu() != null && !request.getGhiChu().isBlank()) {
            hd.setGhiChu((hd.getGhiChu() != null ? hd.getGhiChu() + " | " : "") + request.getGhiChu());
        }

        HoaDon updated = hoaDonRepository.save(hd);

        NhanVien nv = null;
        if (request.getIdNhanVienThaoTac() != null) {
            nv = nhanVienRepository.findById(request.getIdNhanVienThaoTac()).orElse(null);
        }
        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(updated)
                .nhanVien(nv)
                .hanhDong("Chuyển trạng thái sang: " + getTenTrangThaiString(newStatus))
                .ghiChu(request.getGhiChu())
                .ngayTao(LocalDateTime.now())
                .build();
        lichSuHoaDonRepository.save(log);

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void huyHoaDon(Long id, String lyDoHuy) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        if ("DA_HUY".equals(hd.getTrangThaiHoaDon())) {
            throw new RuntimeException("Hóa đơn đã bị hủy trước đó!");
        }

        revertStockAndVoucher(hd, lyDoHuy);

        hd.setTrangThaiHoaDon("DA_HUY");
        hd.setLyDoHuy(lyDoHuy);
        hd.setNgayCapNhat(LocalDateTime.now());
        hoaDonRepository.save(hd);

        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(hd)
                .hanhDong("Hủy hóa đơn")
                .ghiChu("Lý do: " + lyDoHuy)
                .ngayTao(LocalDateTime.now())
                .build();
        lichSuHoaDonRepository.save(log);
    }

    @Override
    @Transactional
    public HoaDonResponseDTO yeuCauHoanHang(Long id, YeuCauHoanHangRequest request) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        if (!"HOAN_THANH".equalsIgnoreCase(hd.getTrangThaiHoaDon())) {
            throw new RuntimeException("Chỉ có thể yêu cầu hoàn hàng cho các đơn hàng đã Hoàn thành!");
        }

        LocalDateTime receivedDate = hd.getNgayNhanHang() != null ? hd.getNgayNhanHang() : hd.getNgayTao();
        long daysBetween = Duration.between(receivedDate, LocalDateTime.now()).toDays();
        if (daysBetween > 7) {
            throw new RuntimeException("Đơn hàng đã nhận quá 7 ngày (" + daysBetween + " ngày), không đủ điều kiện đổi/hoàn hàng!");
        }

        hd.setTrangThaiHoaDon("YEU_CAU_HOAN");
        hd.setLyDoHoan(request.getLyDoHoan().trim());
        hd.setNgayHoan(LocalDateTime.now());
        hd.setTienHoan(request.getTienHoan() != null ? request.getTienHoan() : hd.getTongTienThanhToan());
        hd.setNgayCapNhat(LocalDateTime.now());

        HoaDon saved = hoaDonRepository.save(hd);

        NhanVien nv = request.getIdNhanVien() != null ? nhanVienRepository.findById(request.getIdNhanVien()).orElse(null) : null;
        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(saved)
                .nhanVien(nv)
                .hanhDong("Yêu cầu hoàn hàng")
                .ghiChu("Lý do: " + request.getLyDoHoan())
                .ngayTao(LocalDateTime.now())
                .build();
        lichSuHoaDonRepository.save(log);

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HoaDonResponseDTO xacNhanHoanHang(Long id, Long idNhanVien) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        if (!"YEU_CAU_HOAN".equalsIgnoreCase(hd.getTrangThaiHoaDon())) {
            throw new RuntimeException("Đơn hàng không ở trạng thái yêu cầu hoàn!");
        }

        List<ChiTietHoaDon> items = chiTietHoaDonRepository.findByHoaDonId(id);
        for (ChiTietHoaDon item : items) {
            if (item.getSanPhamChiTiet() != null) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findByIdWithLock(item.getSanPhamChiTiet().getId()).orElse(null);
                if (spct != null) {
                    spct.setSoLuongTon(spct.getSoLuongTon() + item.getSoLuong());
                    spct.setNgaySua(LocalDateTime.now());
                    sanPhamChiTietRepository.save(spct);
                }
            }
        }

        hd.setTrangThaiHoaDon("DA_HOAN");
        hd.setNgayCapNhat(LocalDateTime.now());
        HoaDon saved = hoaDonRepository.save(hd);

        NhanVien nv = idNhanVien != null ? nhanVienRepository.findById(idNhanVien).orElse(null) : null;
        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(saved)
                .nhanVien(nv)
                .hanhDong("Chấp thuận hoàn hàng (Đã hoàn hàng)")
                .ghiChu("Đã hoàn lại " + saved.getTienHoan() + " đ và cộng lại tồn kho")
                .ngayTao(LocalDateTime.now())
                .build();
        lichSuHoaDonRepository.save(log);

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HoaDonResponseDTO tuChoiHoanHang(Long id, String lyDoTuChoi, Long idNhanVien) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        if (!"YEU_CAU_HOAN".equalsIgnoreCase(hd.getTrangThaiHoaDon())) {
            throw new RuntimeException("Đơn hàng không ở trạng thái yêu cầu hoàn!");
        }

        hd.setTrangThaiHoaDon("HOAN_THANH");
        hd.setNgayCapNhat(LocalDateTime.now());
        HoaDon saved = hoaDonRepository.save(hd);

        NhanVien nv = idNhanVien != null ? nhanVienRepository.findById(idNhanVien).orElse(null) : null;
        LichSuHoaDon log = LichSuHoaDon.builder()
                .hoaDon(saved)
                .nhanVien(nv)
                .hanhDong("Từ chối yêu cầu hoàn hàng -> Đơn về Hoàn thành")
                .ghiChu("Lý do từ chối: " + lyDoTuChoi)
                .ngayTao(LocalDateTime.now())
                .build();
        lichSuHoaDonRepository.save(log);

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponseDTO> layHoaDonChoTaiQuay() {
        return hoaDonRepository.findByTrangThaiHoaDonOrderByNgayTaoDesc("CHO_THANH_TOAN")
                .stream()
                .filter(h -> "TAI_QUAY".equalsIgnoreCase(h.getLoaiHoaDon()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void revertStockAndVoucher(HoaDon hd, String reason) {
        List<ChiTietHoaDon> items = chiTietHoaDonRepository.findByHoaDonId(hd.getId());
        for (ChiTietHoaDon item : items) {
            if (item.getSanPhamChiTiet() != null) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findByIdWithLock(item.getSanPhamChiTiet().getId()).orElse(null);
                if (spct != null) {
                    spct.setSoLuongTon(spct.getSoLuongTon() + item.getSoLuong());
                    spct.setNgaySua(LocalDateTime.now());
                    sanPhamChiTietRepository.save(spct);
                }
            }
        }

        if (hd.getPhieuGiamGia() != null) {
            PhieuGiamGia pgg = hd.getPhieuGiamGia();
            if (pgg.getSoLuongDaDung() > 0) {
                pgg.setSoLuongDaDung(pgg.getSoLuongDaDung() - 1);
                phieuGiamGiaRepository.save(pgg);
            }

            if (hd.getKhachHang() != null) {
                Optional<KhachHangPhieuGiamGia> khpgg = khachHangPhieuGiamGiaRepository
                        .findByKhachHangIdAndPhieuGiamGiaId(hd.getKhachHang().getId(), pgg.getId());
                if (khpgg.isPresent()) {
                    KhachHangPhieuGiamGia k = khpgg.get();
                    k.setTrangThai(1);
                    k.setNgaySuDung(null);
                    khachHangPhieuGiamGiaRepository.save(k);
                }
            }
        }
    }

    private HoaDonResponseDTO mapToDTO(HoaDon h) {
        Integer trangThaiCode = switch (h.getTrangThaiHoaDon()) {
            case "CHO_THANH_TOAN" -> 0;
            case "CHO_XAC_NHAN" -> 1;
            case "DANG_GIAO" -> 2;
            case "HOAN_THANH" -> 3;
            case "DA_HUY" -> 4;
            case "YEU_CAU_HOAN" -> 5;
            case "DA_HOAN" -> 6;
            default -> 0;
        };

        String tenTrangThai = getTenTrangThaiString(h.getTrangThaiHoaDon());

        List<ChiTietHoaDon> chiTiets = chiTietHoaDonRepository.findByHoaDonId(h.getId());
        List<ChiTietHoaDonDTO> cthdDTOs = chiTiets.stream().map(c -> {
            SanPhamChiTiet spct = c.getSanPhamChiTiet();
            String tenSp = spct != null && spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : "Sản phẩm WindFit";
            String sku = spct != null ? spct.getMaSpct() : "";
            String mau = spct != null && spct.getMauSac() != null ? spct.getMauSac().getTenMau() : "";
            String size = spct != null && spct.getKichCo() != null ? spct.getKichCo().getTenKichCo() : "";

            return ChiTietHoaDonDTO.builder()
                    .id(c.getId())
                    .idSanPhamChiTiet(spct != null ? spct.getId() : null)
                    .tenSanPham(tenSp)
                    .maSku(sku)
                    .tenMau(mau)
                    .tenKichCo(size)
                    .urlAnh("https://images.unsplash.com/photo-1544441893-675973e31985?w=200")
                    .soLuong(c.getSoLuong())
                    .donGia(c.getDonGia())
                    .thanhTien(c.getThanhTien())
                    .build();
        }).collect(Collectors.toList());

        List<ThanhToanHoaDon> thanhToans = thanhToanHoaDonRepository.findByHoaDonId(h.getId());
        List<ThanhToanHoaDonDTO> tthdDTOs = thanhToans.stream().map(t -> ThanhToanHoaDonDTO.builder()
                .id(t.getId())
                .hinhThuc(t.getPhuongThucThanhToan() != null ? t.getPhuongThucThanhToan().getTenPhuongThuc() : (t.getGhiChu() != null ? t.getGhiChu() : "TIỀN MẶT"))
                .soTien(t.getSoTien())
                .maGiaoDich(t.getMaGiaoDich())
                .ngayThanhToan(t.getNgayThanhToan())
                .trangThai(t.getTrangThai())
                .build()
        ).collect(Collectors.toList());

        List<LichSuHoaDon> logs = lichSuHoaDonRepository.findByHoaDonIdOrderByNgayTaoAsc(h.getId());
        List<LichSuHoaDonDTO> logDTOs = logs.stream().map(l -> LichSuHoaDonDTO.builder()
                .id(l.getId())
                .idHoaDon(l.getHoaDon() != null ? l.getHoaDon().getId() : null)
                .idNhanVien(l.getNhanVien() != null ? l.getNhanVien().getId() : null)
                .tenNhanVien(l.getNhanVien() != null ? l.getNhanVien().getHoTen() : null)
                .hanhDong(l.getHanhDong())
                .ghiChu(l.getGhiChu())
                .ngayTao(l.getNgayTao())
                .build()
        ).collect(Collectors.toList());

        return HoaDonResponseDTO.builder()
                .id(h.getId())
                .maHoaDon(h.getMaHoaDon())
                .loaiHoaDon(h.getLoaiHoaDon())
                .kenhBan(h.getLoaiHoaDon())
                .idKhachHang(h.getKhachHang() != null ? h.getKhachHang().getId() : null)
                .tenKhachHang(h.getKhachHang() != null ? h.getKhachHang().getHoTen() : null)
                .sdtKhachHang(h.getKhachHang() != null ? h.getKhachHang().getSoDienThoai() : null)
                .tenNguoiNhan(h.getTenNguoiNhan())
                .sdtNguoiNhan(h.getSdtNguoiNhan())
                .diaChiGiao(h.getDiaChiNhan())
                .idNhanVien(h.getNhanVien() != null ? h.getNhanVien().getId() : null)
                .tenNhanVien(h.getNhanVien() != null ? h.getNhanVien().getHoTen() : null)
                .maPhieuGiamGia(h.getPhieuGiamGia() != null ? h.getPhieuGiamGia().getMaPhieuGiamGia() : null)
                .tongTien(h.getTongTienHang())
                .tienGiam(h.getTienGiam())
                .phiShip(h.getPhiShip())
                .thanhTien(h.getTongTienThanhToan())
                .ghiChu(h.getGhiChu())
                .lyDoHuy(h.getLyDoHuy())
                .ngayTao(h.getNgayTao())
                .ngaySua(h.getNgayCapNhat())
                .ngayNhanHang(h.getNgayNhanHang())
                .trangThai(trangThaiCode)
                .tenTrangThai(tenTrangThai)
                .trangThaiHoaDon(h.getTrangThaiHoaDon())
                .trangThaiThanhToan(h.getTrangThaiThanhToan())
                .lyDoHoan(h.getLyDoHoan())
                .ngayHoan(h.getNgayHoan())
                .tienHoan(h.getTienHoan())
                .danhSachChiTiet(cthdDTOs)
                .danhSachThanhToan(tthdDTOs)
                .danhSachLichSu(logDTOs)
                .build();
    }

    private String getTenTrangThaiString(String code) {
        return switch (code) {
            case "CHO_THANH_TOAN" -> "Chờ thanh toán";
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DANG_GIAO" -> "Đang giao hàng";
            case "HOAN_THANH" -> "Hoàn thành";
            case "DA_HUY" -> "Đã hủy";
            case "YEU_CAU_HOAN" -> "Yêu cầu hoàn hàng";
            case "DA_HOAN" -> "Đã hoàn hàng";
            default -> code;
        };
    }
}
