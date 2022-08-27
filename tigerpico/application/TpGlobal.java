package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.rkw.IniFile;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class TpGlobal {

	private static TpGlobal singleton = null;
	
	private TpGlobal() {
		initGlobals();
	}
	
	private void initGlobals() {
		
		String iniFile = String.format("%s/tigerpico.ini", System.getProperty("user.home"));
		ini = new IniFile(iniFile);
		
		sceneNav = new SceneNav();
		
		File from = new File(System.getProperty("user.dir") + File.separator +
				"tigerpico.ini");
		
		File go = new File(System.getProperty("user.home") + File.separator +
				"TigerPico");
		
		if (go.exists() == false)
			go.mkdirs();
		
		File to = new File(go.getAbsolutePath() + File.separator + "tigerpico.ini");
		
		if (to.exists() == false) {
			try {
				copyFile(from, to);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ini = new IniFile(to.getAbsolutePath());
	}
	
	Alert alert = null;
	
	public String tpVersion = "1.0.0";
	
	public IniFile ini = null;
	
	public SceneNav sceneNav = null;
	
	public String subDir = null;
	
	public Label lbl = null;
	
	public static TpGlobal getInstance() {
		// return SingletonHolder.singleton;
		if (singleton == null) {
			synchronized (TpGlobal.class) {
				singleton = new TpGlobal();
			}
		}
		return singleton;
	}
	
	public String scenePeek() {
		if (sceneNav.sceneQue == null || sceneNav.sceneQue.isEmpty())
			return SceneNav.PROGRAMOCD;
		else
			return sceneNav.sceneQue.peek();
	}
	
	public void guiRestart(String msg) {
		String errMsg = String.format("A GUI error occurred.\r\nError loading %s\r\n\r\nRestarting GUI.", msg);
		showAlert("GUI Error", errMsg, AlertType.CONFIRMATION, false);
		System.exit(1);
	}
	
	public void loadSceneNav(String fxml) {
		if (sceneNav.loadScene(fxml) == true) {
			guiRestart(fxml);
		}
	}
	
	public void closeAlert() {
		if (alert != null) {
			alert.close();
			alert = null;
		}
	}
	
	public ButtonType showAlert(String title, String msg, AlertType alertType, boolean yesNo) {
		alert = new Alert(alertType);
		alert.getDialogPane().setPrefWidth(725.0);
		for ( ButtonType bt : alert.getDialogPane().getButtonTypes() ) {
		    Button button = ( Button ) alert.getDialogPane().lookupButton( bt );
		    if (yesNo == true) {
			    if (button.getText().equals("Cancel"))
			    	button.setText("No");
			    else if (button.getText().equals("OK"))
			    	button.setText("Yes");
		    }
		    button.setStyle("-fx-font-size: 28px;");
		    button.setPrefWidth(150.0);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);
		
		alert.setContentText(msg);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(
		   getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		
		ButtonType bt = alert.showAndWait().get();
		
		alert = null;
		
		return bt;
	}
	
	public void Msg(String msg) {
		System.out.println(msg);
	}
	
	@SuppressWarnings("resource")
	public void copyFile(File source, File dest) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destChannel.close();
		}
	}
}
