package com.windfit.service;

import com.windfit.dto.PageResponse;
import com.windfit.dto.discount.*;
import com.windfit.dto.product.SanPhamChiTietResponseDTO;
import com.windfit.entity.*;
import com.windfit.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public interface GiamGiaService {

    // --- PHIẾU GIẢM GIÁ ---
    PageResponse<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(
            String keyword,
            String loaiGiamGia,
            String nhomPhieu,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size,
            String sort
    );

    PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id);

    PhieuGiamGiaResponseDTO createPhieuGiamGia(PhieuGiamGiaRequestDTO request);

    PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request);

    void toggleStatusPhieuGiamGia(Long id);

    ThongKeGiamGiaDTO getThongKePhieuGiamGia();

    ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request);

    ValidateVoucherResponse findBestVoucher(Long idKhachHang, BigDecimal tongTienDonHang);

    List<PhieuGiamGiaResponseDTO> getAvailableVouchers();

    PageResponse<KhachHangSimpleDTO> searchKhachHang(String keyword, int page, int size);

    List<Long> getAssignedCustomerIds(Long idVoucher);

    PageResponse<KhachHangSimpleDTO> getAssignedCustomersPaged(Long idVoucher, String keyword, int page, int size);

    void assignCustomersToVoucher(Long idVoucher, List<Long> customerIds);

    void unassignCustomersFromVoucher(Long idVoucher, List<Long> customerIds);

    // --- ĐỢT GIẢM GIÁ ---
    PageResponse<DotGiamGiaResponseDTO> getDotGiamGiaList(
            String keyword,
            String hinhThuc,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size,
            String sort
    );

    DotGiamGiaResponseDTO getDotGiamGiaById(Long id);

    DotGiamGiaResponseDTO createDotGiamGia(DotGiamGiaRequestDTO request);

    DotGiamGiaResponseDTO updateDotGiamGia(Long id, DotGiamGiaRequestDTO request);

    void toggleStatusDotGiamGia(Long id);

    ThongKeGiamGiaDTO getThongKeDotGiamGia();

    PageResponse<DotGiamGiaChiTietResponseDTO> getSkusOfCampaign(Long idDotGiamGia, String keyword, int page, int size);

    void assignSkusToCampaign(Long idDotGiamGia, List<DotGiamGiaItemRequestDTO> items);

    void removeSkusFromCampaign(Long idDotGiamGia, List<Long> idSanPhamChiTiets);

    GiaHienTaiSpctResponseDTO getGiaHienTaiSpct(Long spctId);

    Map<Long, GiaHienTaiSpctResponseDTO> getGiaHienTaiBatch(List<Long> spctIds);

    BigDecimal getActiveDiscountPriceForSpct(Long spctId, BigDecimal originalPrice);

    Map<Long, BigDecimal> getActiveDiscountPricesForSpctList(List<Long> spctIds);

    PageResponse<SanPhamChiTietResponseDTO> searchSanPhamChiTiet(String keyword, Long idSanPham, int page, int size);
}

@Slf4j
@Service
@RequiredArgsConstructor
class GiamGiaServiceImpl implements GiamGiaService {

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;
    private final KhachHangRepository khachHangRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================================================
    // 1. PHIẾU GIẢM GIÁ (VOUCHER)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(
            String keyword,
            String loaiGiamGia,
            String nhomPhieu,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanType = (loaiGiamGia != null && !loaiGiamGia.trim().isEmpty()) ? loaiGiamGia.trim().toUpperCase() : null;
        String cleanGroup = (nhomPhieu != null && !nhomPhieu.trim().isEmpty()) ? nhomPhieu.trim().toUpperCase() : null;

        Specification<PhieuGiamGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cleanKw != null) {
                String pattern = "%" + cleanKw.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maPhieuGiamGia")), pattern),
                        cb.like(cb.lower(root.get("tenPhieuGiamGia")), pattern)
                ));
            }
            if (cleanType != null) {
                predicates.add(cb.equal(root.get("loaiGiamGia"), cleanType));
            }
            if (cleanGroup != null) {
                predicates.add(cb.equal(root.get("nhomPhieu"), cleanGroup));
            }
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }
            if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBatDau"), tuNgay));
            }
            if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayKetThuc"), denNgay));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PhieuGiamGia> resultPage = phieuGiamGiaRepository.findAll(spec, pageable);
        return PageResponse.from(resultPage.map(this::mapVoucherToDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));
        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public PhieuGiamGiaResponseDTO createPhieuGiamGia(PhieuGiamGiaRequestDTO request) {
        String code = request.getMaPhieuGiamGia();
        if (code == null || code.trim().isEmpty()) {
            code = generateNextVoucherCode();
        } else {
            code = code.trim().toUpperCase();
            if (phieuGiamGiaRepository.existsByMaPhieuGiamGia(code)) {
                throw new IllegalArgumentException("Mã phiếu giảm giá '" + code + "' đã tồn tại trong hệ thống!");
            }
        }

        validateVoucherBusinessRules(request);

        String nhomPhieu = (request.getNhomPhieu() != null && !request.getNhomPhieu().trim().isEmpty())
                ? request.getNhomPhieu().trim().toUpperCase() : "CHUNG";

        PhieuGiamGia p = PhieuGiamGia.builder()
                .maPhieuGiamGia(code)
                .tenPhieuGiamGia(request.getTenPhieuGiamGia().trim())
                .moTa(request.getMoTa() != null ? request.getMoTa().trim() : null)
                .nhomPhieu(nhomPhieu)
                .loaiGiamGia(request.getLoaiGiamGia().trim().toUpperCase())
                .giaTriGiam(request.getGiaTriGiam())
                .giamToiDa(request.getGiamToiDa())
                .donToiThieu(request.getDonToiThieu())
                .soLuong(request.getSoLuong())
                .soLuongDaDung(0)
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .ngayTao(LocalDateTime.now())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .build();

        p = phieuGiamGiaRepository.save(p);

        if (request.getCustomerIds() != null && !request.getCustomerIds().isEmpty()) {
            assignCustomersToVoucher(p.getId(), request.getCustomerIds());
        }

        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));

        if (request.getMaPhieuGiamGia() != null && !request.getMaPhieuGiamGia().trim().equalsIgnoreCase(p.getMaPhieuGiamGia())) {
            String newCode = request.getMaPhieuGiamGia().trim().toUpperCase();
            if (phieuGiamGiaRepository.existsByMaPhieuGiamGia(newCode)) {
                throw new IllegalArgumentException("Mã phiếu giảm giá '" + newCode + "' đã tồn tại!");
            }
            p.setMaPhieuGiamGia(newCode);
        }

        validateVoucherBusinessRules(request);

        String nhomPhieu = (request.getNhomPhieu() != null && !request.getNhomPhieu().trim().isEmpty())
                ? request.getNhomPhieu().trim().toUpperCase() : "CHUNG";

        p.setTenPhieuGiamGia(request.getTenPhieuGiamGia().trim());
        p.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);
        p.setNhomPhieu(nhomPhieu);
        p.setLoaiGiamGia(request.getLoaiGiamGia().trim().toUpperCase());
        p.setGiaTriGiam(request.getGiaTriGiam());
        p.setGiamToiDa(request.getGiamToiDa());
        p.setDonToiThieu(request.getDonToiThieu());
        p.setSoLuong(request.getSoLuong());
        p.setNgayBatDau(request.getNgayBatDau());
        p.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) p.setTrangThai(request.getTrangThai());

        p = phieuGiamGiaRepository.save(p);

        if (request.getCustomerIds() != null) {
            assignCustomersToVoucher(p.getId(), request.getCustomerIds());
        }

        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public void toggleStatusPhieuGiamGia(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));

        int currentStatus = (p.getTrangThai() != null) ? p.getTrangThai() : 1;
        if (currentStatus == 1) {
            p.setTrangThai(0);
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(p.getNgayKetThuc())) {
                throw new IllegalArgumentException("Phiếu giảm giá đã hết hạn, không thể kích hoạt lại!");
            }
            if (p.getSoLuongDaDung() >= p.getSoLuong()) {
                throw new IllegalArgumentException("Phiếu giảm giá đã hết lượt sử dụng, không thể kích hoạt lại!");
            }
            p.setTrangThai(1);
        }
        phieuGiamGiaRepository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeGiamGiaDTO getThongKePhieuGiamGia() {
        LocalDateTime now = LocalDateTime.now();
        long tongSo = phieuGiamGiaRepository.count();
        long dangDienRa = phieuGiamGiaRepository.countActiveVouchers(now);
        long sapDienRa = phieuGiamGiaRepository.countUpcomingVouchers(now);
        long daKetThuc = phieuGiamGiaRepository.countExpiredOrDepletedVouchers(now);
        long ngungApDung = phieuGiamGiaRepository.countByTrangThai(0);

        return ThongKeGiamGiaDTO.builder()
                .tongSo(tongSo)
                .dangDienRa(dangDienRa)
                .sapDienRa(sapDienRa)
                .daKetThuc(daKetThuc)
                .ngungApDung(ngungApDung)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request) {
        String code = request.getEffectiveCode();
        if (code == null) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher không được để trống!")
                    .build();
        }

        Optional<PhieuGiamGia> pOpt = phieuGiamGiaRepository.findByMaPhieuGiamGia(code.toUpperCase());
        if (pOpt.isEmpty()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher '" + code + "' không tồn tại trong hệ thống!")
                    .build();
        }

        PhieuGiamGia pgg = pOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (pgg.getTrangThai() == null || pgg.getTrangThai() == 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher đang tạm ngưng áp dụng!")
                    .build();
        }
        if (now.isBefore(pgg.getNgayBatDau())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher chưa đến thời gian áp dụng!")
                    .build();
        }
        if (now.isAfter(pgg.getNgayKetThuc())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher đã hết hạn sử dụng!")
                    .build();
        }
        if (pgg.getSoLuongDaDung() >= pgg.getSoLuong()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher đã hết lượt sử dụng!")
                    .build();
        }

        BigDecimal tongTien = request.getTongTienDonHang() != null ? request.getTongTienDonHang() : BigDecimal.ZERO;
        if (tongTien.compareTo(pgg.getDonToiThieu()) < 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Đơn hàng tối thiểu phải từ " + formatMoney(pgg.getDonToiThieu()) + " mới được áp dụng voucher này!")
                    .build();
        }

        Long customerId = resolveCustomerId(request.getIdKhachHang(), request.getMaKhachHang());

        // Kiểm tra nhóm phiếu riêng / VIP / Sinh nhật: chỉ khách hàng được chỉ định mới dùng được
        String group = pgg.getNhomPhieu() != null ? pgg.getNhomPhieu() : "CHUNG";
        boolean isRestricted = "VIP_SINH_NHAT".equalsIgnoreCase(group) || "CA_NHAN".equalsIgnoreCase(group);
        List<KhachHangPhieuGiamGia> assignedList = khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaIdAndTrangThai(pgg.getId(), 1);

        if (isRestricted || !assignedList.isEmpty()) {
            if (customerId == null) {
                return ValidateVoucherResponse.builder()
                        .valid(false)
                        .message("Mã giảm giá này chỉ áp dụng cho khách hàng được chỉ định!")
                        .build();
            }
            boolean isAssigned = assignedList.stream()
                    .anyMatch(item -> item.getKhachHang() != null && item.getKhachHang().getId().equals(customerId));
            if (!isAssigned) {
                return ValidateVoucherResponse.builder()
                        .valid(false)
                        .message("Mã giảm giá này chỉ áp dụng cho khách hàng được chỉ định!")
                        .build();
            }
        }

        // Mỗi khách chỉ dùng một mã một lần
        if (customerId != null) {
            Optional<KhachHangPhieuGiamGia> khPgg = khachHangPhieuGiamGiaRepository
                    .findByKhachHangIdAndPhieuGiamGiaId(customerId, pgg.getId());
            if (khPgg.isPresent()) {
                KhachHangPhieuGiamGia relation = khPgg.get();
                if ((relation.getTrangThai() != null && relation.getTrangThai() == 0) || relation.getNgaySuDung() != null) {
                    return ValidateVoucherResponse.builder()
                            .valid(false)
                            .message("Quý khách đã sử dụng mã giảm giá này rồi!")
                            .build();
                }
            }
        }

        // Tính toán số tiền giảm
        BigDecimal discountAmount;
        if ("PHAN_TRAM".equalsIgnoreCase(pgg.getLoaiGiamGia())) {
            discountAmount = tongTien.multiply(pgg.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (pgg.getGiamToiDa() != null && discountAmount.compareTo(pgg.getGiamToiDa()) > 0) {
                discountAmount = pgg.getGiamToiDa();
            }
        } else {
            discountAmount = pgg.getGiaTriGiam();
        }

        if (discountAmount.compareTo(tongTien) > 0) {
            discountAmount = tongTien;
        }

        BigDecimal finalTotal = tongTien.subtract(discountAmount);

        return ValidateVoucherResponse.builder()
                .valid(true)
                .idVoucher(pgg.getId())
                .maVoucher(pgg.getMaPhieuGiamGia())
                .tenVoucher(pgg.getTenPhieuGiamGia())
                .loaiGiamGia(pgg.getLoaiGiamGia())
                .giaTriGiam(pgg.getGiaTriGiam())
                .soTienGiam(discountAmount)
                .thanhTienSauGiam(finalTotal)
                .message("Áp dụng mã voucher thành công!")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateVoucherResponse findBestVoucher(Long idKhachHang, BigDecimal tongTienDonHang) {
        List<PhieuGiamGia> available = phieuGiamGiaRepository.findAvailableVouchers(LocalDateTime.now());
        ValidateVoucherResponse best = null;

        for (PhieuGiamGia pgg : available) {
            ValidateVoucherRequest req = ValidateVoucherRequest.builder()
                    .maVoucher(pgg.getMaPhieuGiamGia())
                    .tongTienDonHang(tongTienDonHang)
                    .idKhachHang(idKhachHang)
                    .build();
            ValidateVoucherResponse res = validateVoucher(req);
            if (Boolean.TRUE.equals(res.getValid())) {
                if (best == null || res.getSoTienGiam().compareTo(best.getSoTienGiam()) > 0) {
                    best = res;
                }
            }
        }

        if (best == null) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Không tìm thấy mã giảm giá nào phù hợp cho đơn hàng này!")
                    .build();
        }
        return best;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuGiamGiaResponseDTO> getAvailableVouchers() {
        return phieuGiamGiaRepository.findAvailableVouchers(LocalDateTime.now()).stream()
                .map(this::mapVoucherToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KhachHangSimpleDTO> searchKhachHang(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<KhachHang> pageResult = khachHangRepository.searchKhachHang(cleanKw, 1, pageable);
        return PageResponse.from(pageResult.map(this::mapCustomerToSimpleDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAssignedCustomerIds(Long idVoucher) {
        return khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaIdAndTrangThai(idVoucher, 1).stream()
                .map(item -> item.getKhachHang().getId())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KhachHangSimpleDTO> getAssignedCustomersPaged(Long idVoucher, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<KhachHangPhieuGiamGia> pageResult = khachHangPhieuGiamGiaRepository
                .searchAssignedCustomers(idVoucher, cleanKw, pageable);

        return PageResponse.from(pageResult.map(item -> mapCustomerToSimpleDTO(item.getKhachHang())));
    }

    @Override
    @Transactional
    public void assignCustomersToVoucher(Long idVoucher, List<Long> customerIds) {
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idVoucher)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + idVoucher));

        if (customerIds == null || customerIds.isEmpty()) return;

        List<KhachHangPhieuGiamGia> existing = khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaId(idVoucher);

        for (Long cId : customerIds) {
            KhachHang kh = khachHangRepository.findById(cId).orElse(null);
            if (kh != null) {
                Optional<KhachHangPhieuGiamGia> matched = existing.stream()
                        .filter(e -> e.getKhachHang().getId().equals(cId))
                        .findFirst();

                if (matched.isPresent()) {
                    KhachHangPhieuGiamGia item = matched.get();
                    item.setTrangThai(1);
                    khachHangPhieuGiamGiaRepository.save(item);
                } else {
                    KhachHangPhieuGiamGia newItem = KhachHangPhieuGiamGia.builder()
                            .phieuGiamGia(pgg)
                            .khachHang(kh)
                            .trangThai(1)
                            .build();
                    khachHangPhieuGiamGiaRepository.save(newItem);
                }
            }
        }
    }

    @Override
    @Transactional
    public void unassignCustomersFromVoucher(Long idVoucher, List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) return;

        List<KhachHangPhieuGiamGia> existing = khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaId(idVoucher);
        for (KhachHangPhieuGiamGia item : existing) {
            if (customerIds.contains(item.getKhachHang().getId())) {
                item.setTrangThai(0);
                khachHangPhieuGiamGiaRepository.save(item);
            }
        }
    }

    // =========================================================================
    // 2. ĐỢT GIẢM GIÁ (CAMPAIGN / FLASH SALE)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DotGiamGiaResponseDTO> getDotGiamGiaList(
            String keyword,
            String hinhThuc,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanType = (hinhThuc != null && !hinhThuc.trim().isEmpty()) ? hinhThuc.trim().toUpperCase() : null;

        Specification<DotGiamGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cleanKw != null) {
                String pattern = "%" + cleanKw.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maDotGiamGia")), pattern),
                        cb.like(cb.lower(root.get("tenDotGiamGia")), pattern)
                ));
            }
            if (cleanType != null) {
                predicates.add(cb.equal(root.get("hinhThuc"), cleanType));
            }
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }
            if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBatDau"), tuNgay));
            }
            if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayKetThuc"), denNgay));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<DotGiamGia> resultPage = dotGiamGiaRepository.findAll(spec, pageable);
        return PageResponse.from(resultPage.map(this::mapCampaignToDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public DotGiamGiaResponseDTO getDotGiamGiaById(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));
        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public DotGiamGiaResponseDTO createDotGiamGia(DotGiamGiaRequestDTO request) {
        String code = request.getMaDotGiamGia();
        if (code == null || code.trim().isEmpty()) {
            code = generateNextCampaignCode();
        } else {
            code = code.trim().toUpperCase();
            if (dotGiamGiaRepository.existsByMaDotGiamGia(code)) {
                throw new IllegalArgumentException("Mã đợt giảm giá '" + code + "' đã tồn tại trong hệ thống!");
            }
        }

        validateCampaignBusinessRules(request);

        // Kiểm tra SKU không trùng đợt khác đang trùng thời gian
        if (request.getDanhSachChiTiet() != null && !request.getDanhSachChiTiet().isEmpty()) {
            validateNoConflictingSkus(request.getDanhSachChiTiet(), request.getNgayBatDau(), request.getNgayKetThuc(), null);
        }

        String hinhThuc = (request.getHinhThuc() != null && !request.getHinhThuc().trim().isEmpty())
                ? request.getHinhThuc().trim().toUpperCase() : "THEO_MUA";

        DotGiamGia d = DotGiamGia.builder()
                .maDotGiamGia(code)
                .tenDotGiamGia(request.getTenDotGiamGia().trim())
                .moTa(request.getMoTa())
                .dieuKienApDung(request.getDieuKienApDung())
                .hinhThuc(hinhThuc)
                .gioBatDau(request.getGioBatDau())
                .gioKetThuc(request.getGioKetThuc())
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .build();

        d = dotGiamGiaRepository.save(d);

        if (request.getDanhSachChiTiet() != null) {
            saveCampaignDetails(d, request.getDanhSachChiTiet());
        }

        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public DotGiamGiaResponseDTO updateDotGiamGia(Long id, DotGiamGiaRequestDTO request) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));

        if (request.getMaDotGiamGia() != null && !request.getMaDotGiamGia().trim().equalsIgnoreCase(d.getMaDotGiamGia())) {
            String newCode = request.getMaDotGiamGia().trim().toUpperCase();
            if (dotGiamGiaRepository.existsByMaDotGiamGia(newCode)) {
                throw new IllegalArgumentException("Mã đợt giảm giá '" + newCode + "' đã tồn tại!");
            }
            d.setMaDotGiamGia(newCode);
        }

        validateCampaignBusinessRules(request);

        // Kiểm tra SKU không trùng đợt khác
        if (request.getDanhSachChiTiet() != null && !request.getDanhSachChiTiet().isEmpty()) {
            validateNoConflictingSkus(request.getDanhSachChiTiet(), request.getNgayBatDau(), request.getNgayKetThuc(), d.getId());
        }

        String hinhThuc = (request.getHinhThuc() != null && !request.getHinhThuc().trim().isEmpty())
                ? request.getHinhThuc().trim().toUpperCase() : "THEO_MUA";

        d.setTenDotGiamGia(request.getTenDotGiamGia().trim());
        d.setMoTa(request.getMoTa());
        d.setDieuKienApDung(request.getDieuKienApDung());
        d.setHinhThuc(hinhThuc);
        d.setGioBatDau(request.getGioBatDau());
        d.setGioKetThuc(request.getGioKetThuc());
        d.setNgayBatDau(request.getNgayBatDau());
        d.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) d.setTrangThai(request.getTrangThai());

        d = dotGiamGiaRepository.save(d);

        if (request.getDanhSachChiTiet() != null) {
            // Đánh dấu chi tiết cũ là ngưng áp dụng (không xóa DB)
            List<DotGiamGiaChiTiet> existingDetails = dotGiamGiaChiTietRepository.findByDotGiamGiaId(d.getId());
            for (DotGiamGiaChiTiet detail : existingDetails) {
                detail.setTrangThai(0);
                dotGiamGiaChiTietRepository.save(detail);
            }
            saveCampaignDetails(d, request.getDanhSachChiTiet());
        }

        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public void toggleStatusDotGiamGia(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));

        int currentStatus = (d.getTrangThai() != null) ? d.getTrangThai() : 1;
        if (currentStatus == 1) {
            d.setTrangThai(0);
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(d.getNgayKetThuc())) {
                throw new IllegalArgumentException("Đợt giảm giá đã kết thúc, không thể kích hoạt lại!");
            }
            // Kiểm tra các SKU hiện tại xem có bị xung đột với đợt khác đang chạy không
            List<DotGiamGiaChiTiet> activeSkus = dotGiamGiaChiTietRepository.findByDotGiamGiaIdAndTrangThai(d.getId(), 1);
            List<DotGiamGiaItemRequestDTO> itemsToCheck = activeSkus.stream()
                    .map(c -> DotGiamGiaItemRequestDTO.builder()
                            .idSanPhamChiTiet(c.getSanPhamChiTiet().getId())
                            .phanTramGiam(c.getPhanTramGiam())
                            .soLuong(c.getSoLuong())
                            .build())
                    .collect(Collectors.toList());
            validateNoConflictingSkus(itemsToCheck, d.getNgayBatDau(), d.getNgayKetThuc(), d.getId());

            d.setTrangThai(1);
        }
        dotGiamGiaRepository.save(d);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeGiamGiaDTO getThongKeDotGiamGia() {
        LocalDateTime now = LocalDateTime.now();
        long tongSo = dotGiamGiaRepository.count();
        long dangDienRa = dotGiamGiaRepository.countActiveCampaigns(now);
        long sapDienRa = dotGiamGiaRepository.countUpcomingCampaigns(now);
        long daKetThuc = dotGiamGiaRepository.countExpiredCampaigns(now);
        long ngungApDung = dotGiamGiaRepository.countByTrangThai(0);

        return ThongKeGiamGiaDTO.builder()
                .tongSo(tongSo)
                .dangDienRa(dangDienRa)
                .sapDienRa(sapDienRa)
                .daKetThuc(daKetThuc)
                .ngungApDung(ngungApDung)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DotGiamGiaChiTietResponseDTO> getSkusOfCampaign(Long idDotGiamGia, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<DotGiamGiaChiTiet> pageResult = dotGiamGiaChiTietRepository
                .searchByDotGiamGiaId(idDotGiamGia, cleanKw, pageable);

        return PageResponse.from(pageResult.map(this::mapCampaignDetailToDTO));
    }

    @Override
    @Transactional
    public void assignSkusToCampaign(Long idDotGiamGia, List<DotGiamGiaItemRequestDTO> items) {
        DotGiamGia d = dotGiamGiaRepository.findById(idDotGiamGia)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + idDotGiamGia));

        if (items == null || items.isEmpty()) return;

        validateNoConflictingSkus(items, d.getNgayBatDau(), d.getNgayKetThuc(), d.getId());
        saveCampaignDetails(d, items);
    }

    @Override
    @Transactional
    public void removeSkusFromCampaign(Long idDotGiamGia, List<Long> idSanPhamChiTiets) {
        if (idSanPhamChiTiets == null || idSanPhamChiTiets.isEmpty()) return;

        List<DotGiamGiaChiTiet> details = dotGiamGiaChiTietRepository.findByDotGiamGiaId(idDotGiamGia);
        for (DotGiamGiaChiTiet c : details) {
            if (idSanPhamChiTiets.contains(c.getSanPhamChiTiet().getId())) {
                c.setTrangThai(0);
                dotGiamGiaChiTietRepository.save(c);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GiaHienTaiSpctResponseDTO getGiaHienTaiSpct(Long spctId) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể sản phẩm ID: " + spctId));

        BigDecimal giaGoc = spct.getGiaBan() != null ? spct.getGiaBan() : BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        List<DotGiamGiaChiTiet> activeDiscounts = dotGiamGiaChiTietRepository.findActiveDiscountsForSpct(spctId, now);
        DotGiamGiaChiTiet effectiveDetail = filterEffectiveDiscount(activeDiscounts, now);

        if (effectiveDetail == null) {
            return GiaHienTaiSpctResponseDTO.builder()
                    .idSanPhamChiTiet(spct.getId())
                    .maSpct(spct.getMaSpct())
                    .giaGoc(giaGoc)
                    .giaSauGiam(giaGoc)
                    .phanTramGiam(BigDecimal.ZERO)
                    .coGiamGia(false)
                    .build();
        }

        return GiaHienTaiSpctResponseDTO.builder()
                .idSanPhamChiTiet(spct.getId())
                .maSpct(spct.getMaSpct())
                .giaGoc(giaGoc)
                .giaSauGiam(effectiveDetail.getGiaSauGiam())
                .phanTramGiam(effectiveDetail.getPhanTramGiam())
                .coGiamGia(true)
                .tenDotGiamGia(effectiveDetail.getDotGiamGia().getTenDotGiamGia())
                .maDotGiamGia(effectiveDetail.getDotGiamGia().getMaDotGiamGia())
                .hinhThuc(effectiveDetail.getDotGiamGia().getHinhThuc())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, GiaHienTaiSpctResponseDTO> getGiaHienTaiBatch(List<Long> spctIds) {
        if (spctIds == null || spctIds.isEmpty()) return Collections.emptyMap();

        Map<Long, GiaHienTaiSpctResponseDTO> map = new HashMap<>();
        for (Long spctId : spctIds) {
            try {
                map.put(spctId, getGiaHienTaiSpct(spctId));
            } catch (Exception e) {
                // Nếu không tìm thấy, bỏ qua
            }
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getActiveDiscountPriceForSpct(Long spctId, BigDecimal originalPrice) {
        GiaHienTaiSpctResponseDTO dto = getGiaHienTaiSpct(spctId);
        return dto.getGiaSauGiam();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getActiveDiscountPricesForSpctList(List<Long> spctIds) {
        if (spctIds == null || spctIds.isEmpty()) return Collections.emptyMap();
        Map<Long, BigDecimal> result = new HashMap<>();
        for (Long id : spctIds) {
            try {
                result.put(id, getActiveDiscountPriceForSpct(id, BigDecimal.ZERO));
            } catch (Exception e) {
                // Bỏ qua
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SanPhamChiTietResponseDTO> searchSanPhamChiTiet(String keyword, Long idSanPham, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<SanPhamChiTiet> pageResult = sanPhamChiTietRepository.searchSanPhamChiTiet(cleanKw, idSanPham, 1, pageable);
        return PageResponse.from(pageResult.map(s -> SanPhamChiTietResponseDTO.builder()
                .id(s.getId())
                .idSanPham(s.getSanPham() != null ? s.getSanPham().getId() : null)
                .tenSanPham(s.getSanPham() != null ? s.getSanPham().getTenSanPham() : "")
                .maSpct(s.getMaSpct())
                .maVach(s.getMaVach())
                .idMauSac(s.getMauSac() != null ? s.getMauSac().getId() : null)
                .tenMauSac(s.getMauSac() != null ? s.getMauSac().getTenMau() : "")
                .idKichCo(s.getKichCo() != null ? s.getKichCo().getId() : null)
                .tenKichCo(s.getKichCo() != null ? s.getKichCo().getTenKichCo() : "")
                .giaBan(s.getGiaBan())
                .soLuongTon(s.getSoLuongTon())
                .trangThai(s.getTrangThai())
                .ngayTao(s.getNgayTao())
                .ngaySua(s.getNgaySua())
                .build()));
    }

    // =========================================================================
    // 3. TÁC VỤ ĐỊNH KỲ (@Scheduled)
    // =========================================================================

    @Scheduled(cron = "0 */5 * * * *")
    public void scanAndLogExpiredDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        long expVouchers = phieuGiamGiaRepository.countExpiredOrDepletedVouchers(now);
        long expCampaigns = dotGiamGiaRepository.countExpiredCampaigns(now);
        log.info("[WindFit Scheduler] Đã quét giảm giá: {} voucher hết hạn/lượt, {} đợt giảm giá kết thúc", expVouchers, expCampaigns);
    }

    // =========================================================================
    // HELPER METHODS & BUSINESS VALIDATIONS
    // =========================================================================

    private void validateVoucherBusinessRules(PhieuGiamGiaRequestDTO request) {
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!");
        }

        if ("PHAN_TRAM".equalsIgnoreCase(request.getLoaiGiamGia())) {
            if (request.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Phần trăm giảm giá không được vượt quá 100%!");
            }
        }

        if (request.getGiamToiDa() != null && request.getGiamToiDa().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mức giảm tối đa không được âm!");
        }
    }

    private void validateCampaignBusinessRules(DotGiamGiaRequestDTO request) {
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!");
        }

        if ("FLASH_SALE".equalsIgnoreCase(request.getHinhThuc())) {
            if (request.getGioBatDau() == null || request.getGioKetThuc() == null) {
                throw new IllegalArgumentException("Hình thức Flash Sale giờ vàng yêu cầu phải có giờ bắt đầu và giờ kết thúc!");
            }
            if (!request.getGioKetThuc().isAfter(request.getGioBatDau())) {
                throw new IllegalArgumentException("Giờ kết thúc Flash Sale phải sau giờ bắt đầu!");
            }
        }
    }

    private void validateNoConflictingSkus(
            List<DotGiamGiaItemRequestDTO> items,
            LocalDateTime start,
            LocalDateTime end,
            Long currentCampaignId
    ) {
        for (DotGiamGiaItemRequestDTO item : items) {
            List<DotGiamGiaChiTiet> conflicts = dotGiamGiaChiTietRepository
                    .findConflictingDiscounts(item.getIdSanPhamChiTiet(), start, end, currentCampaignId);

            if (!conflicts.isEmpty()) {
                DotGiamGiaChiTiet conflict = conflicts.get(0);
                SanPhamChiTiet spct = conflict.getSanPhamChiTiet();
                DotGiamGia dot = conflict.getDotGiamGia();
                String tenSp = (spct != null && spct.getSanPham() != null) ? spct.getSanPham().getTenSanPham() : "";
                String maSpct = (spct != null) ? spct.getMaSpct() : "ID " + item.getIdSanPhamChiTiet();

                throw new IllegalArgumentException(String.format(
                        "Sản phẩm chi tiết '%s' (%s) đã thuộc đợt giảm giá '%s' (%s) diễn ra từ %s đến %s!",
                        maSpct,
                        tenSp,
                        dot.getTenDotGiamGia(),
                        dot.getMaDotGiamGia(),
                        dot.getNgayBatDau().format(DATE_TIME_FORMATTER),
                        dot.getNgayKetThuc().format(DATE_TIME_FORMATTER)
                ));
            }
        }
    }

    private void saveCampaignDetails(DotGiamGia d, List<DotGiamGiaItemRequestDTO> items) {
        List<DotGiamGiaChiTiet> existingDetails = dotGiamGiaChiTietRepository.findByDotGiamGiaId(d.getId());

        for (DotGiamGiaItemRequestDTO item : items) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSanPhamChiTiet())
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể áo khoác không tồn tại ID: " + item.getIdSanPhamChiTiet()));

            BigDecimal phanTram = item.getPhanTramGiam();
            BigDecimal multiplier = BigDecimal.valueOf(100).subtract(phanTram).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal giaSauGiam = spct.getGiaBan().multiply(multiplier).setScale(0, RoundingMode.HALF_UP);

            Optional<DotGiamGiaChiTiet> matched = existingDetails.stream()
                    .filter(ed -> ed.getSanPhamChiTiet().getId().equals(spct.getId()))
                    .findFirst();

            if (matched.isPresent()) {
                DotGiamGiaChiTiet detail = matched.get();
                detail.setSoLuong(item.getSoLuong());
                detail.setPhanTramGiam(phanTram);
                detail.setGiaSauGiam(giaSauGiam);
                detail.setTrangThai(1);
                dotGiamGiaChiTietRepository.save(detail);
            } else {
                DotGiamGiaChiTiet newDetail = DotGiamGiaChiTiet.builder()
                        .dotGiamGia(d)
                        .sanPhamChiTiet(spct)
                        .soLuong(item.getSoLuong())
                        .phanTramGiam(phanTram)
                        .giaSauGiam(giaSauGiam)
                        .trangThai(1)
                        .build();
                dotGiamGiaChiTietRepository.save(newDetail);
            }
        }
    }

    private DotGiamGiaChiTiet filterEffectiveDiscount(List<DotGiamGiaChiTiet> list, LocalDateTime now) {
        if (list == null || list.isEmpty()) return null;

        LocalTime currentTime = now.toLocalTime();

        for (DotGiamGiaChiTiet c : list) {
            DotGiamGia dot = c.getDotGiamGia();
            if ("FLASH_SALE".equalsIgnoreCase(dot.getHinhThuc())) {
                if (dot.getGioBatDau() != null && dot.getGioKetThuc() != null) {
                    if (currentTime.isBefore(dot.getGioBatDau()) || currentTime.isAfter(dot.getGioKetThuc())) {
                        continue; // Ngoài khung giờ vàng flash sale
                    }
                }
            }
            return c; // Đã sort DESC theo phanTramGiam
        }
        return null;
    }

    private Long resolveCustomerId(Long idKhachHang, String maKhachHang) {
        if (idKhachHang != null) return idKhachHang;
        if (maKhachHang != null && !maKhachHang.trim().isEmpty()) {
            return khachHangRepository.findByMaKhachHang(maKhachHang.trim().toUpperCase())
                    .map(KhachHang::getId)
                    .orElse(null);
        }
        return null;
    }

    private String generateNextVoucherCode() {
        Optional<PhieuGiamGia> latest = phieuGiamGiaRepository.findTopByOrderByIdDesc();
        long nextNum = latest.map(p -> p.getId() + 1).orElse(1L);
        String code = String.format("PGG%03d", nextNum);
        while (phieuGiamGiaRepository.existsByMaPhieuGiamGia(code)) {
            nextNum++;
            code = String.format("PGG%03d", nextNum);
        }
        return code;
    }

    private String generateNextCampaignCode() {
        Optional<DotGiamGia> latest = dotGiamGiaRepository.findTopByOrderByIdDesc();
        long nextNum = latest.map(d -> d.getId() + 1).orElse(1L);
        String code = String.format("DGG%03d", nextNum);
        while (dotGiamGiaRepository.existsByMaDotGiamGia(code)) {
            nextNum++;
            code = String.format("DGG%03d", nextNum);
        }
        return code;
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "id");
        if (sort != null && !sort.trim().isEmpty()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(dir, field);
        }
        return PageRequest.of(page, size, sortObj);
    }

    private PhieuGiamGiaResponseDTO mapVoucherToDTO(PhieuGiamGia p) {
        LocalDateTime now = LocalDateTime.now();
        String tinhTrang;

        if (p.getTrangThai() == null || p.getTrangThai() == 0) {
            tinhTrang = "NGUNG_AP_DUNG";
        } else if (p.getSoLuongDaDung() >= p.getSoLuong()) {
            tinhTrang = "HET_LUOT";
        } else if (now.isBefore(p.getNgayBatDau())) {
            tinhTrang = "SAP_DIEN_RA";
        } else if (now.isAfter(p.getNgayKetThuc())) {
            tinhTrang = "DA_KET_THUC";
        } else {
            tinhTrang = "DANG_DIEN_RA";
        }

        List<KhachHangPhieuGiamGia> assigned = khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaIdAndTrangThai(p.getId(), 1);

        return PhieuGiamGiaResponseDTO.builder()
                .id(p.getId())
                .maPhieuGiamGia(p.getMaPhieuGiamGia())
                .tenPhieuGiamGia(p.getTenPhieuGiamGia())
                .moTa(p.getMoTa())
                .nhomPhieu(p.getNhomPhieu() != null ? p.getNhomPhieu() : "CHUNG")
                .loaiGiamGia(p.getLoaiGiamGia())
                .giaTriGiam(p.getGiaTriGiam())
                .giamToiDa(p.getGiamToiDa())
                .donToiThieu(p.getDonToiThieu())
                .soLuong(p.getSoLuong())
                .soLuongDaDung(p.getSoLuongDaDung())
                .ngayBatDau(p.getNgayBatDau())
                .ngayKetThuc(p.getNgayKetThuc())
                .ngayTao(p.getNgayTao())
                .trangThai(p.getTrangThai())
                .tinhTrang(tinhTrang)
                .soLuongKhachHang(assigned.size())
                .build();
    }

    private DotGiamGiaResponseDTO mapCampaignToDTO(DotGiamGia d) {
        LocalDateTime now = LocalDateTime.now();
        String tinhTrang;

        if (d.getTrangThai() == null || d.getTrangThai() == 0) {
            tinhTrang = "NGUNG_AP_DUNG";
        } else if (now.isBefore(d.getNgayBatDau())) {
            tinhTrang = "SAP_DIEN_RA";
        } else if (now.isAfter(d.getNgayKetThuc())) {
            tinhTrang = "DA_KET_THUC";
        } else {
            tinhTrang = "DANG_DIEN_RA";
        }

        List<DotGiamGiaChiTiet> details = dotGiamGiaChiTietRepository.findByDotGiamGiaIdAndTrangThai(d.getId(), 1);
        List<DotGiamGiaChiTietResponseDTO> dtos = details.stream()
                .map(this::mapCampaignDetailToDTO)
                .collect(Collectors.toList());

        return DotGiamGiaResponseDTO.builder()
                .id(d.getId())
                .maDotGiamGia(d.getMaDotGiamGia())
                .tenDotGiamGia(d.getTenDotGiamGia())
                .moTa(d.getMoTa())
                .dieuKienApDung(d.getDieuKienApDung())
                .hinhThuc(d.getHinhThuc() != null ? d.getHinhThuc() : "THEO_MUA")
                .gioBatDau(d.getGioBatDau())
                .gioKetThuc(d.getGioKetThuc())
                .ngayBatDau(d.getNgayBatDau())
                .ngayKetThuc(d.getNgayKetThuc())
                .trangThai(d.getTrangThai())
                .tinhTrang(tinhTrang)
                .soLuongSanPham(dtos.size())
                .danhSachChiTiet(dtos)
                .build();
    }

    private DotGiamGiaChiTietResponseDTO mapCampaignDetailToDTO(DotGiamGiaChiTiet c) {
        SanPhamChiTiet spct = c.getSanPhamChiTiet();
        SanPham sp = spct != null ? spct.getSanPham() : null;
        return DotGiamGiaChiTietResponseDTO.builder()
                .id(c.getId())
                .idDotGiamGia(c.getDotGiamGia() != null ? c.getDotGiamGia().getId() : null)
                .idSanPhamChiTiet(spct != null ? spct.getId() : null)
                .idSanPham(sp != null ? sp.getId() : null)
                .tenSanPham(sp != null ? sp.getTenSanPham() : "")
                .maSpct(spct != null ? spct.getMaSpct() : "")
                .maVach(spct != null ? spct.getMaVach() : "")
                .tenMauSac(spct != null && spct.getMauSac() != null ? spct.getMauSac().getTenMau() : "")
                .tenKichCo(spct != null && spct.getKichCo() != null ? spct.getKichCo().getTenKichCo() : "")
                .giaGoc(spct != null ? spct.getGiaBan() : BigDecimal.ZERO)
                .phanTramGiam(c.getPhanTramGiam())
                .giaSauGiam(c.getGiaSauGiam())
                .soLuong(c.getSoLuong())
                .soLuongTonHienTai(spct != null ? spct.getSoLuongTon() : 0)
                .trangThai(c.getTrangThai())
                .build();
    }

    private KhachHangSimpleDTO mapCustomerToSimpleDTO(KhachHang k) {
        if (k == null) return null;
        return KhachHangSimpleDTO.builder()
                .id(k.getId())
                .maKhachHang(k.getMaKhachHang())
                .hoTen(k.getHoTen())
                .soDienThoai(k.getSoDienThoai())
                .email(k.getEmail())
                .trangThai(k.getTrangThai())
                .build();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format("%,.0f đ", amount);
    }
}
