package WhereToWalk;

import java.net.URL;
import java.io.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.geometry.*;

import javafx.event.*;
import javafx.scene.input.*;

import javafx.fxml.FXMLLoader;

public class Window extends Application
{
    private final int mWidth = 480;
    // private final float aspect = 9.f/20.f;
    private final int mHeight = 800;

    Parent landingPageParent;
    Parent hillRecommendationsParent;
    Parent hillPageParent;

    Scene landingPage;
    Scene hillRecommendations;
    Scene hillPage;

    Scene mainScene;

    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws java.io.IOException
    {
        // landingPageParent = FXMLLoader.load(getClass().getResource("fxml/landing_page.fxml"));
        hillRecommendationsParent = FXMLLoader.load(getClass().getResource("fxml/hill_recommendations.fxml"));
        hillPageParent = FXMLLoader.load(getClass().getResource("fxml/hill_page.fxml"));

        loadLandingPage(primaryStage);

        primaryStage.setResizable(false);

        // mainScene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
        //     public void handle(KeyEvent event)
        //     {
        //         if (event.getCode() == KeyCode.ESCAPE)
        //         {
        //             primaryStage.close();
        //         }
        //         event.consume();
        //     }
        // });

    }

    public void loadLandingPage(Stage primaryStage) throws java.io.IOException
    {
        landingPageParent = FXMLLoader.load(getClass().getResource("fxml/landing_page.fxml"));

        landingPage = new Scene(landingPageParent);
        primaryStage.setScene(landingPage);
        primaryStage.show();


        // ScrollPane = (ScrollPane)FXMLLoader.
        ScrollPane hillScroller = (ScrollPane)landingPageParent.lookup("#HillButtonScroller");

        VBox buttons = (VBox)hillScroller.lookup("#HillButtonScrollerVBOX");
        buttons.getChildren().clear();
        for (int i = 0; i < 4; i++)
        {
            landingPageParent = FXMLLoader.load(getClass().getResource("fxml/landing_page.fxml"));
            Button hillButton = (Button)landingPageParent.lookup("#HillButton");
            hillButton.setLayoutX(0);
            hillButton.setLayoutY(0);
            hillButton.setId("AutoButton" + i);

            hillButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event)
                {
                    try
                    {
                        loadHillPage(primaryStage);
                    } catch(Exception e) {}
                }
            });

            buttons.getChildren().add(hillButton);
        }

        hillScroller.setContent(buttons);
    }

    public void loaddHillRecommendations(Stage primaryStage)
    {
    }

    public void loadHillPage(Stage primaryStage) throws java.io.IOException
    {
        hillPageParent = FXMLLoader.load(getClass().getResource("fxml/hill_page.fxml"));
        hillPage = new Scene(hillPageParent);

        primaryStage.setScene(hillPage);
        primaryStage.show();

        Button closeButton = (Button)hillPageParent.lookup("#CloseButton");

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event)
            {
                try
                {
                    loadLandingPage(primaryStage);
                } catch (Exception e) {}
            }
        });
    }
}
