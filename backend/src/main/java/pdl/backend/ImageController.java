package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.catalina.valves.rewrite.QuotedStringTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageAccessException;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  public Folder[] listFolder = new Folder[10];
  public int nbCurrentFolder = 0;

  public Folder filterFolder;
  public Image[] tabImage= new Image[20];
  public int pointeur=0;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
    /*
    ImageDao Dao = new ImageDao();
    Dao.resetHashMap();
    filterFolder = new Folder(Dao, "filterFolder");
    */
    filterFolder = new Folder(imageDao, "filterFolder");


  }

//////////////////////////////////////////////////////////////////////////
//                                                                      //
//  Appellée par une fonction annexe.                                   //
//  Traite les informations liées au filtre et aux paramètres voulant //
//  être appliqués ainsi que l'id de l'image.                           //
//                                                                      //
//  Vérifie la validité des données puis applique le filtre et          //
//  renvoie l'image modifiée.                                           //
//                                                                      //
//////////// Application du filtre et renvoie de l'image /////////////////

  public ResponseEntity<?> applyFilter(String algo,int p1,int p2, long id) throws IOException {
    System.out.println("algo : " + algo + " sur image = "+imageDao.retrieve(id).get().getName());
    Image image = imageDao.retrieve(id).get();
    Optional<Image> imageOpt=imageDao.retrieve(id); 
    BufferedImage input = null;


    ///!!
    //Si premiere image qu'on filtre
    /*
    if(filterFolder.getImageDao().retrieveAll().size() == 0){ 
      filterFolder.addImage(image.get());
    }
    //Si on change d'image pour filtre
    else if (!filterFolder.getImageDao().retrieve(0).equals(image)) {
      filterFolder.getImageDao().resetHashMap();
      filterFolder.addImage(image.get());

    }else{
      long size= filterFolder.getImageDao().retrieveAll().size()-1;
      image = filterFolder.getImageDao().retrieve(size);
    }
*/  if (pointeur==tabImage.length-1){
      Image tmp=tabImage[pointeur];
      for (int i=0; i<tabImage.length; i++){
        tabImage[i]=null;
      }
      tabImage[0]=tmp;
      pointeur=0;

}
    if (tabImage[0]==null){
      tabImage[0]=image;
      pointeur=0;
    }
    else if (!(tabImage[0].equals(image))&&!(tabImage[0].getName().equals(image.getName()))){
      for (int i=0; i<tabImage.length; i++){
        tabImage[i]=null;
      }
      tabImage[0]=image;
      pointeur=0;
    }
    else{
      if(tabImage[pointeur+1]!=null){
      for (int i= pointeur+1; i<tabImage.length;i++){
        if (tabImage[i]!=null)
        tabImage[i]=null;
      }
    }
      image=tabImage[pointeur];
    }
    // pbm image vide !
    

    ////
  //Convertion de type
  if (imageOpt.isPresent()) { //|| filterFolder.getImageDao().retrieveAll().size()!=0) {
    InputStream inputStream = new ByteArrayInputStream(image.getData());
    input = ImageIO.read(inputStream);
  }else 
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);

    //appel fonctions filtre
    Planar<GrayU8> imageFilter = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
    if(algo.equals("changeLum")){
      if (p1<-255 ||p1>255) return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST); //test paramètre
      Color.changeLum(imageFilter,p1);
    }   

    else if(algo.equals("flou")){
      int[][] kernel = {{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
      if(p1 == 1){
        if (p2<0) return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST); // test paramètre
        Color.meanFilterWithBorders(imageFilter, imageFilter, p2, BorderType.EXTENDED); // filtre moyenneur + p2 intensité flou
      }
      if(p1 == 2) Color.convolution(imageFilter, imageFilter,kernel); // filtre gaussien 
    } 

    else if(algo.equals("contour")){
      Color.convertGrey(imageFilter, imageFilter);
      Color.gradientImageSobel(imageFilter, imageFilter);
    }

    else if(algo.equals("histogramme")){Color.histo(imageFilter);}
   
    else if(algo.equals("color")){
      if (p2<-255 ||p2>255) return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST); //verification du delta entre -255 et 255
      Color.color(imageFilter,imageFilter,p1-1,p2);
    }
    else if(algo.equals("negatif")){
      Color.negatif(imageFilter, imageFilter);
    }

    else if(algo.equals("mirorHor")){
      Color.mirorHor(imageFilter,imageFilter);
    }

    else if(algo.equals("mirorVer")){
      Color.mirorVer(imageFilter, imageFilter);
    }
    else{
      return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST);
    }
    // FIN  partie application filtre

    //Sauvegarde et récupération de l'image avec filtre
    String chemin = "src/main/resources/images/"+ algo + "_" +imageDao.retrieve(id).get().getName();
    UtilImageIO.saveImage(imageFilter, chemin);    
    BufferedImage imageLoad = UtilImageIO.loadImageNotNull(chemin);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();


 

    String fe = getExtension(chemin);
    try {
      //Stock puis supprime l'image avec filtre du serveur 
      ImageIO.write(imageLoad, fe, bos);
      Files.delete(Paths.get(chemin));
    } catch (IOException e) {
      e.printStackTrace();
    }

    InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
    Image test = new Image(image.getName(), bos.toByteArray());
    pointeur++;
    tabImage[pointeur]=test;
    //filterFolder.addImage(test);
    //displayFilterFolder();

    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
  }

//////////////////////////////////////////////////////////////////////////
//                                                                      //
//  Appellées lors d'une requete URL ou d'un appel du client.           //
//                                                                      //
//  Récupèrent les informations liées au filtre et aux paramètres       //
//  voulant être appliqués ainsi que l'id de l'image.                   //
//                                                                      //
//  Appellent la fonction applyFilter() avec les paramètres de la       //
//  requête.                                                            //
//                                                                      //
/////////////// Récupère les données et les transfère ////////////////////

  @RequestMapping(value = "/images/{id}", params = {"algorithm"}, method = RequestMethod.GET)
  public ResponseEntity<?> withoutParameter(@RequestParam("algorithm") String algo,@PathVariable long id) throws IOException {
    return applyFilter(algo, 0, 0, id);
  }
  

  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1"}, method = RequestMethod.GET )
  public ResponseEntity<?>withOneParameter(@RequestParam("algorithm") String algo,@RequestParam("p1") int p1,@PathVariable long id) throws IOException {
    return applyFilter(algo, p1, 0, id);
  }

  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1","p2"}, method = RequestMethod.GET)
  public ResponseEntity<?> withTwoParameter(@RequestParam("algorithm") String algo,@RequestParam("p1") int p1,@RequestParam("p2") int p2,@PathVariable long id) throws IOException {
    return applyFilter(algo, p1, p2, id);
  }
  
  //trouver moyen de factoriser fct getImageList et getImageListFromFolder

  //Fonctions pour le undo et redo 

  @RequestMapping(value = "/images/filter/{request}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> undoRedo(@PathVariable String request)  {
    if (request.equals("undo")){
      if (pointeur==0) return new ResponseEntity<>("Action impossible! ", HttpStatus.BAD_REQUEST);
      pointeur-=1;
      InputStream inputStream = new ByteArrayInputStream(tabImage[pointeur].getData());
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    else{
      if (pointeur==tabImage.length-1){
        return new ResponseEntity<>("Action impossible! ", HttpStatus.BAD_REQUEST);
      }
      pointeur+=1;
      InputStream inputStream = new ByteArrayInputStream(tabImage[pointeur].getData());
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    
  }
  // dans terminal : curl -X POST "http://localhost:8080/images?create=XXX"
  //crée le dossier si il n'existe pas 
  @RequestMapping(value = "/images", params = {"create"}, method = RequestMethod.POST)
  public ResponseEntity<?> createFolder(@RequestParam("create") String folderName) {

    if(!existFolder(folderName)){
      ImageDao Dao = new ImageDao();
      Dao.resetHashMap();
      System.out.println(Dao.retrieveAll());
      Folder dos = new Folder(Dao,folderName);
      listFolder[nbCurrentFolder] = dos;
      nbCurrentFolder++;
      displayListFolder(); // print à supprimer si besoin
      return new ResponseEntity<>("Nouveau Dossier créé ! ", HttpStatus.OK);
    }
    displayListFolder(); // print à supprimer si besoin
    return new ResponseEntity<>("Ce dossier existe déja! ", HttpStatus.BAD_REQUEST);
  }

  // dans terminal : curl -X DELETE "http://localhost:8080/images?delete=XXX"
  // supprime le dossier s'il existe
  @RequestMapping(value = "/images", params = {"delete"}, method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteFolder(@RequestParam("delete") String folderName) {
    if(existFolder(folderName)){
      deleteFolderFromList(folderName);
      return new ResponseEntity<>("Dossier supprimé !", HttpStatus.OK);
    }
    displayListFolder(); // print à supprimer si besoin 
    return new ResponseEntity<>("Ce dossier n'existe pas !", HttpStatus.NOT_FOUND);
  }

  // trouver moyen de factoriser fct avec getImageList
  // URL : /images?liste=XXX
  @RequestMapping(value = "/images", params = {"liste"}, method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageListFromFolder(@RequestParam("liste") String folderName) throws IOException {
    int idFolder = idFromName(folderName);
    ArrayNode nodes = mapper.createArrayNode();
    if(idFolder == -1) return nodes; //Dossier inexistant
    List<Image> images = listFolder[idFolder].getImageDao().retrieveAll();
    if(nbCurrentFolder == 0) return nodes;
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());//récupération de l'id de l'image
      objectNode.put("name", image.getName());//récupération du nom 
      String fe = getExtension(image.getName());//récupération de l'extension
      objectNode.put("type",fe);
      BufferedImage input = null;
      InputStream inputStream = new ByteArrayInputStream(image.getData());
      input = ImageIO.read(inputStream);//création de l'image pour récupérer la taille

      Planar<GrayU8> imagePlanar = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
      objectNode.put("size",imagePlanar.getWidth()+"x"+imagePlanar.getHeight()+"x"+imagePlanar.getNumBands());//récupération de la taille
      nodes.add(objectNode);
    }
    return nodes;
  }

  //dans terminal : curl -X POST "http://localhost:8080/images?add=XXX&id=XXX"
  //ajoute une image a un dossier
  @RequestMapping(value = "/images", params = {"add","id"}, method = RequestMethod.POST)
  public ResponseEntity<?> addImageToFolder(@RequestParam("add") String folderName,@RequestParam("id") int id) {
    int idFolder = idFromName(folderName);
    if(nbCurrentFolder == 0) return new ResponseEntity<>("Ce dossier n'existe pas! ", HttpStatus.NOT_FOUND);
    Optional<Image> image = imageDao.retrieve(id);
    if(image.isEmpty()) return new ResponseEntity<>("Cette image n'existe pas! ", HttpStatus.NOT_FOUND);
    listFolder[idFolder].addImage(image.get());
    return new ResponseEntity<>("L'image a été ajoutée dans dossier :3 ! ", HttpStatus.OK);
  }

  //Renvoie la liste des albums créés
  @RequestMapping(value = "/album", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public String[] getFolder() throws IOException {
    String[] listName = new String[nbCurrentFolder];
    for (int i = 0; i < nbCurrentFolder; i++) {
      listName[i] = listFolder[i].getName();
    }
    return listName;
  }

  ///////////////
  ///////////////
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      InputStream inputStream = new ByteArrayInputStream(image.get().getData());
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }
 
  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      imageDao.delete(image.get());
      return new ResponseEntity<>("Image id=" + id + " deleted.", HttpStatus.OK);
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    String contentType = file.getContentType();
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString()) && !contentType.equals(MediaType.IMAGE_PNG.toString()) ) {
      return new ResponseEntity<>("Only JPEG file format supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    try {
      imageDao.create(new Image(file.getOriginalFilename(), file.getBytes()));
    } catch (IOException e) {
      return new ResponseEntity<>("Failure to read file", HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>("Image uploaded", HttpStatus.OK);
  }
//requete json:
  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() throws IOException {
    List<Image> images = imageDao.retrieveAll();
    ArrayNode nodes = mapper.createArrayNode();
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());//récupération de l'id de l'image
      objectNode.put("name", image.getName());//récupération du nom
      String fe = getExtension(image.getName());//récupération de l'extension
      objectNode.put("type",fe);
      BufferedImage input = null;
      InputStream inputStream = new ByteArrayInputStream(image.getData());
      input = ImageIO.read(inputStream);//création de l'image pour récupérer la taille

      Planar<GrayU8> imagePlanar = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
      objectNode.put("size",imagePlanar.getWidth()+"x"+imagePlanar.getHeight()+"x"+imagePlanar.getNumBands());//récupération de la taille
      objectNode.put("data",image.getData().length+" o");
      nodes.add(objectNode);
    }
    return nodes;
  }

//Récupere l'extension du nom d'une image
  public String getExtension(String s){
    String fe = "";
		  int i = s.lastIndexOf('.');
	  	if (i > 0) {
		      fe = s.substring(i+1);
	  	}
    return fe;
  }


  // possibilité de supprimer via ID !!!!!!!!! si plus simple pour frontend

  //Supprime un dossier à partir de son nom
  public void deleteFolderFromList(String name){
    for (int i = 0; i < nbCurrentFolder; i++) {
      if(listFolder[i].getName().equals(name)){
        for (int j = i; j < nbCurrentFolder-1; j++) 
          listFolder[j] = listFolder[j+1];
      } 
    }
    nbCurrentFolder--;
  }

  // Affiche les dossiers par noms ainsi que par position de création 
  public void displayListFolder(){
    System.out.println("Liste Dossier :");
    if(nbCurrentFolder == 0) System.out.println("   Vide");
    else{
      for (int i = 0; i < nbCurrentFolder; i++) {
        System.out.println("    Dossier " + i + " : " + listFolder[i].getName());
      }
    }
  }

  public int idFromName(String name){
    if(nbCurrentFolder == 0) return -1;
    else{
      for (int i = 0; i < nbCurrentFolder; i++) 
        if(listFolder[i].getName().equals(name))
          return i; 
    }
    return -1; //Pas de dossier de ce nom
  }

  public Boolean existFolder(String name){
    if(nbCurrentFolder == 0) return false;
    for (int i = 0; i < nbCurrentFolder; i++) {
      if( listFolder[i].getName().equals(name)) return true;     
    }
    return false;
  }

  public void displayFilterFolder(){
    System.out.println("Les images save pour filtre sont :");
    for (int i = 0; i < filterFolder.getImageDao().retrieveAll().size(); i++) {
      System.out.println("nom = "+filterFolder.getImageDao().retrieve(i).get().getName()+" data = "+filterFolder.getImageDao().retrieve(i).get().getData().length);
    }
    System.out.println("liste ok");
   
  }


}