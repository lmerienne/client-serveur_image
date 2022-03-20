<script setup lang="ts">
import { defineProps, ref } from 'vue';
import { api } from '@/http-api';
import router from '@/router';

const props = defineProps<{ id: number }>()
var link: HTMLAnchorElement;

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    const url = window.URL.createObjectURL(data);
    link = document.createElement('a')
    link.href = url
    link.download = "file.png"
    document.body.appendChild(link)
    console.log(link)
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

function downloadImage(){
  console.log(link)
  link.click()
}

function deleteImage(){
  api.deleteImage(props.id)
  router.push({name:'home'})
}

</script>

<template>
  <figure id="gallery"></figure> 
  <div>
    <button @click="downloadImage">Download</button> 
    <button @click="deleteImage">Delete</button>
  </div>
</template>

<style>
  img{
    max-width: 50%;
    max-height: 5%;
  }
</style>