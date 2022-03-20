<script setup lang="ts">
import { ref } from 'vue';
import router from "@/router";
import { api } from '@/http-api';
import { ImageType } from '@/image'

const selectedId = ref(-1);
const selectedAlgo = ref('');
const selectedParam = ref<number>();
const imageList = ref<ImageType[]>([]);
const selectedParam2 = ref<number>();
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
  if (selectedParam.value){
  api.withOneParameter(selectedAlgo.value, selectedParam.value, selectedId.value )
    .then((data: Blob) => {
    const reader = new window.FileReader();
    
    reader.readAsDataURL(data);
    reader.onload = () => {
      console.log("ouiii")
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
  });
  }
  else if (selectedParam2.value && selectedParam.value){
    api.withTwoParameter(selectedAlgo.value, selectedParam.value, selectedParam2.value, selectedId.value)
    .then((data: Blob) => {
    const reader = new window.FileReader();
    
    reader.readAsDataURL(data);
    reader.onload = () => {
      console.log("ouiii")
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
  });
  }
  else{
    api.withoutParameter(selectedAlgo.value, selectedId.value)
    .then((data: Blob) => {
    const reader = new window.FileReader();
    
    reader.readAsDataURL(data);
    reader.onload = () => {
      console.log("ouiii")
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
  });
  }
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
          <option id="f1">changeLum</option>
          <option id="f2">Filter2</option>
          <option id="f3">Filter3</option>
      </select>
      <input v-if="selectedAlgo == 'changeLum' || selectedAlgo == 'Filter2'" v-model="selectedParam">
      <input v-if="selectedAlgo == 'convolution'" v-model="selectedParam2">
      <button @click="applyFilter">Apply</button>
  </div>
</template>

<style scoped>
</style>
