<template>
  <div class="auth-container">
    <div class="auth-box">
      <h1 class="auth-title">Log In</h1>
      <div class="auth-subtitle">Welcome back! Please enter your details.</div>
      <form class="auth-form">
        <label class="auth-label">Email</label>
        <input class="auth-input" type="email" placeholder="Enter your email" />
        <div class="auth-label-row">
          <label class="auth-label">Password</label>
          <!-- <a class="auth-link" href="#">Forgot password?</a> -->
        </div>
        <input class="auth-input" type="password" placeholder="Enter your password" />
        <button class="auth-btn" type="submit">Log in</button>
      </form>
      <div class="auth-bottom">
        Don't have an account? <a class="auth-link" href="/signup">Sign up</a>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}
.auth-box {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.07);
  padding: 32px 20px 20px 20px;
  width: 380px;
  max-width: 95vw;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.auth-title {
  font-size: 1.6rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
}
.auth-subtitle {
  font-size: 1rem;
  margin-bottom: 18px;
  white-space: nowrap;
  width: max-content;
  max-width: 100%;
  overflow-x: auto;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}
.auth-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: stretch;
}
.auth-label {
  font-size: 0.95rem;
  text-align: left;
  display: block;
  font-weight: bold;
}
.auth-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0px;
}
.auth-label-row .auth-label {
  flex: 1;
  text-align: left;
  display: block;
}
.auth-input {
  width: 100%;
  padding: 8px 10px;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.98rem;
  margin-bottom: 0px;
  outline: none;
  transition: border 0.2s;
}
.auth-input:focus {
  border-color: #111;
}
.auth-btn {
  width: 100%;
  background: #111;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 0;
  font-size: 1rem;
  font-weight: bold;
  margin-top: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.auth-btn:hover {
  background: #333;
}
.auth-bottom {
  margin-top: 14px;
  color: #4b5563;
  font-size: 0.95rem;
  text-align: center;
}
.auth-link {
  color: #111;
  font-weight: bold;
  text-decoration: none;
  margin-left: 2px;
  font-size: 0.98rem;
}
.auth-link:hover {
  text-decoration: underline;
}
</style> 