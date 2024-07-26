package catfan7.catclicker2.catclicker2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable, Runnable {

    private static Controller instance;

    static boolean running = true;
    static boolean idleMode = false;

    public static long points = 0;
    static long rate = 1;

    Controller obj;
    Thread thread;
    Stage stage;

    @FXML
    VBox frame;
    @FXML
    Button pointButton = new Button();
    @FXML
    CheckBox idleSwitch = new CheckBox();
    @FXML
    Label rates = new Label();

    //On window appearing
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        obj = new Controller();
        thread = new Thread(obj);
        thread.start();
    }

    //One second looping thread
    public void run() {
        while (running) {
            System.out.print("");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (idleMode) {
                incrementPoints();
            }
        }
    }
//Reset functions
    public void win() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You Win!");
        alert.setHeaderText("Goal Reached:");
        alert.setContentText("1 Quadrillion Points!");
        alert.showAndWait();
        resetValues();
    }

    public void reset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset");
        alert.setHeaderText("You're about to reset your progress!");
        alert.setContentText("Are you sure you sure to want to reset?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            resetValues();
        }
    }

    public void resetValues() {
        points = 0;
        rates.setText(formatNumber(rate)  + " per click");
        setRateText(1);
        updatePoints();
    }

    //Exit warnings
    public void logoutButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Have you saved your game?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            running = false;
            stage = (Stage) frame.getScene().getWindow();
            stage.close();
            System.exit(0);
        }
    }

    public static void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Make sure you've saved your game.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            running = false;
            stage.close();
            System.exit(0);
        }
    }

    //Something I found on stack overflow
    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:"));
        fileChooser.setInitialFileName("Catclicker-" + LocalDate.now() + "_" + LocalTime.now() + ".txt");

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(frame.getScene().getWindow());

        if (file != null) {
            saveTextToFile("Catclicker." + points + "." + rate, file);
        }
    }

    public void load() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:"));
        fileChooser.setTitle("Choose a File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(frame.getScene().getWindow());

        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            Scanner myReader = new Scanner(selectedFile);
            String data = myReader.nextLine();
            String[] FileInfo = data.split("\\.");
            points = Long.parseLong(FileInfo[1]);
            setRateText(Long.parseLong(FileInfo[2]));
            updatePoints();
        }
    }

    //Number Formatting
    public String formatNumber(long num) {
        double amount = (double) num;
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormat df = new DecimalFormat("#.###");
        if (num > 1000000000000000L) {
            return df.format(((double) num) /1000000000000000L) + "Q";
        } else if (num > 1000000000000L) {
            return df.format(((double) num) /1000000000000L) + "T";
        } else if (num > 1000000000L) {
            return df.format(((double) num) /1000000000) + "B";
        } else if (num > 1000000) {
            return df.format(((double) num) /1000000) + "M";
        } else if (num > 1000) {
            return df.format(((double) num) /1000) + "K";
        } else {
            return formatter.format(amount);
        }
    }

    //Points Update
    public static void updateButtonText(String newText) {
        Platform.runLater(() -> {
            if (instance != null && instance.pointButton != null) {
                instance.pointButton.setText(newText);
            }
        });
    }

    public void updatePoints() {
        if (running) {
            updateButtonText(instance.formatNumber(points));
        }
        if (points > 1000000000000000L) {
            win();
        }
    }

    public void incrementPoints() {
            points += Controller.rate;
            updatePoints();
    }

    //Idle Mode
    public void setIdle() {
        idleMode = idleSwitch.isSelected();
        pointButton.setDisable(idleMode);
        setRateText(rate);
    }

    //Rates
    public void changeRate(long add, long price) {
        if (points >= price) {
            setRateText(rate + add);
            points -= price;
            updatePoints();
        }
    }

    public void setRateText(long rateText) {
        rate = rateText;
        if (idleMode) {
            rates.setText(formatNumber(rateText) + "/s");
        } else {
            rates.setText(formatNumber(rateText)  + " per click");
        }
        System.out.print("");
    }

    //Purchasing button
    //Row 1
    public void add1() {
        changeRate(1, 10);
    }
    public void add5() {
        changeRate(5, 50);
    }
    public void add25() {
        changeRate(25, 250);
    }
    public void add100() {
        changeRate(100, 13000);
    }
    //Row 2
    public void add500() {
        changeRate(500, 64000);
    }
    public void add2500() {
        changeRate(2500, 128000);
    }
    public void add10k() {
        changeRate(10000, 250000);
    }
    public void add50k() {
        changeRate(50000, 1000000);
    }
    //Row 3
    public void add250k() {
        changeRate(250000, 5000000);
    }
    public void add1m() {
        changeRate(1000000, 25000000);
    }
    public void add5m() {
        changeRate(5000000, 100000000);
    }
    public void add25m() {
        changeRate(25000000, 500000000);
    }
    //Row 4
    public void add100m() {
        changeRate(100000000, 2500000000L);
    }
    public void add500m() {
        changeRate(500000000, 12500000000L);
    }
    public void add2500m() {
        changeRate(2500000000L, 50000000000L);
    }
    public void add10b() {
        changeRate(10000000000L, 200000000000L);
    }
    //Row 5
    public void add50b() {
        changeRate(50000000000L, 1000000000000L);
    }
    public void add250b() {
        changeRate(250000000000L, 10000000000000L);
    }
    public void add1t() {
        changeRate(1000000000000L, 50000000000000L);
    }
    public void add5t() {
        changeRate(5000000000000L, 200000000000000L);
    }
}