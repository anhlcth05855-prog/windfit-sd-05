-- =====================================================================================
-- DỰ ÁN TỐT NGHIỆP / XƯỞNG DỰ ÁN: HỆ THỐNG QUẢN LÝ & BÁN ÁO KHOÁC GIÓ WINDFIT (SD_005)
-- HỆ CƠ SỞ DỮ LIỆU SQL SERVER - PHIÊN BẢN CHUẨN HOÀN THIỆN & ĐẦY ĐỦ DỮ LIỆU MẪU
-- =====================================================================================

-- 1. TẠO VÀ SỬ DỤNG DATABASE
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'WindFit_DB')
BEGIN
    CREATE DATABASE WindFit_DB COLLATE Vietnamese_CI_AS;
END
GO

USE WindFit_DB;
GO

-- =====================================================================================
-- XÓA BẢNG CŨ THEO THỨ TỰ RÀNG BUỘC (NẾU ĐÃ TỒN TẠI ĐỂ TẠO LẠI SẠCH SẼ)
-- =====================================================================================
IF OBJECT_ID('lich_su_thanh_toan', 'U') IS NOT NULL DROP TABLE lich_su_thanh_toan;
IF OBJECT_ID('thanh_toan_hoa_don', 'U') IS NOT NULL DROP TABLE thanh_toan_hoa_don;
IF OBJECT_ID('lich_su_hoa_don', 'U') IS NOT NULL DROP TABLE lich_su_hoa_don;
IF OBJECT_ID('chi_tiet_hoa_don', 'U') IS NOT NULL DROP TABLE chi_tiet_hoa_don;
IF OBJECT_ID('hoa_don', 'U') IS NOT NULL DROP TABLE hoa_don;
IF OBJECT_ID('hinh_thuc_thanh_toan', 'U') IS NOT NULL DROP TABLE hinh_thuc_thanh_toan;
IF OBJECT_ID('phuong_thuc_thanh_toan', 'U') IS NOT NULL DROP TABLE phuong_thuc_thanh_toan;

IF OBJECT_ID('dot_giam_gia_chi_tiet', 'U') IS NOT NULL DROP TABLE dot_giam_gia_chi_tiet;
IF OBJECT_ID('dot_giam_gia', 'U') IS NOT NULL DROP TABLE dot_giam_gia;
IF OBJECT_ID('khach_hang_phieu_giam_gia', 'U') IS NOT NULL DROP TABLE khach_hang_phieu_giam_gia;
IF OBJECT_ID('phieu_giam_gia', 'U') IS NOT NULL DROP TABLE phieu_giam_gia;

IF OBJECT_ID('hinh_anh_san_pham', 'U') IS NOT NULL DROP TABLE hinh_anh_san_pham;
IF OBJECT_ID('san_pham_chi_tiet', 'U') IS NOT NULL DROP TABLE san_pham_chi_tiet;
IF OBJECT_ID('san_pham', 'U') IS NOT NULL DROP TABLE san_pham;

IF OBJECT_ID('kich_co', 'U') IS NOT NULL DROP TABLE kich_co;
IF OBJECT_ID('mau_sac', 'U') IS NOT NULL DROP TABLE mau_sac;
IF OBJECT_ID('mon_the_thao', 'U') IS NOT NULL DROP TABLE mon_the_thao;
IF OBJECT_ID('kieu_dang', 'U') IS NOT NULL DROP TABLE kieu_dang;
IF OBJECT_ID('xuat_xu', 'U') IS NOT NULL DROP TABLE xuat_xu;
IF OBJECT_ID('chat_lieu', 'U') IS NOT NULL DROP TABLE chat_lieu;
IF OBJECT_ID('thuong_hieu', 'U') IS NOT NULL DROP TABLE thuong_hieu;
IF OBJECT_ID('danh_muc', 'U') IS NOT NULL DROP TABLE danh_muc;

IF OBJECT_ID('dia_chi_khach_hang', 'U') IS NOT NULL DROP TABLE dia_chi_khach_hang;
IF OBJECT_ID('khach_hang', 'U') IS NOT NULL DROP TABLE khach_hang;
IF OBJECT_ID('nhan_vien', 'U') IS NOT NULL DROP TABLE nhan_vien;
IF OBJECT_ID('vai_tro', 'U') IS NOT NULL DROP TABLE vai_tro;
GO

-- =====================================================================================
-- PHẦN 1: TẠO CẤU TRÚC BẢNG (SCHEMA DDL)
-- =====================================================================================

-- 1. BẢNG VAI TRÒ
CREATE TABLE vai_tro (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_vai_tro NVARCHAR(50) NOT NULL UNIQUE,
    ten_vai_tro NVARCHAR(100) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1 -- 1: Hoạt động, 0: Khóa
);
GO

-- 2. BẢNG NHÂN VIÊN
CREATE TABLE nhan_vien (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_vai_tro BIGINT NOT NULL FOREIGN KEY REFERENCES vai_tro(id),
    ma_nhan_vien NVARCHAR(50) NOT NULL UNIQUE,
    ho_ten NVARCHAR(150) NOT NULL,
    email NVARCHAR(150) UNIQUE,
    so_dien_thoai NVARCHAR(20),
    mat_khau NVARCHAR(255) NOT NULL,
    gioi_tinh BIT DEFAULT 1, -- 1: Nam, 0: Nữ
    dia_chi NVARCHAR(255),
    anh_dai_dien NVARCHAR(500),
    ngay_sinh DATE,
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 3. BẢNG KHÁCH HÀNG
CREATE TABLE khach_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_khach_hang NVARCHAR(50) NOT NULL UNIQUE,
    ho_ten NVARCHAR(150) NOT NULL,
    so_dien_thoai NVARCHAR(20) NOT NULL,
    email NVARCHAR(150),
    mat_khau NVARCHAR(255),
    gioi_tinh BIT DEFAULT 1,
    ngay_sinh DATE,
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 4. BẢNG ĐỊA CHỈ KHÁCH HÀNG
CREATE TABLE dia_chi_khach_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang BIGINT NOT NULL FOREIGN KEY REFERENCES khach_hang(id),
    ten_nguoi_nhan NVARCHAR(150) NOT NULL,
    sdt_nguoi_nhan NVARCHAR(20) NOT NULL,
    tinh_thanh NVARCHAR(100) NOT NULL,
    quan_huyen NVARCHAR(100) NOT NULL,
    phuong_xa NVARCHAR(100) NOT NULL,
    dia_chi_cu_the NVARCHAR(255) NOT NULL,
    is_mac_dinh BIT DEFAULT 0
);
GO

-- 5. CÁC BẢNG THUỘC TÍNH SẢN PHẨM ÁO KHOÁC GIÓ
CREATE TABLE danh_muc (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_danh_muc NVARCHAR(50) NOT NULL UNIQUE,
    ten_danh_muc NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE thuong_hieu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_thuong_hieu NVARCHAR(50) NOT NULL UNIQUE,
    ten_thuong_hieu NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE chat_lieu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_chat_lieu NVARCHAR(50) NOT NULL UNIQUE,
    ten_chat_lieu NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE xuat_xu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_xuat_xu NVARCHAR(50) NOT NULL UNIQUE,
    ten_xuat_xu NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE kieu_dang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_kieu_dang NVARCHAR(50) NOT NULL UNIQUE,
    ten_kieu_dang NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE mon_the_thao (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_mon_the_thao NVARCHAR(50) NOT NULL UNIQUE,
    ten_mon_the_thao NVARCHAR(150) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE mau_sac (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_mau NVARCHAR(50) NOT NULL UNIQUE,
    ten_mau NVARCHAR(100) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE kich_co (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_kich_co NVARCHAR(50) NOT NULL UNIQUE,
    ten_kich_co NVARCHAR(50) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 6. BẢNG SẢN PHẨM CHÍNH (ÁO KHOÁC GIÓ)
CREATE TABLE san_pham (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_danh_muc BIGINT NOT NULL FOREIGN KEY REFERENCES danh_muc(id),
    id_thuong_hieu BIGINT NOT NULL FOREIGN KEY REFERENCES thuong_hieu(id),
    id_chat_lieu BIGINT NOT NULL FOREIGN KEY REFERENCES chat_lieu(id),
    id_xuat_xu BIGINT NOT NULL FOREIGN KEY REFERENCES xuat_xu(id),
    id_kieu_dang BIGINT NOT NULL FOREIGN KEY REFERENCES kieu_dang(id),
    id_mon_the_thao BIGINT NOT NULL FOREIGN KEY REFERENCES mon_the_thao(id),
    ma_san_pham NVARCHAR(50) NOT NULL UNIQUE,
    ten_san_pham NVARCHAR(255) NOT NULL,
    doi_tuong NVARCHAR(50) NOT NULL, -- Nam, Nữ, Unisex
    tinh_nang NVARCHAR(255),
    mo_ta_chi_tiet NVARCHAR(MAX),
    video_url NVARCHAR(500),
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 7. BẢNG SẢN PHẨM CHI TIẾT (SPCT)
CREATE TABLE san_pham_chi_tiet (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham(id),
    id_mau_sac BIGINT NOT NULL FOREIGN KEY REFERENCES mau_sac(id),
    id_kich_co BIGINT NOT NULL FOREIGN KEY REFERENCES kich_co(id),
    ma_spct NVARCHAR(100) NOT NULL UNIQUE,
    ma_vach NVARCHAR(100),
    gia_ban DECIMAL(18,2) NOT NULL,
    so_luong_ton INT NOT NULL DEFAULT 0,
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 8. BẢNG HÌNH ẢNH SẢN PHẨM
CREATE TABLE hinh_anh_san_pham (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham(id),
    url_anh NVARCHAR(500) NOT NULL,
    is_anh_chinh BIT NOT NULL DEFAULT 0
);
GO

-- 9. PHIẾU GIẢM GIÁ (VOUCHER)
CREATE TABLE phieu_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_phieu_giam_gia NVARCHAR(50) NOT NULL UNIQUE,
    ten_phieu_giam_gia NVARCHAR(200) NOT NULL,
    loai_giam_gia NVARCHAR(50) NOT NULL, -- 'PHAN_TRAM' hoặc 'TIEN_MAT'
    gia_tri_giam DECIMAL(18,2) NOT NULL,
    giam_toi_da DECIMAL(18,2),
    don_toi_thieu DECIMAL(18,2) NOT NULL DEFAULT 0,
    so_luong INT NOT NULL DEFAULT 0,
    so_luong_da_dung INT NOT NULL DEFAULT 0,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    mo_ta NVARCHAR(500),
    nhom_phieu NVARCHAR(50) DEFAULT 'CHUNG', -- 'CHUNG', 'CA_NHAN'
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- KHÁCH HÀNG NHẬN VOUCHER CÁ NHÂN
CREATE TABLE khach_hang_phieu_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang BIGINT NOT NULL FOREIGN KEY REFERENCES khach_hang(id),
    id_phieu_giam_gia BIGINT NOT NULL FOREIGN KEY REFERENCES phieu_giam_gia(id),
    ngay_su_dung DATETIME,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 10. ĐỢT GIẢM GIÁ (CAMPAIGN / FLASH SALE)
CREATE TABLE dot_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_dot_giam_gia NVARCHAR(50) NOT NULL UNIQUE,
    ten_dot_giam_gia NVARCHAR(200) NOT NULL,
    mo_ta NVARCHAR(500),
    dieu_kien_ap_dung NVARCHAR(500),
    hinh_thuc NVARCHAR(50) DEFAULT 'THEO_MUA', -- 'THEO_MUA', 'FLASH_SALE'
    gio_bat_dau TIME,
    gio_ket_thuc TIME,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- CHI TIẾT ĐỢT GIẢM GIÁ THEO SPCT
CREATE TABLE dot_giam_gia_chi_tiet (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_dot_giam_gia BIGINT NOT NULL FOREIGN KEY REFERENCES dot_giam_gia(id),
    id_san_pham_chi_tiet BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id),
    so_luong INT NOT NULL DEFAULT 0,
    phan_tram_giam DECIMAL(5,2) NOT NULL,
    gia_sau_giam DECIMAL(18,2) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 11. PHƯƠNG THỨC & HÌNH THỨC THANH TOÁN
CREATE TABLE phuong_thuc_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_phuong_thuc NVARCHAR(50) NOT NULL UNIQUE,
    ten_phuong_thuc NVARCHAR(100) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

CREATE TABLE hinh_thuc_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_hinh_thuc NVARCHAR(50) NOT NULL UNIQUE,
    ten_hinh_thuc NVARCHAR(100) NOT NULL,
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 12. BẢNG HÓA ĐƠN
CREATE TABLE hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang BIGINT FOREIGN KEY REFERENCES khach_hang(id),
    id_phieu_giam_gia BIGINT FOREIGN KEY REFERENCES phieu_giam_gia(id),
    id_nhan_vien BIGINT FOREIGN KEY REFERENCES nhan_vien(id),
    id_hinh_thuc_thanh_toan BIGINT FOREIGN KEY REFERENCES hinh_thuc_thanh_toan(id),
    ma_hoa_don NVARCHAR(50) NOT NULL UNIQUE,
    loai_hoa_don NVARCHAR(50) NOT NULL, -- 'TAI_QUAY', 'ONLINE'
    ten_nguoi_nhan NVARCHAR(150),
    sdt_nguoi_nhan NVARCHAR(20),
    dia_chi_nhan NVARCHAR(255),
    tong_tien_hang DECIMAL(18,2) NOT NULL DEFAULT 0,
    tien_giam DECIMAL(18,2) NOT NULL DEFAULT 0,
    phi_ship DECIMAL(18,2) NOT NULL DEFAULT 0,
    tong_tien_thanh_toan DECIMAL(18,2) NOT NULL DEFAULT 0,
    ghi_chu NVARCHAR(MAX),
    ly_do_huy NVARCHAR(MAX),
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    ngay_mong_muon_nhan DATETIME,
    ngay_giao_hang DATETIME,
    ngay_nhan_hang DATETIME,
    ngay_thanh_toan DATETIME,
    ngay_cap_nhat DATETIME DEFAULT GETDATE(),
    trang_thai_hoa_don NVARCHAR(50) NOT NULL DEFAULT N'Chờ xác nhận',
    trang_thai_thanh_toan NVARCHAR(50) NOT NULL DEFAULT N'Chưa thanh toán',
    ly_do_hoan NVARCHAR(500),
    ngay_hoan DATETIME,
    tien_hoan DECIMAL(18,2)
);
GO

-- 13. CHI TIẾT HÓA ĐƠN
CREATE TABLE chi_tiet_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_san_pham_chi_tiet BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id),
    so_luong INT NOT NULL,
    don_gia DECIMAL(18,2) NOT NULL,
    thanh_tien DECIMAL(18,2) NOT NULL,
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 14. LỊCH SỬ HÓA ĐƠN (TIMELINE)
CREATE TABLE lich_su_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_nhan_vien BIGINT FOREIGN KEY REFERENCES nhan_vien(id),
    hanh_dong NVARCHAR(255) NOT NULL,
    ghi_chu NVARCHAR(MAX),
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE()
);
GO

-- 15. THANH TOÁN HÓA ĐƠN
CREATE TABLE thanh_toan_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_phuong_thuc_thanh_toan BIGINT NOT NULL FOREIGN KEY REFERENCES phuong_thuc_thanh_toan(id),
    ma_giao_dich NVARCHAR(100),
    so_tien DECIMAL(18,2) NOT NULL,
    ghi_chu NVARCHAR(255),
    ngay_thanh_toan DATETIME NOT NULL DEFAULT GETDATE(),
    trang_thai INT NOT NULL DEFAULT 1
);
GO

-- 16. LỊCH SỬ THANH TOÁN
CREATE TABLE lich_su_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_thanh_toan_hoa_don BIGINT FOREIGN KEY REFERENCES thanh_toan_hoa_don(id),
    id_nhan_vien BIGINT FOREIGN KEY REFERENCES nhan_vien(id),
    hanh_dong NVARCHAR(255) NOT NULL,
    so_tien DECIMAL(18,2),
    ghi_chu NVARCHAR(MAX),
    ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
    trang_thai_thanh_toan NVARCHAR(50) DEFAULT N'Chưa thanh toán'
);
GO

-- =====================================================================================
-- PHẦN 2: CHÈN DỮ LIỆU MẪU ĐẦY ĐỦ, CHUẨN XÁC, PHÙ HỢP SHOP ÁO KHOÁC GIÓ WINDFIT
-- =====================================================================================

-- 1. VAI TRÒ
INSERT INTO vai_tro (ma_vai_tro, ten_vai_tro, trang_thai) VALUES
('ADMIN', N'Quản trị viên hệ thống', 1),
('MANAGER', N'Quản lý cửa hàng', 1),
('STAFF', N'Nhân viên bán hàng', 1);
GO

-- 2. NHÂN VIÊN (Mật khẩu mặc định: 123456 - đã mã hóa bcrypt chuẩn)
-- BCrypt $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi tương ứng pass '123456'
INSERT INTO nhan_vien (id_vai_tro, ma_nhan_vien, ho_ten, email, so_dien_thoai, mat_khau, gioi_tinh, dia_chi, anh_dai_dien, ngay_sinh, ngay_tao, trang_thai) VALUES
(1, 'NV001', N'Nguyễn Quản Trị', 'admin@windfit.vn', '0988111222', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, N'Cầu Giấy, Hà Nội', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80', '1995-03-15', GETDATE(), 1),
(2, 'NV002', N'Trần Thị Thu Quản Lý', 'manager@windfit.vn', '0988222333', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, N'Đống Đa, Hà Nội', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80', '1997-07-20', GETDATE(), 1),
(3, 'NV003', N'Lê Hoàng Nam', 'nam.staff@windfit.vn', '0988333444', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, N'Nam Từ Liêm, Hà Nội', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80', '2001-11-10', GETDATE(), 1),
(3, 'NV004', N'Phạm Phương Thảo', 'thao.staff@windfit.vn', '0988444555', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, N'Thanh Xuân, Hà Nội', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80', '2002-05-18', GETDATE(), 1),
(3, 'NV005', N'Đỗ Minh Quân', 'quan.staff@windfit.vn', '0988555666', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, N'Hà Đông, Hà Nội', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80', '2000-09-02', GETDATE(), 1);
GO

-- 3. KHÁCH HÀNG
INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, email, mat_khau, gioi_tinh, ngay_sinh, ngay_tao, trang_thai) VALUES
('KH00001', N'Nguyễn Văn An', '0912345678', 'an.nguyen@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '1996-04-12', GETDATE(), 1),
('KH00002', N'Trần Thị Mai', '0923456789', 'mai.tran@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, '1998-08-25', GETDATE(), 1),
('KH00003', N'Lê Tuấn Hùng', '0934567890', 'hung.tuan@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '1994-01-30', GETDATE(), 1),
('KH00004', N'Hoàng Yến Nhi', '0945678901', 'yennhi.hoang@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, '2001-12-14', GETDATE(), 1),
('KH00005', N'Vũ Đình Trọng', '0956789012', 'trong.vu@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '1992-06-08', GETDATE(), 1),
('KH00006', N'Bùi Bích Phương', '0967890123', 'phuong.bui@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, '1999-03-22', GETDATE(), 1),
('KH00007', N'Đặng Quốc Bảo', '0978901234', 'bao.dang@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '1997-10-19', GETDATE(), 1),
('KH00008', N'Phan Thanh Thảo', '0989012345', 'thao.phan@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, '2003-02-11', GETDATE(), 1);
GO

-- 4. ĐỊA CHỈ KHÁCH HÀNG
INSERT INTO dia_chi_khach_hang (id_khach_hang, ten_nguoi_nhan, sdt_nguoi_nhan, tinh_thanh, quan_huyen, phuong_xa, dia_chi_cu_the, is_mac_dinh) VALUES
(1, N'Nguyễn Văn An', '0912345678', N'Hà Nội', N'Cầu Giấy', N'Dịch Vọng Hậu', N'Số 12 ngõ 86 Duy Tân', 1),
(1, N'Nguyễn Văn An (Cơ quan)', '0912345678', N'Hà Nội', N'Ba Đình', N'Kim Mã', N'Tòa nhà Lotte Center Liễu Giai', 0),
(2, N'Trần Thị Mai', '0923456789', N'Hà Nội', N'Đống Đa', N'Láng Hạ', N'Số 45 Huỳnh Thúc Kháng', 1),
(3, N'Lê Tuấn Hùng', '0934567890', N'Đà Nẵng', N'Hải Châu', N'Thạch Thang', N'Số 150 Bạch Đằng', 1),
(4, N'Hoàng Yến Nhi', '0945678901', N'TP Hồ Chí Minh', N'Quận 1', N'Bến Nghé', N'Số 88 Đồng Khởi', 1),
(5, N'Vũ Đình Trọng', '0956789012', N'Hải Phòng', N'Ngô Quyền', N'Lạc Viên', N'Số 23 Lê Hồng Phong', 1),
(6, N'Bùi Bích Phương', '0967890123', N'Quảng Ninh', N'Hạ Long', N'Bãi Cháy', N'Số 68 Hạ Long', 1),
(7, N'Đặng Quốc Bảo', '0978901234', N'Cần Thơ', N'Ninh Kiều', N'An Cư', N'Số 32 Đại lộ Hòa Bình', 1),
(8, N'Phan Thanh Thảo', '0989012345', N'TP Hồ Chí Minh', N'Quận 7', N'Tân Phong', N'Chung cư Sunrise City Nguyễn Hữu Thọ', 1);
GO

-- 5. THUỘC TÍNH SẢN PHẨM ÁO KHOÁC GIÓ
-- Danh mục
INSERT INTO danh_muc (ma_danh_muc, ten_danh_muc, trang_thai) VALUES
('DM01', N'Áo khoác gió 1 lớp siêu nhẹ', 1),
('DM02', N'Áo khoác gió 2 lớp lót lưới thể thao', 1),
('DM03', N'Áo khoác gió 3 lớp lót lông cừu ấm áp', 1),
('DM04', N'Áo khoác gió chống nước Pro Waterproof', 1),
('DM05', N'Áo khoác gió phản quang Night Runner', 1),
('DM06', N'Áo khoác gió trượt nước chống UV', 1);
GO

-- Thương hiệu
INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, trang_thai) VALUES
('TH01', N'WindFit Original', 1),
('TH02', N'The North Face', 1),
('TH03', N'Nike Windrunner', 1),
('TH04', N'Adidas Terrex', 1),
('TH05', N'Uniqlo Blocktech', 1),
('TH06', N'Columbia Titanium', 1),
('TH07', N'Puma Running Tech', 1);
GO

-- Chất liệu
INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu, trang_thai) VALUES
('CL01', N'Vải dù Micro Polyester siêu nhẹ', 1),
('CL02', N'Vải dù Ripstop 40D chống xé rách', 1),
('CL03', N'Vải Gore-Tex Pro 3L chống thấm cao cấp', 1),
('CL04', N'Vải dù tráng bạc Nano UV50+', 1),
('CL05', N'Vải Polyester lót nỉ nhung ấm', 1),
('CL06', N'Vải dù dệt tổ ong thoáng khí 2 chiều', 1);
GO

-- Xuất xứ
INSERT INTO xuat_xu (ma_xuat_xu, ten_xuat_xu, trang_thai) VALUES
('XX01', N'Việt Nam (Made in Vietnam)', 1),
('XX02', N'Nhật Bản', 1),
('XX03', N'Hàn Quốc', 1),
('XX04', N'Đức', 1),
('XX05', N'Mỹ (USA)', 1),
('XX06', N'Thái Lan', 1);
GO

-- Kiểu dáng
INSERT INTO kieu_dang (ma_kieu_dang, ten_kieu_dang, trang_thai) VALUES
('KD01', N'Regular Fit tiêu chuẩn', 1),
('KD02', N'Slim Fit ôm tôn dáng thể thao', 1),
('KD03', N'Oversize Unisex trẻ trung', 1),
('KD04', N'Sporty năng động kèm mũ trùm', 1),
('KD05', N'Dáng dài Parka chống rét cản gió', 1);
GO

-- Môn thể thao
INSERT INTO mon_the_thao (ma_mon_the_thao, ten_mon_the_thao, trang_thai) VALUES
('TT01', N'Chạy bộ (Running)', 1),
('TT02', N'Trekking & Leo núi', 1),
('TT03', N'Đạp xe (Cycling)', 1),
('TT04', N'Đi phượt / Du lịch dã ngoại', 1),
('TT05', N'Thường ngày (Casual Life)', 1),
('TT06', N'Đánh Golf & Thể thao ngoài trời', 1);
GO

-- Màu sắc
INSERT INTO mau_sac (ma_mau, ten_mau, trang_thai) VALUES
('MS01', N'Đen Tuyển (Black)', 1),
('MS02', N'Xanh Navy (Navy Blue)', 1),
('MS03', N'Xanh Rêu Đậm (Olive Green)', 1),
('MS04', N'Đỏ Đô (Burgundy)', 1),
('MS05', N'Bạc Phản Quang (Silver Reflective)', 1),
('MS06', N'Vàng Chanh Neon (Neon Lime)', 1),
('MS07', N'Xám Ti-tan (Titanium Grey)', 1),
('MS08', N'Trắng Sứ (Pure White)', 1);
GO

-- Kích cỡ
INSERT INTO kich_co (ma_kich_co, ten_kich_co, trang_thai) VALUES
('KC01', N'S', 1),
('KC02', N'M', 1),
('KC03', N'L', 1),
('KC04', N'XL', 1),
('KC05', N'XXL', 1),
('KC06', N'3XL', 1);
GO

-- 6. SẢN PHẨM CHÍNH (ÁO KHOÁC GIÓ)
INSERT INTO san_pham (id_danh_muc, id_thuong_hieu, id_chat_lieu, id_xuat_xu, id_kieu_dang, id_mon_the_thao, ma_san_pham, ten_san_pham, doi_tuong, tinh_nang, mo_ta_chi_tiet, video_url, ngay_tao, ngay_sua, trang_thai) VALUES
(2, 1, 1, 1, 4, 1, 'SP001', N'Áo Khoác Gió WindFit Runner Pro 2026', N'Nam', N'Chống nước nhẹ, Cản gió 100%, Thoát mồ hôi nhanh', N'Áo khoác gió thể thao cao cấp dòng Runner Pro chuyên biệt cho các vận động viên chạy bộ marathon và chạy bộ hàng ngày. Thiết kế lót lưới thoáng khí 2 chiều, dây kéo YKK chống kẹt nước, túi zip tiện lợi hai bên.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(4, 2, 3, 5, 1, 2, 'SP002', N'Áo Gió The North Face Summit Gore-Tex', N'Unisex', N'Chống thấm nước 20.000mm, Chống gió bão, Khóa dán cổ tay', N'Mẫu áo gió chuyên dụng cho leo núi Fansipan và trekking khám phá thiên nhiên khắc nghiệt. Sử dụng công nghệ vải màng Gore-Tex 3 lớp giữ ấm và ngăn nước tuyệt đối, kèm mũ trùm đầu có dây rút điều chỉnh.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(5, 3, 2, 1, 4, 1, 'SP003', N'Áo Khoác Gió Phản Quang Nike Flash Night', N'Unisex', N'Phản quang ban đêm 360 độ, Kháng tia cực tím UV, Siêu nhẹ 180g', N'Áo khoác gió phản quang cao cấp phục vụ chạy bộ hoặc đạp xe ban đêm đảm bảo an toàn tối đa. Họa tiết phản quang độc đáo dọc hai bên tay và logo ngực áo.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(3, 1, 5, 1, 5, 5, 'SP004', N'Áo Gió Lót Lông Cừu WindFit Winter Warm', N'Nam', N'Giữ nhiệt đến 5 độ C, Lớp lót lông mềm mại, Kháng nước nhẹ', N'Áo khoác gió mùa đông lót lông cừu dày dặn giúp giữ ấm tối đa trong những đợt rét đại hàn của miền Bắc. Dáng suông thời trang, bo chun co giãn cổ tay và gấu áo.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(1, 5, 4, 2, 2, 5, 'SP005', N'Áo Khoác Gió Uniqlo UV Cut Pocketable', N'Nữ', N'Gấp gọn bỏ túi nhỏ tiện lợi, Chống nắng UPF50+, Trượt nước', N'Mẫu áo khoác gió siêu nhẹ trứ danh, có thể gấp gọn trong chiếc túi nhỏ mang theo mọi lúc. Thích hợp đi làm, đi chơi, dã ngoại chống nắng và mưa phùn.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(2, 4, 1, 4, 4, 3, 'SP006', N'Áo Gió Thể Thao Adidas Terrex Trail Cycling', N'Nam', N'Cản gió lưng dài che chắn, Túi sau lưng tiện dụng, Co giãn linh hoạt', N'Thiết kế chuyên biệt cho bộ môn đạp xe thể thao đường trường. Vạt áo sau dài hơn vạt trước chống văng bùn đất, khóa zip ngực phản quang và túi mini sau lưng.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(6, 6, 2, 5, 1, 4, 'SP007', N'Áo Gió Dã Ngoại Columbia Omni-Shield Trekker', N'Unisex', N'Chống bám bẩn công nghệ Omni-Shield, Cản gió lạnh, 4 túi hộp', N'Trang bị công nghệ chống bám bẩn cà phê, bùn đất và trượt nước hàng đầu từ Columbia. Thiết kế 4 túi hộp tiện dụng chứa phụ kiện đi phượt dã ngoại.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1),

(1, 1, 1, 1, 3, 5, 'SP008', N'Áo Gió WindFit Streetwear Unisex Bomber', N'Unisex', N'Phong cách Streetwear cá tính, Bo gấu dệt kim, Dù 2 mặt cao cấp', N'Mẫu áo gió bomber oversize phá cách cho giới trẻ, dễ dàng phối đồ với quần jogger, jean hoặc chân váy. Khả năng cản gió và giữ ấm nhẹ nhàng cho tiết trời se lạnh.', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', GETDATE(), GETDATE(), 1);
GO

-- 7. SẢN PHẨM CHI TIẾT (SPCT) - BIẾN THỂ MÀU SẮC & KÍCH CỠ & TỒN KHO & GIÁ BÁN
INSERT INTO san_pham_chi_tiet (id_san_pham, id_mau_sac, id_kich_co, ma_spct, ma_vach, gia_ban, so_luong_ton, ngay_tao, ngay_sua, trang_thai) VALUES
-- SP001: WindFit Runner Pro (Giá: 450.000)
(1, 1, 2, 'SPCT001', '893600000001', 450000.00, 45, GETDATE(), GETDATE(), 1), -- Đen - Size M
(1, 1, 3, 'SPCT002', '893600000002', 450000.00, 60, GETDATE(), GETDATE(), 1), -- Đen - Size L
(1, 1, 4, 'SPCT003', '893600000003', 450000.00, 30, GETDATE(), GETDATE(), 1), -- Đen - Size XL
(1, 2, 2, 'SPCT004', '893600000004', 450000.00, 35, GETDATE(), GETDATE(), 1), -- Navy - Size M
(1, 2, 3, 'SPCT005', '893600000005', 450000.00, 50, GETDATE(), GETDATE(), 1), -- Navy - Size L
(1, 6, 3, 'SPCT006', '893600000006', 450000.00, 20, GETDATE(), GETDATE(), 1), -- Neon Lime - Size L

-- SP002: The North Face Summit Gore-Tex (Giá: 1.250.000)
(2, 1, 3, 'SPCT007', '893600000007', 1250000.00, 25, GETDATE(), GETDATE(), 1), -- Đen - Size L
(2, 1, 4, 'SPCT008', '893600000008', 1250000.00, 20, GETDATE(), GETDATE(), 1), -- Đen - Size XL
(2, 3, 2, 'SPCT009', '893600000009', 1250000.00, 18, GETDATE(), GETDATE(), 1), -- Xanh Rêu - Size M
(2, 3, 3, 'SPCT010', '893600000010', 1250000.00, 22, GETDATE(), GETDATE(), 1), -- Xanh Rêu - Size L

-- SP003: Nike Flash Night (Giá: 790.000)
(3, 5, 2, 'SPCT011', '893600000011', 790000.00, 40, GETDATE(), GETDATE(), 1), -- Bạc Phản Quang - Size M
(3, 5, 3, 'SPCT012', '893600000012', 790000.00, 55, GETDATE(), GETDATE(), 1), -- Bạc Phản Quang - Size L
(3, 5, 4, 'SPCT013', '893600000013', 790000.00, 15, GETDATE(), GETDATE(), 1), -- Bạc Phản Quang - Size XL

-- SP004: WindFit Winter Warm lót lông (Giá: 650.000)
(4, 1, 3, 'SPCT014', '893600000014', 650000.00, 30, GETDATE(), GETDATE(), 1), -- Đen - Size L
(4, 1, 4, 'SPCT015', '893600000015', 650000.00, 35, GETDATE(), GETDATE(), 1), -- Đen - Size XL
(4, 4, 2, 'SPCT016', '893600000016', 650000.00, 25, GETDATE(), GETDATE(), 1), -- Đỏ Đô - Size M
(4, 4, 3, 'SPCT017', '893600000017', 650000.00, 28, GETDATE(), GETDATE(), 1), -- Đỏ Đô - Size L

-- SP005: Uniqlo UV Cut Pocketable (Giá: 390.000)
(5, 8, 1, 'SPCT018', '893600000018', 390000.00, 50, GETDATE(), GETDATE(), 1), -- Trắng Sứ - Size S
(5, 8, 2, 'SPCT019', '893600000019', 390000.00, 60, GETDATE(), GETDATE(), 1), -- Trắng Sứ - Size M
(5, 2, 2, 'SPCT020', '893600000020', 390000.00, 45, GETDATE(), GETDATE(), 1), -- Navy - Size M
(5, 2, 3, 'SPCT021', '893600000021', 390000.00, 40, GETDATE(), GETDATE(), 1), -- Navy - Size L

-- SP006: Adidas Terrex Cycling (Giá: 820.000)
(6, 7, 2, 'SPCT022', '893600000022', 820000.00, 20, GETDATE(), GETDATE(), 1), -- Xám Titan - Size M
(6, 7, 3, 'SPCT023', '893600000023', 820000.00, 30, GETDATE(), GETDATE(), 1), -- Xám Titan - Size L
(6, 1, 3, 'SPCT024', '893600000024', 820000.00, 35, GETDATE(), GETDATE(), 1), -- Đen - Size L

-- SP007: Columbia Omni-Shield (Giá: 950.000)
(7, 3, 3, 'SPCT025', '893600000025', 950000.00, 25, GETDATE(), GETDATE(), 1), -- Rêu - Size L
(7, 3, 4, 'SPCT026', '893600000026', 950000.00, 18, GETDATE(), GETDATE(), 1), -- Rêu - Size XL
(7, 1, 3, 'SPCT027', '893600000027', 950000.00, 30, GETDATE(), GETDATE(), 1), -- Đen - Size L

-- SP008: WindFit Streetwear Bomber (Giá: 520.000)
(8, 1, 2, 'SPCT028', '893600000028', 520000.00, 40, GETDATE(), GETDATE(), 1), -- Đen - Size M
(8, 1, 3, 'SPCT029', '893600000029', 520000.00, 50, GETDATE(), GETDATE(), 1), -- Đen - Size L
(8, 3, 3, 'SPCT030', '893600000030', 520000.00, 35, GETDATE(), GETDATE(), 1); -- Rêu - Size L
GO

-- 8. HÌNH ẢNH SẢN PHẨM (ẢNH THỰC TẾ ÁO GIÓ)
INSERT INTO hinh_anh_san_pham (id_san_pham, url_anh, is_anh_chinh) VALUES
(1, 'https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=600&q=80', 1),
(1, 'https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=600&q=80', 0),
(2, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=600&q=80', 1),
(2, 'https://images.unsplash.com/photo-1544441892-794166f1e3be?auto=format&fit=crop&w=600&q=80', 0),
(3, 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?auto=format&fit=crop&w=600&q=80', 1),
(4, 'https://images.unsplash.com/photo-1578587018452-892bacefd3f2?auto=format&fit=crop&w=600&q=80', 1),
(5, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=600&q=80', 1),
(6, 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=600&q=80', 1),
(7, 'https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=600&q=80', 1),
(8, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?auto=format&fit=crop&w=600&q=80', 1);
GO

-- 9. PHIẾU GIẢM GIÁ (VOUCHER)
INSERT INTO phieu_giam_gia (ma_phieu_giam_gia, ten_phieu_giam_gia, loai_giam_gia, gia_tri_giam, giam_toi_da, don_toi_thieu, so_luong, so_luong_da_dung, ngay_bat_dau, ngay_ket_thuc, mo_ta, nhom_phieu, ngay_tao, trang_thai) VALUES
('WINDFIT10', N'Ưu đãi chào hè giảm 10% đơn từ 500k', 'PHAN_TRAM', 10.00, 100000.00, 500000.00, 100, 14, '2026-05-01 00:00:00', '2026-12-31 23:59:59', N'Áp dụng cho mọi đơn hàng áo khoác gió trên toàn hệ thống', 'CHUNG', GETDATE(), 1),

('WINDFIT20', N'Siêu Sale Giảm 20% cho thành viên VIP', 'PHAN_TRAM', 20.00, 200000.00, 1000000.00, 50, 8, '2026-06-01 00:00:00', '2026-12-31 23:59:59', N'Voucher tri ân khách hàng thân thiết mua áo gió kỹ thuật cao', 'CHUNG', GETDATE(), 1),

('GIAM50K', N'Giảm trực tiếp 50.000 VNĐ cho khách mới', 'TIEN_MAT', 50000.00, NULL, 300000.00, 200, 35, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'Mã giảm chào mừng khách hàng lần đầu mua sắm tại WindFit', 'CHUNG', GETDATE(), 1),

('GIAM100K', N'Giảm 100k cho hóa đơn từ 1.2 triệu', 'TIEN_MAT', 100000.00, NULL, 1200000.00, 50, 12, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'Thỏa sức sắm combo áo gió gia đình giảm ngay 100.000đ', 'CHUNG', GETDATE(), 1),

('FREESHIP30K', N'Hỗ trợ phí vận chuyển 30.000 VNĐ', 'TIEN_MAT', 30000.00, NULL, 400000.00, 500, 80, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'Miễn giảm phí giao hàng toàn quốc', 'CHUNG', GETDATE(), 1),

('VIPBIRTHDAY', N'Quà tặng sinh nhật giảm 25% tối đa 300k', 'PHAN_TRAM', 25.00, 300000.00, 600000.00, 30, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'Voucher cá nhân hóa nhân dịp sinh nhật khách hàng', 'CA_NHAN', GETDATE(), 1),

('HETHAN2025', N'Mã khuyến mại mùa đông cũ đã hết hạn', 'PHAN_TRAM', 15.00, 150000.00, 500000.00, 50, 50, '2025-10-01 00:00:00', '2025-12-31 23:59:59', N'Chương trình cũ đã kết thúc thời gian hiệu lực', 'CHUNG', GETDATE(), 0);
GO

-- KHÁCH HÀNG NHẬN VOUCHER CÁ NHÂN
INSERT INTO khach_hang_phieu_giam_gia (id_khach_hang, id_phieu_giam_gia, ngay_su_dung, trang_thai) VALUES
(1, 1, '2026-06-15 14:30:00', 0), -- Đã dùng
(1, 6, NULL, 1),                 -- Chưa dùng
(2, 1, NULL, 1),
(2, 3, '2026-07-02 09:20:00', 0),
(3, 2, NULL, 1),
(4, 5, NULL, 1);
GO

-- 10. ĐỢT GIẢM GIÁ (CAMPAIGN / FLASH SALE)
INSERT INTO dot_giam_gia (ma_dot_giam_gia, ten_dot_giam_gia, mo_ta, dieu_kien_ap_dung, hinh_thuc, gio_bat_dau, gio_ket_thuc, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES
('DGG01', N'Lễ Hội Áo Gió Chào Hè Sảng Khoái 2026', N'Giảm giá sốc cho toàn bộ dòng áo gió thể thao chạy bộ và đạp xe', N'Áp dụng khi mua các mẫu áo gió thuộc danh mục Runner và Cycling', 'THEO_MUA', '00:00:00', '23:59:59', '2026-05-01 00:00:00', '2026-08-31 23:59:59', 1),

('DGG02', N'Flash Sale Giờ Vàng Phản Quang Runner', N'Ưu đãi đặc biệt buổi tối cho các dòng áo gió phản quang Night Flash', N'Áp dụng trong khung giờ vàng 18:00 - 22:00', 'FLASH_SALE', '18:00:00', '22:00:00', '2026-06-01 00:00:00', '2026-10-31 23:59:59', 1),

('DGG03', N'Tuần Lễ Trekking & Chinh Phục Núi Rừng', N'Trợ giá đến 25% cho dòng áo khoác gió chống nước The North Face & Columbia', N'Áp dụng cho các sản phẩm chuyên dụng dã ngoại leo núi', 'THEO_MUA', '00:00:00', '23:59:59', '2026-09-01 00:00:00', '2026-11-30 23:59:59', 1);
GO

-- CHI TIẾT ĐỢT GIẢM GIÁ (GÁN VÀO CÁC SPCT)
INSERT INTO dot_giam_gia_chi_tiet (id_dot_giam_gia, id_san_pham_chi_tiet, so_luong, phan_tram_giam, gia_sau_giam, trang_thai) VALUES
-- Đợt 1: Runner Pro giảm 15% (450.000 -> 382.500)
(1, 1, 30, 15.00, 382500.00, 1),
(1, 2, 40, 15.00, 382500.00, 1),
(1, 4, 25, 15.00, 382500.00, 1),

-- Đợt 2: Flash Sale Nike Flash Night giảm 20% (790.000 -> 632.000)
(2, 11, 20, 20.00, 632000.00, 1),
(2, 12, 30, 20.00, 632000.00, 1),

-- Đợt 3: Trekking The North Face giảm 20% (1.250.000 -> 1.000.000)
(3, 7, 15, 20.00, 1000000.00, 1),
(3, 9, 10, 20.00, 1000000.00, 1);
GO

-- 11. PHƯƠNG THỨC & HÌNH THỨC THANH TOÁN
INSERT INTO phuong_thuc_thanh_toan (ma_phuong_thuc, ten_phuong_thuc, trang_thai) VALUES
('TIEN_MAT', N'Tiền mặt tại quầy', 1),
('CHUYEN_KHOAN', N'Chuyển khoản Ngân hàng (VietQR)', 1),
('VNPAY', N'Cổng thanh toán điện tử VNPAY-QR', 1),
('THE_POS', N'Quẹt thẻ POS ngân hàng', 1);
GO

INSERT INTO hinh_thuc_thanh_toan (ma_hinh_thuc, ten_hinh_thuc, trang_thai) VALUES
('TAI_QUAY', N'Bán trực tiếp tại quầy Showroom', 1),
('ONLINE_SHIP', N'Đặt hàng Online giao tận nhà', 1),
('ONLINE_COD', N'Thanh toán khi nhận hàng (COD)', 1);
GO

-- 12. HÓA ĐƠN MẪU
INSERT INTO hoa_don (
    id_khach_hang, id_phieu_giam_gia, id_nhan_vien, id_hinh_thuc_thanh_toan,
    ma_hoa_don, loai_hoa_don, ten_nguoi_nhan, sdt_nguoi_nhan, dia_chi_nhan,
    tong_tien_hang, tien_giam, phi_ship, tong_tien_thanh_toan, ghi_chu,
    ngay_tao, ngay_mong_muon_nhan, ngay_giao_hang, ngay_nhan_hang, ngay_thanh_toan, ngay_cap_nhat,
    trang_thai_hoa_don, trang_thai_thanh_toan
) VALUES
-- Hóa đơn 1: Bán tại quầy hoàn tất
(1, 3, 3, 1,
 'HD00001', 'TAI_QUAY', N'Nguyễn Văn An', '0912345678', N'Tại quầy WindFit Cầu Giấy',
 900000.00, 50000.00, 0.00, 850000.00, N'Khách thử đồ ưng ý lấy 2 áo',
 '2026-06-15 10:15:00', NULL, NULL, '2026-06-15 10:25:00', '2026-06-15 10:25:00', '2026-06-15 10:25:00',
 N'Hoàn thành', N'Đã thanh toán'),

-- Hóa đơn 2: Online giao hàng thành công
(2, 1, 2, 2,
 'HD00002', 'ONLINE', N'Trần Thị Mai', '0923456789', N'Số 45 Huỳnh Thúc Kháng, Đống Đa, Hà Nội',
 1250000.00, 100000.00, 30000.00, 1180000.00, N'Giao giờ hành chính giúp em',
 '2026-06-18 14:00:00', '2026-06-20 10:00:00', '2026-06-19 08:30:00', '2026-06-20 11:15:00', '2026-06-20 11:15:00', '2026-06-20 11:15:00',
 N'Hoàn thành', N'Đã thanh toán'),

-- Hóa đơn 3: Online đang vận chuyển
(3, 5, 3, 3,
 'HD00003', 'ONLINE', N'Lê Tuấn Hùng', '0934567890', N'Số 150 Bạch Đằng, Hải Châu, Đà Nẵng',
 790000.00, 30000.00, 30000.00, 790000.00, N'Cho kiểm tra hàng trước khi nhận',
 '2026-07-01 09:30:00', '2026-07-04 15:00:00', '2026-07-02 08:00:00', NULL, NULL, '2026-07-02 08:00:00',
 N'Đang giao hàng', N'Chưa thanh toán'),

-- Hóa đơn 4: Chờ xác nhận mới đặt
(4, NULL, NULL, 3,
 'HD00004', 'ONLINE', N'Hoàng Yến Nhi', '0945678901', N'Số 88 Đồng Khởi, Quận 1, TP Hồ Chí Minh',
 650000.00, 0.00, 35000.00, 685000.00, N'Gọi trước khi giao',
 GETDATE(), DATEADD(day, 3, GETDATE()), NULL, NULL, NULL, GETDATE(),
 N'Chờ xác nhận', N'Chưa thanh toán'),

-- Hóa đơn 5: Đã hủy
(5, NULL, 3, 2,
 'HD00005', 'ONLINE', N'Vũ Đình Trọng', '0956789012', N'Số 23 Lê Hồng Phong, Ngô Quyền, Hải Phòng',
 520000.00, 0.00, 30000.00, 550000.00, N'Khách bận đi công tác đột xuất',
 '2026-06-10 16:20:00', NULL, NULL, NULL, NULL, '2026-06-11 09:00:00',
 N'Đã hủy', N'Chưa thanh toán');
GO

-- 13. CHI TIẾT HÓA ĐƠN
INSERT INTO chi_tiet_hoa_don (id_hoa_don, id_san_pham_chi_tiet, so_luong, don_gia, thanh_tien, ngay_tao, trang_thai) VALUES
-- HD1: 2 chiếc WindFit Runner Pro (SPCT001 và SPCT002)
(1, 1, 1, 450000.00, 450000.00, GETDATE(), 1),
(1, 2, 1, 450000.00, 450000.00, GETDATE(), 1),

-- HD2: 1 chiếc The North Face Summit Gore-Tex (SPCT007)
(2, 7, 1, 1250000.00, 1250000.00, GETDATE(), 1),

-- HD3: 1 chiếc Nike Flash Night (SPCT011)
(3, 11, 1, 790000.00, 790000.00, GETDATE(), 1),

-- HD4: 1 chiếc WindFit Winter Warm (SPCT014)
(4, 14, 1, 650000.00, 650000.00, GETDATE(), 1),

-- HD5: 1 chiếc WindFit Bomber (SPCT028)
(5, 28, 1, 520000.00, 520000.00, GETDATE(), 1);
GO

-- 14. LỊCH SỬ HÓA ĐƠN (TIMELINE)
INSERT INTO lich_su_hoa_don (id_hoa_don, id_nhan_vien, hanh_dong, ghi_chu, ngay_tao) VALUES
(1, 3, N'Tạo đơn hàng tại quầy', N'Nhân viên Lê Hoàng Nam tạo đơn và in hóa đơn cho khách', '2026-06-15 10:15:00'),
(1, 3, N'Thanh toán hoàn tất', N'Khách thanh toán tiền mặt thành công 850.000 VNĐ', '2026-06-15 10:25:00'),

(2, 2, N'Tiếp nhận đơn hàng online', N'Đơn hàng tạo từ website WindFit Store', '2026-06-18 14:00:00'),
(2, 2, N'Xác nhận đơn hàng', N'Gọi điện chốt size L màu Đen với khách hàng', '2026-06-18 15:30:00'),
(2, 3, N'Bàn giao đơn vị vận chuyển', N'Giao cho bưu tá Giao Hàng Tiết Kiệm mã vận đơn GHTK882193', '2026-06-19 08:30:00'),
(2, NULL, N'Giao hàng thành công', N'Khách đã nhận hàng và thanh toán chuyển khoản', '2026-06-20 11:15:00'),

(3, 3, N'Tiếp nhận đơn hàng online', N'Khách đặt mua qua website', '2026-07-01 09:30:00'),
(3, 3, N'Bàn giao đơn vị vận chuyển', N'Đã giao cho Viettel Post', '2026-07-02 08:00:00'),

(4, NULL, N'Tạo đơn hàng online mới', N'Chờ nhân viên WindFit kiểm tra tồn kho và xác nhận', GETDATE()),

(5, 3, N'Tạo đơn hàng', N'Đơn hàng ban đầu', '2026-06-10 16:20:00'),
(5, 3, N'Hủy đơn hàng', N'Khách hàng gọi điện xin hủy vì đi công tác dài ngày', '2026-06-11 09:00:00');
GO

-- 15. THANH TOÁN HÓA ĐƠN
INSERT INTO thanh_toan_hoa_don (id_hoa_don, id_phuong_thuc_thanh_toan, ma_giao_dich, so_tien, ghi_chu, ngay_thanh_toan, trang_thai) VALUES
(1, 1, 'GD_CASH_001', 850000.00, N'Thu tiền mặt trực tiếp tại quầy', '2026-06-15 10:25:00', 1),
(2, 2, 'FT26169821873', 1180000.00, N'Chuyển khoản ngân hàng Vietcombank qua VietQR', '2026-06-20 11:15:00', 1);
GO

-- 16. LỊCH SỬ THANH TOÁN
INSERT INTO lich_su_thanh_toan (id_hoa_don, id_thanh_toan_hoa_don, id_nhan_vien, hanh_dong, so_tien, ghi_chu, ngay_tao, trang_thai_thanh_toan) VALUES
(1, 1, 3, N'Thu tiền mặt tại quầy', 850000.00, N'Giao dịch thành công tại POS', '2026-06-15 10:25:00', N'Đã thanh toán'),
(2, 2, 2, N'Xác nhận tiền về tài khoản', 1180000.00, N'Kế toán xác nhận nhận đủ tiền chuyển khoản', '2026-06-20 11:15:00', N'Đã thanh toán');
GO

-- =====================================================================================
-- KIỂM TRA THỐNG KÊ TỔNG SỐ BẢN GHI ĐÃ TẠO
-- =====================================================================================
SELECT 'vai_tro' AS Bang, COUNT(*) AS SoLuong FROM vai_tro
UNION ALL SELECT 'nhan_vien', COUNT(*) FROM nhan_vien
UNION ALL SELECT 'khach_hang', COUNT(*) FROM khach_hang
UNION ALL SELECT 'dia_chi_khach_hang', COUNT(*) FROM dia_chi_khach_hang
UNION ALL SELECT 'danh_muc', COUNT(*) FROM danh_muc
UNION ALL SELECT 'thuong_hieu', COUNT(*) FROM thuong_hieu
UNION ALL SELECT 'chat_lieu', COUNT(*) FROM chat_lieu
UNION ALL SELECT 'xuat_xu', COUNT(*) FROM xuat_xu
UNION ALL SELECT 'kieu_dang', COUNT(*) FROM kieu_dang
UNION ALL SELECT 'mon_the_thao', COUNT(*) FROM mon_the_thao
UNION ALL SELECT 'mau_sac', COUNT(*) FROM mau_sac
UNION ALL SELECT 'kich_co', COUNT(*) FROM kich_co
UNION ALL SELECT 'san_pham', COUNT(*) FROM san_pham
UNION ALL SELECT 'san_pham_chi_tiet', COUNT(*) FROM san_pham_chi_tiet
UNION ALL SELECT 'hinh_anh_san_pham', COUNT(*) FROM hinh_anh_san_pham
UNION ALL SELECT 'phieu_giam_gia', COUNT(*) FROM phieu_giam_gia
UNION ALL SELECT 'khach_hang_phieu_giam_gia', COUNT(*) FROM khach_hang_phieu_giam_gia
UNION ALL SELECT 'dot_giam_gia', COUNT(*) FROM dot_giam_gia
UNION ALL SELECT 'dot_giam_gia_chi_tiet', COUNT(*) FROM dot_giam_gia_chi_tiet
UNION ALL SELECT 'phuong_thuc_thanh_toan', COUNT(*) FROM phuong_thuc_thanh_toan
UNION ALL SELECT 'hinh_thuc_thanh_toan', COUNT(*) FROM hinh_thuc_thanh_toan
UNION ALL SELECT 'hoa_don', COUNT(*) FROM hoa_don
UNION ALL SELECT 'chi_tiet_hoa_don', COUNT(*) FROM chi_tiet_hoa_don
UNION ALL SELECT 'lich_su_hoa_don', COUNT(*) FROM lich_su_hoa_don
UNION ALL SELECT 'thanh_toan_hoa_don', COUNT(*) FROM thanh_toan_hoa_don
UNION ALL SELECT 'lich_su_thanh_toan', COUNT(*) FROM lich_su_thanh_toan;
GO
