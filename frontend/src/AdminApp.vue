<script setup>
import axios from 'axios'
import { computed, onMounted, ref } from 'vue'

const api = axios.create({ baseURL: 'http://localhost:8080/api' })
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

const loggedIn = ref(Boolean(localStorage.getItem('admin_token')))
const username = ref('admin')
const password = ref('admin123456')
const message = ref('')
const tab = ref('foods')
const categories = ref([])
const foods = ref([])
const orders = ref([])
const feedbacks = ref([])
const users = ref([])
const notices = ref([])
const comments = ref([])
const stats = ref({ userCount: 0, foodCount: 0, orderCount: 0, pendingOrderCount: 0, todayOrderCount: 0, totalRevenue: 0, todayRevenue: 0, topFoods: [] })
const logs = ref([])
const statusFilter = ref('')
const userKeyword = ref('')
const userStatusFilter = ref('')
const categoryName = ref('')
const editingFoodId = ref(null)
const foodForm = ref({ categoryId: '', name: '', description: '', price: '', originalPrice: '', stock: 0, recommended: 0, specialOffer: 0, status: 1 })
const noticeForm = ref({ id: null, title: '', content: '', status: 1 })

const statusOptions = ['PENDING_PAYMENT', 'PAID', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED']
const statusLabels = { PENDING_PAYMENT: '待支付', PAID: '已支付', PREPARING: '制作中', READY: '待取餐', COMPLETED: '已完成', CANCELLED: '已取消' }
const pageTitle = computed(() => ({ dashboard: '经营概览', foods: '菜品管理', categories: '分类管理', orders: '订单处理', feedback: '反馈处理', comments: '评价管理', logs: '操作日志', users: '用户管理', notices: '公告管理' }[tab.value]))

function notify(text) {
  message.value = text
  window.setTimeout(() => { message.value = '' }, 2500)
}

async function login() {
  try {
    const response = await api.post('/admin/auth/login', { username: username.value, password: password.value })
    if (!response.data.success) throw new Error(response.data.message)
    localStorage.setItem('admin_token', response.data.data.token)
    loggedIn.value = true
    await loadAll()
    notify('管理员登录成功')
  } catch (error) { notify(error.response?.data?.message || error.message || '登录失败') }
}

function logout() {
  localStorage.removeItem('admin_token')
  loggedIn.value = false
}

async function loadCategories() { categories.value = (await api.get('/admin/categories')).data.data }
async function loadFoods() { foods.value = (await api.get('/admin/foods')).data.data }
async function loadOrders() { orders.value = (await api.get('/admin/orders', { params: { status: statusFilter.value || undefined } })).data.data }
async function loadFeedbacks() { feedbacks.value = (await api.get('/admin/feedback')).data.data }
async function loadStats() { stats.value = (await api.get('/admin/stats/summary')).data.data }
async function loadLogs() { logs.value = (await api.get('/admin/logs?limit=100')).data.data }
async function loadUsers() { users.value = (await api.get('/admin/users', { params: { keyword: userKeyword.value || undefined, status: userStatusFilter.value === '' ? undefined : Number(userStatusFilter.value) } })).data.data }
async function loadNotices() { notices.value = (await api.get('/admin/notices')).data.data }
async function loadComments() { comments.value = (await api.get('/admin/comments')).data.data }
async function loadAll() { await Promise.all([loadCategories(), loadFoods(), loadOrders(), loadFeedbacks(), loadComments(), loadStats(), loadLogs(), loadUsers(), loadNotices()]) }

async function addCategory() {
  if (!categoryName.value.trim()) return notify('请输入分类名称')
  try {
    await api.post('/admin/categories', { name: categoryName.value, sortOrder: categories.value.length + 1, status: 1 })
    categoryName.value = ''
    await loadCategories()
    notify('分类已添加')
  } catch (error) { notify(error.response?.data?.message || '添加分类失败') }
}

async function addFood() {
  if (!foodForm.value.categoryId || !foodForm.value.name || !foodForm.value.price) return notify('请填写分类、名称和价格')
  try {
    const body = { ...foodForm.value, categoryId: Number(foodForm.value.categoryId), price: Number(foodForm.value.price), originalPrice: foodForm.value.originalPrice ? Number(foodForm.value.originalPrice) : null, stock: Number(foodForm.value.stock), recommended: Number(foodForm.value.recommended), specialOffer: Number(foodForm.value.specialOffer), status: Number(foodForm.value.status) }
    if (editingFoodId.value) await api.put(`/admin/foods/${editingFoodId.value}`, body)
    else await api.post('/admin/foods', body)
    const wasEditing = Boolean(editingFoodId.value)
    resetFoodForm()
    await loadFoods()
    notify(wasEditing ? '菜品已更新' : '菜品已添加')
  } catch (error) { notify(error.response?.data?.message || '菜品保存失败') }
}

function editFood(food) {
  editingFoodId.value = food.id
  foodForm.value = { categoryId: food.categoryId, name: food.name, description: food.description || '', price: food.price, originalPrice: food.originalPrice || '', stock: food.stock, recommended: food.recommended || 0, specialOffer: food.specialOffer || 0, status: food.status }
  tab.value = 'foods'
}

function resetFoodForm() {
  editingFoodId.value = null
  foodForm.value = { categoryId: '', name: '', description: '', price: '', originalPrice: '', stock: 0, recommended: 0, specialOffer: 0, status: 1 }
}

async function setStock(food) {
  const stock = window.prompt(`调整“${food.name}”库存`, food.stock)
  if (stock === null || !/^\d+$/.test(stock)) return
  try { await api.put(`/admin/foods/${food.id}/stock?stock=${Number(stock)}`); await loadFoods(); notify('库存已更新') } catch (error) { notify(error.response?.data?.message || '库存更新失败') }
}

async function toggleFood(food) {
  try { await api.put(`/admin/foods/${food.id}/status?status=${food.status ? 0 : 1}`); await loadFoods(); notify('菜品状态已更新') } catch (error) { notify(error.response?.data?.message || '状态更新失败') }
}

async function editCategory(category) {
  const name = window.prompt('修改分类名称', category.name)
  if (!name?.trim()) return
  try {
    await api.put(`/admin/categories/${category.id}`, { name: name.trim(), sortOrder: category.sortOrder, status: category.status })
    await loadCategories()
    notify('分类已更新')
  } catch (error) { notify(error.response?.data?.message || '分类更新失败') }
}

async function toggleCategory(category) {
  try {
    await api.put(`/admin/categories/${category.id}`, { name: category.name, sortOrder: category.sortOrder, status: category.status ? 0 : 1 })
    await loadCategories()
    notify('分类状态已更新')
  } catch (error) { notify(error.response?.data?.message || '分类状态更新失败') }
}

async function updateOrder(order, status) {
  try { await api.put(`/admin/orders/${order.orderNo}/status?status=${status}`); await loadOrders(); notify('订单状态已更新') } catch (error) { notify(error.response?.data?.message || '订单更新失败') }
}

function orderStatusOptions(order) {
  const next = { PENDING_PAYMENT: ['CANCELLED'], PAID: ['PREPARING', 'CANCELLED'], PREPARING: ['READY', 'CANCELLED'], READY: ['COMPLETED', 'CANCELLED'], COMPLETED: [], CANCELLED: [] }
  return [order.status, ...(next[order.status] || [])]
}

async function replyFeedback(feedback) {
  const reply = window.prompt('填写回复内容', feedback.reply || '')
  if (!reply?.trim()) return
  try { await api.put(`/admin/feedback/${feedback.id}/reply`, { reply }); await loadFeedbacks(); notify('反馈已回复') } catch (error) { notify(error.response?.data?.message || '回复失败') }
}

async function toggleUser(user) {
  try {
    await api.put(`/admin/users/${user.id}/status?status=${user.status ? 0 : 1}`)
    await loadUsers()
    notify('用户状态已更新')
  } catch (error) { notify(error.response?.data?.message || '用户状态更新失败') }
}

function editNotice(notice) {
  noticeForm.value = { id: notice.id, title: notice.title, content: notice.content, status: notice.status }
  tab.value = 'notices'
}

function resetNoticeForm() {
  noticeForm.value = { id: null, title: '', content: '', status: 1 }
}

async function saveNotice() {
  if (!noticeForm.value.title.trim() || !noticeForm.value.content.trim()) return notify('请填写公告标题和内容')
  try {
    const body = { title: noticeForm.value.title, content: noticeForm.value.content, status: Number(noticeForm.value.status) }
    if (noticeForm.value.id) await api.put(`/admin/notices/${noticeForm.value.id}`, body)
    else await api.post('/admin/notices', body)
    await loadNotices()
    notify(noticeForm.value.id ? '公告已更新' : '公告已发布')
    resetNoticeForm()
  } catch (error) { notify(error.response?.data?.message || '公告保存失败') }
}

async function toggleNotice(notice) {
  try {
    await api.put(`/admin/notices/${notice.id}/status?status=${notice.status ? 0 : 1}`)
    await loadNotices()
    notify('公告状态已更新')
  } catch (error) { notify(error.response?.data?.message || '公告状态更新失败') }
}

onMounted(() => { if (loggedIn.value) loadAll() })
</script>

<template>
  <div v-if="!loggedIn" class="admin-login">
    <div class="login-box">
      <p class="eyebrow">STORE CONSOLE</p>
      <h1>商家管理后台</h1>
      <p class="muted">管理你的菜单、库存和订单。</p>
      <input v-model="username" placeholder="管理员账号" />
      <input v-model="password" type="password" placeholder="管理员密码" @keyup.enter="login" />
      <button class="primary full" @click="login">登录后台</button>
      <a href="/" class="back-link">返回用户端</a>
    </div>
  </div>

  <div v-else class="admin-shell">
    <aside class="admin-sidebar">
      <div class="brand">小店后台</div>
      <p class="sidebar-caption">商家控制台</p>
      <button :class="{ selected: tab === 'foods' }" @click="tab = 'foods'">菜品管理</button>
      <button :class="{ selected: tab === 'dashboard' }" @click="tab = 'dashboard'">经营概览</button>
      <button :class="{ selected: tab === 'categories' }" @click="tab = 'categories'">分类管理</button>
      <button :class="{ selected: tab === 'orders' }" @click="tab = 'orders'">订单处理</button>
      <button :class="{ selected: tab === 'feedback' }" @click="tab = 'feedback'">反馈处理</button>
      <button :class="{ selected: tab === 'comments' }" @click="tab = 'comments'">评价管理</button>
      <button :class="{ selected: tab === 'logs' }" @click="tab = 'logs'">操作日志</button>
      <button :class="{ selected: tab === 'users' }" @click="tab = 'users'">用户管理</button>
      <button :class="{ selected: tab === 'notices' }" @click="tab = 'notices'">公告管理</button>
      <div class="sidebar-bottom"><a href="/">用户端</a><button @click="logout">退出登录</button></div>
    </aside>

    <main class="admin-main">
      <div class="admin-header"><div><p class="eyebrow">MERCHANT CONSOLE</p><h1>{{ pageTitle }}</h1></div><span v-if="message" class="admin-notice">{{ message }}</span></div>

      <section v-if="tab === 'dashboard'" class="admin-content">
        <div class="stat-grid"><div class="stat-card"><span>累计营业额</span><strong>¥{{ stats.totalRevenue }}</strong><small>已支付及已完成订单</small></div><div class="stat-card"><span>今日营业额</span><strong>¥{{ stats.todayRevenue }}</strong><small>今日订单收入</small></div><div class="stat-card"><span>订单总量</span><strong>{{ stats.orderCount }}</strong><small>待处理 {{ stats.pendingOrderCount }} 笔</small></div><div class="stat-card"><span>注册用户</span><strong>{{ stats.userCount }}</strong><small>上架菜品 {{ stats.foodCount }} 道</small></div></div>
        <div class="admin-card"><div class="section-title"><h2>热销菜品</h2><span>按销售数量排序</span></div><div class="top-food-list"><div v-for="(food, index) in stats.topFoods" :key="food.foodId" class="top-food"><b>{{ index + 1 }}</b><span>{{ food.foodName }}</span><strong>{{ food.quantity }} 份</strong></div><div v-if="!stats.topFoods?.length" class="empty">暂无销售数据</div></div></div>
      </section>

      <section v-if="tab === 'foods'" class="admin-content">
        <div class="admin-card">
          <div class="section-title"><h2>{{ editingFoodId ? '编辑菜品' : '添加菜品' }}</h2><button v-if="editingFoodId" class="link-button" @click="resetFoodForm">取消编辑</button></div>
          <div class="form-grid">
            <select v-model="foodForm.categoryId"><option value="">选择分类</option><option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option></select>
            <input v-model="foodForm.name" placeholder="菜品名称" />
            <input v-model="foodForm.price" type="number" step="0.01" placeholder="售价" />
            <input v-model="foodForm.originalPrice" type="number" step="0.01" placeholder="原价（可选）" />
            <input v-model="foodForm.stock" type="number" min="0" placeholder="库存" />
            <input v-model="foodForm.description" class="wide" placeholder="菜品描述" />
            <label class="check-field"><input v-model="foodForm.recommended" type="checkbox" :true-value="1" :false-value="0" /> 推荐菜品</label>
            <label class="check-field"><input v-model="foodForm.specialOffer" type="checkbox" :true-value="1" :false-value="0" /> 特价菜品</label>
          </div>
          <button class="primary" @click="addFood">{{ editingFoodId ? '保存菜品' : '添加菜品' }}</button>
        </div>
        <div class="admin-card"><div class="section-title"><h2>菜品列表</h2><span>{{ foods.length }} 道</span></div><div class="table-wrap"><table><thead><tr><th>菜品</th><th>分类</th><th>售价</th><th>库存</th><th>销量</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="food in foods" :key="food.id"><td><strong>{{ food.name }}</strong><small>{{ food.description }}</small></td><td>{{ food.categoryName }}</td><td>¥{{ food.price }}</td><td>{{ food.stock }}</td><td>{{ food.sales }}</td><td><span :class="['status', food.status ? 'on' : 'off']">{{ food.status ? '上架' : '下架' }}</span></td><td><button class="table-action" @click="editFood(food)">编辑</button><button class="table-action" @click="setStock(food)">调库存</button><button class="table-action" @click="toggleFood(food)">{{ food.status ? '下架' : '上架' }}</button></td></tr></tbody></table></div></div>
      </section>

      <section v-if="tab === 'categories'" class="admin-content">
        <div class="admin-card compact-form"><div class="section-title"><h2>添加分类</h2></div><div class="inline-form"><input v-model="categoryName" placeholder="例如：套餐" @keyup.enter="addCategory" /><button class="primary" @click="addCategory">添加</button></div></div>
        <div class="admin-card"><div class="section-title"><h2>分类列表</h2><span>{{ categories.length }} 个</span></div><div class="category-list"><div v-for="category in categories" :key="category.id" class="category-row"><strong>{{ category.name }}</strong><span>排序 {{ category.sortOrder }}</span><span :class="['status', category.status ? 'on' : 'off']">{{ category.status ? '启用' : '停用' }}</span><div class="category-actions"><button class="table-action" @click="editCategory(category)">编辑</button><button class="table-action" @click="toggleCategory(category)">{{ category.status ? '停用' : '启用' }}</button></div></div></div></div>
      </section>

      <section v-if="tab === 'orders'" class="admin-content"><div class="admin-card"><div class="section-title"><h2>订单列表</h2><select v-model="statusFilter" @change="loadOrders"><option value="">全部状态</option><option v-for="status in statusOptions" :key="status" :value="status">{{ statusLabels[status] }}</option></select></div><div class="order-table"><div v-for="order in orders" :key="order.orderNo" class="admin-order"><div><strong>{{ order.orderNo }}</strong><p>{{ order.items?.map(item => `${item.foodNameSnapshot} × ${item.quantity}`).join('、') }}</p><small>{{ order.addressSnapshot }}</small></div><div class="order-amount">¥{{ order.totalAmount }}</div><select :value="order.status" @change="updateOrder(order, $event.target.value)"><option v-for="status in orderStatusOptions(order)" :key="status" :value="status">{{ statusLabels[status] }}</option></select></div><div v-if="!orders.length" class="empty">暂无订单</div></div></div></section>
      <section v-if="tab === 'feedback'" class="admin-content"><div class="admin-card"><div class="section-title"><h2>用户反馈</h2><span>{{ feedbacks.length }} 条</span></div><div class="feedback-list"><div v-for="feedback in feedbacks" :key="feedback.id" class="admin-feedback"><div><strong>{{ feedback.username }}</strong><p>{{ feedback.content }}</p><small>{{ feedback.reply || '尚未回复' }}</small></div><button class="table-action" @click="replyFeedback(feedback)">{{ feedback.reply ? '修改回复' : '回复' }}</button></div><div v-if="!feedbacks.length" class="empty">暂无反馈</div></div></div></section>
      <section v-if="tab === 'comments'" class="admin-content"><div class="admin-card"><div class="section-title"><h2>用户评价</h2><span>{{ comments.length }} 条</span></div><div class="comment-admin-list"><div v-for="comment in comments" :key="comment.id" class="comment-admin-row"><div><strong>{{ comment.foodName }}</strong><p>{{ comment.content }}</p><small>{{ comment.nickname || comment.username }} · {{ comment.createdAt }}</small></div><div class="rating">{{ '★'.repeat(comment.rating) }}<span>{{ '☆'.repeat(5 - comment.rating) }}</span></div></div><div v-if="!comments.length" class="empty">暂无评价</div></div></div></section>
      <section v-if="tab === 'logs'" class="admin-content"><div class="admin-card"><div class="section-title"><h2>最近操作</h2><span>{{ logs.length }} 条</span></div><div class="log-list"><div v-for="log in logs" :key="log.id" class="log-row"><div><strong>{{ log.action }}</strong><p>{{ log.detail || '无补充说明' }}</p></div><div><small>{{ log.adminUsername || '系统' }}</small><small>{{ log.createdAt }}</small></div></div><div v-if="!logs.length" class="empty">暂无操作日志</div></div></div></section>
      <section v-if="tab === 'users'" class="admin-content">
        <div class="admin-card">
          <div class="section-title"><h2>用户列表</h2><span>{{ users.length }} 人</span></div>
          <div class="filter-form">
            <input v-model="userKeyword" placeholder="搜索用户名、昵称或手机" @keyup.enter="loadUsers" />
            <select v-model="userStatusFilter" @change="loadUsers"><option value="">全部状态</option><option value="1">正常</option><option value="0">已停用</option></select>
            <button class="primary" @click="loadUsers">查询</button>
          </div>
          <div class="table-wrap"><table><thead><tr><th>用户名</th><th>昵称</th><th>手机号</th><th>注册时间</th><th>状态</th><th>操作</th></tr></thead><tbody>
            <tr v-for="user in users" :key="user.id"><td>{{ user.username }}</td><td>{{ user.nickname || '-' }}</td><td>{{ user.phone || '-' }}</td><td>{{ user.createdAt }}</td><td><span :class="['status', user.status ? 'on' : 'off']">{{ user.status ? '正常' : '停用' }}</span></td><td><button class="table-action" @click="toggleUser(user)">{{ user.status ? '停用' : '启用' }}</button></td></tr>
          </tbody></table><div v-if="!users.length" class="empty">暂无用户</div></div>
        </div>
      </section>
      <section v-if="tab === 'notices'" class="admin-content">
        <div class="admin-card">
          <div class="section-title"><h2>{{ noticeForm.id ? '编辑公告' : '发布公告' }}</h2><button v-if="noticeForm.id" class="link-button" @click="resetNoticeForm">取消编辑</button></div>
          <div class="notice-form"><input v-model="noticeForm.title" placeholder="公告标题" /><textarea v-model="noticeForm.content" rows="5" placeholder="公告内容"></textarea><label><input v-model="noticeForm.status" type="checkbox" :true-value="1" :false-value="0" /> 发布后立即展示</label><button class="primary" @click="saveNotice">{{ noticeForm.id ? '保存修改' : '发布公告' }}</button></div>
        </div>
        <div class="admin-card"><div class="section-title"><h2>公告列表</h2><span>{{ notices.length }} 条</span></div><div class="notice-admin-list">
          <div v-for="notice in notices" :key="notice.id" class="notice-admin-row"><div><strong>{{ notice.title }}</strong><p>{{ notice.content }}</p><small>{{ notice.createdAt }}</small></div><div class="notice-actions"><span :class="['status', notice.status ? 'on' : 'off']">{{ notice.status ? '展示中' : '已下线' }}</span><button class="table-action" @click="editNotice(notice)">编辑</button><button class="table-action" @click="toggleNotice(notice)">{{ notice.status ? '下线' : '上线' }}</button></div></div><div v-if="!notices.length" class="empty">暂无公告</div>
        </div></div>
      </section>
    </main>
  </div>
</template>
