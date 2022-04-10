<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './ImageG.vue';
import Popup from './Popup.vue';

const props = defineProps<{ name:string }>()
const imageList = ref<ImageType[]>([]);
const imageList2 = ref<ImageType[]>([]);
const boolButtonAddImage = ref<Boolean>(false);
const popupTriggers = ref<boolean>(false);
const idImageToFolder = ref<number>(-1);
const galleryName = ref<string>("Gallery");


if(props.name != "undefined"){
  console.log("gallery albums");
  console.log(props.name);
  galleryName.value = props.name;
  boolButtonAddImage.value = true;
  api.getImageListFromFolder(props.name)
  .then((data) => {
    imageList.value = data;
  })
}else{
  console.log("gallery classique");
  console.log(props.name);
  api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });
}

api.getImageList()
  .then((data) => {
    imageList2.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });

function TogglePopup() {
    console.log("Toggling popup");
    popupTriggers.value = !popupTriggers.value;
}


  function displayPopup(){
    console.log("affiche popup");
    popupTriggers.value = true;
    console.log(popupTriggers.value );
}

function uploadImage() {
  api.addImageToFolder(props.name, idImageToFolder.value);
}
</script>

<template>
  <h3>{{galleryName}}</h3>
  <div class="gallerySKU">
    <Image v-for="image in imageList" :id="image.id" />
  </div>
  <div class="buttonAddFolder" v-if="boolButtonAddImage">
        <div class="circleButton" @click="displayPopup()">
            <div class="plusButton plusButtonHorizontal"></div>
            <div class="plusButton plusButtonVertical"></div>
        </div>
  </div>
  <Popup v-if="popupTriggers">
        <h5>Ajouter un image</h5>
        <select class="sku" v-model="idImageToFolder">
          <option v-for="image in imageList2" :value="image.id" :key="image.id">{{ image.name }}</option>
        </select>
        <button @click="uploadImage(), TogglePopup()">ok</button>
        <br>
        <button class="closePopup" @click="TogglePopup()">Annuler</button>
  </Popup>
</template>

<style scoped>
.buttonAddFolder {
        position: relative;
        float: left;
        padding-left: 40px;
        padding-top: 68px;
    }

    .circleButton {
        width: 50px;
        height: 50px;
        position: relative;
        float: left;
        background-color: #D3D3D3;
        border: 5px rgba(0, 0, 0, 1);
        border-radius: 50%;
        cursor: pointer;
    }

    .plusButton {
        background-color: white;
        margin-left: 12%;
        margin-right: auto;
        margin-top: 48%;
    }

    .plusButtonHorizontal {
        width: 75%;
        height: 5px;
        position: absolute;   
    }

    .plusButtonVertical {
        width: 75%;
        height: 5px;
        position: absolute;
        transform: rotate(90deg);
    }
    .gallerySKU {
      float: left;
      position: relative;
      max-width: 70%;
      max-height: 70%;
      left: 10%;
      right: 90%;
    } 
    
</style>
