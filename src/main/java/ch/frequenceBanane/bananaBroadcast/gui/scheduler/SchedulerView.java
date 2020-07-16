package ch.frequenceBanane.bananaBroadcast.gui.scheduler;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.scheduling.Scheduler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SchedulerView {

	@FXML
	private GridPane plannerGrid;
	@FXML
	private GridPane plannerGridDefault;

	@FXML
	private Label plannerLabel;
	@FXML
	private Label plannerLabelDefault;

	@FXML
	private Label rightTitleLabel;
	@FXML
	private Label rightTitleLabelDefault;

	@FXML
	private Button weekBackButton;
	@FXML
	private Button weekForwardButton;

	@FXML
	private Button saveButton;
	@FXML
	private Button saveButtonDefault;

	@FXML
	private VBox categoriesList;
	@FXML
	private VBox categoriesListDefault;

	private Scheduler scheduler;

	private Label[][] gridLabels;
	private Label[][] gridLabelsDefault;
	private Pane[][] gridPanes;
	private Pane[][] gridPanesDefault;

	private int selectedPaneDay;
	private int selectedPaneHour;

	private LocalDate selectedWeekMonday;
	private ArrayList<String> currentCategoriesSelection;

	private final int GRID_MARGIN_LEFT = 1;
	private final int GRID_MARGIN_TOP = 2;
	private final int GRID_DEFAULT_MARGIN_LEFT = 1;
	private final int GRID_DEFAULT_MARGIN_TOP = 1;

	public SchedulerView(Scheduler scheduler) throws IOException {
		gridLabels = new Label[7][24];
		gridLabelsDefault = new Label[7][24];
		gridPanes = new Pane[7][24];
		gridPanesDefault = new Pane[7][24];

		this.scheduler = scheduler;
		selectedWeekMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

		Stage schedulerStage = new Stage();
		schedulerStage.setTitle("Scheduler");
		schedulerStage.setScene(new Scene(GuiApp.loadLayout(this, "Scheduler.fxml")));

		setButtonsEvents();
		schedulerStage.show();
	}

	@FXML
	public void initialize() {
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				Label curLabel = new Label();
				Pane curPane = new Pane();
				curPane.getChildren().add(curLabel);
				gridLabels[day][hour] = curLabel;
				gridPanes[day][hour] = curPane;
				setCaseClicAction(curPane, gridPanes, categoriesList, false);
				plannerGrid.add(curPane, GRID_MARGIN_LEFT + day, GRID_MARGIN_TOP + hour);

				Label curLabelDefault = new Label();
				Pane curPaneDefault = new Pane();
				curPaneDefault.getChildren().add(curLabelDefault);
				gridLabelsDefault[day][hour] = curLabelDefault;
				gridPanesDefault[day][hour] = curPaneDefault;
				setCaseClicAction(curPaneDefault, gridPanesDefault, categoriesListDefault, true);
				plannerGridDefault.add(curPaneDefault, GRID_DEFAULT_MARGIN_LEFT + day, GRID_DEFAULT_MARGIN_TOP + hour);
			}
		}
		updatePlanner();
		updateDefaultPlanner();
	}

	private void updatePlanner() {
		LocalDate sunday = selectedWeekMonday.plusDays(6);

		DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		plannerLabel.setText("Semaine du " + formater.format(selectedWeekMonday) + " au " + formater.format(sunday));

		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				ArrayList<String> curDayCategories = scheduler.getPlanningOf(selectedWeekMonday.plusDays(day), hour);
				String content = String.join(", ", curDayCategories);
				gridLabels[day][hour].setText(content);
			}
		}
	}

	private void updateDefaultPlanner() {
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				ArrayList<String> curDayCategories = scheduler.getDefaultPlanningOf(day, hour);
				String content = String.join(", ", curDayCategories);
				gridLabelsDefault[day][hour].setText(content);
			}
		}
	}

	private void setCaseClicAction(Pane pane, Pane[][] gridPane, VBox catList, boolean isDefaultPlanning) {
		pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				for (int day = 0; day < 7; day++) {
					for (int hour = 0; hour < 24; hour++) {
						gridPane[day][hour].setStyle("-fx-background-color:none");

						if (gridPane[day][hour] == pane) {
							if (isDefaultPlanning)
								currentCategoriesSelection = scheduler.getDefaultPlanningOf(day, hour);
							else
								currentCategoriesSelection = scheduler.getPlanningOf(selectedWeekMonday.plusDays(day),
										hour);

							selectedPaneDay = day;
							selectedPaneHour = hour;

							catList.getChildren().clear();
							createCheckboxesWithCategories(scheduler.getCategories(), catList);
						}
					}
				}
				pane.setStyle("-fx-background-color:red");
				selectPane(pane);
			}
		});
	}

	private void createCheckboxesWithCategories(ArrayList<String> categories, VBox container) {
		for (int i = 0; i < categories.size(); i++) {
			CheckBox checkbox = new CheckBox();
			checkbox.setText(categories.get(i));
			setCheckboxEvent(checkbox);
			if (currentCategoriesSelection.contains(categories.get(i))) {
				checkbox.setSelected(true);
			}
			container.getChildren().add(checkbox);
		}
	}

	private void selectPane(Pane pane) {
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd.MM");
		LocalDate curDate = selectedWeekMonday.plusDays(selectedPaneDay);
		String curHour = selectedPaneHour + ":00";
		rightTitleLabel.setText(formater.format(curDate) + " - " + curHour);
	}

	private void setButtonsEvents() {
		weekBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				selectedWeekMonday = selectedWeekMonday.minusDays(7);
				updatePlanner();
			}
		});

		weekForwardButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				selectedWeekMonday = selectedWeekMonday.plusDays(7);
				updatePlanner();
			}
		});

		saveButtonDefault.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				scheduler.setDefaultPlanningOf(selectedPaneDay, selectedPaneHour, currentCategoriesSelection);
				updateDefaultPlanner();
			}
		});

		saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				scheduler.setPlanningOf(selectedWeekMonday.plusDays(selectedPaneDay), selectedPaneHour,
						currentCategoriesSelection);
				updatePlanner();
			}
		});
	}

	private void setCheckboxEvent(CheckBox checkbox) {
		checkbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (checkbox.isSelected()) {
					if (!currentCategoriesSelection.contains(checkbox.getText()))
						currentCategoriesSelection.add(checkbox.getText());
				} else {
					if (currentCategoriesSelection.contains(checkbox.getText()))
						currentCategoriesSelection.remove(checkbox.getText());
				}
			}
		});
	}

}
