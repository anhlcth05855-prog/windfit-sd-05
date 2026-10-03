-- =====================================================================================
-- HỆ THỐNG QUẢN LÝ & BÁN ÁO KHOÁC GIÓ WINDFIT (SD_005)
-- CƠ SỞ DỮ LIỆU SQL SERVER - 26 BẢNG CHUẨN THEO ERD MỚI
-- =====================================================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'WindFit_DB')
BEGIN
    CREATE DATABASE WindFit_DB;
END
GO

USE WindFit_DB;
GO

-- 1. BẢNG VAI TRÒ
IF OBJECT_ID('vai_tro', 'U') IS NULL
CREATE TABLE vai_tro (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_vai_tro NVARCHAR(50) NOT NULL UNIQUE,
    ten_vai_tro NVARCHAR(100) NOT NULL,
    trang_thai INT DEFAULT 1 -- 1: Hoạt động, 0: Khóa
);
GO

-- 2. BẢNG NHÂN VIÊN
IF OBJECT_ID('nhan_vien', 'U') IS NULL
CREATE TABLE nhan_vien (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_vai_tro BIGINT FOREIGN KEY REFERENCES vai_tro(id),
    ma_nhan_vien NVARCHAR(50) NOT NULL UNIQUE,
    ho_ten NVARCHAR(150) NOT NULL,
    email NVARCHAR(150),
    so_dien_thoai NVARCHAR(20),
    mat_khau NVARCHAR(255) NOT NULL,
    gioi_tinh BIT DEFAULT 1, -- 1: Nam, 0: Nữ
    dia_chi NVARCHAR(255),
    anh_dai_dien NVARCHAR(500),
    ngay_sinh DATE,
    ngay_tao DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 3. BẢNG KHÁCH HÀNG
IF OBJECT_ID('khach_hang', 'U') IS NULL
CREATE TABLE khach_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_khach_hang NVARCHAR(50) NOT NULL UNIQUE,
    ho_ten NVARCHAR(150) NOT NULL,
    so_dien_thoai NVARCHAR(20) NOT NULL,
    email NVARCHAR(150),
    mat_khau NVARCHAR(255),
    gioi_tinh BIT DEFAULT 1,
    ngay_sinh DATE,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 4. BẢNG ĐỊA CHỈ KHÁCH HÀNG
IF OBJECT_ID('dia_chi_khach_hang', 'U') IS NULL
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
-- Danh mục
IF OBJECT_ID('danh_muc', 'U') IS NULL
CREATE TABLE danh_muc (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_danh_muc NVARCHAR(50) NOT NULL UNIQUE,
    ten_danh_muc NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Thương hiệu
IF OBJECT_ID('thuong_hieu', 'U') IS NULL
CREATE TABLE thuong_hieu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_thuong_hieu NVARCHAR(50) NOT NULL UNIQUE,
    ten_thuong_hieu NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Chất liệu
IF OBJECT_ID('chat_lieu', 'U') IS NULL
CREATE TABLE chat_lieu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_chat_lieu NVARCHAR(50) NOT NULL UNIQUE,
    ten_chat_lieu NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Xuất xứ
IF OBJECT_ID('xuat_xu', 'U') IS NULL
CREATE TABLE xuat_xu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_xuat_xu NVARCHAR(50) NOT NULL UNIQUE,
    ten_xuat_xu NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Kiểu dáng
IF OBJECT_ID('kieu_dang', 'U') IS NULL
CREATE TABLE kieu_dang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_kieu_dang NVARCHAR(50) NOT NULL UNIQUE,
    ten_kieu_dang NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Môn thể thao (Chạy bộ, Leo núi, Đạp xe, Trượt tuyết, Thường ngày...)
IF OBJECT_ID('mon_the_thao', 'U') IS NULL
CREATE TABLE mon_the_thao (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_mon_the_thao NVARCHAR(50) NOT NULL UNIQUE,
    ten_mon_the_thao NVARCHAR(150) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Màu sắc (KHÔNG có ma_hex theo ERD chuẩn)
IF OBJECT_ID('mau_sac', 'U') IS NULL
CREATE TABLE mau_sac (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_mau NVARCHAR(50) NOT NULL UNIQUE,
    ten_mau NVARCHAR(100) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Kích cỡ
IF OBJECT_ID('kich_co', 'U') IS NULL
CREATE TABLE kich_co (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_kich_co NVARCHAR(50) NOT NULL UNIQUE,
    ten_kich_co NVARCHAR(50) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- 6. BẢNG SẢN PHẨM CHÍNH (ÁO KHOÁC GIÓ) - CÓ mon_the_thao, KHÔNG có lop_lot
IF OBJECT_ID('san_pham', 'U') IS NULL
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
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 7. BẢNG SẢN PHẨM CHI TIẾT (SPCT) - KHÔNG có gia_nhap
IF OBJECT_ID('san_pham_chi_tiet', 'U') IS NULL
CREATE TABLE san_pham_chi_tiet (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham(id),
    id_mau_sac BIGINT NOT NULL FOREIGN KEY REFERENCES mau_sac(id),
    id_kich_co BIGINT NOT NULL FOREIGN KEY REFERENCES kich_co(id),
    ma_spct NVARCHAR(100) NOT NULL UNIQUE,
    ma_vach NVARCHAR(100),
    gia_ban DECIMAL(18,2) NOT NULL,
    so_luong_ton INT NOT NULL DEFAULT 0,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 8. HÌNH ẢNH SẢN PHẨM (FK trực tiếp id_san_pham)
IF OBJECT_ID('hinh_anh_san_pham', 'U') IS NULL
CREATE TABLE hinh_anh_san_pham (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham(id),
    url_anh NVARCHAR(500) NOT NULL,
    is_anh_chinh BIT DEFAULT 0
);
GO

-- 9. PHIẾU GIẢM GIÁ (VOUCHER)
IF OBJECT_ID('phieu_giam_gia', 'U') IS NULL
CREATE TABLE phieu_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_phieu_giam_gia NVARCHAR(50) NOT NULL UNIQUE,
    ten_phieu_giam_gia NVARCHAR(200) NOT NULL,
    loai_giam_gia NVARCHAR(50) NOT NULL, -- 'PHAN_TRAM' hoặc 'TIEN_MAT'
    gia_tri_giam DECIMAL(18,2) NOT NULL,
    giam_toi_da DECIMAL(18,2),
    don_toi_thieu DECIMAL(18,2) DEFAULT 0,
    so_luong INT NOT NULL DEFAULT 0,
    so_luong_da_dung INT DEFAULT 0,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- Khách hàng nhận voucher cá nhân
IF OBJECT_ID('khach_hang_phieu_giam_gia', 'U') IS NULL
CREATE TABLE khach_hang_phieu_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang BIGINT NOT NULL FOREIGN KEY REFERENCES khach_hang(id),
    id_phieu_giam_gia BIGINT NOT NULL FOREIGN KEY REFERENCES phieu_giam_gia(id),
    ngay_su_dung DATETIME,
    trang_thai INT DEFAULT 1
);
GO

-- 10. ĐỢT GIẢM GIÁ (CAMPAIGN / FLASH SALE)
IF OBJECT_ID('dot_giam_gia', 'U') IS NULL
CREATE TABLE dot_giam_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_dot_giam_gia NVARCHAR(50) NOT NULL UNIQUE,
    ten_dot_giam_gia NVARCHAR(200) NOT NULL,
    mo_ta NVARCHAR(500),
    dieu_kien_ap_dung NVARCHAR(500),
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- Chi tiết đợt giảm giá theo SPCT
IF OBJECT_ID('dot_giam_gia_chi_tiet', 'U') IS NULL
CREATE TABLE dot_giam_gia_chi_tiet (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_dot_giam_gia BIGINT NOT NULL FOREIGN KEY REFERENCES dot_giam_gia(id),
    id_san_pham_chi_tiet BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id),
    so_luong INT DEFAULT 0,
    phan_tram_giam DECIMAL(5,2) NOT NULL,
    gia_sau_giam DECIMAL(18,2) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- 11. PHƯƠNG THỨC & HÌNH THỨC THANH TOÁN
IF OBJECT_ID('phuong_thuc_thanh_toan', 'U') IS NULL
CREATE TABLE phuong_thuc_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_phuong_thuc NVARCHAR(50) NOT NULL UNIQUE,
    ten_phuong_thuc NVARCHAR(100) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

IF OBJECT_ID('hinh_thuc_thanh_toan', 'U') IS NULL
CREATE TABLE hinh_thuc_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_hinh_thuc NVARCHAR(50) NOT NULL UNIQUE,
    ten_hinh_thuc NVARCHAR(100) NOT NULL,
    trang_thai INT DEFAULT 1
);
GO

-- 12. HÓA ĐƠN
IF OBJECT_ID('hoa_don', 'U') IS NULL
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
    ngay_tao DATETIME DEFAULT GETDATE(),
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
IF OBJECT_ID('chi_tiet_hoa_don', 'U') IS NULL
CREATE TABLE chi_tiet_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_san_pham_chi_tiet BIGINT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id),
    so_luong INT NOT NULL,
    don_gia DECIMAL(18,2) NOT NULL,
    thanh_tien DECIMAL(18,2) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 14. LỊCH SỬ HÓA ĐƠN (TIMELINE)
IF OBJECT_ID('lich_su_hoa_don', 'U') IS NULL
CREATE TABLE lich_su_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_nhan_vien BIGINT FOREIGN KEY REFERENCES nhan_vien(id),
    hanh_dong NVARCHAR(255) NOT NULL,
    ghi_chu NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 15. THANH TOÁN HÓA ĐƠN
IF OBJECT_ID('thanh_toan_hoa_don', 'U') IS NULL
CREATE TABLE thanh_toan_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_phuong_thuc_thanh_toan BIGINT NOT NULL FOREIGN KEY REFERENCES phuong_thuc_thanh_toan(id),
    ma_giao_dich NVARCHAR(100),
    so_tien DECIMAL(18,2) NOT NULL,
    ghi_chu NVARCHAR(255),
    ngay_thanh_toan DATETIME DEFAULT GETDATE(),
    trang_thai INT DEFAULT 1
);
GO

-- 16. LỊCH SỬ THANH TOÁN
IF OBJECT_ID('lich_su_thanh_toan', 'U') IS NULL
CREATE TABLE lich_su_thanh_toan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don BIGINT NOT NULL FOREIGN KEY REFERENCES hoa_don(id),
    id_thanh_toan_hoa_don BIGINT FOREIGN KEY REFERENCES thanh_toan_hoa_don(id),
    id_nhan_vien BIGINT FOREIGN KEY REFERENCES nhan_vien(id),
    hanh_dong NVARCHAR(255) NOT NULL,
    so_tien DECIMAL(18,2),
    ghi_chu NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    trang_thai_thanh_toan NVARCHAR(50) DEFAULT N'Chưa thanh toán'
);
GO
