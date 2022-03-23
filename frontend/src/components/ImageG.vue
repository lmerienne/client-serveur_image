<script setup lang="ts">


/* Fichier spécifique pour afficher les images de la galerie
   pour éviter d'avoir les boutons delete et dowload du 
   Image.vue sous chaques images*/


import { defineProps, ref } from 'vue';
import { api } from '@/http-api';

const props = defineProps<{ id: number }>()


//////////// Affiche l'image correspondante à l'id ////////////

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
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
////////////////////////////////////////////////////////////////
</script>

<template>
  <figure id="gallery"></figure> 
</template>

<style>
  img{
    max-width: 20%;
    max-height: 5%;             /* Redimensionne les images en fonction de la taille de la fenêtre */
    padding-right: 5px;
  }
</style>