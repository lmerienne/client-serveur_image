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

///////// Affiche l'image selectionnée avant de lui appliquer //////////
function showImage2() {
    const id = selectedId.value;
    api.getImage(id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);                                                     // Création d'un URL cliquable menant vers l'image affichée pour la télécharger.
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery");
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        if(galleryElt.hasChildNodes()){                                             // Vérifie si un childNode est déjà présent, si oui, le remplace,
            galleryElt.replaceChildren(imgElt);                                     // sinon, en crée un nouveau. Permet d'éviter l'ajout de multiple fils au noeud
        }else{                                                                      // et l'affichage des anciennes photos.
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
//////////////////////////////////////////////////////////////////////////



//////////////////////////////////////////////////////////////////////////
//                                                                      //
//  Appel une des trois fonctions de l'api .withoutParameter ,          //
//  .withOneParameter , .withTwoParameter , en fonction du nombre de    //
//  paramètres nécéssaires au filtre passé en paramètre.                //
//  Affiche l'image avec le filtre appliqué et crée un URL pour pouvoir // 
//  la télécharger.                                                     //
//                                                                      //
////////// Application du filtre et affichage de l'image /////////////////
function applyFilter() {
  if (selectedParam2.value && selectedParam.value){
  api.withTwoParameter(selectedAlgo.value, selectedParam.value, selectedParam2.value, selectedId.value)            // Requête axios pour les filtres ayant besoin de 2 paramètres
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
    api.withOneParameter(selectedAlgo.value, selectedParam.value, selectedId.value )                                // Requête axios pour les filtres ayant besoin de 1 paramètre
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
    api.withoutParameter(selectedAlgo.value, selectedId.value)                                                      // Requête axios pour les filtres n'ayant pas besoin de paramètres
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
/////////////////////////////////////////////////////////////////////////

function downloadImage(){
  console.log(link)
  link.click()
}

function applyFlou(){
  if(selectedOptionFlou.value == "Moyenneur"){ selectedParam.value = 1;}                                // Fonction intermédiaire pour permettre une meilleure compréhension
  else{ selectedParam.value = 2;}                                                                       // lors du choix des options pour le filtre flou.          
  applyFilter();
}

function applyColor(){
  if(selectedOptionColor.value == "Rouge"){ selectedParam.value = 1;}                                   // Fonction intermédiaire pour les mêmes raisons qu'au dessus.
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
          <option id="f6">negatif</option>
          <option id="f7">mirorHor</option>
          <option id="f8">mirorVer</option>
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
      <input v-if="selectedAlgo == 'changeLum' " v-model="selectedParam">
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
    max-width: 50%;       /* Redimension de l'image en fonction de la taille de la fenêtre */
    max-height: 5%;
  }
</style>
