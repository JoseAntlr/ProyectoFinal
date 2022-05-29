package Pruebas_Junit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *Clase de prueba de DBManager
 * @author Jose Antonio López Romero
 */
class DBManagerTest {

	// Conexion a la base de datos
	public static Connection conn = null;

	// Configuracion de la conexion a la base de datos
	private static final String DB_HOST = "localhost";
	private static final String DB_PORT = "3306";
	private static final String DB_NAME = "tienda";
	private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";
	private static final String DB_USER = "root";
	private static final String DB_PASS = "usuario";

	// Configuracion de la tabla Clientes
	private static final String DB_CLI = "clientes";
	private static final String DB_CLI_SELECT = "SELECT * FROM " + DB_CLI;
	private static final String DB_CLI_ID = "id";
	private static final String DB_CLI_NOM = "nombre";
	private static final String DB_CLI_DIR = "direccion";

	//Primero establecemos la conexion con la base de datos para poder hacer las pruebas
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try {
			System.out.print("Conectando a la base de datos...");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			System.out.println("OK!");
		} catch (SQLException ex) {
			System.err.println("No ha sido posible conectarse a la base de datos");
		}
	}

	//Cuando acaben los test cerraremos la conexion
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		try {
			conn.close();
		} catch (SQLException ex) {
			System.err.println("No ha sido posible cerrar la conexion con la base de datos");
		}
	}

	//Probamos la funcion de filtrar por ciudad
	@Test
	void testFiltrarPorCiudad() {

		try {

			CallableStatement proc=conn.prepareCall("{call FiltrarPorCiudad(?)}");
			proc.setString(1,"Valencia");
			proc.execute();
			ResultSet resultado=proc.getResultSet();

			if(resultado.isBeforeFirst()) {

				while(resultado.next()) {

					int id = resultado.getInt(DB_CLI_ID);
					String n = resultado.getString(DB_CLI_NOM);
					String d = resultado.getString(DB_CLI_DIR);
					System.out.println(id + "\t" + n + "\t" + d);
				}


			}else {

				System.out.println("No existe la ciudad en la base de datos");

			}

		} catch (SQLException e) {

			e.printStackTrace();

		}
	}

	//Probamos la funcion de volcar los datos a un fichero
	@Test
	void testVolcarAFichero() {

		File rutadatos=new File("Archivos/prueba.txt");
		
		try {
		FileWriter escritor=new FileWriter(rutadatos);
		
		escritor.write(DB_NAME+"\t"+DB_CLI+"\n");
		escritor.write(DB_CLI_ID+"\t"+DB_CLI_NOM+"\t\t"+DB_CLI_DIR+"\n");
		
		String sql = DB_CLI_SELECT ;
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
		
        while (rs.next()) {
            int id = rs.getInt(DB_CLI_ID);
            String n = rs.getString(DB_CLI_NOM);
            String d = rs.getString(DB_CLI_DIR);
            escritor.write(id+"\t"+n+"\t\t"+d+"\n");
        }
        escritor.close();
        rs.close();
        System.out.println("La informacion se ha volcado correctamente en el archivo.");
		
	} catch (IOException ex) {
		System.err.println("No ha sido posible volcar los datos en el fichero con la ruta especificada");
	} catch (SQLException e1) {
		System.err.println("No ha sido posible volcar los datos en el fichero con la ruta especificada");
	}

	}

	

	//Probamos la funcion de insertar en una tabla
	@Test
	void testInsertarTabla() {
		
		String nuevatabla="create table prueba ( prueba varchar(20), prueba2 varchar(20))";
    	
    	try {
			PreparedStatement stmt=conn.prepareStatement(nuevatabla);
			stmt.executeUpdate();
			
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	//Probamos la funcion de filtrar por filas
	@Test
	void testFiltrarFilas() {
		
	String filaseleccionada="select direccion from "+DB_CLI;
    	
    	try {
			PreparedStatement stmt=conn.prepareStatement(filaseleccionada);
			ResultSet rs=stmt.executeQuery();
			
			while(rs.next()) {
				
				String fila=rs.getString("direccion");
				System.out.println(fila);
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
