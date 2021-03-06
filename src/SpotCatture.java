

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 *
 * @author Gioele
 */
public class SpotCatture {
    public ImageView mappaLago;
    public VBox vb;
    public Label[] cattura;
    public ComboBox numeroSelezionato;
    private Label selezioneCattura;
    private HBox hboxSelezione;
    private int nmaxPesci;
    SpotCatture() {
           nmaxPesci=ParametriConfigurazioneXML.ottieniParametriConfigurazioneXML().numeroMassimoPesci;
           String s= "file:"+ParametriConfigurazioneXML.ottieniParametriConfigurazioneXML().pathImmagine;
           vb =new VBox();
           mappaLago=new ImageView(s);     
           mappaLago.setFitHeight(300);
           mappaLago.setFitWidth(300);
           cattura = new Label[nmaxPesci];
         
           hbox();
           vb.getChildren().addAll(mappaLago,hboxSelezione);
           for (int i=1;i<nmaxPesci+1;i++){
              cattura[i-1]=new Label (String.valueOf(i));
              cattura[i-1].setVisible(false);
              vb.getChildren().add(cattura[i-1]);
           }
           vb.setSpacing(10);
           hboxSelezione.setPadding(new Insets(10, 30, 10, 30));
    }
    private void hbox(){
        
        selezioneCattura= new Label ("Seleziona Cattura");
        ObservableList<Integer> opzioni= FXCollections.observableArrayList();
        selezioneCattura.setStyle("-fx-font-size: 20px;");
        for (int i=1;i<nmaxPesci+1;i++){
           opzioni.add(i);
        }
        numeroSelezionato= new ComboBox(opzioni);
        numeroSelezionato.setValue(1);
        hboxSelezione = new HBox(20);
        hboxSelezione.setSpacing(10);
        hboxSelezione.setPadding(new Insets(10, 30, 10, 30));
        hboxSelezione.getChildren().addAll(selezioneCattura,numeroSelezionato);
    }
    public void clickMappa(int i,double x, double y,boolean caricamento,String d){
        cattura[i].setTranslateX(x);
        cattura[i].setTranslateY(y);
        if(x!=0)
            cattura[i].setVisible(true);
        if(!caricamento){
            DatiCattura dc = null;
            int j;
            for (j = 0; j < nmaxPesci; j++) {
                dc=DepositoInformazioniCatture.getIstanza().listaCatture.get(i);
                if(dc.getData().equals("")){
                    j=-1;
                    break;
                }
                if(dc.getNumero()==i+1){
                    break;
                }
            }
            if(j!=-1){
                dc.setCoordinataX(x);
                dc.setCoordinataY(y);
                DepositoInformazioniCatture.getIstanza().listaCatture.set(i, dc);
            }else{
                cattura[i].setVisible(false);
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("ATTENZIONE");
                alert.setContentText("non puoi inserire una posizione per una cattura inesistente");
                alert.showAndWait();
            }
        }
        ParametriConfigurazioneXML p= ParametriConfigurazioneXML.ottieniParametriConfigurazioneXML();
        if(!caricamento){
            System.out.println("mando log click");
            ClientLogEventiXML.inviaLog("Click Mappa", p.IPServerLog, p.portaServerLog, p.IPClient);
        }
    }
    public void caricaPosizioni(String d){
        DatiCattura dc;
        numeroSelezionato.setValue(1);
        for(int i=0;i<nmaxPesci;i++){ 
            ObservableList<DatiCattura> ol =DepositoInformazioniCatture.getIstanza().listaCatture;           
            if(ol.isEmpty()){
                System.out.println("attenzione observable list vuota");
                return;
            }
            dc=ol.get(i);
            if(dc.getCoordinataX()==0)
                cattura[i].setVisible(false);
            clickMappa(dc.getNumero()-1,dc.getCoordinataX(),dc.getCoordinataY(),true,d);
        }
    }
}
