package backEnd;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import middleWare.DatiCattura;
/**
 *
 * @author Gioele
 */
public class DepositoInformazioniCatture {
    public ObservableList<DatiCattura> listaCatture;
    private final String queryInserimentoCattura = "INSERT INTO tabellacatture(data,cattura,peso,tecnica,esca,"
            + "                                 coordinataX,coordinataY) VALUES(?,?,?,?,?,?,?) ";
    private final String queryCaricamentoCatture = "SELECT * FROM tabellacatture WHERE data= ?";
    private final String queryModificaCattura = "UPDATE tabellacatture SET peso=?, "
                                        + "tecnica=?, esca=? where codicecattura=?";
    private final String queryPersonalBest = "SELECT max(peso) as personal FROM tabellacatture";
    private final String queryBestBag= "SELECT max(bag) as bestbag FROM bagpergiorno";
    private final String queryCurrentBag= "SELECT bag FROM bagpergiorno WHERE data=?";
    private final String queryPercentuale= "SELECT * FROM percentuali";
    public Text personal;
    public Text current;
    public Text best;
    
    
    private static DepositoInformazioniCatture istanza;
    
    
    public DepositoInformazioniCatture(){
        listaCatture= FXCollections.observableArrayList();
        personal = new Text();
        current = new Text();
        best = new Text();
    }
    public static DepositoInformazioniCatture getIstanza(){
        if (istanza==null){
            istanza=new DepositoInformazioniCatture();
        }
        return istanza;
    }
    public int inserisciCattura(DatiCattura dc) { 
        try( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybass","root","");
            PreparedStatement st = co.prepareStatement(queryInserimentoCattura);
        ) {
            st.setDate(1,Date.valueOf(dc.getData()));
            st.setInt(2, dc.getNumero());
            st.setString(3,dc.getPeso());
            st.setString(4, dc.getTecnica());
            st.setString(5, dc.getEsca());
            st.setDouble(6,dc.getCoordinataX());
            st.setDouble(7,dc.getCoordinataY());
            st.setInt(2, dc.getCodiceCattura());
            st.executeUpdate();

            return 1;
        } catch (SQLException ex) {
            System.out.println("Errore di inserimento di una cattura ");
            return -1;
        }        
    }
    /*"SELECT * FROM tabellacatture WHERE data="+d);//(*/
    public void caricaCatture(String d) {
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybass","root","");
            PreparedStatement st = co.prepareStatement(queryCaricamentoCatture);
        ) { 
            st.setString(1,d);
            ResultSet rs = st.executeQuery();
            System.out.println("data parametro"+d);              
            Integer i;
            i=0;
            listaCatture.clear();
            while (rs.next()) {
               System.out.println("data parametro"+d); 
               listaCatture.add(new DatiCattura( rs.getInt("codicecattura"),rs.getInt("cattura"),rs.getString("esca"), 
               rs.getString("data"),rs.getString("tecnica"),rs.getString("peso"),
               rs.getDouble("coordinataX"),rs.getDouble("coordinataY")));
               i++;
            }
            if(i!=5)
                while(i<5){
                    listaCatture.add(new DatiCattura(-1,i+1,"","","","",0.0,0.0));
                    i++;
                }
            aggiornaDati(d);
        } catch (SQLException e) {System.err.println(e.getMessage());}     

    }
    public void aggiornaDati(String d){
         try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybass","root","");   //9
            PreparedStatement stBestBag = co.prepareStatement(queryBestBag);
            PreparedStatement stPersonal = co.prepareStatement(queryPersonalBest);
            PreparedStatement stCurrent = co.prepareStatement(queryCurrentBag);
            ResultSet rsBest = stBestBag.executeQuery();
            ResultSet rsPersonal = stPersonal.executeQuery();
        ) { 
            stCurrent.setDate(1,Date.valueOf(d));
            ResultSet rsCurrent = stCurrent.executeQuery();
            if(rsPersonal.next())
                personal.setText(String.valueOf(rsPersonal.getInt("personal")));
            else
                personal.setText("0");
            if(rsBest.next())
                best.setText(String.valueOf(rsBest.getInt("bestbag")));
            else 
                best.setText("0");
            if(rsCurrent.next())
                current.setText(String.valueOf(rsCurrent.getInt("bag")));
            else 
                current.setText("0");
        } catch (SQLException e) {System.err.println(e.getMessage());}     
    }
    public int modificaCattura(DatiCattura dc){
        System.out.println("modificaCattura");
        try( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybass", "root","");//,"");
            PreparedStatement st = co.prepareStatement(queryModificaCattura);
        ) {
            st.setInt(4,dc.getCodiceCattura());
            st.setString(1,dc.getPeso());
            st.setString(2, dc.getTecnica());
            st.setString(3, dc.getEsca());
            
            st.executeUpdate();
            return 1;
        } catch (SQLException ex) {
            
            System.out.println("Errore di inserimento di una cattura "+ex);
            return -1;
        }        
    }
    public ObservableList<PieChart.Data> percentuale(){
        ObservableList<PieChart.Data> l;
        l = FXCollections.observableArrayList();
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybass","root","");   //9
            PreparedStatement st = co.prepareStatement(queryPercentuale);
                ResultSet rs = st.executeQuery();
        ) { 
            while(rs.next())
                l.add(new PieChart.Data(rs.getString("tecnica"),rs.getDouble("totale")));
        } catch (SQLException e) {System.err.println(e.getMessage());} 
        return l;
    }
}
