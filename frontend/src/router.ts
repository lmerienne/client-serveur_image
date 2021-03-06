import { createWebHashHistory, createRouter } from "vue-router";
import { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: () => import("./components/Home.vue"),
    props: true
  },
  {
    path: "/gallery",
    name: "gallery",
    component: () => import("./components/Gallery.vue"),
    props: ({params}) => ({name: String(params.ski) || null})
  },
  {
    path: "/slide",
    name: "slide",
    component: () => import("./components/Slide.vue"),
    props: true
  },
  {
    path: "/image/:id",
    name: "image",
    component: () => import("./components/Image.vue"),
    props: ({ params }) => ({ id: Number(params.id) || 0 })
  },
  {
    path: "/upload",
    name: "upload",
    component: () => import("./components/Upload.vue"),
    props: true
  },
  {
    path: "/imageG/:id",
    name: "imageG",
    component: () => import("./components/ImageG.vue"),
    props: ({ params }) => ({ id: Number(params.id) || 0 })
  },
  {
    path: "/filter",
    name: "filter",
    component: () => import("./components/Filter.vue"),
    props: ({params}) => ({id: Number(params.id) || null})
  },
  {
    path: "/album",
    name: "album",
    component: () => import("./components/Album.vue"),
    props: true
  },
  {
    path: "/data",
    name: "data",
    component: () => import("./components/Data.vue"),
    props: true
  }

];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;