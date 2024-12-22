<template>
  <div>
    <header>
      <h1>Welcome to EGoSpace</h1>
    </header>
    <main>
      <router-link :to="`/${$route.params.lang}/login`">Login</router-link>
    </main>
    <el-button type="primary" @click="showConfetti">BUTTON</el-button>
    <div>THIS IS THE JAVAWEB PROJECT</div>
  </div>
</template>

<script>
import confetti from 'canvas-confetti';

export default {
  name: 'HomeView',
  data() {
    return {
      confettiInterval: null
    }
  },
  methods: {
    showConfetti() {
      const duration = 15 * 1000;
      const animationEnd = Date.now() + duration;
      const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };

      function randomInRange(min, max) {
        return Math.random() * (max - min) + min;
      }

      // Clear any existing interval
      if (this.confettiInterval) {
        clearInterval(this.confettiInterval);
      }

      this.confettiInterval = setInterval(() => {
        const timeLeft = animationEnd - Date.now();

        if (timeLeft <= 0) {
          clearInterval(this.confettiInterval);
          this.confettiInterval = null;
          return;
        }

        const particleCount = 50 * (timeLeft / duration);
        confetti(Object.assign({}, defaults, { 
          particleCount, 
          origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 } 
        }));
        confetti(Object.assign({}, defaults, { 
          particleCount, 
          origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 } 
        }));
      }, 250);
    }
  },
  beforeDestroy() {
    if (this.confettiInterval) {
      clearInterval(this.confettiInterval);
    }
  }
}
</script>

<style scoped>
body {
  margin: 0;
  padding: 0;
  overflow: hidden;
}
</style>
