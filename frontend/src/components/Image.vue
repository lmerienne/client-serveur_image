<script setup lang="ts">
import { defineProps, ref } from 'vue';
import { api } from '@/http-api';
import router from '@/router';
import { ImageType } from '@/image';

const props = defineProps<{ id: number }>()
var link: HTMLAnchorElement;
const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
const target = ref<HTMLInputElement>();
const selectedBool = ref<Boolean>(true);
getImageList();

function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

//////////// Affiche l'image correspondante à l'id ////////////

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();   
    const url = window.URL.createObjectURL(data);              // Création d'un URL cliquable menant vers l'image affichée pour la télécharger.
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
            galleryElt.replaceChildren(imgElt);                 // Vérifie si un childNode est déjà présent, si oui, le remplace,
        }else{                                                  // sinon, en crée un nouveau. Permet d'éviter l'ajout de multiple fils au noeud
            galleryElt.appendChild(imgElt);                     // et l'affichage des anciennes photos.
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
//////////////////////////////////////////////////////////////////

function showImage() {
  router.push({ name: 'image', params: { id: selectedId.value } })
}

function filter() {
  router.push({ name: 'filter', params: { id: props.id} })
}

function downloadImage(){
  console.log(link)
  link.click()                                                   // Simule un clique sur le lien de l'image crée pour la télécharger.
}

function deleteImage(){
  api.deleteImage(props.id)
  router.push({name:'home'})                                     // Renvoi l'utilisateur dans le Home après la suppréssion de l'élément.
}

</script>

<template>
<h3>Selectionner une image</h3>
    <!-- arrondi de selection -->
    <div id="liste" >
      <select v-model="selectedId" @change="showImage" class="liste" value="toto">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
    </div>
  <figure id="gallery"></figure> 

  
  <div >
    <button class="image" @click="downloadImage">Download</button> 
    <button class="image milieu" @click="filter">Edit</button> 
    <button class="image" @click="deleteImage">Delete</button>
  </div>
</template>

<style>
  img{
    max-width: 400%;                                               /* Redimensionne l'image affichée en fonction de la taille de la page. */
    max-height: 5%;
  }
  .image {
    padding: 8px;
    width: 150px; 
    font-size: 16px;
    border: 2px solid #2c3e50;
    border-radius: 100px;
    font-size: 16px;
    background-color: #fff;
    position: relative;
}

.liste {
    padding: 16px;
    font-size: 16px;
    border: 2px solid #2c3e50;
    border-radius: 100px;
    font-size: 16px;
}
</style>