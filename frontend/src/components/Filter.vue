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
const selectedOptionFlou = ref('');
const selectedOptionColor = ref('');
var link: HTMLAnchorElement;

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
  if (selectedParam2.value && selectedParam.value){
  api.withTwoParameter(selectedAlgo.value, selectedParam.value, selectedParam2.value, selectedId.value)
    .then((data: Blob) => {
    const reader = new window.FileReader();
    console.log("2 param")
    console.log(selectedParam2.value)
    const url = window.URL.createObjectURL(data);
    link = document.createElement('a')
    link.href = url
    link.download = "file.png"
    document.body.appendChild(link)
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
  });
  }
  else if (selectedParam.value){
    api.withOneParameter(selectedAlgo.value, selectedParam.value, selectedId.value )
    .then((data: Blob) => {
    const reader = new window.FileReader();
    console.log("1 params")
    const url = window.URL.createObjectURL(data);
    link = document.createElement('a')
    link.href = url
    link.download = "file.png"
    document.body.appendChild(link)
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
  });
  }
  else{
    api.withoutParameter(selectedAlgo.value, selectedId.value)
    .then((data: Blob) => {
    const reader = new window.FileReader();
    console.log("no param")
    const url = window.URL.createObjectURL(data);
    link = document.createElement('a')
    link.href = url
    link.download = "file.png"
    document.body.appendChild(link)
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
  });
  }
}

function downloadImage(){
  console.log(link)
  link.click()
}

function applyFlou(){
  if(selectedOptionFlou.value == "Moyenneur"){ selectedParam.value = 1;}
  else{ selectedParam.value = 2;}
  applyFilter();
}

function applyColor(){
  if(selectedOptionColor.value == "Rouge"){ selectedParam.value = 1;}
  else if(selectedOptionColor.value == "Vert"){ selectedParam.value = 2;}
  else{ selectedParam.value = 3;}
  applyFilter();
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
          <option id="f2">flou</option>
          <option id="f3">color</option>
          <option id="f4">histogramme</option>
          <option id="f5">contour</option>
      </select>
      <select v-if="selectedAlgo == 'flou' " v-model="selectedOptionFlou">
        <option id="p1"> Moyenneur</option>
        <option id="p2"> Gaussien </option>
      </select>
      <select v-if="selectedAlgo == 'color'" v-model="selectedOptionColor">
        <option id="c1">Rouge</option>
        <option id="c2">Vert</option>
        <option id="c3">Bleu</option>
      </select>
      <input v-if="selectedAlgo == 'changeLum' || selectedAlgo == 'histogramme'" v-model="selectedParam">
      <input v-if="selectedOptionFlou == 'Moyenneur' || selectedAlgo == 'color'" v-model="selectedParam2">
      <button v-if="selectedAlgo == 'flou'" @click="applyFlou">Apply</button>
      <button v-if="selectedAlgo == 'color'" @click="applyColor">Apply</button>
      <button v-if="selectedAlgo != 'flou' && selectedAlgo != 'color'" @click="applyFilter">Apply</button>
  </div>

  <div>
    <button @click="downloadImage">Download</button>
  </div>
</template>

<style scoped>
img{
    max-width: 50%;
    max-height: 5%;
  }
</style>
