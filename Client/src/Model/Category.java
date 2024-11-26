package Model;

public class Category {
private int idCategory;
private String nameCategory; 
public Category(int idCategory, String nameCategory) {
	super();
	this.idCategory = idCategory;
	this.nameCategory = nameCategory;
}
public int getIdCategory() {
	return idCategory;
}
public void setIdCategory(int idCategory) {
	this.idCategory = idCategory;
}
public String getNameCategory() {
	return nameCategory;
}
public void setNameCategory(String nameCategory) {
	this.nameCategory = nameCategory;
}
  @Override
	public String toString() {
		return "id:" + idCategory + " - name:" + nameCategory;
	}
}
