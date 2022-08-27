package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class ProgramOcdController implements Initializable, RefreshScene {
	
	private TpGlobal tp = TpGlobal.getInstance();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnBaseDir;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnInterfaces;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnElf;

    @FXML
    private Button btnExecute;

    @FXML
    private Button btnOpenOcd;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbConfigs;
    
    @FXML
    private ComboBox<String> cbDebugOptions;

    @FXML
    private TextArea taConfigs;

    @FXML
    private TextArea taPaths;

    @FXML
    private TextArea taStatus;
    
    @FXML
    private TextField txtOpenOcd;

    @FXML
    private TextField txtElf;

    @FXML
    void doConfigs(ActionEvent event) {

    	String name = "cfg_" + cbConfigs.getValue();
		
		if (tp.ini.sectionExists(name) == true) {
			String elf = tp.ini.getString(name, "elf");
			String cfg = tp.ini.getString(name, "cfg");
			String ocd = tp.ini.getString(name, "ocd");
			String path = tp.ini.getString(name, "path");
		
			if (elf != null)
				txtElf.setText(elf);
			else
				txtElf.setText("");
			if (cfg != null)
				taConfigs.setText(cfg);
			else
				taConfigs.setText("");
			if (ocd != null)
				txtOpenOcd.setText(ocd);
			else
				txtOpenOcd.setText("");
			if (path != null)
				taPaths.setText(path);
			else
				taPaths.setText("");
		}
    }

    @FXML
    void doDelete(ActionEvent event) {
    	String name = "cfg_" + cbConfigs.getValue();
    	
    	if (tp.ini.sectionExists(name) == true) {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Confirmation Dialog");
    		alert.setHeaderText("Delete config: '" + cbConfigs.getValue() + "'.");
    		alert.setContentText("Are you ok with this?");

    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == ButtonType.OK){
    		    tp.ini.removeSection(name);
    		    
    		    tp.ini.writeFile(true);
    		    
				txtElf.setText("");
				taConfigs.setText("");
				txtOpenOcd.setText("");
				taPaths.setText("");
				
				loadCfgs();
    		}
    	}
    }

    @FXML
    void doElf(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().addAll(
		     new FileChooser.ExtensionFilter("ELF Files", "*.elf"),
		     new FileChooser.ExtensionFilter("All Files", "*.*")
		);
    	File sf = fc.showOpenDialog(btnElf.getScene().getWindow());
    	
    	if (sf != null) {
    		txtElf.setText(sf.getAbsolutePath());
    	}
    }

    @FXML
    void doExecute(ActionEvent event) {
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (tp.lbl != null && tp.lbl.isDisable() == false)
					tp.lbl.setText("Serial Port: CLOSED");
			}
		});
    	
    	String elf = txtElf.getText();
    	String cfg = taConfigs.getText();
    	String ocd = txtOpenOcd.getText();
    	String path = taPaths.getText();
    	String dbg = cbDebugOptions.getValue();
    	
    	String cmd = ocd;
    	
    	String cfgs = null;
    	
    	if (ocd.length() <= 0) {
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("Warning Dialog");
    		alert.setHeaderText("No Openocd program given.");
    		alert.setContentText("Select openocd program to use.");

    		alert.showAndWait();
    		
    		return;
    	}
    	
    	if (cfg.length() <= 0) {
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("Warning Dialog");
    		alert.setHeaderText("No config file given.");
    		alert.setContentText("Select config files to use with the '-f' option.");

    		alert.showAndWait();
    		
    		return;
    	}
    	
    	if (elf.length() <= 0) {
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("Warning Dialog");
    		alert.setHeaderText("No ELF file given.");
    		alert.setContentText("Select elf file to load into Pico.");

    		alert.showAndWait();
    		
    		return;
    	}
    	
    	for (String s : cfg.split("\s") ) {
    		if (cfgs == null)
    			cfgs = "-f " + s;
    		else
    			cfgs += " -f " + s;
    	}
    	
    	cmd += " " + cfgs;
    	
    	String paths = null;
    	
    	if (path != null && path.length() > 0) {
    		for (String p : path.split("\s") ) {
    			if (paths == null)
    				paths = " -s " + p;
    			else
    				paths += " -s " + p;
    		}
    	}
    	
    	if (paths != null)
    		cmd += paths;
    	
    	if (dbg != null && dbg.length() > 0) {
    		switch(dbg) {
    		case "Errors":
    			cmd += " -d0";
    			break;
    		case "Warnings":
    			cmd += " -d1";
    			break;
    		case "Informational":
    			cmd += " -d2";
    			break;
    		case "Debug":
    			cmd += " -d3";
    			break;
    		}
    	}
    	
    	cmd += " -c \"program " + elf + " verify reset exit\"";
    	
//    	System.out.println("bash -c " + cmd);
    	
    	taStatus.clear();
    	
    	ProcessBuilder processBuilder = new ProcessBuilder();
		
		processBuilder.command("bash", "-c", cmd);
		
		taStatus.setText(" \n");
		
		try {

			Process process = processBuilder.start();

//			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));

			String line;
			while ((line = reader.readLine()) != null) {
//				System.out.println("LINE " + line);
				final String txt = line;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						taStatus.appendText(txt + "\n");
//						taStatus.setScrollTop(Double.MAX_VALUE);
					}
				});
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
//				System.out.println("Success!");
//				System.out.println(output);
//				System.exit(0);
			} else {
				//abnormal...
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				taStatus.setScrollTop(Double.MAX_VALUE);
			}
		});
    }

    @FXML
    void doOpenOcd(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	File sf = fc.showOpenDialog(btnElf.getScene().getWindow());
    	
    	if (sf != null) {
    		txtOpenOcd.setText(sf.getAbsolutePath());
    	}
    }

    @FXML
    void doPaths(ActionEvent event) {

    }

    @FXML
    void doSave(ActionEvent event) {
    	String name = "cfg_" + cbConfigs.getValue();
		
		if (tp.ini.sectionExists(name) == true) {
			tp.ini.removeSection(name);
		}
		
		tp.ini.addSection(name);
		tp.ini.addValuePair(name, "elf", txtElf.getText());
		tp.ini.addValuePair(name, "cfg", taConfigs.getText());
		tp.ini.addValuePair(name, "ocd", txtOpenOcd.getText());
		tp.ini.addValuePair(name, "path", taPaths.getText());
		
		tp.ini.writeFile(true);
		
		loadCfgs();
    }

	@Override
	public void refreshScene() {
		
	}

	@Override
	public void leaveScene() {
		
	}

	@Override
	public void clickIt(String text, WidgetType widgetType) {
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert btnInterfaces != null : "fx:id=\"btnInterfaces\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert btnElf != null : "fx:id=\"btnElf\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert btnExecute != null : "fx:id=\"btnExecute\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert btnOpenOcd != null : "fx:id=\"btnOpenOcd\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert btnSave != null : "fx:id=\"btnSave\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert cbConfigs != null : "fx:id=\"cbConfigs\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert cbDebugOptions != null : "fx:id=\"cbDebugOptions\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert taConfigs != null : "fx:id=\"taConfigs\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert taPaths != null : "fx:id=\"taPaths\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert taStatus != null : "fx:id=\"taStatus\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert txtOpenOcd != null : "fx:id=\"txtOpenOcd\" was not injected: check your FXML file 'ProgramOcd.fxml'.";
        assert txtElf != null : "fx:id=\"txtElf\" was not injected: check your FXML file 'ProgramOcd.fxml'.";

        loadCfgs();
		
		cbDebugOptions.getItems().add("Errors");
		cbDebugOptions.getItems().add("Warnings");
		cbDebugOptions.getItems().add("Informational");
		cbDebugOptions.getItems().add("Debug");
		
		cbDebugOptions.getSelectionModel().select(2);
		
	}
	
	private void loadCfgs() {
		cbConfigs.getItems().clear();
		
		Object[] secs = tp.ini.getSectionNames();
		
		for (Object o : secs) {
			String s = (String)o;
			
			if (s.startsWith("cfg_"))
				cbConfigs.getItems().add(s.substring(4));
		}
	}

}



