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
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Create a view of list holding AudioFIles
 * 
 * @author Corentin
 * @author corentin.junod@epfl.ch
 * @param <AudioType> AudioFIle or one of its subtypes
 */
public class AudioFileListView {

	@FXML
	private VBox rootLayout;
	@FXML
	private TableView<AudioFile> tableView;

	private final Playlist<AudioFile> playlist;

	/**
	 * Instantiate the view of the list
	 * 
	 * @param playlist         The playlist linked with the view
	 * @param getAudioFileData A function describing how to retrieve datas from the
	 *                         given AudioType
	 * @throws IOException If an error occurs during the layout file reading
	 */
	public AudioFileListView(final Playlist<AudioFile> playlist) throws IOException {
		this.playlist = playlist;
		GuiApp.loadLayout(this, "TableView.fxml");
	}

	@FXML
	public void initialize() {
		rootLayout.getStylesheets().add("css/listFilter.css");

		playlist.setOnChange(() -> {
			Platform.runLater(() -> {
				updateView();
			});
		});
		updateView();
	}

	/**
	 * Give a consumer triggered every time an element on the list is double clicked
	 * 
	 * @param consumer the consumer to trigger. Its parameters is the element in the
	 *                 l list on which the double click has occurred
	 */
	public void setOnElementDoubleClick(final Consumer<AudioFile> consumer) {
		tableView.setOnMouseClicked(event -> {
			AudioFile selected = tableView.getSelectionModel().getSelectedItem();
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && selected != null) {
				consumer.accept(selected);
			}
		});
	}

	// C'est bien la pire maniere de peupler un tableau que j'ai jamais vu.
	@SuppressWarnings("unchecked")
	public void updateView() {

		tableView.setItems(FXCollections.observableList(playlist.getList()));
		ObservableList<?> columnList = tableView.getColumns();

		TableColumn<AudioFile, String> col;
		for (int i = 0; i < getAudioFileData().size(); i++) {
			final int index = i;
			col = (TableColumn<AudioFile, String>) columnList.get(index);
			col.setCellValueFactory(new Callback<>() {
				public ObservableValue<String> call(CellDataFeatures<AudioFile, String> p) {
					return new ReadOnlyObjectWrapper<>(getAudioFileData().get(index).apply(p.getValue()));
				}
			});
		}
		
		tableView.setRowFactory(tv -> {
	        TableRow<AudioFile> row = new TableRow<AudioFile>();
	        row.setOnDragOver(evt -> {
	            if (evt.getDragboard().hasString()) {
	                evt.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	            }
	            evt.consume();
	        });
	        
	       /* row.setOnDragDropped(evt -> {
	            Dragboard db = evt.getDragboard();
	            if (db.hasString()) {
	                Item item = new Item(db.getString());
	                if (row.isEmpty()) {
	                    // row is empty (at the end -> append item)
	                    table.getItems().add(item);
	                } else {
	                    // decide based on drop position whether to add the element before or after
	                    int offset = evt.getY() > row.getHeight() / 2 ? 1 : 0;
	                    table.getItems().add(row.getIndex() + offset, item);
	                    evt.setDropCompleted(true);
	                }
	            }
	            evt.consume();
	        });*/
	        
	        row.setOnMouseDragged(evt -> System.out.println("DRAGGED"));
	        
	        return row;
	    });
	}

	/**
	 * Enable or disable the action of sorting the list by clicking on the column
	 * headers
	 * 
	 * @param isSortable The columns can be sorted if and only if the value is true
	 */
	public void setSortable(final boolean isSortable) {
		tableView.getColumns().forEach((elem) -> elem.setSortable(isSortable));
	}

	public VBox getRootLayout() {
		return rootLayout;
	}
	
	public Playlist<AudioFile> getPlaylist() {
		return playlist;
	}

	private ArrayList<Function<AudioFile, String>> getAudioFileData() {
		return new ArrayList<>(
			Arrays.asList(
					(e) -> e.title, 
					(e) -> e.artist, 
					(e) -> String.valueOf(e.duration),
					(e) -> String.join(",", e.categories))
			);
	};
}
