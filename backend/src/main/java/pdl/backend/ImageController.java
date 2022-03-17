package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.catalina.valves.rewrite.QuotedStringTokenizer;
import org.ddogleg.struct.DogLambdas.NewInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import boofcv.alg.feature.detect.edge.impl.ImplGradientToEdgeFeatures;
import boofcv.core.image.impl.ImplConvertImage;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;

////////// autre import mathis
import java.nio.file.Files; 
import java.nio.file.Path;
import java.nio.file.Paths;
import boofcv.io.image.ConvertBufferedImage;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }
  public ResponseEntity<?> applyFilter(String algo,int p1,int p2, long id) {
    System.out.println("Application de filtre :");
    String nameImg = imageDao.retrieve(id).get().getName();
    String chemin = "src/main/resources/images/";
    String newNameImge = algo + "_" + nameImg;

    BufferedImage input = UtilImageIO.loadImage(chemin + nameImg);                                      //charge L'image qu'on veut modif
    Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);     //convertis en planar pour appliquer filtre
    System.out.println("ça a merdé 4");
    if (algo.equals("changeLum")){
        System.out.println("algo  = " + algo + " sur image = " + nameImg);

        //Applique et sauvegarde le resultat dans une nouvelle image
        Color.changeLum(image,p1);
        UtilImageIO.saveImage(image, chemin + newNameImge );
        //////////////////////////////////////////////////////
        
        
        /*
        //Selectionne l'image a afficher : ici img de base donc pas ouf
        Optional<Image> image2 = imageDao.retrieve(id);
        InputStream inputStream = new ByteArrayInputStream(image2.get().getData());
        ////////////////////////////////////////////////////////
        */
        

        /// test :
        //byte[] fileContent = (chemin + newNameImge).getBytes();
        byte[] fileContent = ("backend/src/main/resources/images/changeLum_test.jpg").getBytes();
        
        Image img = new Image(newNameImge, fileContent);
        System.out.println("toto : " + fileContent.length);

        List<Image> test = imageDao.retrieveAll();
        imageDao.create(img);
        test = imageDao.retrieveAll();



        Optional<Image> image2 = imageDao.retrieve(test.size()-1);
        Optional<Image> imageTest = imageDao.retrieve(id);

        System.out.println("\n\nj'essaie d'afficher la nouvelle image qui est :" + image2.get().getName());
        System.out.println("taille data de base  : " + imageTest.get().getData().length);
        System.out.println("taille data nouvelle : " + image2.get().getData().length);
        System.out.println("si meme taile cool");

        InputStream inputStream = new ByteArrayInputStream(image2.get().getData());


        
        System.out.println("Filtre bien appliqué / nouvelle image crée");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
        
    }
    return new ResponseEntity<>("Image id=" + algo + " not found.", HttpStatus.NOT_FOUND);
  }



  @RequestMapping(value = "/images/{id}", params = {"algorithm"}, method = RequestMethod.GET)
  public ResponseEntity<?> withoutParameter(@RequestParam("algorithm") String algo,@PathVariable long id) {
    return applyFilter(algo, 0, 0, id);
  }

  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1"}, method = RequestMethod.GET)
  public ResponseEntity<?>withOneParameter(@RequestParam("algorithm") String algo,@RequestParam("p1") int p1,@PathVariable long id) {
    return applyFilter(algo, p1, 0, id);
  }

  

  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1","p2"}, method = RequestMethod.GET)
  public ResponseEntity<?> withTwoParameter(@RequestParam("algorithm") String algo,@RequestParam("p1") int p1,@RequestParam("p2") int p2,@PathVariable long id) {
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
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString())) {
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
  public ArrayNode getImageList() {
    List<Image> images = imageDao.retrieveAll();
    ArrayNode nodes = mapper.createArrayNode();
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());
      objectNode.put("name", image.getName());
      //Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
      String fe = "";
		  int i = image.getName().lastIndexOf('.');
	  	if (i > 0) {
		      fe = image.getName().substring(i+1);
	  	}
      objectNode.put("type",fe);
      objectNode.put("size",fe);
      nodes.add(objectNode);
    }
    return nodes;
  }

 
}
