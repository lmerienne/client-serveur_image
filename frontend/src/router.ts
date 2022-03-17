import { createWebHistory, createRouter } from "vue-router";
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
  /*{
    path: "/image/:id?algorithm=:algo&p1=:p1",
    name: "algo",
    component: () => import("./components/Image.vue"),
    props: ({params}) => ({  })
  }*/
  {
    //path: "/images/:id?algorithm=:algo&p1=:p1",
    path: "/filter",
    name: "filter",
    component: () => import("./components/Filter.vue"),
    props: ({params}) => ({id: Number(params.id), algorithm: String(params.algorithm), p1: Number(params.p1)})
  }

];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;