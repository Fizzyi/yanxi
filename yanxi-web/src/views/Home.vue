<template>
  <div class="home-hero">
    <h1>言磎精品课堂</h1>
    <p class="subtitle">快乐学习语文数学！</p>
    <div class="btn-group">
      <button class="btn btn-white" @click="goToCourses">查看课程</button>
      <button class="btn btn-black" @click="goToContact">联系我们</button>
    </div>
  </div>
  <!-- 新增特色区块 -->
  <div class="features-section">
    <h2 class="features-title">我们的课程与众不同</h2>
    <div class="features-list">
      <div class="feature-card">
        <div class="feature-icon icon-red">🌏</div>
        <div class="feature-title">MultiLanguage learning</div>
        <div class="feature-desc">可以由英语和中文教授。</div>
      </div>
      <div class="feature-card">
        <div class="feature-icon icon-blue">🧮</div>
        <div class="feature-title">精心培养</div>
        <div class="feature-desc">为学生们锻炼思维能力。</div>
      </div>
      <div class="feature-card">
        <div class="feature-icon icon-green">🎓</div>
        <div class="feature-title">特选老师</div>
        <div class="feature-desc">细心又有耐性的老师</div>
      </div>
    </div>
  </div>

  
  <!-- 课程区块 -->
  <div class="course-section">
    <h2 class="course-title">课程</h2>
    <div class="course-card">
      <div class="course-card-header">
        <div>
          <div class="course-card-name">数学和中文</div>
          <div class="course-card-desc">快速提升中文和数学</div>
        </div>
        <span class="course-tag">联合</span>
      </div>
      <div class="course-card-info">
        <div class="course-info-item">📖 Level: 5-6年级学生</div>
        <div class="course-info-item">⏰ Duration: 学期期间每周一</div>
      </div>
      <a class="course-link" href="#">View Course Details →</a>
    </div>
    <button class="all-courses-btn" @click="goToCourses">查看所有课程 <span style="font-size:1.2em;">&gt;</span></button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToCourses = () => {
  router.push('/courses')
}

const goToContact = () => {
  // You can either navigate to a contact page or scroll to contact section
  // For now, I'll navigate to the about page as there's no contact route
  router.push('/about')
}
</script>

<style scoped>
.home-hero {
  width: 100vw;
  min-height: 350px;
  background: linear-gradient(90deg, #e53935 100%, #d32f2f 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* border-radius: 0 0 8px 8px; */
  margin-left: calc(50% - 50vw);
  margin-right: calc(50% - 50vw);
  margin-top: 100px;
  /* margin-bottom: 0; */
  padding-left: 50px;
}
.home-hero h1 {
  color: #fff;
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 20px;
}
.subtitle {
  color: #fff;
  font-size: 1.5rem;
  margin-bottom: 32px;
}
.btn-group {
  display: flex;
  gap: 20px;
}
.btn {
  font-size: 1.1rem;
  padding: 12px 32px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}
.btn-white {
  background: #fff;
  color: #d32f2f;
  border: 2px solid #fff;
}
.btn-white:hover {
  background: #f8d7da;
}
.btn-black {
  background: #111;
  color: #fff;
  border: 2px solid #111;
}
.btn-black:hover {
  background: #333;
}
.features-section {
  width: 100%;
  margin: 40px 0 0 0;
  text-align: center;
}
.features-title {
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 32px;
}
.features-list {
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
}
.feature-card {
  width: 320px;
  min-width: 320px;
  max-width: 320px;
  background: #f6fbf9;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 32px 28px;
  text-align: left;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin-bottom: 18px;
}
.icon-red { background: #ffeaea; color: #e53935; }
.icon-blue { background: #eaf3ff; color: #3b82f6; }
.icon-green { background: #eaffea; color: #22c55e; }
.feature-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.feature-desc {
  color: #444;
  font-size: 1rem;
}
.course-section {
  width: 100%;
  background: #f6fbf9;
  text-align: center;
  padding: 60px 0 80px 0;
  margin-top: 64px;
}
.course-title {
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 32px;
}
.course-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  width: 520px;
  margin: 0 auto 40px auto;
  padding: 32px 28px;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 18px;
  align-items: flex-start;
}
.course-card-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.course-card-name {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 6px;
}
.course-card-desc {
  color: #666;
  font-size: 1.1rem;
}
.course-tag {
  background: #f3e8ff;
  color: #a855f7;
  border-radius: 12px;
  padding: 4px 16px;
  font-size: 1rem;
  font-weight: 500;
  margin-left: 12px;
  white-space: nowrap;
}
.course-card-info {
  display: flex;
  gap: 24px;
  color: #444;
  font-size: 1rem;
}
.course-link {
  color: #2563eb;
  text-decoration: none;
  font-size: 1rem;
  margin-top: 8px;
}
.course-link:hover {
  text-decoration: underline;
}
.all-courses-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 16px 48px;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 20px;
  transition: background 0.2s;
}
.all-courses-btn:hover {
  background: #b71c1c;
}
</style> 