<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image';


const folderList = ref<String[]>([]);
const imageList = ref<ImageType[]>([]);

const sumData = ref<number>(0)

const nbImageUplaod = ref<number>(0)


api.getNbImageUpload()
    .then((data) => {
        nbImageUplaod.value = data;
    })
    .catch((err) => {
        console.log(err.message);
    });

api.getData()
    .then((data) => {
        sumData.value = data;
    })
    .catch((err) => {
        console.log(err.message);
    });

api.getListFolder()
    .then((data) => {
        folderList.value = data;
    })
    .catch((err) => {
        console.log(err.message);
    });


api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });

</script>
<template>
    
   
    <div class="firstLine">
            <div class="pos image">
                <img class="icone" src="../assets/umage.png">
                <span > {{imageList.length}} images</span> 
            </div>
            <div class="pos folder">
                <img class="icone" src="../assets/folder.png">
                <span> {{folderList.length}} albums</span> 
            </div>
            <div class="pos data">
                <img class="icone"  src="../assets/data.png">
                <span > {{sumData}} Ko d'espace occupé</span> 
            </div>
    </div>
    <div class="secondLine">
            <div class="pos download">
                <img class="icone" src="../assets/download.png">
                <span > {{}} 0 images téléchargées</span> 
            </div>


            <div class="pos upload">
                <img class="icone" src="../assets/upload.png">
                <span> {{nbImageUplaod}} images déposées</span> 
            </div>
    </div> 
</template>
<style>

.firstLine{
    position: relative;
    top:40px;
    left: 20%;
    right: 80%;

}

.secondLine{
    position: relative;
    top: 260px;
    left: -28%;
}

.pos {
    position: relative;
    float: left;
    width: 187px;
    height: 184px;
}

.icone{
    width: 187px;
    height: 184px;
}

.folder{
    padding-left: 8%;
    padding-right: 8%;
}

.upload{
    padding-left: 100px;
}



</style>