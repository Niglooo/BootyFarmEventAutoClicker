
module nigloo.bfhotsauce {

	requires java.logging;
	
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.swing;
	
	requires jnativehook;
	requires org.apache.commons.lang3;
	requires java.desktop;
	
	opens nigloo.bfhotsauce														to javafx.graphics;
}