import axios from 'axios'
import router from '../router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// Request Interceptor: Tự động đính kèm JWT Bearer Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('windfit_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response Interceptor: Xử lý lỗi 401 / 403 tập trung
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('windfit_token')
      localStorage.removeItem('windfit_user')
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    return Promise.reject(error.response?.data || error)
  }
)

export const authService = {
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  getProfile() {
    return api.get('/auth/me')
  }
}

export const attributeService = {
  getAllOptions() {
    return api.get('/thuoc-tinh/all-options')
  },
  getByType(type) {
    return api.get(`/thuoc-tinh/${type}`)
  },
  create(type, data) {
    return api.post(`/thuoc-tinh/${type}`, data)
  },
  update(type, id, data) {
    return api.put(`/thuoc-tinh/${type}/${id}`, data)
  },
  toggleStatus(type, id) {
    return api.delete(`/thuoc-tinh/${type}/${id}`)
  }
}

export const productService = {
  getAll(params = {}) {
    return api.get('/san-pham', { params })
  },
  getById(id) {
    return api.get(`/san-pham/${id}`)
  },
  create(data) {
    return api.post('/san-pham', data)
  },
  update(id, data) {
    return api.put(`/san-pham/${id}`, data)
  },
  toggleStatus(id) {
    return api.patch(`/san-pham/${id}/toggle-status`)
  },
  getVariants(productId) {
    return api.get(`/san-pham/${productId}/bien-the`)
  },
  getVariantById(spctId) {
    return api.get(`/san-pham/bien-the/${spctId}`)
  },
  updateVariant(spctId, data) {
    return api.put(`/san-pham/bien-the/${spctId}`, data)
  },
  toggleVariantStatus(spctId) {
    return api.patch(`/san-pham/bien-the/${spctId}/toggle-status`)
  },
  scanBarcode(barcode) {
    return api.get(`/san-pham/barcode/${encodeURIComponent(barcode)}`)
  }
}

export const discountService = {
  getVouchers(params = {}) {
    return api.get('/phieu-giam-gia', { params })
  },
  getVoucherById(id) {
    return api.get(`/phieu-giam-gia/${id}`)
  },
  createVoucher(data) {
    return api.post('/phieu-giam-gia', data)
  },
  updateVoucher(id, data) {
    return api.put(`/phieu-giam-gia/${id}`, data)
  },
  toggleVoucherStatus(id) {
    return api.patch(`/phieu-giam-gia/${id}/toggle-status`)
  },
  validateVoucher(data) {
    return api.post('/phieu-giam-gia/validate', data)
  },
  getAvailableVouchers() {
    return api.get('/phieu-giam-gia/available')
  },
  getCampaigns(params = {}) {
    return api.get('/dot-giam-gia', { params })
  },
  getCampaignById(id) {
    return api.get(`/dot-giam-gia/${id}`)
  },
  createCampaign(data) {
    return api.post('/dot-giam-gia', data)
  },
  updateCampaign(id, data) {
    return api.put(`/dot-giam-gia/${id}`, data)
  },
  toggleCampaignStatus(id) {
    return api.patch(`/dot-giam-gia/${id}/toggle-status`)
  }
}

export const customerService = {
  getAll(params = {}) {
    return api.get('/khach-hang', { params })
  },
  getById(id) {
    return api.get(`/khach-hang/${id}`)
  },
  create(data) {
    return api.post('/khach-hang', data)
  },
  update(id, data) {
    return api.put(`/khach-hang/${id}`, data)
  },
  toggleStatus(id) {
    return api.patch(`/khach-hang/${id}/toggle-status`)
  }
}

export const employeeService = {
  getAll(params = {}) {
    return api.get('/nhan-vien', { params })
  },
  getById(id) {
    return api.get(`/nhan-vien/${id}`)
  },
  create(data) {
    return api.post('/nhan-vien', data)
  },
  update(id, data) {
    return api.put(`/nhan-vien/${id}`, data)
  },
  toggleStatus(id) {
    return api.patch(`/nhan-vien/${id}/toggle-status`)
  }
}

export default api

