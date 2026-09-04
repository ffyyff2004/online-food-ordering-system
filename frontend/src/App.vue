<script setup>
import axios from 'axios'
import { computed, onMounted, ref } from 'vue'

const api = axios.create({ baseURL: 'http://localhost:8080/api' })
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('food_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

const categories = ref([])
const foods = ref([])
const cart = ref([])
const orders = ref([])
const keyword = ref('')
const selectedCategory = ref(null)
const foodPage = ref(1)
const foodPageSize = 6
const foodTotal = ref(0)
const loading = ref(false)
const message = ref('')
const showLogin = ref(true)
const username = ref('')
const password = ref('')
const nickname = ref('')
const address = ref('')
const authState = ref(Boolean(localStorage.getItem('food_token')))
const profile = ref({ nickname: '', phone: '', avatar: '' })
const addresses = ref([])
const favorites = ref([])
const feedbacks = ref([])
const notices = ref([])
const selectedFood = ref(null)
const foodComments = ref([])
const detailLoading = ref(false)
const profileAddress = ref({ receiver: '', phone: '', detail: '', isDefault: 1 })
const feedbackContent = ref('')

const loggedIn = computed(() => authState.value)
const cartCount = computed(() => cart.value.reduce((sum, item) => sum + item.quantity, 0))
const cartTotal = computed(() => cart.value.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0).toFixed(2))
const orderStatusLabels = { PENDING_PAYMENT: '待支付', PAID: '已支付', PREPARING: '制作中', READY: '待取餐', COMPLETED: '已完成', CANCELLED: '已取消' }
const orderStatusSteps = ['PENDING_PAYMENT', 'PAID', 'PREPARING', 'READY', 'COMPLETED']
const averageRating = computed(() => {
  if (!foodComments.value.length) return '暂无评分'
  return (foodComments.value.reduce((sum, item) => sum + Number(item.rating), 0) / foodComments.value.length).toFixed(1)
})
const foodTotalPages = computed(() => Math.max(1, Math.ceil(foodTotal.value / foodPageSize)))

function showMessage(text) {
  message.value = text
  window.setTimeout(() => { message.value = '' }, 2500)
}

async function loadFoods() {
  const response = await api.get('/foods', {
    params: { page: foodPage.value, size: foodPageSize, categoryId: selectedCategory.value || undefined, keyword: keyword.value || undefined }
  })
  foods.value = response.data.data.records
  foodTotal.value = response.data.data.total
}

function searchFoods() {
  foodPage.value = 1
  loadFoods()
}

function changeFoodPage(page) {
  if (page < 1 || page > foodTotalPages.value || page === foodPage.value) return
  foodPage.value = page
  loadFoods()
}

async function loadCategories() {
  const response = await api.get('/foods/categories')
  categories.value = response.data.data
}

async function loadNotices() {
  const response = await api.get('/notices')
  notices.value = response.data.data
}

async function openFoodDetail(foodId) {
  detailLoading.value = true
  selectedFood.value = null
  foodComments.value = []
  try {
    const [foodResponse, commentResponse] = await Promise.all([
      api.get(`/foods/${foodId}`),
      api.get(`/comments/food/${foodId}`)
    ])
    selectedFood.value = foodResponse.data.data
    foodComments.value = commentResponse.data.data
  } catch (error) {
    showMessage(error.response?.data?.message || '菜品详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

function closeFoodDetail() {
  selectedFood.value = null
  foodComments.value = []
}

async function loadCartAndOrders() {
  if (!loggedIn.value) return
  const [cartResponse, orderResponse] = await Promise.all([api.get('/cart'), api.get('/orders')])
  cart.value = cartResponse.data.data
  orders.value = orderResponse.data.data
}

async function loadPersonal() {
  if (!loggedIn.value) return
  const [profileResponse, addressResponse, favoriteResponse, feedbackResponse] = await Promise.all([
    api.get('/profile'), api.get('/profile/addresses'), api.get('/favorites'), api.get('/feedback')
  ])
  profile.value = {
    nickname: profileResponse.data.data.nickname || '',
    phone: profileResponse.data.data.phone || '',
    avatar: profileResponse.data.data.avatar || ''
  }
  addresses.value = addressResponse.data.data
  const defaultAddress = addresses.value.find((item) => item.isDefault === 1)
  if (defaultAddress && !address.value) address.value = `${defaultAddress.receiver} ${defaultAddress.phone} ${defaultAddress.detail}`
  favorites.value = favoriteResponse.data.data
  feedbacks.value = feedbackResponse.data.data
}

async function submitAuth() {
  try {
    const path = showLogin.value ? '/auth/login' : '/auth/register'
    const body = showLogin.value
      ? { username: username.value, password: password.value }
      : { username: username.value, password: password.value, nickname: nickname.value }
    const response = await api.post(path, body)
    if (!response.data.success) throw new Error(response.data.message)
    if (showLogin.value) {
      localStorage.setItem('food_token', response.data.data.token)
      authState.value = true
      showMessage('登录成功')
      await Promise.all([loadCartAndOrders(), loadPersonal()])
    } else {
      showLogin.value = true
      showMessage('注册成功，请登录')
    }
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '操作失败')
  }
}

async function addToCart(foodId) {
  if (!loggedIn.value) {
    showMessage('请先登录')
    return
  }
  try {
    const food = foods.value.find((item) => item.id === foodId) || (selectedFood.value?.id === foodId ? selectedFood.value : null)
    if (food && food.stock < 1) {
      showMessage('该菜品暂时售罄')
      return
    }
    const response = await api.post('/cart/items', { foodId, quantity: 1 })
    if (!response.data.success) throw new Error(response.data.message)
    await loadCartAndOrders()
    showMessage('已加入购物车')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '加入失败')
  }
}

function orderStepClass(order, step) {
  if (order.status === 'CANCELLED') return 'cancelled'
  const currentIndex = orderStatusSteps.indexOf(order.status)
  const stepIndex = orderStatusSteps.indexOf(step)
  if (stepIndex < currentIndex) return 'done'
  if (stepIndex === currentIndex) return 'current'
  return ''
}

function isFavorite(foodId) {
  return favorites.value.some((item) => item.foodId === foodId)
}

async function toggleFavorite(foodId) {
  if (!loggedIn.value) return showMessage('请先登录')
  try {
    const wasFavorite = isFavorite(foodId)
    if (wasFavorite) await api.delete(`/favorites/${foodId}`)
    else await api.post(`/favorites/${foodId}`)
    await loadPersonal()
    showMessage(wasFavorite ? '已取消收藏' : '收藏成功')
  } catch (error) {
    showMessage(error.response?.data?.message || '收藏操作失败')
  }
}

async function updateProfile() {
  try {
    const response = await api.put('/profile', profile.value)
    if (!response.data.success) throw new Error(response.data.message)
    showMessage('资料已更新')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '资料更新失败')
  }
}

async function addAddress() {
  try {
    const response = await api.post('/profile/addresses', profileAddress.value)
    if (!response.data.success) throw new Error(response.data.message)
    profileAddress.value = { receiver: '', phone: '', detail: '', isDefault: 0 }
    await loadPersonal()
    showMessage('地址已添加')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '地址添加失败')
  }
}

async function removeAddress(id) {
  try {
    await api.delete(`/profile/addresses/${id}`)
    await loadPersonal()
    showMessage('地址已删除')
  } catch (error) {
    showMessage(error.response?.data?.message || '地址删除失败')
  }
}

async function submitFeedback() {
  if (!feedbackContent.value.trim()) return showMessage('请填写反馈内容')
  try {
    const response = await api.post('/feedback', { content: feedbackContent.value })
    if (!response.data.success) throw new Error(response.data.message)
    feedbackContent.value = ''
    await loadPersonal()
    showMessage('反馈已提交')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '反馈提交失败')
  }
}

async function updateCart(item, quantity) {
  if (quantity < 1) return removeFromCart(item.foodId)
  try {
    const response = await api.put(`/cart/items/${item.foodId}`, { quantity })
    if (!response.data.success) throw new Error(response.data.message)
    await loadCartAndOrders()
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '更新失败')
  }
}

async function removeFromCart(foodId) {
  await api.delete(`/cart/items/${foodId}`)
  await loadCartAndOrders()
}

async function createOrder() {
  if (!address.value.trim()) {
    showMessage('请填写收货地址')
    return
  }
  try {
    const response = await api.post('/orders', { addressSnapshot: address.value })
    if (!response.data.success) throw new Error(response.data.message)
    address.value = ''
    await loadCartAndOrders()
    showMessage(`订单 ${response.data.data.orderNo} 创建成功`)
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '下单失败')
  }
}

async function pay(orderNo) {
  try {
    const response = await api.post(`/orders/${orderNo}/pay`)
    if (!response.data.success) throw new Error(response.data.message)
    await loadCartAndOrders()
    showMessage('模拟支付成功')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '支付失败')
  }
}

async function cancelOrder(orderNo) {
  if (!window.confirm('确定取消这个订单吗？取消后库存会恢复。')) return
  try {
    const response = await api.post(`/orders/${orderNo}/cancel`)
    if (!response.data.success) throw new Error(response.data.message)
    await loadCartAndOrders()
    showMessage('订单已取消，库存已恢复')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '取消订单失败')
  }
}

async function reviewItem(order, item) {
  const ratingText = window.prompt(`请为“${item.foodNameSnapshot}”评分（1-5）`, '5')
  if (ratingText === null) return
  const rating = Number(ratingText)
  if (!Number.isInteger(rating) || rating < 1 || rating > 5) {
    showMessage('评分必须是1到5之间的整数')
    return
  }
  const content = window.prompt('请输入评价内容', '味道不错，下次还会再来')
  if (!content?.trim()) return
  try {
    const response = await api.post('/comments', { foodId: item.foodId, orderId: order.id, rating, content: content.trim() })
    if (!response.data.success) throw new Error(response.data.message)
    showMessage('评价成功，感谢你的反馈')
  } catch (error) {
    showMessage(error.response?.data?.message || error.message || '评价失败')
  }
}

function logout() {
  localStorage.removeItem('food_token')
  authState.value = false
  cart.value = []
  orders.value = []
  profile.value = { nickname: '', phone: '', avatar: '' }
  addresses.value = []
  favorites.value = []
  feedbacks.value = []
  showMessage('已退出登录')
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadCategories(), loadFoods(), loadNotices(), loadCartAndOrders(), loadPersonal()])
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <header class="topbar">
    <div class="brand">小店点餐</div>
    <div class="top-actions">
      <span v-if="loggedIn" class="welcome">欢迎回来</span>
      <button v-if="loggedIn" class="ghost" @click="logout">退出</button>
      <button v-else class="ghost" @click="showLogin = true">登录</button>
    </div>
  </header>

  <main class="page">
    <section v-if="notices.length" class="notice-card">
      <div class="section-title"><h2>店铺公告</h2><span>{{ notices.length }} 条</span></div>
      <div v-for="notice in notices" :key="notice.id" class="notice-item"><strong>{{ notice.title }}</strong><p>{{ notice.content }}</p></div>
    </section>
    <section class="hero">
      <div>
        <p class="eyebrow">LOCAL FOOD · SIMPLE ORDER</p>
        <h1>今天想吃点什么？</h1>
        <p class="muted">附近小店现做现送，选好菜品后即可提交订单。</p>
      </div>
      <div class="hero-badge">{{ cartCount }} 件商品在购物车</div>
    </section>

    <div v-if="message" class="toast">{{ message }}</div>

    <div v-if="detailLoading || selectedFood" class="modal-backdrop" @click.self="closeFoodDetail">
      <section class="food-detail-modal">
        <button class="modal-close" @click="closeFoodDetail">×</button>
        <div v-if="detailLoading" class="empty">正在加载菜品详情…</div>
        <template v-else>
          <div class="detail-heading"><div class="food-image detail-image">{{ selectedFood.name.slice(0, 1) }}</div><div><p class="eyebrow">FOOD DETAIL</p><h2>{{ selectedFood.name }}</h2><p class="muted">{{ selectedFood.categoryName }}</p></div></div>
          <div class="detail-tags"><span v-if="selectedFood.recommended" class="tag">推荐</span><span v-if="selectedFood.specialOffer" class="tag">特价</span><span class="stock-label">库存 {{ selectedFood.stock }}</span><span class="rating-label">★ {{ averageRating }}（{{ foodComments.length }} 条评价）</span></div>
          <p class="detail-description">{{ selectedFood.description || '暂无菜品描述' }}</p>
          <div class="detail-price"><strong>¥{{ selectedFood.price }}</strong><del v-if="selectedFood.originalPrice">¥{{ selectedFood.originalPrice }}</del><button class="primary" :disabled="selectedFood.stock < 1" @click="addToCart(selectedFood.id)">{{ selectedFood.stock < 1 ? '暂时售罄' : '加入购物车' }}</button></div>
          <div class="detail-comments"><div class="section-title"><h3>用户评价</h3><span>{{ foodComments.length }} 条</span></div><div v-for="comment in foodComments" :key="comment.id" class="detail-comment"><div class="comment-meta"><strong>{{ comment.nickname || comment.username }}</strong><span class="rating">{{ '★'.repeat(comment.rating) }}<i>{{ '☆'.repeat(5 - comment.rating) }}</i></span></div><p>{{ comment.content }}</p><small>{{ comment.createdAt }}</small></div><div v-if="!foodComments.length" class="empty">暂时还没有评价</div></div>
        </template>
      </section>
    </div>

    <section class="toolbar">
      <div class="categories">
        <button :class="{ active: !selectedCategory }" @click="selectedCategory = null; foodPage = 1; loadFoods()">全部</button>
        <button v-for="category in categories" :key="category.id" :class="{ active: selectedCategory === category.id }" @click="selectedCategory = category.id; foodPage = 1; loadFoods()">
          {{ category.name }}
        </button>
      </div>
      <input v-model="keyword" class="search" placeholder="搜索菜品" @keyup.enter="searchFoods" />
    </section>

    <div class="layout">
      <section class="food-panel">
        <div class="section-title"><h2>今日菜单</h2><span>{{ foods.length }} 道菜品</span></div>
        <div v-if="loading" class="empty">正在加载菜单…</div>
        <div v-else class="food-grid">
          <article v-for="food in foods" :key="food.id" class="food-card" @click="openFoodDetail(food.id)">
            <div class="food-image">{{ food.name.slice(0, 1) }}</div>
            <div class="food-info">
              <div class="food-name-row"><h3>{{ food.name }}</h3><span v-if="food.recommended" class="tag">推荐</span><span v-if="food.specialOffer" class="tag">特价</span></div>
              <p class="muted description">{{ food.description }}</p>
              <div class="food-bottom"><div><strong>¥{{ food.price }}</strong><del v-if="food.originalPrice" class="original-price">¥{{ food.originalPrice }}</del></div><div class="food-actions"><button class="favorite-button" :class="{ liked: isFavorite(food.id) }" @click.stop="toggleFavorite(food.id)">♥</button><button @click.stop="addToCart(food.id)">{{ food.stock < 1 ? '售罄' : '加入' }}</button></div></div>
            </div>
          </article>
        </div>
        <div v-if="foodTotal > foodPageSize" class="pagination"><button class="page-button" :disabled="foodPage === 1" @click="changeFoodPage(foodPage - 1)">上一页</button><span>第 {{ foodPage }} / {{ foodTotalPages }} 页</span><button class="page-button" :disabled="foodPage === foodTotalPages" @click="changeFoodPage(foodPage + 1)">下一页</button></div>
      </section>

      <aside class="side-panel">
        <div v-if="!loggedIn" class="auth-card">
          <div class="section-title"><h2>{{ showLogin ? '登录点餐' : '注册账号' }}</h2></div>
          <input v-model="username" placeholder="用户名" />
          <input v-model="password" type="password" placeholder="密码（至少6位）" />
          <input v-if="!showLogin" v-model="nickname" placeholder="昵称（可选）" />
          <button class="primary full" @click="submitAuth">{{ showLogin ? '登录' : '注册' }}</button>
          <button class="link-button" @click="showLogin = !showLogin">{{ showLogin ? '还没有账号？去注册' : '已有账号？去登录' }}</button>
        </div>

        <div v-else class="cart-card">
          <div class="section-title"><h2>购物车</h2><span>{{ cartCount }} 件</span></div>
          <div v-if="!cart.length" class="empty">购物车还是空的</div>
          <div v-for="item in cart" :key="item.foodId" class="cart-item">
            <div><strong>{{ item.foodName }}</strong><p>¥{{ item.price }}</p></div>
            <div class="quantity"><button @click="updateCart(item, item.quantity - 1)">−</button><span>{{ item.quantity }}</span><button @click="updateCart(item, item.quantity + 1)">＋</button></div>
          </div>
          <div v-if="cart.length" class="cart-total"><span>合计</span><strong>¥{{ cartTotal }}</strong></div>
          <input v-if="cart.length" v-model="address" placeholder="收货地址" />
          <button v-if="cart.length" class="primary full" @click="createOrder">提交订单</button>
        </div>

        <div v-if="loggedIn" class="orders-card">
          <div class="section-title"><h2>我的订单</h2><span>{{ orders.length }} 笔</span></div>
          <div v-if="!orders.length" class="empty">暂时没有订单</div>
          <div v-for="order in orders" :key="order.orderNo" class="order-item">
            <div class="order-head"><strong>{{ order.orderNo.slice(-8) }}</strong><span>{{ orderStatusLabels[order.status] || order.status }}</span></div>
            <p>{{ order.items?.map(item => `${item.foodNameSnapshot} × ${item.quantity}`).join('、') }}</p>
            <div v-if="order.status !== 'CANCELLED'" class="order-progress"><span v-for="step in orderStatusSteps" :key="step" :class="['progress-step', orderStepClass(order, step)]">{{ orderStatusLabels[step] }}</span></div><p v-else class="cancelled-text">订单已取消，相关库存已恢复</p>
            <div class="order-foot"><strong>¥{{ order.totalAmount }}</strong><div class="order-actions"><button v-if="order.status === 'PENDING_PAYMENT'" class="pay" @click="pay(order.orderNo)">模拟支付</button><button v-if="['PENDING_PAYMENT', 'PAID', 'PREPARING', 'READY'].includes(order.status)" class="cancel-button" @click="cancelOrder(order.orderNo)">取消订单</button><template v-for="item in order.items" :key="item.foodId"><button v-if="order.status === 'COMPLETED'" class="review-button" @click="reviewItem(order, item)">评价{{ item.foodNameSnapshot }}</button></template></div></div>
          </div>
        </div>

        <div v-if="loggedIn" class="profile-card">
          <div class="section-title"><h2>个人中心</h2><span>{{ favorites.length }} 个收藏</span></div>
          <div class="profile-form">
            <input v-model="profile.nickname" placeholder="昵称" />
            <input v-model="profile.phone" placeholder="手机号" />
            <button class="primary full" @click="updateProfile">保存资料</button>
          </div>
          <div class="sub-title">我的地址</div>
          <div v-for="item in addresses" :key="item.id" class="address-item">
            <div><strong>{{ item.receiver }} · {{ item.phone }}</strong><p>{{ item.detail }}</p></div>
            <button class="text-button" @click="removeAddress(item.id)">删除</button>
          </div>
          <div class="address-form">
            <input v-model="profileAddress.receiver" placeholder="收货人" />
            <input v-model="profileAddress.phone" placeholder="联系电话" />
            <input v-model="profileAddress.detail" placeholder="详细地址" />
            <label><input v-model="profileAddress.isDefault" type="checkbox" :true-value="1" :false-value="0" /> 设为默认地址</label>
            <button class="secondary full" @click="addAddress">添加地址</button>
          </div>
          <div class="sub-title">意见反馈</div>
          <textarea v-model="feedbackContent" placeholder="告诉商家你的建议" rows="3"></textarea>
          <button class="secondary full" @click="submitFeedback">提交反馈</button>
          <div v-for="item in feedbacks.slice(0, 2)" :key="item.id" class="feedback-item"><p>{{ item.content }}</p><small>{{ item.reply || '等待商家回复' }}</small></div>
        </div>
      </aside>
    </div>
  </main>
</template>
