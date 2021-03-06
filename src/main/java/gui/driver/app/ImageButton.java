package gui.driver.app;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button {

	public void updateImages(final Image selected, final Image unselected) {
		final ImageView iv = new ImageView(selected);
		getChildren().add(iv);

		iv.setOnMousePressed(λ -> iv.setImage(unselected));
		iv.setOnMouseReleased(λ -> iv.setImage(selected));

		super.setGraphic(iv);
	}
}