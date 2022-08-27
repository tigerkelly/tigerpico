package application;
	
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Main extends Application {
	
	private Pane mainPane = null;
	private TpGlobal tp = TpGlobal.getInstance();
	
	@Override
	public void start(Stage primaryStage) {
		Thread.setDefaultUncaughtExceptionHandler(Main::showError);
		
		try {
			primaryStage.setScene(createScene(loadMainPane()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop(){
	    System.out.println("TigerPico is Ending.");
	    // Save file
	    
	    SceneInfo si = tp.sceneNav.fxmls.get(tp.scenePeek());
	    if (si.controller instanceof RefreshScene) {
    		RefreshScene c = (RefreshScene)si.controller;
    		c.leaveScene();
		}
	}
	
	/**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
//    @SuppressWarnings("resource")
	private Pane loadMainPane() throws IOException {
		tp.Msg("*** Starting TigerPico ***");
		
        FXMLLoader loader = new FXMLLoader();

        mainPane = (Pane) loader.load(getClass().getResourceAsStream(SceneNav.MAIN));	// SceneNav

        SceneNavController mainController = loader.getController();

        tp.sceneNav.setMainController(mainController);
        tp.sceneNav.loadScene(SceneNav.PROGRAMOCD);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     *
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);

        scene.getStylesheets().setAll(getClass().getResource("application.css").toExternalForm());

        return scene;
    }
    
    private static void showError(Thread t, Throwable e) {
//		System.err.println("***Default exception handler***");
		if (Platform.isFxApplicationThread()) {
			if (e.getMessage().contains("Too many touch")) {
				e.printStackTrace();
				showErrorDialog(e);
			} else {
				System.out.println("Error: exception: " + e);
				e.printStackTrace();
			}
		} else {
			System.err.println("An unexpected error occurred in " + t);

		}
	}
	
	private static void showErrorDialog(Throwable e) {
        StringWriter errorMsg = new StringWriter();
        e.printStackTrace(new PrintWriter(errorMsg));
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Error.fxml"));
        try {
            Parent root = loader.load();
            ((ErrorController)loader.getController()).setErrorText(errorMsg.toString());
            dialog.setScene(new Scene(root, 1000, 700));
            dialog.show();
            
            Timer timer = new Timer();
            
            TimerTask task = new TimerTask() {
            	public void run() {
            		System.exit(1);
            	}
            };
            
            timer.schedule(task, 10000);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
