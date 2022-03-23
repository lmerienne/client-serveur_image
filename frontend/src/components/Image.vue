<script setup lang="ts">
import { defineProps, ref } from 'vue';
import { api } from '@/http-api';
import router from '@/router';

const props = defineProps<{ id: number }>()
var link: HTMLAnchorElement;

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
  <figure id="gallery"></figure> 
  <div>
    <button @click="downloadImage">Download</button> 
    <button @click="deleteImage">Delete</button>
  </div>
</template>

<style>
  img{
    max-width: 50%;                                               /* Redimensionne l'image affichée en fonction de la taille de la page. */
    max-height: 5%;
  }
</style>