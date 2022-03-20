<script setup lang="ts">
import { defineProps, ref } from 'vue';
import { api } from '@/http-api';

const props = defineProps<{ id: number }>()
var link: HTMLAnchorElement;

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    const url = window.URL.createObjectURL(data);
    link = document.createElement('a')
    link.href = url
    link.setAttribute('downlaod', 'file.png')
    document.body.appendChild(link)
    console.log(link)
    reader.readAsDataURL(data);
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery");
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        galleryElt.appendChild(imgElt);
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

</script>

<template>
  <figure id="gallery"></figure> 
  <div>
    <button @click="downloadImage">Download</button> 
  </div>
</template>

<style></style>