<template>
  <div class="admin-orders-page">
    <!-- Header Thống Kê & Tiêu Đề -->
    <div class="page-top-bar">
      <div>
        <h1 class="page-title"><i class="fas fa-file-invoice-dollar text-primary"></i> Quản Lý Hóa Đơn & Đơn Hàng</h1>
        <p class="page-subtitle">Theo dõi, kiểm soát toàn diện luồng hóa đơn bán lẻ tại quầy POS và đơn hàng online WindFit</p>
      </div>

      <div class="top-actions">
        <button class="btn btn-secondary" @click="taiDuLieu">
          <i class="fas fa-sync-alt" :class="{ 'fa-spin': isRefreshing }"></i> Làm mới
        </button>
        <button class="btn btn-primary" @click="$router.push('/admin/pos')">
          <i class="fas fa-cash-register"></i> Bán Hàng Tại Quầy (POS)
        </button>
      </div>
    </div>

    <!-- 5 Thẻ Thống Kê Nhanh (Metrics Cards) -->
    <div class="metrics-grid">
      <div class="metric-card bg-gradient-blue">
        <div class="metric-icon"><i class="fas fa-coins"></i></div>
        <div class="metric-info">
          <span class="metric-label">Tổng Doanh Thu</span>
          <span class="metric-value">{{ formatVND(stats.tongDoanhThu) }}</span>
        </div>
      </div>

      <div class="metric-card bg-gradient-slate">
        <div class="metric-icon"><i class="fas fa-receipt"></i></div>
        <div class="metric-info">
          <span class="metric-label">Tổng Đơn Hàng</span>
          <span class="metric-value">{{ stats.tongDonHang }} đơn</span>
        </div>
      </div>

      <div class="metric-card bg-gradient-amber">
        <div class="metric-icon"><i class="fas fa-clock"></i></div>
        <div class="metric-info">
          <span class="metric-label">Chờ Xác Nhận</span>
          <span class="metric-value">{{ stats.choXacNhan }} đơn</span>
        </div>
      </div>

      <div class="metric-card bg-gradient-cyan">
        <div class="metric-icon"><i class="fas fa-truck-moving"></i></div>
        <div class="metric-info">
          <span class="metric-label">Đang Giao Hàng</span>
          <span class="metric-value">{{ stats.dangGiao }} đơn</span>
        </div>
      </div>

      <div class="metric-card bg-gradient-emerald">
        <div class="metric-icon"><i class="fas fa-check-double"></i></div>
        <div class="metric-info">
          <span class="metric-label">Đơn Hoàn Thành</span>
          <span class="metric-value">{{ stats.hoanThanh }} đơn</span>
        </div>
      </div>
    </div>

    <!-- MAIN TABS: Kênh Bán -->
    <div class="tabs-container">
      <div class="main-channel-tabs">
        <button 
          class="channel-tab-btn" 
          :class="{ active: filters.kenhBan === 'all' }"
          @click="changeChannel('all')"
        >
          <i class="fas fa-layer-group"></i> Tất Cả Kênh ({{ danhSachGoc.length }})
        </button>
        <button 
          class="channel-tab-btn" 
          :class="{ active: filters.kenhBan === 'TAI_QUAY' }"
          @click="changeChannel('TAI_QUAY')"
        >
          <i class="fas fa-store"></i> Tại Quầy POS ({{ countKenh('TAI_QUAY') }})
        </button>
        <button 
          class="channel-tab-btn" 
          :class="{ active: filters.kenhBan === 'ONLINE' }"
          @click="changeChannel('ONLINE')"
        >
          <i class="fas fa-globe"></i> Bán Trực Tuyến Online ({{ countKenh('ONLINE') }})
        </button>
      </div>
    </div>

    <!-- SUB TABS: Trạng Thái Hóa Đơn -->
    <div class="status-sub-tabs">
      <button 
        v-for="st in statusTabs" 
        :key="st.val"
        class="sub-tab-btn"
        :class="{ active: filters.trangThai === st.val }"
        @click="changeStatus(st.val)"
      >
        <i :class="st.icon"></i>
        <span>{{ st.label }}</span>
        <span class="tab-badge-num">{{ getCountByStatus(st.val) }}</span>
      </button>
    </div>

    <!-- Filter & Search Toolbar -->
    <div class="filter-toolbar">
      <div class="search-input-box">
        <i class="fas fa-search search-icon"></i>
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="Tìm theo mã HĐ, tên khách hàng, SĐT..."
          @input="applyFilters"
        />
        <button v-if="filters.keyword" class="btn-clear" @click="filters.keyword = ''; applyFilters()">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="toolbar-right">
        <span class="result-count">Hiển thị <strong>{{ danhSachHienThi.length }}</strong> hóa đơn</span>
      </div>
    </div>

    <!-- Data Table Hóa Đơn -->
    <div class="table-card">
      <div v-if="isLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin text-primary fa-2x"></i>
        <p class="mt-2 text-muted">Đang tải dữ liệu hóa đơn WindFit...</p>
      </div>

      <div v-else-if="danhSachHienThi.length === 0" class="empty-state">
        <div class="empty-icon"><i class="fas fa-folder-open"></i></div>
        <h3>Không tìm thấy hóa đơn nào</h3>
        <p class="text-muted">Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
        <button class="btn btn-secondary mt-2" @click="resetFilters">
          <i class="fas fa-undo"></i> Đặt lại bộ lọc
        </button>
      </div>

      <div v-else class="table-responsive">
        <table class="windfit-table">
          <thead>
            <tr>
              <th>Mã Hóa Đơn</th>
              <th>Khách Hàng / SĐT</th>
              <th>Kênh Bán</th>
              <th>Thời Gian</th>
              <th>Sản Phẩm</th>
              <th>Thành Tiền</th>
              <th>Trạng Thái</th>
              <th class="text-center">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="hd in danhSachHienThi" :key="hd.id" class="table-row-hover">
              <td>
                <span class="order-code-badge" @click="xemChiTiet(hd)">
                  <i class="fas fa-receipt mr-1"></i> {{ hd.maHoaDon }}
                </span>
              </td>
              <td>
                <div class="customer-info-cell">
                  <span class="font-semibold text-slate-900">{{ hd.tenKhachHang || hd.tenNguoiNhan || 'Khách vãng lai' }}</span>
                  <span class="text-muted text-xs"><i class="fas fa-phone-alt"></i> {{ hd.sdtKhachHang || hd.sdtNguoiNhan || 'Không có SĐT' }}</span>
                </div>
              </td>
              <td>
                <span class="channel-badge" :class="hd.kenhBan === 'TAI_QUAY' ? 'channel-pos' : 'channel-onl'">
                  <i :class="hd.kenhBan === 'TAI_QUAY' ? 'fas fa-store' : 'fas fa-globe'"></i>
                  {{ hd.kenhBan === 'TAI_QUAY' ? 'Tại Quầy' : 'Online' }}
                </span>
              </td>
              <td>
                <div class="text-xs text-muted">
                  <div>{{ formatDate(hd.ngayTao) }}</div>
                  <div>{{ formatTime(hd.ngayTao) }}</div>
                </div>
              </td>
              <td>
                <div class="items-count-badge" :title="renderItemsPreview(hd)">
                  <i class="fas fa-tshirt"></i> {{ getTotalItems(hd) }} áo gió
                </div>
              </td>
              <td>
                <div class="price-cell">
                  <span class="final-amount-val">{{ formatVND(hd.thanhTien) }}</span>
                  <span v-if="hd.tienGiam > 0" class="discount-val">-{{ formatVND(hd.tienGiam) }}</span>
                </div>
              </td>
              <td>
                <span class="status-badge" :class="'badge-status-' + hd.trangThai">
                  <span class="status-dot"></span>
                  {{ hd.tenTrangThai }}
                </span>
              </td>
              <td class="text-center">
                <div class="action-btn-group">
                  <button class="action-btn btn-view" title="Xem chi tiết" @click="xemChiTiet(hd)">
                    <i class="fas fa-eye"></i>
                  </button>
                  <button class="action-btn btn-print" title="In hóa đơn" @click="inHoaDon(hd)">
                    <i class="fas fa-print"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modals -->
    <InvoiceDetailModal 
      :isOpen="isDetailOpen" 
      :hoaDon="selectedOrder" 
      @close="isDetailOpen = false"
      @statusChange="handleStatusChange"
      @cancelOrder="handleCancelOrder"
      @requestReturn="handleRequestReturn"
      @approveReturn="handleApproveReturn"
      @rejectReturn="handleRejectReturn"
      @print="handlePrintFromDetail"
    />

    <InvoicePrintModal 
      :isOpen="isPrintOpen" 
      :hoaDon="selectedOrder" 
      @close="isPrintOpen = false" 
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { hoaDonService } from '../services/hoaDonService.js'
import InvoiceDetailModal from '../components/InvoiceDetailModal.vue'
import InvoicePrintModal from '../components/InvoicePrintModal.vue'

defineEmits(['switchPos'])

const danhSachGoc = ref([])
const danhSachHienThi = ref([])
const isLoading = ref(false)
const isRefreshing = ref(false)

const filters = ref({
  keyword: '',
  kenhBan: 'all',
  trangThai: 'all'
})

const selectedOrder = ref(null)
const isDetailOpen = ref(false)
const isPrintOpen = ref(false)

const statusTabs = [
  { val: 'all', label: 'Tất cả trạng thái', icon: 'fas fa-list' },
  { val: 0, label: 'Chờ thanh toán', icon: 'fas fa-file-invoice' },
  { val: 1, label: 'Chờ xác nhận', icon: 'fas fa-clock' },
  { val: 2, label: 'Đang giao hàng', icon: 'fas fa-truck' },
  { val: 3, label: 'Hoàn thành', icon: 'fas fa-check-circle' },
  { val: 4, label: 'Đã hủy', icon: 'fas fa-times-circle' },
  { val: 5, label: 'Yêu cầu hoàn hàng', icon: 'fas fa-undo' },
  { val: 6, label: 'Đã hoàn hàng', icon: 'fas fa-box-open' }
]

const stats = computed(() => {
  const all = danhSachGoc.value
  const tongDoanhThu = all
    .filter(h => h.trangThai === 3)
    .reduce((sum, h) => sum + (h.thanhTien || 0), 0)
  
  return {
    tongDoanhThu,
    tongDonHang: all.length,
    choXacNhan: all.filter(h => h.trangThai === 1).length,
    dangGiao: all.filter(h => h.trangThai === 2).length,
    hoanThanh: all.filter(h => h.trangThai === 3).length
  }
})

onMounted(() => {
  taiDuLieu()
})

async function taiDuLieu() {
  isLoading.value = true
  isRefreshing.value = true
  try {
    const data = await hoaDonService.layDanhSachHoaDon()
    danhSachGoc.value = data
    applyFilters()
  } finally {
    isLoading.value = false
    isRefreshing.value = false
  }
}

function applyFilters() {
  let res = [...danhSachGoc.value]
  if (filters.value.kenhBan !== 'all') {
    res = res.filter(h => h.kenhBan === filters.value.kenhBan)
  }
  if (filters.value.trangThai !== 'all') {
    res = res.filter(h => h.trangThai === Number(filters.value.trangThai))
  }
  if (filters.value.keyword) {
    const kw = filters.value.keyword.toLowerCase().trim()
    res = res.filter(h => 
      (h.maHoaDon && h.maHoaDon.toLowerCase().includes(kw)) ||
      (h.tenKhachHang && h.tenKhachHang.toLowerCase().includes(kw)) ||
      (h.sdtKhachHang && h.sdtKhachHang.includes(kw)) ||
      (h.tenNguoiNhan && h.tenNguoiNhan.toLowerCase().includes(kw))
    )
  }
  danhSachHienThi.value = res
}

function changeChannel(ch) {
  filters.value.kenhBan = ch
  applyFilters()
}

function changeStatus(st) {
  filters.value.trangThai = st
  applyFilters()
}

function resetFilters() {
  filters.value = { keyword: '', kenhBan: 'all', trangThai: 'all' }
  applyFilters()
}

function countKenh(kb) {
  return danhSachGoc.value.filter(h => h.kenhBan === kb).length
}

function getCountByStatus(st) {
  if (st === 'all') return danhSachGoc.value.length
  return danhSachGoc.value.filter(h => h.trangThai === Number(st)).length
}

function xemChiTiet(hd) {
  selectedOrder.value = hd
  isDetailOpen.value = true
}

function inHoaDon(hd) {
  selectedOrder.value = hd
  isPrintOpen.value = true
}

function handlePrintFromDetail(hd) {
  isDetailOpen.value = false
  inHoaDon(hd)
}

async function handleStatusChange({ id, newStatus }) {
  await hoaDonService.capNhatTrangThai(id, newStatus, 'Chuyển trạng thái bởi Quản trị viên')
  await taiDuLieu()
  if (selectedOrder.value && selectedOrder.value.id === id) {
    selectedOrder.value = danhSachGoc.value.find(h => h.id === id)
  }
}

async function handleCancelOrder({ id, reason }) {
  await hoaDonService.huyHoaDon(id, reason)
  await taiDuLieu()
  if (selectedOrder.value && selectedOrder.value.id === id) {
    selectedOrder.value = danhSachGoc.value.find(h => h.id === id)
  }
}

async function handleRequestReturn({ id, reason, amount }) {
  await hoaDonService.yeuCauHoan(id, reason, amount)
  await taiDuLieu()
  if (selectedOrder.value && selectedOrder.value.id === id) {
    selectedOrder.value = danhSachGoc.value.find(h => h.id === id)
  }
}

async function handleApproveReturn({ id }) {
  await hoaDonService.xacNhanHoan(id)
  await taiDuLieu()
  if (selectedOrder.value && selectedOrder.value.id === id) {
    selectedOrder.value = danhSachGoc.value.find(h => h.id === id)
  }
}

async function handleRejectReturn({ id, reason }) {
  await hoaDonService.tuChoiHoan(id, reason)
  await taiDuLieu()
  if (selectedOrder.value && selectedOrder.value.id === id) {
    selectedOrder.value = danhSachGoc.value.find(h => h.id === id)
  }
}

function getTotalItems(hd) {
  if (!hd.danhSachChiTiet) return 0
  return hd.danhSachChiTiet.reduce((sum, i) => sum + i.soLuong, 0)
}

function renderItemsPreview(hd) {
  if (!hd.danhSachChiTiet) return ''
  return hd.danhSachChiTiet.map(i => `${i.tenSanPham} (${i.tenMau}/${i.tenKichCo}) x${i.soLuong}`).join(' | ')
}

function formatDate(dt) {
  if (!dt) return ''
  return dt.substring(0, 10)
}

function formatTime(dt) {
  if (!dt) return ''
  return dt.substring(11, 16)
}

function formatVND(val) {
  if (!val) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}
</script>
