package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.io.*;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    try {
      byte[] fileContent;
      File dir  = new File("src/main/resources/images");
	  	File[] liste = dir.listFiles();
      for (int i = 0; i < liste.length; i++) {
        String name = liste[i].getName();
        final ClassPathResource imgFile = new ClassPathResource("images/" + name);
        if(checkIfExtensionSupported(name)){
          System.out.println("File : " + name + " is add.");
          fileContent = Files.readAllBytes(imgFile.getFile().toPath());
          Image img = new Image(name, fileContent);
          images.put(img.getId(), img);
        }else{
          System.out.println("File : " + name + " isn't add.");
        }
      }
    } catch (final IOException e) {
      //System.out.println("Dossier images non présent");
      e.printStackTrace();
    }
  }

  public Boolean checkIfExtensionSupported (String fileName){
    String fe = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    fe = fileName.substring(i+1);
		}
    if (fe.equals("png") || fe.equals("jpg") || fe.equals("jpeg")) {
      return true;
    }
    return false;
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));

    images.put(img.getId(), img);
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
  }
}
