package util;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class PotgresConnection {
    private String url = "";
    private final String user = "";
    private final String password = "";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public List<String> getFeature(String table, String dataset){
        List<String> list = new ArrayList<String>();

        try {
            String consulta = "";
            if(Objects.equals(dataset,"")){
                consulta = "SELECT * FROM public."+table;
            }
            if(Objects.equals(dataset,"")){

                consulta = "SELECT * FROM public."+table;
            }
            if(Objects.equals(dataset,"")){
                this.url = "";
                consulta = "SELECT * FROM public."+table;
            }

            Connection conn = connect();

            // testa se a conexão foi efetuada com sucesso
            if (conn != null) {
                System.out.println("Conexão efetuada com sucesso!");
            } else {
                System.out.println("Problemas!");
            }

            Statement stm = conn.createStatement();

            ResultSet rs = stm.executeQuery(consulta);

            while (rs.next()) {
                String name = rs.getString("");
                list.add(name);
            }

        } catch (SQLException ex) {
            Logger.getLogger("Erro de execução na consulta");
        }
        return list;
    }
    public ResultSet getData(String table, String dataset){
        try {
            String consulta = "";
            if(Objects.equals(dataset,"")){
                 consulta = "SELECT * FROM public."+table;
            }
            if(Objects.equals(dataset,"")){

                 consulta = "SELECT * FROM public."+table;
            }
            if(Objects.equals(dataset,"")){
                this.url = "";
                consulta = "SELECT * FROM public."+table;
            }

            Connection conn = connect();

            // testa se a conexão foi efetuada com sucesso
            if (conn != null) {
                System.out.println("Conexão efetuada com sucesso!");
            } else {
                System.out.println("Problemas!");
            }

            Statement stm = conn.createStatement();

            ResultSet rs = stm.executeQuery(consulta);

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger("Erro de execução na consulta");
        }
        return null;
    }
}
