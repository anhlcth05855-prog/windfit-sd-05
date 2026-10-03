<template>
  <div v-if="isOpen && hoaDon" class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal-card animate-scale-up">
      <!-- Modal Header -->
      <div class="modal-header">
        <div class="header-left">
          <div class="invoice-badge-wrap">
            <span class="channel-tag" :class="hoaDon.kenhBan === 'TAI_QUAY' ? 'tag-pos' : 'tag-onl'">
              <i :class="hoaDon.kenhBan === 'TAI_QUAY' ? 'fas fa-store' : 'fas fa-globe'"></i>
              {{ hoaDon.kenhBan === 'TAI_QUAY' ? 'Tại Quầy (POS)' : 'Online Web' }}
            </span>
            <span class="status-tag" :class="'status-' + hoaDon.trangThai">
              {{ hoaDon.tenTrangThai }}
            </span>
          </div>
          <h2 class="invoice-title">Hóa Đơn: <span>{{ hoaDon.maHoaDon }}</span></h2>
          <p class="invoice-time"><i class="far fa-clock"></i> Khởi tạo lúc: {{ formatDate(hoaDon.ngayTao) }}</p>
        </div>

        <div class="header-actions">
          <button class="btn btn-secondary" @click="$emit('print', hoaDon)">
            <i class="fas fa-print"></i> In Hóa Đơn
          </button>
          <button class="btn-close-modal" @click="$emit('close')">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>

      <!-- Stepper / Timeline Tiến Trình Đơn Hàng -->
      <div class="stepper-box">
        <h4 class="stepper-title"><i class="fas fa-route"></i> Tiến Trình Trạng Thái Đơn Hàng</h4>
        <div class="timeline-steps">
          <div 
            v-for="step in availableSteps" 
            :key="step.status"
            class="timeline-step"
            :class="{
              'step-completed': hoaDon.trangThai >= step.status && hoaDon.trangThai !== 4 && hoaDon.trangThai !== 5 && hoaDon.trangThai !== 6,
              'step-active': hoaDon.trangThai === step.status,
              'step-cancelled': (hoaDon.trangThai === 4 && step.status === 4) || (hoaDon.trangThai === 5 && step.status === 5) || (hoaDon.trangThai === 6 && step.status === 6)
            }"
          >
            <div class="step-icon">
              <i :class="step.icon"></i>
            </div>
            <div class="step-label">{{ step.label }}</div>
          </div>
        </div>
      </div>

      <!-- Return Request Warning Box (If in Return State) -->
      <div v-if="hoaDon.trangThai === 5 || hoaDon.trangThai === 6" class="alert-return-box mx-4 mb-3 p-3 rounded-3" :class="hoaDon.trangThai === 5 ? 'alert-warning' : 'alert-success'">
        <div class="d-flex align-items-center gap-2 mb-2">
          <i :class="hoaDon.trangThai === 5 ? 'fas fa-exclamation-triangle fs-5 text-warning' : 'fas fa-check-circle fs-5 text-success'"></i>
          <h4 class="m-0 fw-bold fs-6">
            {{ hoaDon.trangThai === 5 ? 'Đơn Hàng Đang Có Yêu Cầu Hoàn Hàng' : 'Đơn Hàng Đã Được Hoàn Tiền & Nhập Lại Kho' }}
          </h4>
        </div>
        <div class="small">
          <div><strong>Lý do hoàn:</strong> {{ hoaDon.lyDoHoan || 'Khách báo áo không vừa / đổi mẫu khác' }}</div>
          <div><strong>Số tiền hoàn:</strong> <span class="text-danger fw-bold">{{ formatVND(hoaDon.tienHoan || hoaDon.thanhTien) }}</span></div>
          <div v-if="hoaDon.ngayHoan"><strong>Thời gian yêu cầu:</strong> {{ formatDate(hoaDon.ngayHoan) }}</div>
        </div>
      </div>

      <div class="modal-body-grid">
        <!-- Cột Trái: Thông tin khách hàng & Giao hàng -->
        <div class="info-card">
          <h3 class="card-subtitle"><i class="fas fa-user-shield"></i> Thông Tin Khách Hàng & Giao Nhận</h3>
          <div class="info-list">
            <div class="info-row">
              <span class="label">Khách hàng:</span>
              <span class="val font-semibold">{{ hoaDon.tenKhachHang || hoaDon.tenNguoiNhan || 'Khách vãng lai' }}</span>
            </div>
            <div class="info-row">
              <span class="label">Số điện thoại:</span>
              <span class="val">{{ hoaDon.sdtKhachHang || hoaDon.sdtNguoiNhan || 'Không có' }}</span>
            </div>
            <div class="info-row">
              <span class="label">Địa chỉ giao:</span>
              <span class="val">{{ hoaDon.diaChiGiao || 'Nhận trực tiếp tại Showroom WindFit' }}</span>
            </div>
            <div class="info-row">
              <span class="label">Nhân viên phục vụ:</span>
              <span class="val text-primary font-semibold">{{ hoaDon.tenNhanVien || 'Hệ thống tự động' }}</span>
            </div>
            <div class="info-row" v-if="hoaDon.ghiChu">
              <span class="label">Ghi chú đơn:</span>
              <span class="val highlight-note">{{ hoaDon.ghiChu }}</span>
            </div>
            <div class="info-row" v-if="hoaDon.lyDoHuy">
              <span class="label text-danger">Lý do hủy:</span>
              <span class="val text-danger fw-bold">{{ hoaDon.lyDoHuy }}</span>
            </div>
          </div>

          <!-- Thông tin thanh toán -->
          <h3 class="card-subtitle mt-4"><i class="fas fa-credit-card"></i> Hình Thức Thanh Toán</h3>
          <div class="payment-box">
            <div v-if="hoaDon.danhSachThanhToan && hoaDon.danhSachThanhToan.length > 0">
              <div v-for="tt in hoaDon.danhSachThanhToan" :key="tt.id" class="payment-item">
                <div class="payment-method-badge">
                  <i :class="getPaymentIcon(tt.hinhThuc)"></i>
                  <span>{{ formatPaymentMethod(tt.hinhThuc) }}</span>
                </div>
                <div class="payment-amount">{{ formatVND(tt.soTien) }}</div>
                <div class="payment-ref">Mã GD: {{ tt.maGiaoDich || 'TXN-WINDFIT' }}</div>
              </div>
            </div>
            <div v-else class="text-muted p-2">
              <i class="fas fa-info-circle"></i> Chưa ghi nhận giao dịch thanh toán
            </div>
          </div>

          <!-- Audit History Timeline -->
          <h3 class="card-subtitle mt-4" v-if="hoaDon.danhSachLichSu && hoaDon.danhSachLichSu.length > 0">
            <i class="fas fa-history"></i> Lịch Sử Thao Tác
          </h3>
          <div class="audit-history-box" v-if="hoaDon.danhSachLichSu && hoaDon.danhSachLichSu.length > 0">
            <div v-for="log in hoaDon.danhSachLichSu" :key="log.id" class="audit-item py-1 border-bottom">
              <div class="d-flex justify-content-between small">
                <span class="fw-bold text-dark">{{ log.hanhDong }}</span>
                <span class="text-muted">{{ formatDate(log.ngayTao) }}</span>
              </div>
              <div class="text-muted small" v-if="log.ghiChu">{{ log.ghiChu }}</div>
            </div>
          </div>
        </div>

        <!-- Cột Phải: Danh sách sản phẩm áo khoác gió -->
        <div class="items-card">
          <h3 class="card-subtitle"><i class="fas fa-tshirt"></i> Danh Sách Áo Khoác Gió ({{ hoaDon.danhSachChiTiet ? hoaDon.danhSachChiTiet.length : 0 }})</h3>
          <div class="table-container">
            <table class="custom-table">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Phân loại</th>
                  <th>Đơn giá</th>
                  <th>SL</th>
                  <th class="text-right">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in hoaDon.danhSachChiTiet" :key="item.id">
                  <td>
                    <div class="product-cell">
                      <img :src="item.urlAnh || 'https://images.unsplash.com/photo-1544441893-675973e31985?w=200'" :alt="item.tenSanPham" class="product-thumb" />
                      <div>
                        <div class="product-name">{{ item.tenSanPham }}</div>
                        <div class="product-sku">SKU: {{ item.maSku }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="variant-badge">{{ item.tenMau }}</span>
                    <span class="size-badge">{{ item.tenKichCo }}</span>
                  </td>
                  <td>{{ formatVND(item.donGia) }}</td>
                  <td><span class="qty-badge">{{ item.soLuong }}</span></td>
                  <td class="text-right font-semibold text-primary">{{ formatVND(item.thanhTien) }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Tổng kết tiền -->
          <div class="financial-summary">
            <div class="fin-row">
              <span>Tổng tiền hàng:</span>
              <span>{{ formatVND(hoaDon.tongTien) }}</span>
            </div>
            <div class="fin-row text-success" v-if="hoaDon.tienGiam > 0">
              <span>Voucher giảm giá ({{ hoaDon.maPhieuGiamGia || 'Khuyến mãi' }}):</span>
              <span>-{{ formatVND(hoaDon.tienGiam) }}</span>
            </div>
            <div class="fin-row final-amount">
              <span>Tổng thanh toán:</span>
              <span class="amount">{{ formatVND(hoaDon.thanhTien) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Footer & State Transition Actions -->
      <div class="modal-footer">
        <div class="footer-left">
          <span class="text-muted text-sm">Cập nhật lần cuối: {{ formatDate(hoaDon.ngaySua || hoaDon.ngayTao) }}</span>
        </div>
        <div class="footer-actions d-flex gap-2">
          <!-- Khi đơn đang hoàn thành: có thể yêu cầu hoàn hàng trong 7 ngày -->
          <button 
            v-if="hoaDon.trangThai === 3" 
            class="btn btn-outline-warning"
            @click="handleRequestReturn"
          >
            <i class="fas fa-undo"></i> Yêu Cầu Đổi / Hoàn Hàng
          </button>

          <!-- Khi đơn đang ở trạng thái Yêu Cầu Hoàn Hàng (5) -->
          <button 
            v-if="hoaDon.trangThai === 5" 
            class="btn btn-success"
            @click="handleApproveReturn"
          >
            <i class="fas fa-check"></i> Chấp Thuận Hoàn Hàng
          </button>
          <button 
            v-if="hoaDon.trangThai === 5" 
            class="btn btn-outline-danger"
            @click="handleRejectReturn"
          >
            <i class="fas fa-times"></i> Từ Chối Hoàn Hàng
          </button>

          <!-- Nút hủy hóa đơn thông thường -->
          <button 
            v-if="hoaDon.trangThai !== 4 && hoaDon.trangThai !== 3 && hoaDon.trangThai !== 5 && hoaDon.trangThai !== 6" 
            class="btn btn-danger"
            @click="handleCancelOrder"
          >
            <i class="fas fa-ban"></i> Hủy Hóa Đơn
          </button>

          <!-- Nút chuyển sang trạng thái tiếp theo -->
          <button 
            v-if="nextActionText" 
            class="btn btn-primary"
            @click="handleNextStatus"
          >
            <i :class="nextActionIcon"></i> {{ nextActionText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  isOpen: Boolean,
  hoaDon: Object
})

const emit = defineEmits(['close', 'statusChange', 'cancelOrder', 'print', 'requestReturn', 'approveReturn', 'rejectReturn'])

const availableSteps = [
  { status: 0, label: 'Chờ thanh toán', icon: 'fas fa-file-invoice' },
  { status: 1, label: 'Chờ xác nhận', icon: 'fas fa-check-circle' },
  { status: 2, label: 'Đang giao hàng', icon: 'fas fa-shipping-fast' },
  { status: 3, label: 'Hoàn thành', icon: 'fas fa-box-open' }
]

const nextActionText = computed(() => {
  if (!props.hoaDon) return null
  switch (props.hoaDon.trangThai) {
    case 0: return 'Xác nhận & Thu tiền'
    case 1: return 'Xác nhận đơn & Xuất kho'
    case 2: return 'Xác nhận đã giao thành công'
    default: return null
  }
})

const nextActionIcon = computed(() => {
  if (!props.hoaDon) return ''
  switch (props.hoaDon.trangThai) {
    case 0: return 'fas fa-money-bill-wave'
    case 1: return 'fas fa-dolly'
    case 2: return 'fas fa-check-double'
    default: return ''
  }
})

function handleNextStatus() {
  let next = 3
  if (props.hoaDon.trangThai === 0) next = 1
  else if (props.hoaDon.trangThai === 1) next = 2
  else if (props.hoaDon.trangThai === 2) next = 3
  emit('statusChange', { id: props.hoaDon.id, newStatus: next })
}

function handleCancelOrder() {
  const reason = prompt('Nhập lý do hủy hóa đơn:')
  if (reason) {
    emit('cancelOrder', { id: props.hoaDon.id, reason })
  }
}

function handleRequestReturn() {
  const reason = prompt('Nhập lý do khách yêu cầu hoàn hàng (Chính sách trong vòng 7 ngày):')
  if (reason) {
    emit('requestReturn', { id: props.hoaDon.id, reason, amount: props.hoaDon.thanhTien })
  }
}

function handleApproveReturn() {
  if (confirm(`Xác nhận hoàn hàng cho hóa đơn ${props.hoaDon.maHoaDon}? Hệ thống sẽ cộng lại số lượng tồn kho tự động.`)) {
    emit('approveReturn', { id: props.hoaDon.id })
  }
}

function handleRejectReturn() {
  const reason = prompt('Nhập lý do từ chối yêu cầu hoàn hàng:')
  if (reason) {
    emit('rejectReturn', { id: props.hoaDon.id, reason })
  }
}

function formatVND(val) {
  if (!val) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function formatPaymentMethod(m) {
  switch (m) {
    case 'TIEN_MAT': return 'Tiền mặt tại quầy'
    case 'VIETQR': return 'Quét mã VietQR ngân hàng'
    case 'CHUYEN_KHOAN': return 'Chuyển khoản VCB/MB'
    case 'VNPAY': return 'Cổng thanh toán VNPAY'
    default: return m || 'Thanh toán trực tiếp'
  }
}

function getPaymentIcon(m) {
  switch (m) {
    case 'TIEN_MAT': return 'fas fa-money-bill-alt'
    case 'VIETQR': return 'fas fa-qrcode'
    case 'CHUYEN_KHOAN': return 'fas fa-university'
    default: return 'fas fa-credit-card'
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  padding: 1.5rem;
}

.modal-card {
  background: #ffffff;
  border-radius: 1.25rem;
  width: 100%;
  max-width: 1050px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.modal-header {
  padding: 1.25rem 1.75rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

.invoice-badge-wrap {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  margin-bottom: 0.25rem;
}

.channel-tag {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 9999px;
  text-transform: uppercase;
}
.tag-pos { background: #dbeafe; color: #1e40af; }
.tag-onl { background: #fef3c7; color: #92400e; }

.status-tag {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 9999px;
  background: #e2e8f0;
  color: #334155;
}
.status-0 { background: #e0e7ff; color: #4338ca; }
.status-1 { background: #fef9c3; color: #854d0e; }
.status-2 { background: #cffafe; color: #155e75; }
.status-3 { background: #dcfce7; color: #166534; }
.status-4 { background: #fee2e2; color: #991b1b; }
.status-5 { background: #fed7aa; color: #9a3412; }
.status-6 { background: #ede9fe; color: #5b21b6; }

.invoice-title {
  font-size: 1.35rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}
.invoice-title span { color: #0284c7; }

.invoice-time {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0;
}

.btn-close-modal {
  background: transparent;
  border: none;
  font-size: 1.25rem;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 0.5rem;
}
.btn-close-modal:hover { color: #0f172a; background: #e2e8f0; }

.stepper-box {
  padding: 1rem 1.75rem;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
}
.stepper-title {
  font-size: 0.85rem;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  margin-bottom: 0.75rem;
}
.timeline-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}
.timeline-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.35rem;
  flex: 1;
  position: relative;
}
.step-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f1f5f9;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  border: 2px solid #e2e8f0;
}
.step-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
}

.step-completed .step-icon {
  background: #0284c7;
  color: #ffffff;
  border-color: #0284c7;
}
.step-completed .step-label { color: #0284c7; }

.step-active .step-icon {
  background: #22c55e;
  color: #ffffff;
  border-color: #22c55e;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.2);
}
.step-active .step-label { color: #166534; font-weight: 700; }

.modal-body-grid {
  display: grid;
  grid-template-columns: 1fr 1.3fr;
  gap: 1.5rem;
  padding: 1.5rem 1.75rem;
  overflow-y: auto;
  flex: 1;
}

.card-subtitle {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1rem;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
  font-size: 0.88rem;
}
.info-row {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px dashed #f1f5f9;
  padding-bottom: 0.4rem;
}
.info-row .label { color: #64748b; }
.info-row .val { color: #0f172a; text-align: right; }

.payment-box {
  background: #f8fafc;
  border-radius: 0.75rem;
  padding: 0.75rem;
}
.payment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.88rem;
}
.payment-method-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #0284c7;
}
.payment-amount { font-weight: 700; color: #0f172a; }

.table-container {
  max-height: 240px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
}
.custom-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.82rem;
}
.custom-table th {
  background: #f8fafc;
  padding: 0.6rem 0.75rem;
  font-weight: 700;
  color: #475569;
  text-align: left;
}
.custom-table td {
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #f1f5f9;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.product-thumb {
  width: 36px;
  height: 36px;
  border-radius: 0.35rem;
  object-fit: cover;
}
.product-name { font-weight: 600; color: #0f172a; }
.product-sku { font-size: 0.72rem; color: #64748b; font-family: monospace; }

.variant-badge, .size-badge {
  display: inline-block;
  font-size: 0.72rem;
  padding: 0.15rem 0.4rem;
  border-radius: 0.25rem;
  margin-right: 0.25rem;
}
.variant-badge { background: #0f172a; color: #fff; }
.size-badge { background: #e2e8f0; color: #475569; }

.financial-summary {
  margin-top: 1rem;
  background: #f8fafc;
  border-radius: 0.75rem;
  padding: 0.85rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  font-size: 0.88rem;
}
.fin-row {
  display: flex;
  justify-content: space-between;
}
.final-amount {
  border-top: 1px solid #e2e8f0;
  padding-top: 0.5rem;
  font-weight: 800;
  color: #0f172a;
}
.final-amount .amount {
  font-size: 1.15rem;
  color: #0284c7;
}

.modal-footer {
  padding: 1rem 1.75rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

.alert-return-box {
  border-left: 4px solid;
}
.alert-warning {
  background: #fffbeb;
  border-color: #f59e0b;
  color: #92400e;
}
.alert-success {
  background: #f0fdf4;
  border-color: #22c55e;
  color: #166534;
}
</style>
