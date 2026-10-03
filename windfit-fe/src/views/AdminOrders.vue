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

    <!-- Advanced Filter & Search Toolbar -->
    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white mb-4">
      <div class="row g-2 align-items-center">
        <!-- Search keyword -->
        <div class="col-md-4">
          <div class="search-input-box">
            <i class="fas fa-search search-icon"></i>
            <input 
              v-model="filters.keyword" 
              type="text" 
              placeholder="Tìm theo mã HĐ, tên khách hàng, SĐT..."
              @input="page = 0"
            />
            <button v-if="filters.keyword" class="btn-clear" @click="filters.keyword = ''; page = 0">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>

        <!-- Date Range: Start -->
        <div class="col-sm-6 col-md-2">
          <input 
            v-model="filters.startDate" 
            type="date" 
            class="form-control form-control-sm bg-light" 
            title="Từ ngày tạo"
            @change="page = 0"
          />
        </div>

        <!-- Date Range: End -->
        <div class="col-sm-6 col-md-2">
          <input 
            v-model="filters.endDate" 
            type="date" 
            class="form-control form-control-sm bg-light" 
            title="Đến ngày tạo"
            @change="page = 0"
          />
        </div>

        <!-- Price Range -->
        <div class="col-sm-6 col-md-2">
          <select v-model="filters.priceTier" class="form-select form-select-sm bg-light" @change="page = 0">
            <option value="all">Tất cả mức giá</option>
            <option value="under500">Dưới 500.000 đ</option>
            <option value="500to1000">500.000 đ - 1.000.000 đ</option>
            <option value="1000to2000">1.000.000 đ - 2.000.000 đ</option>
            <option value="over2000">Trên 2.000.000 đ</option>
          </select>
        </div>

        <!-- Reset Button -->
        <div class="col-sm-6 col-md-2 d-flex justify-content-end">
          <button class="btn btn-sm btn-outline-secondary w-100" @click="resetFilters">
            <i class="fas fa-undo me-1"></i> Đặt lại
          </button>
        </div>
      </div>
    </div>

    <!-- Data Table Hóa Đơn -->
    <div class="table-card">
      <div v-if="isLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin text-primary fa-2x"></i>
        <p class="mt-2 text-muted">Đang tải dữ liệu hóa đơn WindFit...</p>
      </div>

      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon"><i class="fas fa-folder-open"></i></div>
        <h3>Không tìm thấy hóa đơn nào</h3>
        <p class="text-muted">Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
        <button class="btn btn-secondary mt-2" @click="resetFilters">
          <i class="fas fa-undo"></i> Đặt lại bộ lọc
        </button>
      </div>

      <div v-else>
        <div class="table-responsive">
          <table class="windfit-table">
            <thead>
              <tr>
                <th style="width: 50px;">#</th>
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
              <tr v-for="(hd, idx) in pagedOrders" :key="hd.id" class="table-row-hover">
                <td class="text-muted small">{{ page * size + idx + 1 }}</td>
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

        <!-- Full Pagination Toolbar -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mt-4 pt-3 border-top gap-3">
          <div class="d-flex align-items-center gap-2 text-muted small">
            <span>Hiển thị</span>
            <select class="form-select form-select-sm w-auto bg-light" v-model.number="size" @change="page = 0">
              <option :value="5">5</option>
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
            </select>
            <span>dòng / trang</span>
            <span class="ms-2 border-start ps-3 text-secondary">
              Hiển thị <strong>{{ paginationFrom }} - {{ paginationTo }}</strong> trên tổng số <strong>{{ totalElements }}</strong> hóa đơn
            </span>
          </div>

          <nav aria-label="Order navigation" v-if="totalPages > 0">
            <ul class="pagination pagination-sm mb-0">
              <li class="page-item" :class="{ disabled: page === 0 }">
                <button class="page-link" @click="page = 0" title="Trang đầu">
                  <i class="fas fa-angle-double-left"></i>
                </button>
              </li>
              <li class="page-item" :class="{ disabled: page === 0 }">
                <button class="page-link" @click="page = Math.max(0, page - 1)" title="Trang trước">
                  <i class="fas fa-angle-left"></i>
                </button>
              </li>
              <li 
                v-for="pNum in visiblePageNumbers" 
                :key="pNum" 
                class="page-item"
                :class="{ active: page === pNum - 1 }"
              >
                <button class="page-link" @click="page = pNum - 1">{{ pNum }}</button>
              </li>
              <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
                <button class="page-link" @click="page = Math.min(totalPages - 1, page + 1)" title="Trang sau">
                  <i class="fas fa-angle-right"></i>
                </button>
              </li>
              <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
                <button class="page-link" @click="page = totalPages - 1" title="Trang cuối">
                  <i class="fas fa-angle-double-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
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
const isLoading = ref(false)
const isRefreshing = ref(false)

// Pagination states
const page = ref(0)
const size = ref(10)

const filters = ref({
  keyword: '',
  kenhBan: 'all',
  trangThai: 'all',
  startDate: '',
  endDate: '',
  priceTier: 'all'
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

// Filter logic
const filteredOrders = computed(() => {
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
  if (filters.value.startDate) {
    res = res.filter(h => h.ngayTao && h.ngayTao.substring(0, 10) >= filters.value.startDate)
  }
  if (filters.value.endDate) {
    res = res.filter(h => h.ngayTao && h.ngayTao.substring(0, 10) <= filters.value.endDate)
  }
  if (filters.value.priceTier !== 'all') {
    if (filters.value.priceTier === 'under500') res = res.filter(h => (h.thanhTien || 0) < 500000)
    else if (filters.value.priceTier === '500to1000') res = res.filter(h => (h.thanhTien || 0) >= 500000 && (h.thanhTien || 0) <= 1000000)
    else if (filters.value.priceTier === '1000to2000') res = res.filter(h => (h.thanhTien || 0) > 1000000 && (h.thanhTien || 0) <= 2000000)
    else if (filters.value.priceTier === 'over2000') res = res.filter(h => (h.thanhTien || 0) > 2000000)
  }
  return res
})

// Pagination computed
const totalElements = computed(() => filteredOrders.value.length)
const totalPages = computed(() => Math.ceil(totalElements.value / size.value) || 1)

const paginationFrom = computed(() => {
  if (totalElements.value === 0) return 0
  return page.value * size.value + 1
})

const paginationTo = computed(() => {
  return Math.min((page.value + 1) * size.value, totalElements.value)
})

const visiblePageNumbers = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, page.value + 1 - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const pagedOrders = computed(() => {
  const start = page.value * size.value
  return filteredOrders.value.slice(start, start + size.value)
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
  } finally {
    isLoading.value = false
    isRefreshing.value = false
  }
}

function changeChannel(ch) {
  filters.value.kenhBan = ch
  page.value = 0
}

function changeStatus(st) {
  filters.value.trangThai = st
  page.value = 0
}

function resetFilters() {
  filters.value = {
    keyword: '',
    kenhBan: 'all',
    trangThai: 'all',
    startDate: '',
    endDate: '',
    priceTier: 'all'
  }
  page.value = 0
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

<style scoped>
.admin-orders-page {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.page-subtitle {
  color: #64748b;
  font-size: 0.875rem;
  margin-top: 4px;
}

.top-actions {
  display: flex;
  gap: 10px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.metric-card {
  border-radius: 16px;
  padding: 18px 20px;
  color: white;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.bg-gradient-blue { background: linear-gradient(135deg, #2563eb, #1d4ed8); }
.bg-gradient-slate { background: linear-gradient(135deg, #475569, #334155); }
.bg-gradient-amber { background: linear-gradient(135deg, #d97706, #b45309); }
.bg-gradient-cyan { background: linear-gradient(135deg, #0891b2, #0e7490); }
.bg-gradient-emerald { background: linear-gradient(135deg, #059669, #047857); }

.metric-icon {
  font-size: 1.8rem;
  opacity: 0.9;
}

.metric-info {
  display: flex;
  flex-direction: column;
}

.metric-label {
  font-size: 0.8rem;
  font-weight: 500;
  opacity: 0.9;
}

.metric-value {
  font-size: 1.25rem;
  font-weight: 700;
}

.tabs-container {
  margin-bottom: 12px;
}

.main-channel-tabs {
  display: flex;
  gap: 8px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 2px;
}

.channel-tab-btn {
  border: none;
  background: none;
  padding: 10px 18px;
  font-weight: 600;
  color: #64748b;
  border-radius: 10px 10px 0 0;
  cursor: pointer;
  transition: all 0.2s;
}

.channel-tab-btn.active {
  color: #2563eb;
  background: white;
  border-bottom: 3px solid #2563eb;
}

.status-sub-tabs {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  padding-bottom: 12px;
  margin-bottom: 16px;
}

.sub-tab-btn {
  border: 1px solid #e2e8f0;
  background: white;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 500;
  color: #475569;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.15s;
}

.sub-tab-btn.active {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}

.tab-badge-num {
  background: rgba(0,0,0,0.08);
  padding: 2px 7px;
  border-radius: 10px;
  font-size: 0.75rem;
}

.sub-tab-btn.active .tab-badge-num {
  background: rgba(255,255,255,0.25);
  color: white;
}

.search-input-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input-box input {
  width: 100%;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  border-radius: 10px;
  padding: 7px 32px 7px 34px;
  font-size: 0.85rem;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: #94a3b8;
  font-size: 0.85rem;
}

.btn-clear {
  position: absolute;
  right: 10px;
  border: none;
  background: none;
  color: #94a3b8;
  cursor: pointer;
}

.table-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

.windfit-table {
  width: 100%;
  border-collapse: collapse;
}

.windfit-table thead th {
  background: #f8fafc;
  color: #475569;
  font-weight: 600;
  font-size: 0.82rem;
  padding: 12px 14px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
}

.windfit-table tbody td {
  padding: 14px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 0.86rem;
}

.order-code-badge {
  font-weight: 700;
  color: #2563eb;
  cursor: pointer;
  background: #eff6ff;
  padding: 4px 8px;
  border-radius: 6px;
}

.customer-info-cell {
  display: flex;
  flex-direction: column;
}

.channel-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 0.78rem;
  font-weight: 600;
}

.channel-pos {
  background: #fef3c7;
  color: #92400e;
}

.channel-onl {
  background: #e0e7ff;
  color: #3730a3;
}

.items-count-badge {
  background: #f1f5f9;
  color: #475569;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.price-cell {
  display: flex;
  flex-direction: column;
}

.final-amount-val {
  font-weight: 700;
  color: #0f172a;
}

.discount-val {
  font-size: 0.75rem;
  color: #ef4444;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.78rem;
  font-weight: 600;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.badge-status-0 { background: #f1f5f9; color: #475569; }
.badge-status-1 { background: #fef3c7; color: #b45309; }
.badge-status-2 { background: #cffafe; color: #0e7490; }
.badge-status-3 { background: #dcfce7; color: #15803d; }
.badge-status-4 { background: #fee2e2; color: #b91c1c; }
.badge-status-5 { background: #ffedd5; color: #c2410c; }
.badge-status-6 { background: #f3e8ff; color: #7e22ce; }

.action-btn-group {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s;
}

.action-btn:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.btn-view:hover {
  background: #eff6ff;
  color: #2563eb;
  border-color: #bfdbfe;
}

.btn-print:hover {
  background: #f0fdf4;
  color: #16a34a;
  border-color: #bbf7d0;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 48px 16px;
}

.empty-icon {
  font-size: 3rem;
  color: #cbd5e1;
  margin-bottom: 12px;
}
</style>
