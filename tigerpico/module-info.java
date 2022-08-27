module tigerpico {
	requires javafx.controls;
	requires inifile8;
	requires javafx.fxml;
	requires log4j;
	
	opens application to javafx.graphics, javafx.fxml;
}
