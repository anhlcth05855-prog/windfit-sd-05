<template>
  <div v-if="isOpen && hoaDon" class="modal-backdrop print-backdrop" @click.self="$emit('close')">
    <div class="print-wrapper animate-scale-up">
      <div class="print-toolbar no-print">
        <button class="btn btn-secondary" @click="$emit('close')">
          <i class="fas fa-arrow-left"></i> Quay lại
        </button>
        <button class="btn btn-primary" @click="triggerPrint">
          <i class="fas fa-print"></i> Bấm để In Ngay
        </button>
      </div>

      <!-- Khổ Giấy Hóa Đơn In (A5/K80 chuyên nghiệp) -->
      <div class="invoice-paper" id="printable-invoice">
        <div class="paper-header">
          <div class="brand-logo">WINDFIT</div>
          <div class="brand-sub">PERFORMANCE JACKETS - CHUYÊN ÁO KHOÁC GIÓ CAO CẤP</div>
          <p class="store-info"><i class="fas fa-map-marker-alt"></i> Showroom: Số 88 Cầu Giấy, Hà Nội | Hotline: 1900 6868</p>
          <div class="divider-dashed"></div>
        </div>

        <div class="paper-title-section">
          <h2 class="paper-title">HÓA ĐƠN BÁN LẺ</h2>
          <div class="paper-invoice-code">Mã số: <strong>{{ hoaDon.maHoaDon }}</strong></div>
          <div class="paper-time">Ngày tạo: {{ hoaDon.ngayTao }}</div>
        </div>

        <div class="paper-client-info">
          <div><strong>Khách hàng:</strong> {{ hoaDon.tenKhachHang || hoaDon.tenNguoiNhan || 'Khách lẻ tại quầy' }}</div>
          <div><strong>SĐT:</strong> {{ hoaDon.sdtKhachHang || hoaDon.sdtNguoiNhan || '—' }}</div>
          <div><strong>Địa chỉ:</strong> {{ hoaDon.diaChiGiao || 'Nhận tại cửa hàng WindFit' }}</div>
          <div><strong>Kênh bán:</strong> {{ hoaDon.kenhBan === 'TAI_QUAY' ? 'Bán tại quầy POS' : 'Đơn đặt trực tuyến' }}</div>
          <div><strong>Thu ngân:</strong> {{ hoaDon.tenNhanVien || 'Quản Trị Viên' }}</div>
        </div>

        <!-- Bảng Sản Phẩm -->
        <table class="paper-table">
          <thead>
            <tr>
              <th style="width: 5%">STT</th>
              <th style="width: 45%">Sản phẩm áo khoác</th>
              <th style="width: 15%">Phân loại</th>
              <th style="width: 10%">SL</th>
              <th style="width: 25%" class="text-right">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in hoaDon.danhSachChiTiet" :key="item.id">
              <td>{{ idx + 1 }}</td>
              <td>
                <div class="fw-bold">{{ item.tenSanPham }}</div>
                <div class="small-text">{{ item.maSku }}</div>
              </td>
              <td>{{ item.tenMau }} - {{ item.tenKichCo }}</td>
              <td>{{ item.soLuong }}</td>
              <td class="text-right">{{ formatVND(item.thanhTien) }}</td>
            </tr>
          </tbody>
        </table>

        <div class="divider-dashed"></div>

        <!-- Bảng Tính Tiền -->
        <div class="paper-totals">
          <div class="total-row">
            <span>Tổng tiền hàng:</span>
            <span>{{ formatVND(hoaDon.tongTien) }}</span>
          </div>
          <div class="total-row" v-if="hoaDon.tienGiam > 0">
            <span>Chiết khấu/Voucher:</span>
            <span>-{{ formatVND(hoaDon.tienGiam) }}</span>
          </div>
          <div class="total-row final">
            <span>KHÁCH PHẢI TRẢ:</span>
            <span class="final-price">{{ formatVND(hoaDon.thanhTien) }}</span>
          </div>
        </div>

        <!-- VietQR Box -->
        <div class="paper-qr-box">
          <div class="qr-info">
            <div class="fw-bold">Thanh toán chuyển khoản VietQR:</div>
            <div>Ngân Hàng Quân Đội (MB Bank)</div>
            <div>STK: <strong>0981111001</strong> - CTK: <strong>WINDFIT STORE</strong></div>
            <div>Cú pháp: <strong>{{ hoaDon.maHoaDon }}</strong></div>
          </div>
          <img 
            :src="'https://api.qrserver.com/v1/create-qr-code/?size=120x120&data=WINDFIT_' + hoaDon.maHoaDon" 
            alt="VietQR WindFit" 
            class="qr-img" 
          />
        </div>

        <div class="paper-footer">
          <p class="thank-you">Cảm ơn Quý khách đã tin chọn Áo Khoác Gió WindFit!</p>
          <p class="policy-note">Quý khách vui lòng giữ lại hóa đơn để được hỗ trợ đổi size/mẫu trong vòng 7 ngày.</p>
          <p class="hotline">Website: windfit.vn | Hỗ trợ: support@windfit.vn</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isOpen: Boolean,
  hoaDon: Object
})

defineEmits(['close'])

function triggerPrint() {
  window.print()
}

function formatVND(val) {
  if (!val) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}
</script>
