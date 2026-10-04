<template>
  <div class="pos-container">
    <!-- Header POS -->
    <div class="pos-top-bar">
      <div class="pos-brand">
        <i class="fas fa-wind text-primary"></i>
        <span class="brand-title">WINDFIT POS</span>
        <span class="pos-badge">Bán Hàng Tại Quầy Showroom</span>
      </div>

      <div class="pos-nav-actions">
        <button class="btn btn-secondary" @click="$router.push('/admin/hoa-don')">
          <i class="fas fa-arrow-left"></i> Quay Lại Quản Lý Hóa Đơn
        </button>
      </div>
    </div>

    <!-- 2 CỘT CHÍNH -->
    <div class="pos-layout">
      <!-- CỘT TRÁI: DANH SÁCH ÁO KHOÁC GIÓ -->
      <div class="pos-catalog-panel">
        <div class="catalog-toolbar">
          <div class="search-box-pos">
            <i class="fas fa-search"></i>
            <input 
              v-model="productKeyword" 
              type="text" 
              placeholder="Tìm theo tên áo khoác gió, SKU, chất liệu..." 
            />
          </div>

          <!-- Tabs danh mục áo gió -->
          <div class="category-pills">
            <button 
              class="cat-pill" 
              :class="{ active: selectedCategory === 'all' }"
              @click="selectedCategory = 'all'"
            >
              Tất cả ({{ danhSachSanPham.length }})
            </button>
            <button 
              v-for="cat in danhMucList" 
              :key="cat"
              class="cat-pill"
              :class="{ active: selectedCategory === cat }"
              @click="selectedCategory = cat"
            >
              {{ cat }}
            </button>
          </div>
        </div>

        <!-- Lưới sản phẩm áo gió -->
        <div class="jacket-grid">
          <div 
            v-for="sp in filteredProducts" 
            :key="sp.id" 
            class="jacket-card animate-hover"
            @click="addToCart(sp)"
          >
            <div class="jacket-img-wrap">
              <img :src="sp.urlAnh" :alt="sp.tenSanPham" class="jacket-img" />
              <span class="stock-tag">Còn: {{ sp.tonKho }} áo</span>
            </div>

            <div class="jacket-details">
              <h4 class="jacket-name">{{ sp.tenSanPham }}</h4>
              <div class="jacket-tags">
                <span class="tag-color" :style="{ backgroundColor: sp.maMau }"></span>
                <span class="tag-text">{{ sp.mauSac }}</span>
                <span class="tag-size">Size: {{ sp.kichCo }}</span>
              </div>
              <div class="jacket-price-row">
                <span class="jacket-price">{{ formatVND(sp.giaNiemYet) }}</span>
                <button class="btn-quick-add" title="Thêm vào hóa đơn">
                  <i class="fas fa-plus"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- CỘT PHẢI: GIỎ HÀNG & HÓA ĐƠN ĐANG TẠO -->
      <div class="pos-checkout-panel">
        <div class="cart-card">
          <!-- Chọn khách hàng -->
          <div class="customer-select-bar">
            <div class="customer-picker">
              <i class="fas fa-user-circle text-primary"></i>
              <select v-model="selectedCustomerId" class="select-client" @change="onCustomerChange">
                <option :value="null">Khách lẻ tại quầy (Không lưu tên)</option>
                <option v-for="kh in danhSachKhachHang" :key="kh.id" :value="kh.id">
                  {{ kh.hoTen }} - {{ kh.soDienThoai }} (Hạng: {{ kh.hang }})
                </option>
              </select>
            </div>
          </div>

          <!-- Danh sách áo đã chọn vào hóa đơn -->
          <div class="cart-items-wrapper">
            <div v-if="cartItems.length === 0" class="empty-cart-state">
              <i class="fas fa-shopping-basket fa-3x text-muted"></i>
              <p class="mt-2 text-muted">Chưa có áo khoác gió nào trong hóa đơn</p>
              <span class="text-xs text-muted">Nhấn vào sản phẩm bên trái để thêm</span>
            </div>

            <div v-else class="cart-items-list">
              <div v-for="item in cartItems" :key="item.idSanPhamChiTiet" class="cart-item-row">
                <img :src="item.urlAnh" :alt="item.tenSanPham" class="cart-item-thumb" />
                <div class="cart-item-info">
                  <div class="cart-item-title">{{ item.tenSanPham }}</div>
                  <div class="cart-item-sub">{{ item.mauSac }} | Size {{ item.kichCo }}</div>
                  <div class="cart-item-price">{{ formatVND(item.donGia) }}</div>
                </div>

                <div class="cart-qty-ctrl">
                  <button class="qty-btn" @click="decreaseQty(item)">-</button>
                  <span class="qty-val">{{ item.soLuong }}</span>
                  <button class="qty-btn" @click="increaseQty(item)">+</button>
                </div>

                <div class="cart-item-total">
                  {{ formatVND(item.soLuong * item.donGia) }}
                </div>

                <button class="btn-remove-item" @click="removeItem(item)">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </div>

          <!-- Áp dụng Voucher -->
          <div class="voucher-box">
            <i class="fas fa-ticket-alt text-amber-500"></i>
            <select v-model="selectedVoucherCode" class="voucher-select">
              <option value="">Chọn phiếu giảm giá (Voucher)</option>
              <option v-for="vc in danhSachVoucher" :key="vc.maPhieu" :value="vc.maPhieu">
                {{ vc.maPhieu }} - {{ vc.tenPhieu }}
              </option>
            </select>
          </div>

          <!-- Phương thức thanh toán -->
          <div class="payment-method-selector">
            <label 
              v-for="pm in paymentMethods" 
              :key="pm.code" 
              class="payment-option"
              :class="{ active: selectedPaymentMethod === pm.code }"
            >
              <input type="radio" v-model="selectedPaymentMethod" :value="pm.code" />
              <i :class="pm.icon"></i>
              <span>{{ pm.name }}</span>
            </label>
          </div>

          <!-- Bảng tính toán tiền -->
          <div class="checkout-totals">
            <div class="ch-row">
              <span>Tổng tiền hàng:</span>
              <span>{{ formatVND(calcSubtotal) }}</span>
            </div>
            <div class="ch-row text-success" v-if="calcDiscount > 0">
              <span>Giảm giá Voucher:</span>
              <span>-{{ formatVND(calcDiscount) }}</span>
            </div>
            <div class="ch-row final-due">
              <span>KHÁCH CẦN TRẢ:</span>
              <span class="due-amount">{{ formatVND(calcFinal) }}</span>
            </div>

            <!-- Nhập tiền khách đưa -->
            <div class="cash-input-row" v-if="selectedPaymentMethod === 'TIEN_MAT'">
              <span>Tiền khách đưa:</span>
              <input 
                v-model.number="customerCash" 
                type="number" 
                placeholder="Nhập số tiền..."
                class="cash-input"
              />
            </div>
            <div class="change-row text-primary" v-if="selectedPaymentMethod === 'TIEN_MAT' && customerCash > calcFinal">
              <span>Tiền thừa trả khách:</span>
              <span class="font-bold">{{ formatVND(customerCash - calcFinal) }}</span>
            </div>
          </div>

          <!-- Nút hành động thanh toán -->
          <div class="checkout-actions">
            <button 
              class="btn btn-primary btn-block btn-lg" 
              :disabled="cartItems.length === 0"
              @click="submitCheckout"
            >
              <i class="fas fa-check-circle"></i> Thanh Toán & Hoàn Tất Hóa Đơn
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { danhSachSanPham, danhSachKhachHang, danhSachVoucher } from '../data/mockData.js'
import { hoaDonService } from '../services/hoaDonService.js'
import { showConfirm, showToast } from '../utils/dialog.js'

const emit = defineEmits(['switchAdmin', 'orderCreated'])

const productKeyword = ref('')
const selectedCategory = ref('all')
const selectedCustomerId = ref(null)
const selectedVoucherCode = ref('')
const selectedPaymentMethod = ref('TIEN_MAT')
const customerCash = ref(0)
const cartItems = ref([])

const danhMucList = computed(() => {
  return [...new Set(danhSachSanPham.map(p => p.danhMuc))]
})

const paymentMethods = [
  { code: 'TIEN_MAT', name: 'Tiền mặt', icon: 'fas fa-money-bill-wave' },
  { code: 'VIETQR', name: 'VietQR Động', icon: 'fas fa-qrcode' },
  { code: 'CHUYEN_KHOAN', name: 'Chuyển khoản', icon: 'fas fa-university' }
]

const filteredProducts = computed(() => {
  return danhSachSanPham.filter(p => {
    const matchCat = selectedCategory.value === 'all' || p.danhMuc === selectedCategory.value
    const matchKw = !productKeyword.value || 
      p.tenSanPham.toLowerCase().includes(productKeyword.value.toLowerCase()) ||
      p.maSku.toLowerCase().includes(productKeyword.value.toLowerCase()) ||
      p.mauSac.toLowerCase().includes(productKeyword.value.toLowerCase())
    return matchCat && matchKw
  })
})

const calcSubtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.soLuong * item.donGia, 0)
})

const calcDiscount = computed(() => {
  if (!selectedVoucherCode.value) return 0
  const vc = danhSachVoucher.find(v => v.maPhieu === selectedVoucherCode.value)
  if (!vc) return 0
  if (calcSubtotal.value < vc.donToiThieu) return 0
  
  if (vc.loaiGiam === 'PHAN_TRAM') {
    let d = (calcSubtotal.value * vc.giaTriGiam) / 100
    if (vc.giamToiDa && d > vc.giamToiDa) d = vc.giamToiDa
    return d
  }
  return vc.giaTriGiam
})

const calcFinal = computed(() => {
  return Math.max(0, calcSubtotal.value - calcDiscount.value)
})

function addToCart(sp) {
  const existing = cartItems.value.find(item => item.idSanPhamChiTiet === sp.idSanPhamChiTiet)
  if (existing) {
    existing.soLuong++
  } else {
    cartItems.value.push({
      idSanPhamChiTiet: sp.idSanPhamChiTiet,
      tenSanPham: sp.tenSanPham,
      maSku: sp.maSku,
      mauSac: sp.mauSac,
      kichCo: sp.kichCo,
      donGia: sp.giaNiemYet,
      urlAnh: sp.urlAnh,
      soLuong: 1
    })
  }
}

function increaseQty(item) {
  item.soLuong++
}

function decreaseQty(item) {
  if (item.soLuong > 1) {
    item.soLuong--
  } else {
    removeItem(item)
  }
}

function removeItem(item) {
  cartItems.value = cartItems.value.filter(i => i.idSanPhamChiTiet !== item.idSanPhamChiTiet)
}

function onCustomerChange() {
  // auto select voucher if customer has special rank
}

async function submitCheckout() {
  if (cartItems.value.length === 0) return

  const customer = danhSachKhachHang.find(k => k.id === selectedCustomerId.value)
  const customerName = customer ? customer.hoTen : 'Khách lẻ tại quầy'

  const isConfirmed = await showConfirm(
    'Xác nhận tạo hóa đơn & Thanh toán',
    `Tạo hóa đơn cho ${customerName} (${cartItems.value.length} sản phẩm)? Tổng thanh toán: ${formatVND(calcFinal.value)}`
  )
  if (!isConfirmed) return

  const payload = {
    kenhBan: 'TAI_QUAY',
    idKhachHang: selectedCustomerId.value,
    tenKhachHang: customerName,
    sdtKhachHang: customer ? customer.soDienThoai : '',
    maPhieuGiamGia: selectedVoucherCode.value || null,
    tienGiam: calcDiscount.value,
    hinhThucThanhToan: selectedPaymentMethod.value,
    ghiChu: `Thanh toán tại quầy (${selectedPaymentMethod.value})`,
    items: cartItems.value.map(i => ({
      idSanPhamChiTiet: i.idSanPhamChiTiet,
      tenSanPham: i.tenSanPham,
      maSku: i.maSku,
      tenMau: i.mauSac,
      tenKichCo: i.kichCo,
      urlAnh: i.urlAnh,
      soLuong: i.soLuong,
      donGia: i.donGia
    }))
  }

  const created = await hoaDonService.taoHoaDon(payload)
  showToast.success(`Tạo hóa đơn ${created.maHoaDon} thành công!`)
  cartItems.value = []
  customerCash.value = 0
  selectedVoucherCode.value = ''
  emit('switchAdmin')
}

function formatVND(val) {
  if (!val) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}
</script>
