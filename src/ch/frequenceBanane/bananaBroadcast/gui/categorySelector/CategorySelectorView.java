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

public class CategorySelectorView {
	
	@FXML Pane rootLayout;
	@FXML GridPane categorySelector;
	
	private final int nbColumnPerType = 2;
	private final CategorySelector selector;
	
	public CategorySelectorView(CategorySelector selector) throws IOException{
		this.selector = selector;
		GuiApp.loadLayout(this, "CategorySelector.fxml");
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
			double nbRows = Math.ceil(nbElements/(double)nbColumnPerType);
			
			for(int i=0; i<nbRows; i++) {
				for(int j=0; j<nbColumnPerType && j+i*nbRows < nbElements; j++) {
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
					
					categorySelector.add(label, j+curIndex*nbColumnPerType, i);
					GridPane.setHalignment(label, HPos.CENTER);
				}
			}
		}
	}
	
	public Pane getRootLayout() {
		return rootLayout;
	}
	
}
