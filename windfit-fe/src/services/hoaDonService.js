import api from './api.js'
import { danhSachHoaDonMock } from '../data/mockData.js'

let localHoaDonList = JSON.parse(localStorage.getItem('windfit_orders')) || [...danhSachHoaDonMock]

function saveToLocalStorage() {
  localStorage.setItem('windfit_orders', JSON.stringify(localHoaDonList))
}

export const hoaDonService = {
  // Lấy danh sách hóa đơn kèm bộ lọc
  async layDanhSachHoaDon(params = {}) {
    try {
      const res = await api.get('/hoa-don', {
        params: {
          keyword: params.keyword || undefined,
          trangThai: params.trangThai !== undefined && params.trangThai !== 'all' ? params.trangThai : undefined,
          kenhBan: params.kenhBan && params.kenhBan !== 'all' ? params.kenhBan : undefined
        }
      })
      if (res && res.data && res.data.content) {
        return res.data.content
      }
    } catch (e) {
      console.warn('API error, using local fallback:', e)
    }

    // Filter local fallback
    let list = [...localHoaDonList]
    if (params.keyword) {
      const kw = params.keyword.toLowerCase().trim()
      list = list.filter(h => 
        (h.maHoaDon && h.maHoaDon.toLowerCase().includes(kw)) ||
        (h.tenKhachHang && h.tenKhachHang.toLowerCase().includes(kw)) ||
        (h.sdtKhachHang && h.sdtKhachHang.includes(kw)) ||
        (h.tenNguoiNhan && h.tenNguoiNhan.toLowerCase().includes(kw))
      )
    }
    if (params.trangThai !== undefined && params.trangThai !== 'all') {
      list = list.filter(h => h.trangThai === Number(params.trangThai))
    }
    if (params.kenhBan && params.kenhBan !== 'all') {
      list = list.filter(h => h.kenhBan === params.kenhBan)
    }

    return list.sort((a, b) => new Date(b.ngayTao) - new Date(a.ngayTao))
  },

  // Lấy chi tiết theo ID
  async layChiTiet(id) {
    try {
      const res = await api.get(`/hoa-don/${id}`)
      if (res && res.data) return res.data
    } catch (e) {}

    return localHoaDonList.find(h => h.id === Number(id))
  },

  // Cập nhật trạng thái
  async capNhatTrangThai(id, trangThaiMoi, ghiChu = '') {
    try {
      const res = await api.put(`/hoa-don/${id}/trang-thai`, {
        trangThaiMoi,
        ghiChu
      })
      if (res && res.data) return res.data
    } catch (e) {}

    const index = localHoaDonList.findIndex(h => h.id === Number(id))
    if (index !== -1) {
      localHoaDonList[index].trangThai = trangThaiMoi
      localHoaDonList[index].tenTrangThai = getTenTrangThai(trangThaiMoi)
      localHoaDonList[index].ngaySua = new Date().toISOString()
      if (ghiChu) {
        localHoaDonList[index].ghiChu = (localHoaDonList[index].ghiChu ? localHoaDonList[index].ghiChu + ' | ' : '') + ghiChu
      }
      saveToLocalStorage()
      return localHoaDonList[index]
    }
  },

  // Hủy hóa đơn
  async huyHoaDon(id, lyDoHuy) {
    try {
      await api.put(`/hoa-don/${id}/huy?lyDoHuy=${encodeURIComponent(lyDoHuy)}`)
    } catch (e) {}
    return this.capNhatTrangThai(id, 4, `Lý do hủy: ${lyDoHuy}`)
  },

  // Yêu cầu hoàn hàng
  async yeuCauHoan(id, lyDoHoan, tienHoan) {
    try {
      const res = await api.post(`/hoa-don/${id}/yeu-cau-hoan`, {
        lyDoHoan,
        tienHoan
      })
      if (res && res.data) return res.data
    } catch (e) {}
    return this.capNhatTrangThai(id, 5, `Yêu cầu hoàn hàng: ${lyDoHoan}`)
  },

  // Xác nhận hoàn hàng
  async xacNhanHoan(id) {
    try {
      const res = await api.post(`/hoa-don/${id}/xac-nhan-hoan`)
      if (res && res.data) return res.data
    } catch (e) {}
    return this.capNhatTrangThai(id, 6, 'Đã hoàn tiền và nhận lại hàng vào kho')
  },

  // Từ chối hoàn hàng
  async tuChoiHoan(id, lyDoTuChoi) {
    try {
      const res = await api.post(`/hoa-don/${id}/tu-choi-hoan?lyDoTuChoi=${encodeURIComponent(lyDoTuChoi)}`)
      if (res && res.data) return res.data
    } catch (e) {}
    return this.capNhatTrangThai(id, 3, `Từ chối hoàn hàng: ${lyDoTuChoi}`)
  },

  // Tạo hóa đơn (POS hoặc Online)
  async taoHoaDon(payload) {
    try {
      const res = await api.post('/hoa-don', payload)
      if (res && res.data) return res.data
    } catch (e) {}

    const newId = Date.now()
    const maHD = 'HD-' + (payload.kenhBan === 'TAI_QUAY' ? 'POS' : 'ONL') + '-' + (newId % 1000000)
    
    let tongTien = 0
    const danhSachChiTiet = (payload.items || []).map((item, idx) => {
      const tt = item.soLuong * item.donGia
      tongTien += tt
      return {
        id: newId + idx,
        idSanPhamChiTiet: item.idSanPhamChiTiet,
        tenSanPham: item.tenSanPham || 'Áo Khoác Gió WindFit',
        maSku: item.maSku || 'WF-SKU-001',
        tenMau: item.tenMau || 'Đen',
        tenKichCo: item.tenKichCo || 'L',
        urlAnh: item.urlAnh || 'https://images.unsplash.com/photo-1544441893-675973e31985?w=200',
        soLuong: item.soLuong,
        donGia: item.donGia,
        thanhTien: tt
      }
    })

    const tienGiam = payload.tienGiam || 0
    const thanhTien = Math.max(0, tongTien - tienGiam)
    const trangThai = payload.kenhBan === 'TAI_QUAY' ? 3 : 1

    const newOrder = {
      id: newId,
      maHoaDon: maHD,
      kenhBan: payload.kenhBan,
      idKhachHang: payload.idKhachHang || null,
      tenKhachHang: payload.tenKhachHang || 'Khách lẻ tại quầy',
      sdtKhachHang: payload.sdtKhachHang || '',
      tenNguoiNhan: payload.tenNguoiNhan || payload.tenKhachHang || 'Khách lẻ tại quầy',
      sdtNguoiNhan: payload.sdtNguoiNhan || payload.sdtKhachHang || '',
      diaChiGiao: payload.diaChiGiao || 'Mua tại quầy WindFit Store',
      idNhanVien: payload.idNhanVien || 1,
      tenNhanVien: 'Quản Trị Viên',
      maPhieuGiamGia: payload.maPhieuGiamGia || null,
      tongTien,
      tienGiam,
      thanhTien,
      ghiChu: payload.ghiChu || '',
      ngayTao: new Date().toISOString(),
      ngaySua: new Date().toISOString(),
      trangThai,
      tenTrangThai: getTenTrangThai(trangThai),
      danhSachChiTiet,
      danhSachThanhToan: [
        {
          id: newId + 10,
          hinhThuc: payload.hinhThucThanhToan || 'TIEN_MAT',
          soTien: thanhTien,
          maGiaoDich: 'TXN-' + newId,
          ngayThanhToan: new Date().toISOString(),
          trangThai: 1
        }
      ]
    }

    localHoaDonList.unshift(newOrder)
    saveToLocalStorage()
    return newOrder
  }
}

function getTenTrangThai(tt) {
  switch (tt) {
    case 0: return 'Chờ thanh toán'
    case 1: return 'Chờ xác nhận'
    case 2: return 'Đang giao hàng'
    case 3: return 'Hoàn thành'
    case 4: return 'Đã hủy'
    case 5: return 'Yêu cầu hoàn hàng'
    case 6: return 'Đã hoàn hàng'
    default: return 'Không xác định'
  }
}
