import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import About from '../views/About.vue'
import Courses from '../views/Courses.vue'
import Resources from '../views/Resources.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'
import ClassManagement from '../views/teacher/ClassManagement.vue'
import StudentManagement from '../views/teacher/StudentManagement.vue'
import AssignmentManagement from '../views/teacher/AssignmentManagement.vue'
import TeacherHome from '../views/teacher/Home.vue'
import TeacherClassView from '../views/teacher/ClassView.vue'
import StudentHome from '../views/student/Home.vue'
import StudentAssignmentManagement from '../views/student/AssignmentManagement.vue'
import StudentClassAssignments from '../views/student/ClassAssignments.vue'
import TeacherLayout from '../views/teacher/TeacherLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/about',
      name: 'about',
      component: About
    },
    {
      path: '/courses',
      name: 'courses',
      component: Courses
    },
    {
      path: '/resources',
      name: 'resources',
      component: Resources,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignUp
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: TeacherHome,
      meta: { requiresAuth: true, requiresTeacher: true }
    },
    {
      path: '/teacher/class/:classId',
      name: 'teacher-class',
      component: TeacherClassView,
      meta: { requiresAuth: true, requiresTeacher: true }
    },
    {
      path: '/student',
      name: 'student',
      component: StudentHome,
      meta: { requiresAuth: true, requiresStudent: true }
    },
    {
      path: '/student/home',
      name: 'student-home',
      component: StudentHome,
      meta: { requiresAuth: true, requiresStudent: true }
    },
    {
      path: '/student/assignments',
      name: 'student-assignments',
      component: StudentAssignmentManagement,
      meta: { requiresAuth: true, requiresStudent: true }
    },
    {
      path: '/student/class/:classId',
      name: 'student-class',
      component: StudentClassAssignments,
      meta: { requiresAuth: true, requiresStudent: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录，重定向到登录页
    next({ name: 'login' })
  } else if (to.meta.requiresTeacher && (!userRole || userRole.toUpperCase() !== 'TEACHER')) {
    // 需要教师权限但不是教师，重定向到首页
    next({ name: 'home' })
  } else if (to.meta.requiresStudent && (!userRole || userRole.toUpperCase() !== 'STUDENT')) {
    // 需要学生权限但不是学生，重定向到首页
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router 