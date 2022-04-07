import axios, { AxiosResponse, AxiosError } from 'axios';
import { ImageType } from '@/image';

const instance = axios.create({
  baseURL: "/",
  timeout: 15000,
});

const responseBody = (response: AxiosResponse) => response.data;

const requests = {
  get: (url: string, param: {}) => instance.get(url, param).then(responseBody),
  post: (url: string, body: {}) => instance.post(url, body, { headers: { "Content-Type": "multipart/form-data" }, }).then(responseBody),
  put: (url: string, body: {}) => instance.put(url, body).then(responseBody),
  delete: (url: string) => instance.delete(url).then(responseBody)
};

export const api = {
  getImageList: (): Promise<ImageType[]> => requests.get('images', {}),
  getImage: (id: number): Promise<Blob> => requests.get(`images/${id}`, { responseType: "blob" }),
  createImage: (form: FormData): Promise<ImageType> => requests.post('images', form),
  deleteImage: (id: number): Promise<void> => requests.delete(`images/${id}`), 
  withoutParameter: (algo: string, id: number): Promise<Blob> => requests.get(`images/${id}?algorithm=${algo}`, {responseType: "blob"}),
  withOneParameter: (algo: string, p1: number, id: number): Promise<Blob> => requests.get(`images/${id}?algorithm=${algo}&p1=${p1}`, {responseType: "blob"}),
  withTwoParameter: (algo: string, p1: number, p2: number, id: number): Promise<Blob> => requests.get(`images/${id}?algorithm=${algo}&p1=${p1}&p2=${p2}`, {responseType: "blob"}),
  getListFolder: (): Promise<string[]> => requests.get('album',{}),
  getImageListFromFolder: (foldername: string): Promise<ImageType[]> => requests.get(`images?liste=${foldername}`, {}),
  createFolder: (foldername: string): Promise<void> => axios.post(`images?create=${foldername}`, {}).then(responseBody),
  addImageToFolder: (foldername: string, id: number): Promise<void> => axios.post(`images?add=${foldername}&id=${id}`, {}).then(responseBody)
};