-- =====================================================================================
-- MIGRATION CHO MODULE GIẢM GIÁ (PHIẾU GIẢM GIÁ & ĐỢT GIẢM GIÁ) - WINDFIT
-- An toàn, có kiểm tra IF NOT EXISTS (Idempotent)
-- =====================================================================================

USE WindFit_DB;
GO

-- 1. Bổ sung cột cho bảng phieu_giam_gia
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('phieu_giam_gia') AND name = 'mo_ta')
BEGIN
    ALTER TABLE phieu_giam_gia ADD mo_ta NVARCHAR(500);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('phieu_giam_gia') AND name = 'nhom_phieu')
BEGIN
    ALTER TABLE phieu_giam_gia ADD nhom_phieu NVARCHAR(50) DEFAULT 'CHUNG';
END
GO

-- Cập nhật dữ liệu mặc định cho các phiếu hiện có
UPDATE phieu_giam_gia 
SET nhom_phieu = 'CHUNG' 
WHERE nhom_phieu IS NULL;
GO

-- 2. Bổ sung cột cho bảng dot_giam_gia
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dot_giam_gia') AND name = 'hinh_thuc')
BEGIN
    ALTER TABLE dot_giam_gia ADD hinh_thuc NVARCHAR(50) DEFAULT 'THEO_MUA';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dot_giam_gia') AND name = 'gio_bat_dau')
BEGIN
    ALTER TABLE dot_giam_gia ADD gio_bat_dau TIME;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dot_giam_gia') AND name = 'gio_ket_thuc')
BEGIN
    ALTER TABLE dot_giam_gia ADD gio_ket_thuc TIME;
END
GO

-- Cập nhật dữ liệu mặc định cho các đợt giảm giá hiện có
UPDATE dot_giam_gia 
SET hinh_thuc = 'THEO_MUA' 
WHERE hinh_thuc IS NULL;
GO
