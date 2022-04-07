<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import router from '@/router';
import Popup from './Popup.vue';


const folderList = ref<String[]>([]);
var selectedFolder = "";
const popupTriggers = ref<boolean>(false);
const newFolderName = ref<string>("");

api.getListFolder()
    .then((data) => {
        folderList.value = data;
    })
    .catch((err) => {
        console.log(err.message);
    });


function TogglePopup() {
    console.log("Toggling popup");
    popupTriggers.value = !popupTriggers.value;
}

function showImagesFolder(folder: string) {
    selectedFolder = folder;
    console.log("redirect to gallery..")
    router.push({name: "gallery", params: {ski: selectedFolder}});
}

function displayPopup(){
    console.log("affiche popup");
    popupTriggers.value = true;
    console.log(popupTriggers.value );
}

function createFolder(){
    api.createFolder(newFolderName.value);
}


</script>
<template>
    <div v-for="folder in folderList">
        <div class="folder" @click= "showImagesFolder(folder as string)">
            <img class="imgFolder" src="../assets/folder.png">
            <div class="folderName">
                {{folder}}
            </div>
        </div>
    </div>
    <div class="buttonAddFolder" >
        <div class="circleButton" @click="displayPopup()">
            <div class="plusButton plusButtonHorizontal"></div>
            <div class="plusButton plusButtonVertical"></div>
        </div>
    </div>
    <Popup v-if="popupTriggers">
        <h5>Donnez un nom à votre album :</h5>
        <input v-model="newFolderName">
        <button class="submit" @click="createFolder(), TogglePopup()">Ok</button>
        <br>
        <button class="closePopup" @click="TogglePopup()">Annuler</button>
    </Popup>
</template>
<style>
    .folder {
        position: relative;
        float: left;
        padding-left: 40px;
        padding-top: 50px;
    }

    img {
        width: 110px;
        height: 110px;
    }

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
</style>