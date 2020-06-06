package ch.frequenceBanane.bananaBroadcast.gui.categorySelector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ch.frequenceBanane.bananaBroadcast.categories.CategorySelector;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Create a table filled with the different categories
 * They are activable on/off. This enables the user to have a selection of categories
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class CategorySelectorView {
	
	@FXML Pane rootLayout;
	@FXML GridPane categorySelector;
	
	private final int NB_COLUMN_PER_TYPE = 2;
	private final CategorySelector selector;
	
	public CategorySelectorView(CategorySelector selector) throws IOException{
		this.selector = selector;
		GuiApp.loadLayout(this, "categorySelector/CategorySelector.fxml");
	}
	
	@FXML
    public void initialize() {
		
		ArrayList<ArrayList<String>> categories = new ArrayList<>(Arrays.asList(
			selector.musicsCategories,
			selector.jinglesCategories,
			selector.cartouchesCategories,
			selector.tapisCategories
		));
		
		for(int curIndex=0; curIndex<categories.size(); curIndex++) {
			ArrayList<String> currentCategories = categories.get(curIndex);
			
			int nbElements = currentCategories.size();
			double nbRows = Math.ceil(nbElements/(double)NB_COLUMN_PER_TYPE);
			
			for(int i=0; i<nbRows; i++) {
				for(int j=0; j<NB_COLUMN_PER_TYPE && j+i*nbRows < nbElements; j++) {
					Label label = new Label(currentCategories.get((int)(j+i*nbRows)));
					
					label.setOnMouseClicked(event -> {
						String curCategory = label.textProperty().getValue();
						if(selector.isSelectedCategory(curCategory)) {
							selector.removeSelectedCategory(curCategory);
							label.setTextFill(Color.BLACK);
						}else {
							selector.addSelectedCategory(curCategory);
							label.setTextFill(Color.RED);
						}
					});
					
					categorySelector.add(label, j+curIndex*NB_COLUMN_PER_TYPE, i);
					GridPane.setHalignment(label, HPos.CENTER);
				}
			}
		}
	}
	
	/** @return The base layout of the object */
	public Pane getRootLayout() {
		return rootLayout;
	}
}
