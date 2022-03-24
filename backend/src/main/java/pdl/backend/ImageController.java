package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
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
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }
  /*  
    La fonction applyFilter prend quatre paramètres qui sont ensuite utilisés suivant le nom de 
  l'agorithme passé en paramètre (String algo), ce nom permet d'appeler la fonction du même nom,
  avec les paramètres qui sont donnés. 
  */
  public ResponseEntity<?> applyFilter(String algo,int p1,int p2, long id) throws IOException {
    System.out.println("algo : " + algo + " sur image = "+imageDao.retrieve(id).get().getName());
    Optional<Image> image = imageDao.retrieve(id); 
    BufferedImage input = null;
    //transforme l'image optional en image buffered:
    if (image.isPresent()) {
      InputStream inputStream = new ByteArrayInputStream(image.get().getData());
      input = ImageIO.read(inputStream);
    }else{
      return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }
    //appel des fonctions de Color
    Planar<GrayU8> imageFilter = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
    if(algo.equals("changeLum")){
      if (p1<-255 ||p1>255) return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST); //verification du delta entre -255 et 255
      Color.changeLum(imageFilter,p1);
    }   

    else if(algo.equals("flou")){
      int[][] kernel = {{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
      if(p1 == 1) Color.meanFilterWithBorders(imageFilter, imageFilter, p2, BorderType.EXTENDED); // filtre moyenneur + p2 intensité flou

      if (p2<=0) return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST); //test si le deuxième paramètre est une size valide 

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

    else{
      return new ResponseEntity<>("Algo not found.", HttpStatus.BAD_REQUEST);
    }

    // FIN application filtre
    //téléchargement de l'image
    String chemin = "src/main/resources/images/"+ algo + "_" +imageDao.retrieve(id).get().getName();          
        
    UtilImageIO.saveImage(imageFilter, chemin);    
    //recupération de l'image téléchargée
    BufferedImage imageLoad =UtilImageIO.loadImageNotNull(chemin);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    String fe = getExtension(chemin);
    try {
      //supression de l'image dans le dossier car utilisée juste pour récuperer le inpustream
      ImageIO.write(imageLoad, fe, bos);
      Files.delete(Paths.get(chemin));
    } catch (IOException e) {
      e.printStackTrace();
    }                                                                  
    InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

    // return HttpStatus.OK pour code 2OO!! + renvois l'image au client
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
  }
/*
  Les trois prochaines fonctions sont du même style ,ce sont elles qui permettent de récuperer si le client
à besoin d'une fonction avec zéro (withoutParameter), un (withOneParameter) ou deux (withTwoParameter)
paramètres elles appelent notre fonction applyFilter. Elles renvoient ensuite l'image si 
elle existe au client. 
*/ 
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
      nodes.add(objectNode);
    }
    return nodes;
  }
//fonction qui récuperer l'extension de l'image
  public String getExtension(String s){
    String fe = "";
		  int i = s.lastIndexOf('.');
	  	if (i > 0) {
		      fe = s.substring(i+1);
	  	}
    return fe;
  }

 
}
