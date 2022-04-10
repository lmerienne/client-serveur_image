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
const boolSkuShowImage = ref(true);
const valueOfRed = ref<number>(0);
const valueOfGreen = ref<number>(0);
const valueOfBlue = ref<number>(0);
const valueOfFlou = ref<number>(0);


getImageList();

const props = defineProps<{ id:number}>()



if(props.id != undefined){
  boolSkuShowImage.value = false ;
  api.getImage(props.id-1)
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


function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

///////// Affiche l'image selectionnée avant de lui appliquer //////////
function showImage2() {
    boolSkuShowImage.value = false;
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
function undo(request: string){
 api.undoredo(request)
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
          imgElt.setAttribute("style", "max-width: 400px; max-height: 400px;");
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
          imgElt.setAttribute("style", "max-width: 400px; max-height: 400px;");
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
          imgElt.setAttribute("style", 
          "max-height: 350px; max-width: auto; ");
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
  if(selectedOptionFlou.value == "Moyenneur"){ selectedParam.value = 1; selectedParam2.value = valueOfFlou.value}                                // Fonction intermédiaire pour permettre une meilleure compréhension
  else{ selectedParam.value = 2;}                                                                       // lors du choix des options pour le filtre flou.          
  applyFilter();
}

function applyColor(){
  if(selectedOptionColor.value == "Rouge"){ selectedParam.value = 1; selectedParam2.value = valueOfRed.value; console.log}                                   // Fonction intermédiaire pour les mêmes raisons qu'au dessus.
  else if(selectedOptionColor.value == "Vert"){ selectedParam.value = 2; selectedParam2.value = valueOfGreen.value;}
  else{ selectedParam.value = 3; selectedParam2.value = valueOfBlue.value;}
  console.log("Red level : " + valueOfRed.value);
  console.log("Green level : " + valueOfGreen.value);
  console.log("Blue level : " + valueOfBlue.value);
  console.log(selectedParam2.value);
  applyFilter();
}

function reloadSKU() {
  console.log("skuuu");
  api.reset().then;
  location.reload();
}

</script>

<template>
<div class="boxFilter">
  <div class="boxFilter--filterMenu">
      <div class="slidersContainer1">
        <span class="title">Couleur :</span>
        <div class="sliderFilter ColorRed">
          <input @change="selectedOptionColor = 'Rouge'; selectedAlgo = 'color'; applyColor() " v-model="valueOfRed" type="range" min="0" max="255" class="slider Red" id="myRange"> 
        </div>
        <div class="sliderFilter ColorGreen">
          <input @change=" selectedOptionColor = 'Vert'; selectedAlgo = 'color'; applyColor()" v-model="valueOfGreen" type="range" min="0" max="255" class="slider Green" id="myRange"> 
        </div>
        <div class="sliderFilter ColorBlue">
          <input @change=" selectedOptionColor = 'Bleu'; selectedAlgo = 'color'; applyColor()" v-model="valueOfBlue" type="range" min="0" max="255" class="slider Blue" id="myRange"> 
        </div>
      </div>
      <span class="title flou">Flou :</span>
      <div class="slidersContainer2">
        <div class="sliderFilter flou">
          <button @click="selectedAlgo = 'flou'; selectedOptionFlou = 'Gaussien'; applyFlou()" class="flouB">+</button>
        </div>
      </div>
      <span class="title miroir">Miroir   :</span>
      <div class="buttonSlideSKU">
          <label @change="selectedAlgo = 'mirorVer'; applyFilter()" class="switch">
            <input type="checkbox">
            <span class="buttonSlide round"></span>
          </label>
          <label @change="selectedAlgo = 'mirorHor' ; applyFilter()" class="switch -v">
            <input type="checkbox">
            <span class="buttonSlide round"></span>
          </label>
      </div>
    
      <div class="b1">
        <button class="histo" @click="selectedAlgo = 'histogramme'; applyFilter() " style="border-radius: 2px;">Histogramme</button>
      </div>
      <div class="b3">
        <button class="contour" @click="selectedAlgo = 'contour'; applyFilter() " style="border-radius: 2px;">Contour</button>
      </div>
      <div class="b2">
        <button class="negatif" @click="selectedAlgo = 'negatif'; applyFilter() " style="border-radius: 2px;">Négatif</button>
      </div>
      
  </div>
  <div class="boxFilter--filterContainer">
    <div class="boxFilter--filterContainer-menu">
      <h4 v-if="boolSkuShowImage">Choose an image</h4>
      <select v-if="boolSkuShowImage" style="border-radius: 15px;" v-model="selectedId" @change="showImage2">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>  
    </div>
    <div class="boxFilter--filterContainer-img" id="gallery"></div>
  </div>
</div>

<div class="divContainerBt">
    <button class="buttonDL" @click="downloadImage">Download</button>
</div>

<div class="divContainerBt">
  <button class="buttonRf" @click="reloadSKU()" >New Image</button>
</div>

<div class="arrows">
  <div @click="undo('undo')" class="undoArrow left"></div>
  <div @click="undo('redo')" class="undoArrow"></div>
</div>


</template>


<style scoped>
img{
    max-width: 50%;       /* Redimension de l'image en fonction de la taille de la fenêtre */
    max-height: 5%;
  }

.b1 {
  margin-top: 20px;
  width: 100%;
  
}

.b2 {
  margin-top: 20px;
  width: 100%;
}

.b3 {
  margin-top: 20px;
  width: 100%;
}
.histo {
  margin-top: 20px;
  position: relative;
  float: left;
}

.contour {
  margin-top: 10px;
  position: relative;
  float: left;
}

.negatif {
  margin-top: 10px;
  position: relative;
  float: left;
}

.boxFilter {
  top: 10px;
  margin-left: 10%;
  position: center;
  width: 80%;
  height: 500px;
  margin-top: 4%;
}

.boxFilter--filterMenu {
  background-color: #FFFDFD;
  position: relative;
  float: left;
  height: 100%;
  border: solid 1px black;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
}

.boxFilter--filterContainer {
  background-color: #D3D3D3;
  overflow-x: hidden;
  position: relative;
  float: center;
  height: 100%;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

.boxFilter--filterContainer-img {
  margin-top: auto;
  margin-bottom: auto;
}

.boxFilter--filterContainer-menu {
  margin-top: 15%;
  position: relative;
  float: center;
}

.slidersContainer1 {
  margin-top: 25%;
}

.sliderFilter {
  width: 100%;
}

.slider {
  margin-top: 10%;
  margin-left: 15px;
  float: left;
  width: 70%;
  cursor: pointer;
}
.slider:hover {
  opacity: 1;
}

.title {
  float: left; 
  margin-left:10%;
}
.title.miroir {
  margin-top: 15%;
  padding-bottom: 10%;
}

.flouB {
  margin-top:10px;
  width: 75%;
  border-radius: 15px;
}

.buttonSlideSKU {
  position: relative;
  float: left;
  margin-left: 10%;
}

.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 22.6px;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.switch.-v {
  transform: rotateZ(90deg);
  margin-left: 10px;
}

/* The slider */
.buttonSlide {
  position: absolute;
  float: left;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.buttonSlide:before {
  position: absolute;
  content: "";
  height: 17.3px;
  width: 17.3px;
  left: 2.6px;
  bottom: 2.6px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .buttonSlide {
  background-color: #2196F3;
}

input:focus + .buttonSlide {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .buttonSlide:before {
  -webkit-transform: translateX(17.3px);
  -ms-transform: translateX(17.3px);
  transform: translateX(17.3px);
}

/* Rounded sliders */
.buttonSlide.round {
  border-radius: 22.6px;
}

.buttonSlide.round:before {
  border-radius: 50%;
}

.buttonDL {
  border-radius: 100px;
  background-color: #fff;
  width: 150px; 
  font-size: 16px;
  cursor: pointer;
}

.buttonRf {
  border-radius: 100px;
  background-color: #fff;
  width: 150px; 
  font-size: 16px;
  cursor: pointer;
}
.divContainerBt {
  float: left;
  position: relative;
  margin-top: 3%;
  padding-right: 5%;
}

.arrows {
  float: right;
  padding-right:20%;
}
.undoArrow {
  border: 5px solid transparent;
  border-top-color: black;
  border-left-color: black;
  height: 25px;
  width: 25px;
  border-radius: 50%;
  position: relative;
  float: left;
  -webkit-transform: rotate(-45deg);
  -ms-transform: rotate(-45deg);
  transform: rotate(-45deg) rotateZ(40deg);
  margin:30px auto;
  margin-left: 10px;
  cursor: pointer;
}

.undoArrow:before {
  content: "";
  position: absolute;
  top: -8px;
  left: 80%;
  height: 0;
  width: 0;
  border-left: 12px solid black;
  border-top: 12px solid transparent;
  border-bottom: 12px solid transparent;
  -webkit-transform: rotate(45deg);
  -ms-transform: rotate(45deg);
  transform: rotate(45deg);
}

.undoArrow.left {
  transform: rotateY(180deg) rotateZ(360deg);
}

input[type=range] {
  height: 22px;
  -webkit-appearance: none;
  margin: 10px 0;
  margin-left: 10%;
  width: 75%;
}
input[type=range]:focus {
  outline: none;
}
input[type=range].slider::-webkit-slider-runnable-track {
  width: 100%;
  height: 11px;
  cursor: pointer;
  animate: 0.2s;
  box-shadow: 1px 1px 1px #000000;
  background: #FF3705;
  border-radius: 15px;
  border: 0px solid #010101;
}
input[type=range].slider::-webkit-slider-thumb {
  box-shadow: 0px 0px 0px #000031;
  border: 1px solid #00001E;
  height: 18px;
  width: 18px;
  border-radius: 15px;
  background: #FFFFFF;
  cursor: pointer;
  -webkit-appearance: none;
  margin-top: -3px;
}
input[type=range].slider:focus::-webkit-slider-runnable-track {
  background: #FF3705;
}
input[type=range].slider::-moz-range-track {
  width: 100%;
  height: 11px;
  cursor: pointer;
  animate: 0.2s;
  box-shadow: 1px 1px 1px #000000;
  background: #FF3705;
  border-radius: 1px;
  border: 0px solid #010101;
}
input[type=range].slider::-moz-range-thumb {
  box-shadow: 0px 0px 0px #000031;
  border: 1px solid #00001E;
  height: 18px;
  width: 18px;
  border-radius: 15px;
  background: #FFFFFF;
  cursor: pointer;
}
input[type=range].slider::-ms-track {
  width: 100%;
  height: 11px;
  cursor: pointer;
  animate: 0.2s;
  background: transparent;
  border-color: transparent;
  color: transparent;
}
input[type=range].slider::-ms-fill-lower {
  background: #FF3705;
  border: 0px solid #010101;
  border-radius: 2px;
  box-shadow: 1px 1px 1px #000000;
}
input[type=range].slider::-ms-fill-upper {
  background: #FF3705;
  border: 0px solid #010101;
  border-radius: 2px;
  box-shadow: 1px 1px 1px #000000;
}
input[type=range].slider::-ms-thumb {
  margin-top: 1px;
  box-shadow: 0px 0px 0px #000031;
  border: 1px solid #00001E;
  height: 18px;
  width: 18px;
  border-radius: 15px;
  background: #FFFFFF;
  cursor: pointer;
}
input[type=range].slider:focus::-ms-fill-lower {
  background: #FF3705;
}
input[type=range].slider:focus::-ms-fill-upper {
  background: #FF3705;
}

input[type=range].slider.Green::-webkit-slider-runnable-track {
  background: rgb(24, 203, 24);
}

input[type=range].slider.Blue::-webkit-slider-runnable-track {
  background: blue;
}

input[type=range].slider.flou::-webkit-slider-runnable-track {
  background: grey;
}

input[type=range].slider.flou::-moz-range-track {
  background: grey;
}
input[type=range].slider.Green::-moz-range-track {
  background: rgb(24, 203, 24);
}
input[type=range].slider.Blue::-moz-range-track {
  background: blue;
}

</style>
