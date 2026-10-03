import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import AdminLayout from '../layout/AdminLayout.vue'
import LoginView from '../views/auth/LoginView.vue'
import DashboardView from '../views/analytics/DashboardView.vue'
import AdminOrders from '../views/AdminOrders.vue'
import PosCheckout from '../views/PosCheckout.vue'
import ProductList from '../views/products/ProductList.vue'
import AttributeList from '../views/products/AttributeList.vue'
import VoucherList from '../views/vouchers/VoucherList.vue'
import CustomerList from '../views/customers/CustomerList.vue'
import EmployeeList from '../views/employees/EmployeeList.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { title: 'Đăng Nhập Hệ Thống' }
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashboardView,
        meta: { title: 'Bảng Điều Khiển Tổng Quan', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'pos',
        name: 'PosCheckout',
        component: PosCheckout,
        meta: { title: 'Bán Hàng Tại Quầy (POS)', requiresAuth: true }
      },
      {
        path: 'hoa-don',
        name: 'AdminOrders',
        component: AdminOrders,
        meta: { title: 'Quản Lý Hóa Đơn & Đơn Hàng', requiresAuth: true }
      },
      {
        path: 'san-pham',
        name: 'ProductList',
        component: ProductList,
        meta: { title: 'Quản Lý Áo Khoác Gió (SPCT)', requiresAuth: true }
      },
      {
        path: 'thuoc-tinh',
        name: 'AttributeList',
        component: AttributeList,
        meta: { title: 'Thuộc Tính Sản Phẩm', requiresAuth: true }
      },
      {
        path: 'phieu-giam-gia',
        name: 'VoucherList',
        component: VoucherList,
        meta: { title: 'Phiếu Giảm Giá (Voucher)', requiresAuth: true }
      },
      {
        path: 'dot-giam-gia',
        redirect: '/admin/phieu-giam-gia'
      },
      {
        path: 'khach-hang',
        name: 'CustomerList',
        component: CustomerList,
        meta: { title: 'Quản Lý Khách Hàng', requiresAuth: true }
      },
      {
        path: 'nhan-vien',
        name: 'EmployeeList',
        component: EmployeeList,
        meta: { title: 'Nhân Viên & Phân Quyền', requiresAuth: true, role: 'ADMIN' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/admin/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Set document title
  document.title = (to.meta?.title ? `${to.meta.title} | ` : '') + 'WindFit Performance Jackets'

  // Kiểm tra đăng nhập
  if (to.meta?.requiresAuth && !authStore.isAuthenticated) {
    return next('/login')
  }

  // Kiểm tra quyền ADMIN
  if (to.meta?.role === 'ADMIN' && !authStore.isAdmin) {
    alert('Bạn không có quyền truy cập vào mục này!')
    return next('/admin/pos')
  }

  next()
})

export default router
