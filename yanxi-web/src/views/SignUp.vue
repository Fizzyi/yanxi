<template>
  <div class="auth-container">
    <div class="auth-box">
      <h1 class="auth-title">Sign Up</h1>
      <div class="auth-subtitle">Create a student account to upload documents.</div>
      <form class="auth-form">
        <label class="auth-label">Full Name</label>
        <input class="auth-input" type="text" placeholder="Enter your full name" />
        <label class="auth-label">Email</label>
        <input class="auth-input" type="email" placeholder="Enter your email" />
        <label class="auth-label">Password</label>
        <input class="auth-input" type="password" placeholder="Enter your password" />
        <div class="auth-tip">Password must be at least 6 characters long.</div>
        <label class="auth-label">Confirm Password</label>
        <input class="auth-input" type="password" placeholder="Confirm your password" />
        <button class="auth-btn" type="submit">Sign up</button>
      </form>
      <div class="auth-bottom">
        Already have an account? <a class="auth-link" href="/login">Log in</a>
        <div class="auth-note">
          Note: If you have made an account before and your credentials did not change, you will not recieve a verification email.
        </div>
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
  color: #4b5563;
  font-size: 1rem;
  text-align: center;
  margin-bottom: 18px;
}
.auth-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.auth-label {
  font-size: 0.95rem;
  text-align: left;
  display: block;
  font-weight: bold;
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
.auth-tip {
  color: #6b7280;
  font-size: 0.72rem;
  margin-bottom: 2px;
  margin-top: -10px;
  text-align: left;
}
.auth-note {
  color: #444;
  font-size: 0.92rem;
  margin-top: 8px;
}
</style> 