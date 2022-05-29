package AppGestionClientes;


import java.util.Scanner;
import DbManager.DBManager;

/**
 *
 * @author Jose Antonio López Romero
 */
public class GestionClientes {

    public static void main(String[] args) {

        DBManager.loadDriver();
        DBManager.connect();

        boolean salir = false;
        do {
            salir = menuPrincipal();
        } while (!salir);

        DBManager.close();

    }
    
    //Funcion con el menu principal

    /**
     * Menu principal donde se ofrecen las diferentes opciones para trabajar con la base de datos
     * @return false si seguimos en el menu, true si le damos a salir
     */
    public static boolean menuPrincipal() {
        System.out.println("");
        System.out.println("MENU PRINCIPAL");
        System.out.println("1. Listar clientes");
        System.out.println("2. Nuevo cliente");
        System.out.println("3. Modificar cliente");
        System.out.println("4. Eliminar cliente");
        System.out.println("5. Filtrar clientes por ciudad (Callable Statement)");
        System.out.println("6. Volcar datos a fichero");
        System.out.println("7. Insertar datos desde fichero");
        System.out.println("8. Borrar datos desde fichero");
        System.out.println("9. Actualizar datos desde fichero");
        System.out.println("10. Crear una nueva tabla");
        System.out.println("11. Filtrar por filas");
        System.out.println("0. Salir");
        
        Scanner in = new Scanner(System.in);
            
        int opcion = pideInt("Elige una opción: ");
        
        switch (opcion) {
            case 1:
                opcionMostrarClientes();
                return false;
            case 2:
                opcionNuevoCliente();
                return false;
            case 3:
                opcionModificarCliente();
                return false;
            case 4:
                opcionEliminarCliente();
                return false;
            case 5:
            	opcionFiltrarPorCiudad();
                return false;
            case 6:
            	opcionVolcarAFichero();
                return false;
            case 7:
            	opcionInsertarFichero();
            	return false;
            case 8:
            	opcionBorrarDesdeFichero();
            	return false;
            case 9:
            	opcionActualizarDesdeFichero();
            	return false;
            	
            case 10:
            	opcionCrearNuevaTabla();
            	return false;
            	
            case 11:
            	opcionFiltrarFilas();
            	return false;
            	
            case 0:
            	return true;
            default:
                System.out.println("Opción elegida incorrecta");
                return false;
        }
        
    }
    
    
    /**
     * Metodo para pedir numeros enteros por teclado
     * @param mensaje String que sera convertido a entero
     * @return devuelve un entero 
     */
    public static int pideInt(String mensaje){
        
        while(true) {
            try {
                System.out.print(mensaje);
                Scanner in = new Scanner(System.in);
                int valor = in.nextInt();
                //in.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.println("No has introducido un número entero. Vuelve a intentarlo.");
            }
        }
    }
    
    /**
     * Metodo para pedir palabras por teclado
     * @param mensaje Palabras introducidas por teclado
     * @return cadena de texto introducida por teclado
     */
    public static String pideLinea(String mensaje){
        
        while(true) {
            try {
                System.out.print(mensaje);
                Scanner in = new Scanner(System.in);
                String linea = in.nextLine();
                return linea;
            } catch (Exception e) {
                System.out.println("No has introducido una cadena de texto. Vuelve a intentarlo.");
            }
        }
    }

    /**
     * Metodo del menu que muestra la tabla de clientes
     */
    public static void opcionMostrarClientes() {
        System.out.println("Listado de Clientes:");
        DBManager.printTablaClientes();
    }

    /**
     * Metodo que pide los valores y crea un nuevo cliente en la base de datos
     */
    public static void opcionNuevoCliente() {
        Scanner in = new Scanner(System.in);

        System.out.println("Introduce los datos del nuevo cliente:");
        String nombre = pideLinea("Nombre: ");
        String direccion = pideLinea("Dirección: ");

        boolean res = DBManager.insertCliente(nombre, direccion);

        if (res) {
            System.out.println("Cliente registrado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    
    /**
     * Metodo para modificar los valores de los clientes en la base de datos
     */
    public static void opcionModificarCliente() {
        Scanner in = new Scanner(System.in);

        int id = pideInt("Indica el id del cliente a modificar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Mostramos datos del cliente a modificar
        DBManager.printCliente(id);

        // Solicitamos los nuevos datos
        String nombre = pideLinea("Nuevo nombre: ");
        String direccion = pideLinea("Nueva dirección: ");

        // Registramos los cambios
        boolean res = DBManager.updateCliente(id, nombre, direccion);

        if (res) {
            System.out.println("Cliente modificado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    
    /**
     * Metodo para eliminar un cliente de la base de datos
     */
    public static void opcionEliminarCliente() {
        Scanner in = new Scanner(System.in);

        int id = pideInt("Indica el id del cliente a eliminar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Eliminamos el cliente
        boolean res = DBManager.deleteCliente(id);

        if (res) {
            System.out.println("Cliente eliminado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }
    
    /**
     * Metodo para usar el procedimiento almacenado de filtrar por ciudad de la base de datos
     */
    public static void opcionFiltrarPorCiudad() {
    	
        Scanner in = new Scanner(System.in);
        String ciudad = pideLinea("Introduce el nombre de la ciudad:");
        
        DBManager.filtrarPorCiudad(ciudad);

    }
    
    /**
     * Metodo para volcar los datos de la base de datos a un fichero
     */
    public static void opcionVolcarAFichero() {
    	
    	Scanner in = new Scanner(System.in);
        String archivo = pideLinea("Introduce el nombre del archivo destino (sin extension):");
        DBManager.volcarAFichero(archivo);
    	
    }
    
    /**
     * Metodo para insertar clientes desde un archivo
     */
    public static void opcionInsertarFichero() {
    	
    	Scanner in = new Scanner(System.in);
        String archivo = pideLinea("Introduce el nombre del archivo del que insertar (sin extension):");
        DBManager.insertarDesdeFichero(archivo);
    	
    }
    
    /**
     * Metodo para borrar registros de la base de datos desde un archivo
     */
    public static void opcionBorrarDesdeFichero() {
    	
    	Scanner in = new Scanner(System.in);
        String archivo = pideLinea("Introduce el nombre del archivo del que borrar (sin extension):");
        DBManager.borrarDesdeFichero(archivo);
    	
    }
    
    /**
     * Metodo para actualizar registros de la base de datos desde un archivo
     */
    public static void opcionActualizarDesdeFichero() {
    	
    	Scanner in = new Scanner(System.in);
        String archivo = pideLinea("Introduce el nombre del archivo del que borrar (sin extension):");
        DBManager.actualizarDesdeFichero(archivo);
    	
    }
    
    /**
     * Metodo para crear una nueva tabla con dos campos
     */
    public static void opcionCrearNuevaTabla() {
    	
    	Scanner in = new Scanner(System.in);
        String nombretabla = pideLinea("Introduce el nombre de la nueva tabla:");
        String campo1 = pideLinea("Introduce el nombre del campo 1:");
        String campo2 = pideLinea("Introduce el nombre del campo 2:");
        
        DBManager.insertarTabla(nombretabla, campo1, campo2);
        
    	
    }
    
    /**
     * Metodo para filtrar por una fila en la base de datos
     */
    public static void opcionFiltrarFilas() {
    	
    	Scanner in = new Scanner(System.in);
        String fila = pideLinea("Introduce el nombre de la fila por la que filtrar:");    
        DBManager.filtrarFilas(fila);
        
    	
    }
    
}
