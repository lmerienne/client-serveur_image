<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './ImageG.vue';
import router from '@/router';



const imageList = ref<ImageType[]>([]);
const galleryName = ref<string>("Gallery");

const props = defineProps<{ id: number }>()
var link: HTMLAnchorElement;

const selectedId = ref<number>(0);



api.getImageList()
.then((data) => {
  imageList.value = data;
  console.log("Liste actu : " + imageList.value)
})
.catch(e => {
  console.log(e.message);
});

showImage();

function showImage() {
  
    const id = selectedId.value;
    api.getImage(id)
  .then((data: Blob) => {
    console.log("name : " + imageList.value[selectedId.value].name)
    const reader = new window.FileReader();
    reader.readAsDataURL(data);                                                     // Création d'un URL cliquable menant vers l'image affichée pour la télécharger.
    reader.onload = () => {
      const galleryElt = document.getElementById("image");
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        if(galleryElt.hasChildNodes()){                                             // Vérifie si un childNode est déjà présent, si oui, le remplace,
            galleryElt.replaceChildren(imgElt);                                     // sinon, en crée un nouveau. Permet d'éviter l'ajout de multiple fils au noeud
        }else{                                                                      // et l'affichage des anciennes photos.
            galleryElt.appendChild(imgElt);
        }
        if (imgElt !== null && reader.result as string) {
          imgElt.setAttribute("src", (reader.result as string));
          imgElt.setAttribute("style", 
          "max-height: 350px; max-width: auto; ");
        }
      }
    };
  })
  .catch(e => {
    console.log(e.message);
  });
}

function prev() {
  if(selectedId.value != 0) selectedId.value--;
  else selectedId.value = imageList.value.length-1;
  showImage();
}

function next(){
  if(selectedId.value != imageList.value.length-1) selectedId.value++;
  else selectedId.value = 0;
  showImage(); 
}

function filter() {
  router.push({ name: 'filter', params: { id: selectedId.value} })
}

function toGallery(){
  router.push({name:'gallery'})  
}

function downloadImage(){
  console.log(link)
  link.click()                                              
}

function deleteImage(){
  api.deleteImage(selectedId.value) 
  next()                              
}
</script>

<template>
  <h3>{{galleryName}}</h3>
  <div class="body">
    <div class="carrousel">
      <div class="container">
        <figure id="image"></figure>
      </div>
      <img class="bouton" @click="prev" src="../assets/fleche.png" id="g">
      <img class="bouton" @click="next" src="../assets/fleche.png" id="d">
    </div>
  </div>
  <div class="toGallery">
    <img class="bouton" @click="toGallery" src="../assets/slideToGallery.png" id="b">
  </div>

  <div class="ensemble">
    <button class="but" @click="downloadImage">Download</button> 
    <button class="but" @click="filter">Edit</button> 
    <button class="but" @click="deleteImage">Delete</button>
  </div>
  
</template>

<style scoped>

.ensemble{
  padding-top: 20px;
}
.body{
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.carrousel {
  width: 800px;
  height: 400px;
  border: solid 4px #fff;
  box-shadow: 0 0 14px rgba(0, 0, 0, 0.3);
  position: relative;
}  

.container{
  width: 800px;
  height: 400px;
}

.bouton{
  position: absolute;
  width: 70px;
  cursor: pointer;
  top: 150px;
}
.but {
    padding: 8px;
    width: 150px; 
    font-size: 16px;
    border: 2px solid #2c3e50;
    border-radius: 100px;
    font-size: 16px;
    background-color: #fff;
    position: relative;
}

#g{
  left: 0;
  opacity: 0.4;
  transform: rotate(0deg);
  transition: all 0.3s ease;
}

#d{
  right: 0;
  opacity: 0.4;
  transform: rotate(180deg);
  transition: all 0.3s ease;
}

#g:hover{
  transform: rotate(0deg) scale(1.1);
  opacity:1;
}

#d:hover{
  transform: rotate(180deg) scale(1.1);
  opacity:1;
}

#b{
  right: 50px;
  opacity: 0.4;
  transition: all 0.3s ease;
}

#b:hover{
  transform: scale(1.1);
  opacity:1;
}
</style>
