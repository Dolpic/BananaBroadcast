package ch.frequenceBanane.bananaBroadcast.gui.playlist;

import java.io.IOException;
import java.util.*;
import java.util.function.*;

import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.scheduling.Playlist;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class AudioFileListView<AudioType extends AudioFile> {

	@FXML private VBox rootLayout;
	@FXML private TableView<AudioType> tableView;
	
	ArrayList<Function<AudioType, String>> getAudioFileData;
	private Playlist<AudioType> playlist;
	
	public AudioFileListView(Playlist<AudioType> playlist, ArrayList<Function<AudioType, String>> getAudioFileData) throws IOException {
		this.playlist  = playlist;
		this.getAudioFileData = getAudioFileData;
		GuiApp.loadLayout(this, "TableView.fxml");
	}
	
	@FXML
    public void initialize() { 
		rootLayout.getStylesheets().add("ressources/css/listFilter.css");
		
		playlist.setOnChange(() -> {
			Platform.runLater( () -> {
				updateView();
			});
		});
		updateView();
	}
	
	public void setOnElementDoubleClick(Consumer<AudioFile> consumer) {		
		tableView.setOnMouseClicked(event -> {
			AudioFile selected = tableView.getSelectionModel().getSelectedItem();
	        if (event.getButton() == MouseButton.PRIMARY && 
	        	event.getClickCount() == 2 && selected != null) {
	        	consumer.accept(selected);
	        }
		});
	}
	
	// C'est bien la pire manière de peupler un tableau que j'ai jamais vu.
	@SuppressWarnings("unchecked")
	public void updateView(){
		
		tableView.setItems(FXCollections.observableList(playlist.getList()));
		//C'est dégoutant
		ObservableList<?> columnList = tableView.getColumns();
		
		TableColumn<AudioType, String> col;
		for(int i=0; i<getAudioFileData.size(); i++) {
			final int index = i;
			//Quel enfer
			col = (TableColumn<AudioType, String>) columnList.get(index);
			col.setCellValueFactory(new Callback<>() {
				public ObservableValue<String> call(CellDataFeatures<AudioType, String> p) {
					return new ReadOnlyObjectWrapper<>(getAudioFileData.get(index).apply(p.getValue()));
				}
			});
		}
	}

	public VBox getRootLayout() {
		return rootLayout;
	}
	
	public static ArrayList<Function<Music, String>> getMusicData(){
		return new ArrayList<>(Arrays.asList(
			(e) -> e.title,
			(e) -> e.artist,
			(e) -> String.valueOf(e.duration),
			(e) -> String.join(",", e.categories)
		));
	};
	
	public static ArrayList<Function<AudioFile, String>> getAudioData(){
		return new ArrayList<>(Arrays.asList(
			(e) -> e.title,
			(e) -> String.valueOf(e.duration)
		));
	};
}
