<script setup lang="ts">
</script>

<template>
  <div id="app">
    <div class="c-header">
      <router-link to="/">
        <h1 class="home-button">PDL - L3</h1>
      </router-link>
      <router-view />
    </div>
    <input type="checkbox" class="c-btn-toggle" id="btnToggle">
    <label class="c-btn-icon" for="btnToggle">
      <div class="c-btn-bar c-btn-bar--diagonal diagonal-1"></div>
      <div class="c-btn-bar c-btn-bar--horizontal"></div>
      <div class="c-btn-bar c-btn-bar--diagonal diagonal-2"></div>
    </label>

    <nav class="c-sidebar">
      <ul class="c-sidebar-items">
        <li>
          <router-link to="/">Home</router-link>
        </li>
        <li>
          <router-link to="/slide">Gallery</router-link>
        </li>
        <li>
          <router-link to="/filter">Filter</router-link>
        </li>
        <li>
          <router-link to="/album">Albums</router-link>
        </li>
        <li>
          <router-link to="/data">Data</router-link>
        </li>
      </ul>
    </nav>
  </div>
  
</template>

<style>
#app {
  text-align: center;
  font-family: Arial, Helvetica, sans-serif;
  color: #2c3e50;
  text-decoration: none;
}
body {
  padding: 0;
  margin: 0;
}
html, body {
  overflow-x: hidden;
  height: 100%;
}

.c-header {
  width: 100%;
  height: 60px;
  background-color: #595959;
  position: fixed;
  text-decoration: none;
}

.home-button {
  color: #fff;
  text-decoration: none;
}

.error {
  color: red;
  font-weight: bold;
  list-style-type: none;
  margin-top: 1em;
}

a:link {
  text-decoration: none;
}

.c-btn-icon {
  position: absolute;
  top: 20px;
  left: 15px;
  height: 22px;
  width: 22px;
  cursor: pointer;
}

.c-btn-bar {
  width: 100%;
  height: 3px;
  background-color: #fff;
  position: absolute;
}

.c-btn-bar--horizontal {
  position: relative;
  float: left;
  margin-top: 3px;
  transition: all .3s;
}

.c-btn-bar--diagonal {
  position: relative;
  float: left;
  transition: all .3s;
}

.c-btn-bar--diagonal.diagonal-1 {
  transition: all .3s;
}

.c-btn-bar--diagonal.diagonal-2 {
  margin-top: 3px;
  transition: all .3s;
}

input[type="checkbox"] {
  display : none;
}

input[type="checkbox"]:checked ~ .c-btn-icon > .c-btn-bar--horizontal{
  opacity: 0;
  transition: all .3s;
}

input[type="checkbox"]:checked ~ .c-btn-icon > .c-btn-bar--diagonal.diagonal-1{
  transform: rotate(135deg);
  margin-top: 8px;
  transition: all .3s;
}

input[type="checkbox"]:checked ~ .c-btn-icon > .c-btn-bar--diagonal.diagonal-2{
  transform: rotate(-135deg);
  margin-top: -8px;
  transition: all .3s;
}

.c-sidebar {
  position: fixed;
  width: 250px;
  height: 100%;
  margin-top: 60px;
  transform: translate(-250px);
  transition: transform 250ms ease-in-out;
  background-color: #595959;
}

input[type="checkbox"]:checked ~ .c-sidebar {
  transform: translateX(0);
}

.c-sidebar-items {
  border-top: 1px solid rgba(255, 255, 255, .10);
  margin: 0;
  padding: 0;
}

.c-sidebar-items li {
  cursor: pointer;
  list-style-type: none;
  text-transform: uppercase;
  color:#fff;
  font-weight: bold;
  padding: 20px;
  
}
.c-sidebar-items li a {
  color: #fff;
  text-decoration: none;
}

</style>
