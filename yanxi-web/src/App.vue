<script setup>
import Footer from './components/Footer.vue'
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
          <router-link to="/courses">Courses</router-link> |
          <router-link to="/resources">Resources</router-link>
        </div>
      </div>
    </nav>
  </header>

  <router-view/>
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

/* 移除 .router-view 的 margin-top，避免影响 home 页 */
</style>
