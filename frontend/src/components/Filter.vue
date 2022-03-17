<script setup lang="ts">
import { ref } from 'vue';
import router from "@/router";
import { api } from '@/http-api';
import { ImageType } from '@/image'

const selectedId = ref(-1);
const selectedAlgo = ref('');
const selectedParam = ref(-1);
const imageList = ref<ImageType[]>([]);
getImageList();

function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}


function showImage2() {
    const id = selectedId.value;
    api.getImage(id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    
    reader.readAsDataURL(data);
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery");
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        if(galleryElt.hasChildNodes()){
            galleryElt.replaceChildren(imgElt);
        }else{
            galleryElt.appendChild(imgElt);
        }
        if (imgElt !== null && reader.result as string) {
          imgElt.setAttribute("src", (reader.result as string));
        }
      }
    };
  })
  .catch(e => {
    console.log(e.message);
  });
}

function applyFilter() {

}

</script>

<template>
  <div>
    <h3>Choose an image</h3>
    <div>
      <select v-model="selectedId" @change="showImage2">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
      <figure id="gallery"></figure>  
    </div>
  </div>
  <div>
      <h4>Choose a filter</h4>
      <select v-model="selectedAlgo">
          <option id="f1">Filter1</option>
          <option id="f2">Filter2</option>
          <option id="f3">Filter3</option>
      </select>
      <input v-model="selectedParam">
      <button @click="applyFilter">Apply</button>         <!--@click="submitFilter"-->
  </div>
</template>

<style scoped>
</style>
