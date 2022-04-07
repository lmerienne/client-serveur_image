package pdl.backend;



import org.springframework.beans.factory.annotation.Autowired;

public class Folder {
  private final ImageDao imageDao;
  private final String name;
  


  @Autowired
  public Folder(ImageDao imageDao, String name) {
    this.imageDao = imageDao;
    this.name = name;
  }

  public String getName(){
    return this.name;
  }

  public ImageDao getImageDao(){
    return this.imageDao;
  }

  public void addImage(Image img){
    this.imageDao.create(img);
  }
  
}
