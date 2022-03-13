package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
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

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
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
  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1","p2"}, method = RequestMethod.GET)
  public ResponseEntity<?> parametres2(@RequestParam("algorithm") String name,@RequestParam("p1") String p1,@RequestParam("p2") String p2,@PathVariable long id) {
    System.out.println("id="+id);
    System.out.println("name="+name);
    System.out.println("p1="+p1);
    System.out.println("p2="+p2);
    return new ResponseEntity<>("Image id=" + name + " not found.", HttpStatus.NOT_FOUND);
  }
  @RequestMapping(value = "/images/{id}", params = {"algorithm", "p1"}, method = RequestMethod.GET)
  public ResponseEntity<?> parametre1(@RequestParam("algorithm") String name,@RequestParam("p1") int p1,@PathVariable long id) {
    //Optional<Image> image = imageDao.retrieve(id);
    System.out.println("id="+id);
    System.out.println("name="+name);
    System.out.println("p1="+p1);
    if (name.equals("changeLum")){
      System.out.println("named image="+imageDao.retrieve(id).get().getName());
        BufferedImage input = UtilImageIO.loadImage("src/main/resources/images/"+imageDao.retrieve(id).get().getName());//imageDao.retrieve(id).get().getName());
        //System.out.println("input="+input);
        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
        Color.changeLum(image,p1);
        UtilImageIO.saveImage(image, "src/main/resources/images/"+imageDao.retrieve(0).get().getName());
        System.out.println("image modifiée");
        Optional<Image> image2 = imageDao.retrieve(0);
        InputStream inputStream = new ByteArrayInputStream(image2.get().getData());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    return new ResponseEntity<>("Image id=" + name + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images/{id}", params = {"algorithm"}, method = RequestMethod.GET)
  public ResponseEntity<?> WithoutParametre(@RequestParam("algorithm") String name,@PathVariable long id) {
    System.out.println("id="+id);
    System.out.println("name="+name);
    //System.out.println("p2="+p2);
    return new ResponseEntity<>("Image id=" + name + " not found.", HttpStatus.NOT_FOUND);
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
