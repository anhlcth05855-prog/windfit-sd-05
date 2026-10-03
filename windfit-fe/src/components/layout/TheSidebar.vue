<template>
  <aside class="windfit-sidebar-clean" :class="{ 'collapsed': isCollapsed }">
    <!-- Brand Header -->
    <div class="sidebar-brand-box">
      <router-link to="/admin/dashboard" class="brand-container">
        <div class="brand-logo-circle">
          <img src="/windfit-logo.png" alt="WindFit Logo" class="brand-png-img" />
        </div>
        <div class="brand-text-block" v-show="!isCollapsed">
          <h2 class="brand-main-title">WINDFIT</h2>
          <span class="brand-sub-title">PERFORMANCE JACKETS</span>
        </div>
      </router-link>
    </div>

    <!-- Navigation Menu Items -->
    <div class="sidebar-nav-container custom-scrollbar">
      <ul class="nav-tree">
        <!-- 1. Tổng quan -->
        <li class="nav-row">
          <router-link to="/admin/dashboard" class="nav-item-link" active-class="active">
            <span class="item-icon-box text-blue">
              <i class="fa-solid fa-table-cells-large"></i>
            </span>
            <span class="item-label" v-show="!isCollapsed">Tổng quan</span>
          </router-link>
        </li>

        <!-- 2. Quản Lý Hóa Đơn (Accordion) -->
        <li class="nav-row nav-accordion" :class="{ 'is-open': openMenus.hoaDon }">
          <div class="nav-item-link accordion-header" @click="toggleMenu('hoaDon')">
            <span class="item-icon-box text-indigo">
              <i class="fa-solid fa-receipt"></i>
            </span>
            <span class="item-label" v-show="!isCollapsed">Quản Lý Hóa Đơn</span>
            <span class="chevron-arrow" v-show="!isCollapsed">
              <i class="fa-solid" :class="openMenus.hoaDon ? 'fa-chevron-down' : 'fa-chevron-right'"></i>
            </span>
          </div>

          <transition name="slide-sub">
            <ul class="sub-nav-list" v-show="openMenus.hoaDon && !isCollapsed">
              <li>
                <router-link to="/admin/hoa-don" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-regular fa-file-lines sub-icon"></i>
                  <span>Danh sách hóa đơn</span>
                </router-link>
              </li>
              <li>
                <router-link to="/admin/hoa-don/lich-su" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-clock-rotate-left sub-icon"></i>
                  <span>Lịch sử đơn hàng</span>
                </router-link>
              </li>
            </ul>
          </transition>
        </li>

        <!-- 3. BÁN HÀNG TẠI QUẦY (Green Highlight) -->
        <li class="nav-row">
          <router-link to="/admin/pos" class="nav-item-link pos-green-link" active-class="active">
            <span class="item-icon-box text-emerald">
              <i class="fa-solid fa-cart-shopping"></i>
            </span>
            <span class="item-label green-label fw-bold" v-show="!isCollapsed">BÁN HÀNG TẠI QUẦY</span>
          </router-link>
        </li>

        <!-- 4. Quản Lý Sản phẩm (Accordion) -->
        <li class="nav-row nav-accordion" :class="{ 'is-open': openMenus.sanPham }">
          <div class="nav-item-link accordion-header" @click="toggleMenu('sanPham')">
            <span class="item-icon-box text-amber">
              <i class="fa-solid fa-box-archive"></i>
            </span>
            <span class="item-label" v-show="!isCollapsed">Quản Lý Sản phẩm</span>
            <span class="chevron-arrow" v-show="!isCollapsed">
              <i class="fa-solid" :class="openMenus.sanPham ? 'fa-chevron-down' : 'fa-chevron-right'"></i>
            </span>
          </div>

          <transition name="slide-sub">
            <ul class="sub-nav-list" v-show="openMenus.sanPham && !isCollapsed">
              <li>
                <router-link to="/admin/san-pham" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-vest sub-icon"></i>
                  <span>Sản phẩm (Áo khoác gió)</span>
                </router-link>
              </li>
              <li>
                <router-link to="/admin/thuoc-tinh" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-tags sub-icon"></i>
                  <span>Thuộc tính sản phẩm</span>
                </router-link>
              </li>
            </ul>
          </transition>
        </li>

        <!-- 5. Quản Lý Tài Khoản (Accordion) -->
        <li class="nav-row nav-accordion" :class="{ 'is-open': openMenus.taiKhoan }">
          <div class="nav-item-link accordion-header" @click="toggleMenu('taiKhoan')">
            <span class="item-icon-box text-cyan">
              <i class="fa-solid fa-user-group"></i>
            </span>
            <span class="item-label" v-show="!isCollapsed">Quản Lý Tài Khoản</span>
            <span class="chevron-arrow" v-show="!isCollapsed">
              <i class="fa-solid" :class="openMenus.taiKhoan ? 'fa-chevron-down' : 'fa-chevron-right'"></i>
            </span>
          </div>

          <transition name="slide-sub">
            <ul class="sub-nav-list" v-show="openMenus.taiKhoan && !isCollapsed">
              <li>
                <router-link to="/admin/nhan-vien" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-user-shield sub-icon"></i>
                  <span>Nhân viên</span>
                </router-link>
              </li>
              <li>
                <router-link to="/admin/khach-hang" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-users sub-icon"></i>
                  <span>Khách hàng</span>
                </router-link>
              </li>
            </ul>
          </transition>
        </li>

        <!-- 6. Quản Lý Khuyến Mãi (Accordion) -->
        <li class="nav-row nav-accordion" :class="{ 'is-open': openMenus.khuyenMai }">
          <div class="nav-item-link accordion-header" @click="toggleMenu('khuyenMai')">
            <span class="item-icon-box text-pink">
              <i class="fa-solid fa-tag"></i>
            </span>
            <span class="item-label" v-show="!isCollapsed">Quản Lý Khuyến Mãi</span>
            <span class="chevron-arrow" v-show="!isCollapsed">
              <i class="fa-solid" :class="openMenus.khuyenMai ? 'fa-chevron-down' : 'fa-chevron-right'"></i>
            </span>
          </div>

          <transition name="slide-sub">
            <ul class="sub-nav-list" v-show="openMenus.khuyenMai && !isCollapsed">
              <li>
                <router-link to="/admin/phieu-giam-gia" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-ticket sub-icon"></i>
                  <span>Phiếu giảm giá</span>
                </router-link>
              </li>
              <li>
                <router-link to="/admin/dot-giam-gia" class="sub-nav-link" active-class="sub-active">
                  <i class="fa-solid fa-percent sub-icon"></i>
                  <span>Đợt giảm giá</span>
                </router-link>
              </li>
            </ul>
          </transition>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()

const openMenus = reactive({
  hoaDon: true,
  sanPham: false,
  taiKhoan: false,
  khuyenMai: false
})

function toggleMenu(menu) {
  openMenus[menu] = !openMenus[menu]
}

// Auto open accordion depending on route
watch(() => route.path, (newPath) => {
  if (newPath.includes('/admin/hoa-don')) {
    openMenus.hoaDon = true
  } else if (newPath.includes('/admin/san-pham') || newPath.includes('/admin/thuoc-tinh')) {
    openMenus.sanPham = true
  } else if (newPath.includes('/admin/nhan-vien') || newPath.includes('/admin/khach-hang')) {
    openMenus.taiKhoan = true
  } else if (newPath.includes('/admin/phieu-giam-gia') || newPath.includes('/admin/dot-giam-gia')) {
    openMenus.khuyenMai = true
  }
}, { immediate: true })
</script>

<style scoped>
.windfit-sidebar-clean {
  width: 250px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: #ffffff;
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  z-index: 1020;
  transition: width 0.25s ease;
  user-select: none;
}

.windfit-sidebar-clean.collapsed {
  width: 78px;
}

/* Brand Header */
.sidebar-brand-box {
  padding: 24px 16px 20px;
  text-align: center;
  border-bottom: 1px solid #f8fafc;
}

.brand-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
}

.brand-logo-circle {
  width: 78px;
  height: 78px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  transition: transform 0.2s;
  overflow: hidden;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}

.brand-logo-circle:hover {
  transform: scale(1.05);
}

.brand-png-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.brand-text-block {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-main-title {
  font-size: 1.15rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 1.5px;
  margin: 0;
  line-height: 1.2;
}

.brand-sub-title {
  font-size: 0.58rem;
  font-weight: 700;
  color: #94a3b8;
  letter-spacing: 1.8px;
  margin-top: 3px;
}

/* Navigation List */
.sidebar-nav-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px;
}

.nav-tree {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-row {
  display: flex;
  flex-direction: column;
}

.nav-item-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 12px;
  color: #334155;
  text-decoration: none;
  font-size: 0.92rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s ease;
}

.nav-item-link:hover {
  background-color: #f8fafc;
  color: #0f172a;
}

.nav-item-link.active {
  background-color: #eff6ff;
  color: #2563eb;
}

/* Item Icons */
.item-icon-box {
  width: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.05rem;
  flex-shrink: 0;
}

.text-blue { color: #3b82f6; }
.text-indigo { color: #6366f1; }
.text-emerald { color: #10b981; }
.text-amber { color: #f59e0b; }
.text-cyan { color: #06b6d4; }
.text-pink { color: #ec4899; }

.item-label {
  flex: 1;
  white-space: nowrap;
}

.green-label {
  color: #10b981 !important;
  font-size: 0.88rem;
}

.chevron-arrow {
  font-size: 0.72rem;
  color: #94a3b8;
  transition: transform 0.2s ease;
}

/* Submenu items */
.sub-nav-list {
  list-style: none;
  padding: 4px 0 6px 12px;
  margin: 2px 0 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sub-nav-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  border-radius: 10px;
  font-size: 0.86rem;
  font-weight: 500;
  color: #64748b;
  text-decoration: none;
  transition: all 0.18s ease;
}

.sub-nav-link:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.sub-nav-link.sub-active {
  background-color: #eff6ff;
  color: #2563eb;
  font-weight: 600;
  border-left: 3px solid #2563eb;
  border-radius: 4px 10px 10px 4px;
}

.sub-icon {
  font-size: 0.9rem;
}

/* Animations */
.slide-sub-enter-active,
.slide-sub-leave-active {
  transition: max-height 0.25s ease, opacity 0.2s ease;
  overflow: hidden;
}

.slide-sub-enter-from,
.slide-sub-leave-to {
  max-height: 0;
  opacity: 0;
}

.slide-sub-enter-to,
.slide-sub-leave-from {
  max-height: 160px;
  opacity: 1;
}

/* Custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #e2e8f0;
  border-radius: 4px;
}
</style>
