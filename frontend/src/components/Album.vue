<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/http-api';

const folderList = ref<String[]>([]);

api.getListFolder()
    .then((data) => {
        folderList.value = data;
    })
    .catch((err) => {
        console.log(err.message);
    })

</script>
<template>
    <div v-for="folder in folderList">
        <img src="frontend/src/assets/folder.png">
        <div>
            <span>{{folder}}</span>
        </div>
    </div>
</template>
<style scoped>
</style>