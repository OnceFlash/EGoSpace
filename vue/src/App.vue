<template>
  <div id="app">
    <el-container>
      <el-header class="header">
        <img src="@/assets/Hnnu.png" alt="Logo" class="logo">
        <span class="title">EGoSpace-Hnnu</span>
        <el-select v-model="currentLang" @change="changeLanguage" class="language-select">
          <el-option label="English" value="us-en"></el-option>
          <el-option label="中文" value="zh-cn"></el-option>
        </el-select>
      </el-header>
      <el-container>
        <el-aside width="250px" class="sidebar-container">
          <el-menu
            :default-active="currentRoutePath"
            router
            class="sidebar-menu"
            background-color="#ACACAC"
            text-color="#fff"
            active-text-color="#FFD04B"
          >
            <el-menu-item :index="`/${currentLang}/home`">
              <i class="el-icon-s-home"></i>
              <span slot="title">{{ $t('menu.home') }}</span>
            </el-menu-item>
            <el-submenu index="management">
              <template slot="title">
                <i class="el-icon-setting"></i>
                <span>{{ $t('menu.management') }}</span>
              </template>
              <el-menu-item-group>
                <el-menu-item :index="`/${currentLang}/admin`">{{ $t('menu.admin') }}</el-menu-item>
                <el-menu-item :index="`/${currentLang}/user`">{{ $t('menu.user') }}</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentLang: this.$route.params.lang || 'us-en',
    };
  },
  computed: {
    currentRoutePath() {
      return this.$route.path;
    },
  },
  watch: {
    '$route.params.lang'(newLang) {
      this.currentLang = newLang || 'us-en';
    },
  },
  methods: {
    changeLanguage(lang) {
      const currentPath = this.$route.fullPath;
      const newPath = currentPath.replace(/^\/(us-en|zh-cn)/, `/${lang}`);
      this.$router.push(newPath).then(() => {
        setTimeout(() => {
          window.location.reload();
        }, 100);
      }).catch(() => {});
    },
  },
  created() {
    this.$message({
      message: this.$t('message.welcome'),
      type: 'success',
    });
  },
};
</script>

<style>
/* Global styles without scoped */
.sidebar-container {
  width: 250px !important;
  min-height: 100vh !important;
  background-color: #ACACAC;
  overflow: hidden;
}

.sidebar-menu {
  width: 250px !important;
  border-right: none;
  min-height: 100vh !important;
}

.el-menu::-webkit-scrollbar,
.sidebar-container::-webkit-scrollbar,
.sidebar-menu::-webkit-scrollbar {
  width: 0;
  display: none;
}

.el-menu,
.sidebar-container,
.sidebar-menu {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

.el-aside {
  overflow: hidden !important;
}

.el-menu {
  height: auto !important;
}

</style>

<style scoped>
.header {
  background-color: #1F4A81;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  width: 50px;
}

.title {
  font-family: 'Segoe PRINT';
  font-weight: bold;
  font-size: 20px;
  margin-left: 14px;
  color: white;
}

.language-select {
  margin-left: auto;
  width: 100px;
}

</style>