package com.windfit.controller;

import com.windfit.entity.NhanVien;
import com.windfit.enums.TrangThaiNhanVien;
import com.windfit.repository.NhanVienRepository;
import com.windfit.service.NhanVienService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {
    private final NhanVienService service;
    private final NhanVienRepository repository;

    public NhanVienController(NhanVienService service, NhanVienRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String phongBan,
                        @RequestParam(required = false) String chucVu,
                        @RequestParam(required = false) TrangThaiNhanVien status,
                        Model model) {
        populateList(model, keyword, phongBan, chucVu, status);
        if (!model.containsAttribute("nhanVien")) {
            model.addAttribute("nhanVien", new NhanVien());
        }
        return "nhan-vien/index";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("nhanVien") NhanVien form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        normalize(form);

        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            boolean duplicate = form.getId() == null
                    ? repository.existsByEmailIgnoreCase(form.getEmail())
                    : repository.existsByEmailIgnoreCaseAndIdNot(form.getEmail(), form.getId());
            if (duplicate) {
                result.rejectValue("email", "duplicate", "Email này đã được sử dụng bởi nhân viên khác");
            }
        }

        if (form.getSoDienThoai() != null && !form.getSoDienThoai().isBlank()) {
            boolean duplicatePhone = form.getId() == null
                    ? repository.existsBySoDienThoai(form.getSoDienThoai())
                    : repository.existsBySoDienThoaiAndIdNot(form.getSoDienThoai(), form.getId());
            if (duplicatePhone) {
                result.rejectValue("soDienThoai", "duplicate", "Số điện thoại này đã được sử dụng bởi nhân viên khác");
            }
        }

        if (result.hasErrors()) {
            populateList(model, null, null, null, null);
            model.addAttribute("openModal", true);
            model.addAttribute("editMode", form.getId() != null);
            return "nhan-vien/index";
        }

        try {
            if (form.getId() == null) {
                service.save(form);
                redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công");
            } else {
                NhanVien old = service.get(form.getId());
                old.setHoTen(form.getHoTen());
                old.setEmail(form.getEmail());
                old.setSoDienThoai(form.getSoDienThoai());
                old.setChucVu(form.getChucVu());
                old.setPhongBan(form.getPhongBan());
                old.setNgayVaoLam(form.getNgayVaoLam());
                old.setLuongCoBan(form.getLuongCoBan());
                old.setTrangThai(form.getTrangThai());
                service.save(old);
                redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công");
            }
        } catch (IllegalArgumentException ex) {
            populateList(model, null, null, null, null);
            model.addAttribute("openModal", true);
            model.addAttribute("editMode", form.getId() != null);
            model.addAttribute("error", ex.getMessage());
            return "nhan-vien/index";
        }

        return "redirect:/nhan-vien";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.softDelete(id);
        redirectAttributes.addFlashAttribute("success", "Đã chuyển nhân viên sang trạng thái nghỉ việc");
        return "redirect:/nhan-vien";
    }

    private void normalize(NhanVien form) {
        if (form.getHoTen() != null) form.setHoTen(form.getHoTen().trim());
        if (form.getEmail() != null) form.setEmail(form.getEmail().trim());
        if (form.getSoDienThoai() != null) form.setSoDienThoai(form.getSoDienThoai().trim());
        if (form.getChucVu() != null) form.setChucVu(form.getChucVu().trim());
        if (form.getPhongBan() != null) form.setPhongBan(form.getPhongBan().trim());
    }

    private void populateList(Model model, String keyword, String phongBan, String chucVu, TrangThaiNhanVien status) {
        List<NhanVien> danhSach = service.search(keyword, phongBan, chucVu, status);
        List<NhanVien> all = service.search(null, null, null, null);
        model.addAttribute("danhSach", danhSach);
        model.addAttribute("keyword", keyword);
        model.addAttribute("phongBan", phongBan);
        model.addAttribute("chucVu", chucVu);
        model.addAttribute("status", status);
        model.addAttribute("trangThais", TrangThaiNhanVien.values());
        model.addAttribute("phongBans", all.stream().map(NhanVien::getPhongBan).filter(Objects::nonNull).filter(s -> !s.isBlank()).distinct().sorted().toList());
        model.addAttribute("chucVus", all.stream().map(NhanVien::getChucVu).filter(Objects::nonNull).filter(s -> !s.isBlank()).distinct().sorted().toList());
    }
}
