package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import it.polito.tdp.extflightdelays.model.Rotta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//controller turno A --> switchare ai branch master_turnoB o master_turnoC per turno B o C

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextField distanzaMinima;

    @FXML
    private Button btnAnalizza;

    @FXML
    private ComboBox<Airport> cmbBoxAeroportoPartenza;

    @FXML
    private Button btnAeroportiConnessi;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    private Button btnCercaItinerario;

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	this.txtResult.clear();
    	
    	try {
    		Double distanza = Double.valueOf(this.distanzaMinima.getText());
    		model.creaGrafo(distanza);
    		
    		this.cmbBoxAeroportoPartenza.getItems().clear();
    		this.cmbBoxAeroportoPartenza.getItems().addAll(model.vertici());
    		this.cmbBoxAeroportoPartenza.setValue(model.vertici().get(0));
    		
    		this.txtResult.appendText(String.format("Grafo creato!\n#vertici: %d\n#archi: %d",model.nVertici(), model.nArchi()));
    	
    	}catch(NumberFormatException nfe) {
    		this.txtResult.appendText("Inserisci valore corretto");
    	}

    }

    @FXML
    void doCalcolaAeroportiConnessi(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Airport a = this.cmbBoxAeroportoPartenza.getValue();
    	
    	if(model.adiacenti(a).size()>0) {
    		this.txtResult.appendText("Aeroporti connessi a "+a+"\n");
    		for(Rotta r: model.adiacenti(a)) {
    			this.txtResult.appendText(r.toString()+"\n");
    		}
    	}else {
    		this.txtResult.appendText("Nessun adiacente");
    	}

    }

    @FXML
    void doCercaItinerario(ActionEvent event) {
    	this.txtResult.clear();
    	
    	try {
    		Integer distanza = Integer.valueOf(this.numeroVoliTxtInput.getText());
    		Airport a = this.cmbBoxAeroportoPartenza.getValue();
    		this.txtResult.appendText("Itinerario con "+distanza+" miglia:\n");
    		for(Airport air : model.cerca(distanza, a)) {
    			this.txtResult.appendText(air+"\n");
    		}
    		this.txtResult.appendText("\n\nDistanza percorsa: "+model.getDistanzaTot());
    	}
    	catch(NumberFormatException nfe) {
    		this.txtResult.appendText("Inserisci valore corretto");
    	}

    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnCercaItinerario != null : "fx:id=\"btnCercaItinerario\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
	}
}
