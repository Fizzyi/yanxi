<script setup>
import Footer from './components/Footer.vue'
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(false)
const userRole = ref('')

const updateAuthState = () => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('userRole')
  isLoggedIn.value = !!token
  userRole.value = role || ''
  console.log('Auth state updated:', { isLoggedIn: isLoggedIn.value, userRole: userRole.value })
}

onMounted(() => {
  updateAuthState()
  
  // Listen for storage changes (when user logs in from another tab)
  window.addEventListener('storage', updateAuthState)
})

// Watch for route changes to update auth state
watch(() => route.path, () => {
  updateAuthState()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('realName')
  isLoggedIn.value = false
  userRole.value = ''
  router.push('/login')
}
</script>

<template>
  <header>
    <nav>
      <div class="nav-container">
        <div class="logo">
          <img src="/Logo.jpg" alt="Logo" height="60">
        </div>
        <div class="nav-links">
          <router-link to="/">Home</router-link> |
          <router-link to="/about">About</router-link> |
          <router-link to="/courses">Courses</router-link>
          <template v-if="isLoggedIn">
            | <router-link to="/resources">Resources</router-link>
          </template>
          <template v-if="!isLoggedIn">
            <router-link class="nav-btn nav-login" to="/login">Log In</router-link>
            <router-link class="nav-btn nav-signup" to="/signup">Sign Up</router-link>
          </template>
          <template v-else>
            <router-link v-if="userRole && userRole.toUpperCase() === 'TEACHER'" class="nav-btn nav-teacher" to="/teacher">Teacher Zone</router-link>
            <router-link v-if="userRole && userRole.toUpperCase() === 'STUDENT'" class="nav-btn nav-student" to="/student">Student Zone</router-link>
            <button class="nav-btn nav-logout" @click="handleLogout">Logout</button>
          </template>
        </div>
      </div>
    </nav>
  </header>

  <div class="main-content">
    <router-view/>
  </div>
  <Footer />
</template>

<style>
#app {
  font-family: Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background-color: #ffffff;
  margin-top: 0;
  padding-top: 0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 120px; /* Account for fixed header */
}

header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  margin-top: 0;
  padding-top: 0;
  margin-bottom: 0;
}

nav {
  padding: 0 20px;
  margin-top: 0;
  margin-bottom: 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  height: 100px;
  width: auto;
  object-fit: contain;
}

.nav-links {
  display: flex;
  align-items: center;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  margin: 0 10px;
  font-size: 16px;
}

nav a.router-link-exact-active {
  color: #42b983;
}

.nav-btn {
  margin-left: 18px;
  padding: 7px 20px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  border: 1.5px solid #e5e7eb;
  background: #fff;
  transition: background 0.2s, color 0.2s, border 0.2s;
}
.nav-btn.nav-login:hover {
  background: #f3f4f6;
  color: #e53935;
  border-color: #e53935;
}
.nav-btn.nav-signup {
  background: #e53935;
  color: #fff;
  border: 1.5px solid #e53935;
  margin-left: 10px;
}
.nav-btn.nav-signup:hover {
  background: #b71c1c;
  border-color: #b71c1c;
}

.nav-btn.nav-logout {
  margin-left: 18px;
  padding: 7px 20px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  border: 1.5px solid #e5e7eb;
  background: #fff;
  transition: background 0.2s, color 0.2s, border 0.2s;
  cursor: pointer;
}

.nav-btn.nav-logout:hover {
  background: #f3f4f6;
  color: #e53935;
  border-color: #e53935;
}

.nav-btn.nav-teacher {
  margin-left: 18px;
  padding: 7px 20px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  border: 1.5px solid #4CAF50;
  background: #fff;
  color: #4CAF50;
  transition: background 0.2s, color 0.2s, border 0.2s;
  text-decoration: none;
}

.nav-btn.nav-teacher:hover {
  background: #4CAF50;
  color: #fff;
  border-color: #4CAF50;
}

/* 移除 .router-view 的 margin-top，避免影响 home 页 */
</style>
