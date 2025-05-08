<template>
  <div class="about-page">
    <!-- 顶部标题 -->
    <div class="about-header">
      <h1>言磎精品中文</h1>
      <div class="about-subtitle">带着学生快乐学习汉学。</div>
    </div>

    <!-- Our Story 区块 -->
    <div class="about-story">
      <div class="story-left">
        <div class="story-title">Our Story</div>
        <div class="story-text">
          <p>What began as a small weekend program has grown into a comprehensive educational institution serving hundreds of students across different age groups and proficiency levels.</p>
          <p>Our unique approach integrates Chinese language acquisition with mathematical concepts, creating a powerful synergy that enhances learning in both disciplines.</p>
        </div>
      </div>
      <div class="story-right">
        <div class="campus-line">
          Our campus 图片占位
        </div>
      </div>
    </div>

    <!-- Mission & Vision -->
    <div class="about-section">
      <div class="section-title">Our Mission & Vision</div>
      <div class="mission-vision-row">
        <div class="mission-card">
          <div class="mission-label">Our Mission</div>
          <div class="mission-desc">To provide exceptional education that integrates Chinese language learning with mathematical concepts, fostering bilingual proficiency and analytical thinking in our students.</div>
        </div>
        <div class="vision-card">
          <div class="vision-label">Our Vision</div>
          <div class="vision-desc">To be the leading institution for integrated Chinese-Mathematics education, recognized for our innovative curriculum and the outstanding achievements of our students.</div>
        </div>
      </div>
    </div>

    <!-- Educational Philosophy -->
    <div class="about-section">
      <div class="section-title">Our Educational Philosophy</div>
      <div class="philosophy-row">
        <div class="philosophy-card">
          <div class="philosophy-title">Integrated Learning</div>
          <div class="philosophy-desc">We believe that learning Chinese and mathematics together creates powerful cognitive connections that enhance understanding in both subjects.</div>
        </div>
        <div class="philosophy-card">
          <div class="philosophy-title">Cultural Context</div>
          <div class="philosophy-desc">We teach mathematics within the context of Chinese culture and history, providing a rich, meaningful learning experience.</div>
        </div>
        <div class="philosophy-card">
          <div class="philosophy-title">Practical Application</div>
          <div class="philosophy-desc">We emphasize real-world applications of mathematical concepts, taught in both Chinese and English.</div>
        </div>
      </div>
    </div>

    <!-- Leadership -->
    <div class="about-section">
      <div class="section-title">Meet Our Leadership</div>
      <div class="leadership-row">
        <div class="leader-block">
          <div class="leader-name">Dr. Li Wei.</div>
          <div class="leader-title">Founder & Academic Director</div>
        </div>
        <div class="leader-block">
          <div class="leader-name">Prof. Sarah Chen</div>
          <div class="leader-title">Chinese Language Director</div>
        </div>
        <div class="leader-block">
          <div class="leader-name">Dr. Michael Zhang</div>
          <div class="leader-title">Mathematics Director</div>
        </div>
      </div>
    </div>

    <!-- Join Community -->
    <div class="about-section join-section">
      <div class="join-title">Join Our Community</div>
      <div class="join-desc">Experience the benefits of our unique educational approach. Enroll in one of our courses or contact us to learn more.</div>
      <div class="join-btns">
        <button class="btn-red">Explore Courses</button>
        <button class="btn-white">Contact Us</button>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.about-page {
  background: #fff;
  min-height: 100vh;
  padding-bottom: 40px;
  margin-top: 140px;
}
.about-header {
  text-align: center;
  margin-top: 32px;
  margin-bottom: 32px;
}
.about-header h1 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.about-subtitle {
  color: #222;
  font-size: 1.1rem;
  font-weight: bold;
}
.about-story {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 48px;
  max-width: 900px;
  margin: 0 auto 40px auto;
  padding-bottom: 32px;
  border-bottom: 1px solid #eee;
}
.story-left {
  flex: 1.2;
  min-width: 320px;
}
.story-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 16px;
}
.story-text p {
  color: #222;
  font-size: 1.08rem;
  margin-bottom: 12px;
  line-height: 1.7;
}
.story-right {
  flex: 1;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  min-width: 180px;
}
.campus-line {
  position: relative;
  width: 220px;
  height: 32px;
  margin-top: 32px;
}
.campus-dot {
  position: absolute;
  left: 0;
  top: 12px;
  width: 32px;
  height: 32px;
  background: #fbeaea;
  border-radius: 50%;
  border: 2px solid #e53935;
  z-index: 2;
}
.campus-label {
  position: absolute;
  left: 36px;
  top: 16px;
  color: #e53935;
  font-size: 1rem;
  font-weight: 500;
  z-index: 3;
}
.campus-line::after {
  content: '';
  position: absolute;
  left: 16px;
  top: 26px;
  width: 180px;
  height: 4px;
  background: #fbeaea;
  border-radius: 2px;
  z-index: 1;
}
.about-section {
  max-width: 900px;
  margin: 0 auto 40px auto;
  text-align: center;
}
.section-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 28px;
}
.mission-vision-row {
  display: flex;
  gap: 24px;
  justify-content: center;
  flex-wrap: wrap;
}
.mission-card, .vision-card {
  flex: 1;
  min-width: 260px;
  max-width: 380px;
  background: #fff;
  border: 1.5px solid #eee;
  border-radius: 10px;
  padding: 24px 20px;
  text-align: left;
}
.mission-label {
  color: #e53935;
  font-weight: bold;
  font-size: 1.1rem;
  margin-bottom: 10px;
}
.vision-label {
  color: #2563eb;
  font-weight: bold;
  font-size: 1.1rem;
  margin-bottom: 10px;
}
.mission-desc, .vision-desc {
  color: #222;
  font-size: 1rem;
  line-height: 1.7;
}
.philosophy-row {
  display: flex;
  gap: 18px;
  justify-content: center;
  flex-wrap: wrap;
}
.philosophy-card {
  flex: 1;
  min-width: 220px;
  max-width: 300px;
  background: #f6fbf9;
  border-radius: 10px;
  padding: 24px 18px;
  text-align: left;
}
.philosophy-title {
  font-weight: bold;
  font-size: 1.08rem;
  margin-bottom: 10px;
}
.philosophy-desc {
  color: #444;
  font-size: 0.98rem;
  line-height: 1.6;
}
.leadership-row {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 24px;
  flex-wrap: wrap;
}
.leader-block {
  min-width: 180px;
  text-align: center;
}
.leader-name {
  font-weight: bold;
  font-size: 1.08rem;
  margin-bottom: 6px;
}
.leader-title {
  color: #888;
  font-size: 0.98rem;
}
.join-section {
  margin-top: 48px;
  padding-bottom: 0;
}
.join-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 10px;
}
.join-desc {
  color: #444;
  font-size: 1rem;
  margin-bottom: 24px;
}
.join-btns {
  display: flex;
  justify-content: center;
  gap: 18px;
}
.btn-red {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-red:hover {
  background: #b71c1c;
}
.btn-white {
  background: #fff;
  color: #222;
  border: 1.5px solid #ccc;
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.btn-white:hover {
  background: #f6fbf9;
  color: #e53935;
}
@media (max-width: 900px) {
  .about-story, .about-section {
    max-width: 98vw;
  }
  .mission-vision-row, .philosophy-row, .leadership-row {
    flex-direction: column;
    gap: 18px;
    align-items: center;
  }
}
</style> 