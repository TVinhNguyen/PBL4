package Controller_UI;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;

import Controller.Client;
import Interface.Hover;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ControllerGame extends BaseController implements Hover {
	

	private Client client = Client.getInstance();

    @FXML
    private AnchorPane content; 
    @FXML
    private Label remaining_hours;
    @FXML
    private Label remaining_money;
  
	@FXML
	private Label fare;
	@FXML
	private Label nameUser;
	
	@FXML private Slider xpSlider;
	 
	@FXML private Label xpLabel;
	 
	@FXML 
	public void initialize() {
	double a = this.client.getUser().getBalance();
    double b = this.client.getComputer().getHourlyRate();
    xpLabel.textProperty().bind(xpSlider.valueProperty().asString("%.0fXP"));

    
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    String remainingMoneyText = currencyFormat.format(a).replaceAll("[^0-9,.]", "").trim();
    String hourlyRateText = currencyFormat.format(b).replaceAll("[^0-9,.]", "").trim() + "đ/h"; 
    
    long remainingHours =  this.client.getUser().getTimeRemain();
    
    String remainingHoursText = formatRemainTime(Duration.ofSeconds(remainingHours));
    
    this.nameUser.setText(this.client.getUser().getUsername());
    this.remaining_money.setText(remainingMoneyText);
    this.fare.setText(hourlyRateText);
    this.remaining_hours.setText(remainingHoursText);
    xpSlider.setValue(this.client.getUser().getPoints());
    double sliderMax = xpSlider.getMax();
    xpLabel.translateXProperty().bind(xpSlider.valueProperty().multiply(320 / sliderMax)); 

    xpLabel.textProperty().bind(xpSlider.valueProperty().asString("%.0fXP"));
}


   
    @FXML
    public void DepositMoney(ActionEvent e) {
        showConfirmDepositMoney();
        System.out.println("run");
    }
    
   
    private void showConfirmDepositMoney() {
    	  	Stage confirmStage = new Stage();
	        confirmStage.initModality(Modality.APPLICATION_MODAL); 
	        confirmStage.initStyle(StageStyle.UNDECORATED); 

	        DepositMoneyPage confirmPage = new DepositMoneyPage();
	        Scene scene = confirmPage.createScene();
	        confirmStage.setScene(scene);
	        confirmStage.setTitle("Nạp tiền");
	        confirmStage.setResizable(false);  
	        confirmStage.showAndWait();
	        try {
				Thread.sleep(1000);
				String kq;
				if((kq=client.receiMessage())!="") {
					NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
					String[] parts = kq.split(",");
					String responese = parts[0];
					if(responese.equals("SUCCESS")) {
					String remainingMoney = parts[1];
				    String remainingMoneyText = currencyFormat.format(Double.valueOf(remainingMoney)).replaceAll("[^0-9,.]", "").trim();
				    this.client.getUser().setBalance(Double.valueOf(remainingMoney));
				    remaining_money.setText(remainingMoneyText);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
	        
    }
    @FXML
    public void handleNavigateButtonAction(MouseEvent event) {
        		Node clickedNode = (Node) event.getSource();
        	    String elementId = clickedNode.getId();
                String hbox = elementId.length() > 5 ? elementId.substring(0, 5) : elementId;
    	        switch (hbox) {
    	            case "hbox1":
    	                controllerBar.triggerPackageButtonAction();
    	                break;
    	            case "hbox2":
    	                controllerBar.triggerNewButtonAction();
    	                break;
    	            default:
    	                System.out.println("Không có màn hình phù hợp cho HBox với ID: " );
    	                break;
    	        }
   } 
        
    
    @Override
	public void hoverHbox(MouseEvent e) {
		 	HBox hbox = (HBox) e.getSource(); 
		    Button button = (Button) hbox.lookup("#" + hbox.getId() + "button"); 
		    FontAwesomeIcon icon = (FontAwesomeIcon) hbox.lookup("#" + hbox.getId() + "arrowIcon"); 
		    createColorTransition(button, icon, Color.RED);		
	}

	@Override
	public void exitedHBox(MouseEvent e) {
		 HBox hbox = (HBox) e.getSource(); 
		    Button button = (Button) hbox.lookup("#" + hbox.getId() + "button"); 
		    FontAwesomeIcon icon = (FontAwesomeIcon) hbox.lookup("#" + hbox.getId() + "arrowIcon"); 
		    createColorTransition(button, icon, Color.WHITE);
		
	}

	@Override
	public void createColorTransition(Button button, FontAwesomeIcon icon, Color targetColor) {
		 Timeline timeline = new Timeline();

		    int frames = 10; 
		    for (int i = 0; i <= frames; i++) {
		        final double fraction = i / (double) frames; 
		        KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(i * 0.05), event -> {
		       
		            Color currentTextColor = (Color) button.getTextFill();
		            Color newTextColor = interpolateColor(currentTextColor, targetColor, fraction);
		            button.setTextFill(newTextColor);

		            Color currentIconColor = (Color) icon.getFill();
		            Color newIconColor = interpolateColor(currentIconColor, targetColor, fraction);
		            icon.setFill(newIconColor);
		        });
		        timeline.getKeyFrames().add(keyFrame);
		    }
		    timeline.play();		
	}

	@Override
	public Color interpolateColor(Color startColor, Color endColor, double fraction) {
		 double red = startColor.getRed() + (endColor.getRed() - startColor.getRed()) * fraction;
		    double green = startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * fraction;
		    double blue = startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * fraction;
		    return Color.color(red, green, blue);
	}
	private String formatRemainTime(Duration duration) {
	    long totalSeconds = duration.getSeconds();
	    long days = totalSeconds / (24 * 3600); // Tổng số ngày
	    long hours = (totalSeconds % (24 * 3600)) / 3600; // Giờ còn lại
	    long minutes = (totalSeconds % 3600) / 60; // Phút còn lại
	    long seconds = totalSeconds % 60; // Giây còn lại
	    if (days > 0) {
	        return String.format("%d ngày %02d:%02d:%02d", days, hours, minutes, seconds);
	    } else {
	        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    }
	}
	public void setRemainHour(String remainingHours) {
		Duration remainTime = Duration.ofSeconds(Long.valueOf(remainingHours));
		this.client.getUser().setTimeRemain(Long.valueOf(remainingHours));;

		remainingHours = formatRemainTime(remainTime);

		this.remaining_hours.setText(remainingHours);
	}
}