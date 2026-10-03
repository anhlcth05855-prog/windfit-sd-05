import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import AdminLayout from '../layout/AdminLayout.vue'
import LoginView from '../views/auth/LoginView.vue'
import VoucherList from '../views/vouchers/VoucherList.vue'
import CampaignList from '../views/vouchers/CampaignList.vue'

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
    redirect: '/admin/phieu-giam-gia',
    children: [
      {
        path: 'phieu-giam-gia',
        name: 'VoucherList',
        component: VoucherList,
        meta: { title: 'Phiếu Giảm Giá (Voucher)', requiresAuth: true }
      },
      {
        path: 'dot-giam-gia',
        name: 'CampaignList',
        component: CampaignList,
        meta: { title: 'Đợt Giảm Giá (Sale)', requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/admin/phieu-giam-gia'
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
  document.title = (to.meta?.title ? `${to.meta.title} | ` : '') + 'WindFit - Phân Hệ Giảm Giá'

  // Kiểm tra đăng nhập
  if (to.meta?.requiresAuth && !authStore.isAuthenticated) {
    return next('/login')
  }

  next()
})

export default router
