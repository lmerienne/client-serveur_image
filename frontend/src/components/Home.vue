<script setup lang="ts">
import { ref } from 'vue';
import router from "@/router";
import { api } from '@/http-api';
import { ImageType } from '@/image'

const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
const target = ref<HTMLInputElement>();
getImageList();

function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

function showImage() {
  router.push({ name: 'image', params: { id: selectedId.value } })
}

function submitFile() {
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.value.value = '';
    }).catch(e => {
      console.log(e.message);
    });
  }
}

function handleFileUpload(event: Event) {
  target.value = (event.target as HTMLInputElement);
  submitFile();
}


function displayDiv( id: string) {
  var div = document.getElementById(id);
  if (div == null)return;
  if (div.style.display === "none") {
    div.style.display = "block";
  } else {
    div.style.display = "none";
  }
}


</script>



<template>
  <div class="bloc">

    <h3>Selectionner une image</h3>
    <!-- arrondi de selection -->
      
      <select v-model="selectedId" @change="showImage" class="liste" value="toto">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
    

    <!-- trait séparation -->
    <hr/>
    Ou déposer une 
    <button @click="displayDiv('upload')">image</button>

  </div>
  <!-- bouton choisir fichier quand on appuie sur "image" -->
  <div id="upload" style="display:none;">
      <input type="file" id="file" ref="file" @change="handleFileUpload" @click="displayDiv('upload')" />
  </div>  

  

  
  
 


</template>

<style >

.liste {
    width: 400px; 
    height: 50px;
    padding: 16px;
    font-size: 16px;
    border: 2px solid #2c3e50;
    border-radius: 100px;
    font-size: 16px;
    background-color: #fff;
}

.bloc {
    padding-top: 10%;
    position: relative;
    display: inline-block;
}

</style>
