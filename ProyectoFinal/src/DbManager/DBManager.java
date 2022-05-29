package DbManager;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSet;

/**
 *
 * @author Jose Antonio López Romero
 */
public class DBManager {

    // Conexión a la base de datos
    private static Connection conn = null;

    // Configuración de la conexión a la base de datos
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "tienda";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "usuario";
    private static final String DB_MSQ_CONN_OK = "CONEXIÓN CORRECTA";
    private static final String DB_MSQ_CONN_NO = "ERROR EN LA CONEXIÓN";

    // Configuración de la tabla Clientes
    private static final String DB_CLI = "clientes";
    private static final String DB_CLI_SELECT = "SELECT * FROM " + DB_CLI;
    private static final String DB_CLI_ID = "id";
    private static final String DB_CLI_NOM = "nombre";
    private static final String DB_CLI_DIR = "direccion";

    //////////////////////////////////////////////////
    // MÉTODOS DE CONEXIÓN A LA BASE DE DATOS
    //////////////////////////////////////////////////
    ;
    
    /**
     * Intenta cargar el JDBC driver.
     * @return true si pudo cargar el driver, false en caso contrario
     */
    public static boolean loadDriver() {
        try {
            System.out.print("Cargando Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("OK!");
            return true;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Intenta conectar con la base de datos.
     *
     * @return true si pudo conectarse, false en caso contrario
     */
    public static boolean connect() {
        try {
            System.out.print("Conectando a la base de datos...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("OK!");
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Comprueba la conexión y muestra su estado por pantalla
     *
     * @return true si la conexión existe y es válida, false en caso contrario
     */
    public static boolean isConnected() {
        // Comprobamos estado de la conexión
        try {
            if (conn != null && conn.isValid(0)) {
                System.out.println(DB_MSQ_CONN_OK);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(DB_MSQ_CONN_NO);
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Cierra la conexión con la base de datos
     */
    public static void close() {
        try {
            System.out.print("Cerrando la conexión...");
            conn.close();
            System.out.println("OK!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //////////////////////////////////////////////////
    // MÉTODOS DE TABLA CLIENTES
    //////////////////////////////////////////////////
    ;
    
    // Devuelve 
    // Ya incluiomos el tipo de result set en el prepared statement
    /**
     * Obtiene toda la tabla clientes de la base de datos
     * @return ResultSet con la tabla, null en caso de error
     */
    public static ResultSet getTablaClientes() {
        try {
            PreparedStatement stmt=conn.prepareStatement(DB_CLI_SELECT,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            //stmt.close();
            return rs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }

    }
    
    /**
     * Filtra los clientes de la base de datos segun la ciudad indicada
     * @param ciudad 
     * @return ResultSet con el resultado de la consulta, null en caso de error
     */
    
    public static void  filtrarPorCiudad(String ciudad) {
    	
    	try {
    		
			CallableStatement proc=conn.prepareCall("{call FiltrarPorCiudad(?)}");
			proc.setString(1,ciudad);
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
    
    /**
     * Imprime por pantalla el contenido de la tabla clientes
     */
    public static void printTablaClientes() {
        try {
            ResultSet rs = getTablaClientes();
            while (rs.next()) {
                int id = rs.getInt(DB_CLI_ID);
                String n = rs.getString(DB_CLI_NOM);
                String d = rs.getString(DB_CLI_DIR);
                System.out.println(id + "\t" + n + "\t" + d);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //////////////////////////////////////////////////
    // MÉTODOS DE UN SOLO CLIENTE
    //////////////////////////////////////////////////
    ;
    
    /**
     * Solicita a la BD el cliente con id indicado
     * @param id id del cliente
     * @return ResultSet con el resultado de la consulta, null en caso de error
     */
    public static ResultSet getCliente(int id) {
        try {
            // Realizamos la consulta SQL
        	String sql = DB_CLI_SELECT + " WHERE " + DB_CLI_ID + "=?;";
        	PreparedStatement stmt=conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        	stmt.setInt(1,id);
            //System.out.println(sql);
            ResultSet rs = stmt.executeQuery();
            //stmt.close();
            
            // Si no hay primer registro entonces no existe el cliente
            if (!rs.first()) {
                return null;
            }

            // Todo bien, devolvemos el cliente
            return rs;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Comprueba si en la BD existe el cliente con id indicado
     *
     * @param id id del cliente
     * @return verdadero si existe, false en caso contrario
     */
    public static boolean existsCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);

            // Si rs es null, se ha producido un error
            if (rs == null) {
                return false;
            }

            // Si no existe primer registro
            if (!rs.first()) {
                rs.close();
                return false;
            }

            // Todo bien, existe el cliente
            rs.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Imprime los datos del cliente con id indicado
     *
     * @param id id del cliente
     */
    public static void printCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);
            if (rs == null || !rs.first()) {
                System.out.println("Cliente " + id + " NO EXISTE");
                return;
            }
            
            // Imprimimos su información por pantalla
            int cid = rs.getInt(DB_CLI_ID);
            String nombre = rs.getString(DB_CLI_NOM);
            String direccion = rs.getString(DB_CLI_DIR);
            System.out.println("Cliente " + cid + "\t" + nombre + "\t" + direccion);

        } catch (SQLException ex) {
            System.out.println("Error al solicitar cliente " + id);
            ex.printStackTrace();
        }
    }

    /**
     * Solicita a la BD insertar un nuevo registro cliente
     *
     * @param nombre nombre del cliente
     * @param direccion dirección del cliente
     * @return verdadero si pudo insertarlo, false en caso contrario
     */
    public static boolean insertCliente(String nombre, String direccion) {
        try {
            // Obtenemos la tabla clientes
            System.out.print("Insertando cliente " + nombre + "...");
            ResultSet rs = getTablaClientes();

            // Insertamos el nuevo registro
            rs.moveToInsertRow();
            rs.updateString(DB_CLI_NOM, nombre);
            rs.updateString(DB_CLI_DIR, direccion);
            rs.insertRow();

            // Todo bien, cerramos ResultSet y devolvemos true
            rs.close();
            System.out.println("OK!");
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Solicita a la BD modificar los datos de un cliente
     *
     * @param id id del cliente a modificar
     * @param nombre nuevo nombre del cliente
     * @param direccion nueva dirección del cliente
     * @return verdadero si pudo modificarlo, false en caso contrario
     */
    public static boolean updateCliente(int id, String nuevoNombre, String nuevaDireccion) {
        try {
            // Obtenemos el cliente
            System.out.print("Actualizando cliente " + id + "... ");
            ResultSet rs = getCliente(id);

            // Si no existe el Resultset
            if (rs == null) {
                System.out.println("Error. ResultSet null.");
                return false;
            }

            // Si tiene un primer registro, lo eliminamos
            if (rs.first()) {
                rs.updateString(DB_CLI_NOM, nuevoNombre);
                rs.updateString(DB_CLI_DIR, nuevaDireccion);
                rs.updateRow();
                rs.close();
                System.out.println("OK!");
                return true;
            } else {
                System.out.println("ERROR. ResultSet vacío.");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Solicita a la BD eliminar un cliente
     *
     * @param id id del cliente a eliminar
     * @return verdadero si pudo eliminarlo, false en caso contrario
     */
    public static boolean deleteCliente(int id) {
    	try {
    		System.out.print("Eliminando cliente " + id + "... ");

    		// Obtenemos el cliente
    		ResultSet rs = getCliente(id);

    		// Si no existe el Resultset
    		if (rs == null) {
    			System.out.println("ERROR. ResultSet null.");
    			return false;
    		}

    		// Si existe y tiene primer registro, lo eliminamos
    		if (rs.first()) {
    			rs.deleteRow();
    			rs.close();
    			System.out.println("OK!");
    			return true;
    		} else {
    			System.out.println("ERROR. ResultSet vacío.");
    			return false;
    		}

    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		return false;
    	}
    }

    /**
     * Escribe la tabla de la base de datos en un fichero indicado
     * @param archivo Nombre del archivo vonde volcaremos los datos
     * */
    public static void volcarAFichero(String archivo) {

    	File rutadatos=new File("VolcadoDatos/"+archivo+".txt");
    	ResultSet datos = getTablaClientes();
    	try {

    		FileWriter datosvolcados=new FileWriter(rutadatos);
    		datosvolcados.write(DB_NAME+" "+DB_CLI+"\n");
    		datosvolcados.write(DB_CLI_ID+" "+DB_CLI_NOM+" "+DB_CLI_DIR+"\n");

    		while(datos.next()) {

    			int id = datos.getInt(DB_CLI_ID);
    			String n = datos.getString(DB_CLI_NOM);
    			String d = datos.getString(DB_CLI_DIR);
    			datosvolcados.write(id+" "+n+" "+d+"\n");
    			
    		}


    		datosvolcados.close();
    	}
    	catch (SQLException e1) {
    		
    		e1.printStackTrace();
    	} 
    	catch (IOException e) {

    		e.printStackTrace();
    	}
    }
    
    
    /**
     * Inserta registros en la tabla desde un archivo
     * @param archivo Nombre del archivo de donde insertamos
     * 
     */
     public static void insertarDesdeFichero(String archivo) {
    	
    	File datosInsertar=new File("VolcadoDatos/"+archivo+".txt");
    	String tabla;
    	try {
    		
			Scanner lector=new Scanner (datosInsertar);
			lector.nextLine();
			lector.nextLine();
			lector.nextLine();
			
			while(lector.hasNext()) {
			String datosleidos=lector.nextLine();
			String[] datosseparados=datosleidos.split(" ");
			insertCliente(datosseparados[0], datosseparados[1]);
			}
			lector.close();
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
    	
    }
     
     
     
     /**
      * Borra los registros indicados en un archivo
      * @param archivo Nombre del archivo de donde extraemos los que borrar
      * 
      */
    public static void borrarDesdeFichero(String archivo) {
    	
    	File datosInsertar=new File("VolcadoDatos/"+archivo+".txt");
    	
    	try {
    		
			Scanner lector=new Scanner (datosInsertar);
			lector.nextLine();
			lector.nextLine();
			lector.nextLine();
			
			while(lector.hasNext()) {
			String datosleidos=lector.nextLine();
			String[] datosseparados=datosleidos.split(" ");
			int id=Integer.parseInt(datosseparados[0]);
			deleteCliente(id);
			}
			lector.close();
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
    	
    }
    
    
    /**
     * Inserta actualiza los campos de las tablas desde un archivo
     * @param archivo Nombre del archivo de donde extraemos los datos
     * 
     */
    public static void actualizarDesdeFichero(String archivo) {
    	
    	File datosActualizar=new File("VolcadoDatos/"+archivo+".txt");
    	
    	try {
    		
			Scanner lector=new Scanner (datosActualizar);
			lector.nextLine();
			lector.nextLine();
			lector.nextLine();
			
			while(lector.hasNext()) {
			String datosleidos=lector.nextLine();
			String[] datosseparados=datosleidos.split(" ");
			int id=Integer.parseInt(datosseparados[0]);
			updateCliente(id, datosseparados[1], datosseparados[2]);
			}
			lector.close();
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
    	
    }
   
    
    /**
     * Inserta una nueva tabla con dos campos en la base de datos
     * @param nombreTabla Nombre de la tabla que vamos a crear
     * @param campo1 Nombre del primer campo de la nueva tabla
     * @param campo2 Nombre del segundo campo de la nueva tabla
     * 
     */
    
    public static void insertarTabla(String nombreTabla,String campo1, String campo2) {
    	
    	String nuevatabla="create table "+nombreTabla+" ( "+campo1+" varchar(20), "+campo2+" varchar(20))";
    	
    	try {
			PreparedStatement stmt=conn.prepareStatement(nuevatabla);
			stmt.executeUpdate();
			
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	
    }
    
    
    /**
     * Hace una busqueda segun el nombre de una fila de la tabla
     * @param nombreFila Nombre de la fila por la que queremos filtrar
     * 
     */
    public static void filtrarFilas(String nombreFila) {
    	
    	String filaseleccionada="select "+nombreFila+" from "+DB_CLI;
    	
    	try {
			PreparedStatement stmt=conn.prepareStatement(filaseleccionada);
			ResultSet rs=stmt.executeQuery();
			
			while(rs.next()) {
				
				String fila=rs.getString(nombreFila);
				System.out.println(fila);
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
}
