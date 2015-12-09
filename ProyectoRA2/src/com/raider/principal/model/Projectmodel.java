package com.raider.principal.model;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.raider.principal.util.Values;
import com.raider.principal.base.Cuartel;
import com.raider.principal.base.Soldado;
import com.raider.principal.base.Unidad;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.postgresql.util.PSQLException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import raider.Util.Utilities;

/**
 * Created by raider on 5/11/15.
 */
public class Projectmodel {

    private Connection conexion;

    public void conexion() throws SQLException, ClassNotFoundException {

        Properties configuracion = new Properties();

        try {
            configuracion.load(new FileInputStream("configuracion.props"));

            String driver = configuracion.getProperty("driver");
            Values.driver = driver;
            String protocolo = configuracion.getProperty("protocolo");
            String servidor = configuracion.getProperty("servidor");
            String puerto = configuracion.getProperty("puerto");
            String baseDatos = configuracion.getProperty("basedatos");
            String usuario = configuracion.getProperty("usuario");
            String contrasena = configuracion.getProperty("contrasena");

            Class.forName(driver).newInstance();
            try {
                conexion = DriverManager.getConnection(protocolo + servidor + ":" +
                        puerto + "/" + baseDatos, usuario, contrasena);
                Utilities.mensajeInformacion("Conexion realizada con exito");
            } catch (MySQLSyntaxErrorException msqlsee) {
                Utilities.mensajeError("Porfavor introduzca la base de datos ejercito en Mysql");
                msqlsee.printStackTrace();
                Values.warningBaseDatos = true;
            } catch (PSQLException psqlsee) {
                Utilities.mensajeError("Porfavor introduzca la base de datos ejercito en PostgreSQL");
                psqlsee.printStackTrace();
                Values.warningBaseDatos = true;
            }



        } catch (FileNotFoundException fnfe) {
            Utilities.mensajeInformacion(" Preferencias de conexion no encontradas.\n " +
                    "Base de datos cargada con preferencias por defecto.");
                Properties con = new Properties();

                con.setProperty("basedatos", "ejercito");
                con.setProperty("puerto", "3306");
                con.setProperty("servidor", "81.169.242.83");
                con.setProperty("contrasena", "1234");
                con.setProperty("usuario", "Raider");
                con.setProperty("protocolo", "jdbc:mysql://");
                con.setProperty("driver", "com.mysql.jdbc.Driver");

                Values.driver = "com.mysql.jdbc.Driver";
                try {
                    con.store(new FileOutputStream("configuracion.props"), "|--- PREFERENCIAS ---|");
                } catch (FileNotFoundException fn) {
                    Utilities.mensajeError("Error al leer fichero de configuracion");
                } catch (IOException io) {
                    io.printStackTrace();
                }

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/ejercito", "root", "pamaloyo18");
                Utilities.mensajeInformacion("Conexion realizada con exito");
            } catch (MySQLSyntaxErrorException msqlsee) {
                Utilities.mensajeError("Porfavor introduzca la base de datos ejercito en Mysql");
                msqlsee.printStackTrace();
                Values.warningBaseDatos = true;
            } catch (PSQLException psqlsee) {
                Utilities.mensajeError("Porfavor introduzca la base de datos ejercito en PostgreSQL");
                psqlsee.printStackTrace();
                Values.warningBaseDatos = true;
            }

        } catch (IOException ioe) {
            Utilities.mensajeError("Error al leer fichero de configuracion");
        } catch (ClassNotFoundException cnfe) {
            Utilities.mensajeError("No se ha podido cargar el driver de la base de datos");
        } catch (SQLException sqle) {
            Utilities.mensajeError("No se ha podido conectar con la base de datos");
            sqle.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String login(String usuario, String contrasena) {

        if (Values.warningBaseDatos == false) {
            String sql;

            if (Values.driver.equalsIgnoreCase("org.postgresql.Driver")) {
                sql = "SELECT usuario, rol FROM usuarios WHERE " +
                        "usuario = ? AND password = ?";
            } else {
                sql = "SELECT usuario, rol FROM usuarios WHERE " +
                        "usuario = ? AND password = SHA1(?)";
            }


            String rol = "";

            try {
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                sentencia.setString(1, usuario);
                sentencia.setString(2, contrasena);
                ResultSet resultado = sentencia.executeQuery();

                if (!resultado.next()) {
                    Utilities.mensajeError("Usuario y contraseña incorrectos");
                    return null;
                }
                rol = resultado.getString("rol");
                return rol;
            } catch (SQLException sqle) {
                Utilities.mensajeError("Error al hacer login");
                return null;
            }
        } else {
            Utilities.mensajeError("\tAvise al encargado del sistema.\n" +
                    "Login no activo debido a fallo de conexion con base de datos.\n" +
                    "Revise fichero de configuracion o compruebe si esta" +
                    " disponible la base de datos ejercito en el servidor.\n" +
                    "Tras solucionar el problema reinicie la aplicacion.");
            return null;
        }
    }

    public void guardarCuartelSentencia(String nombre_cuartel, String localidad,
                                        Double latitud, Double longitud, Boolean actividad) {

        if (usoCuartel(nombre_cuartel) == 0) {
            cambiarUsoCuartel(1, nombre_cuartel);

            if (Values.driver.equalsIgnoreCase("org.postgresql.Driver")) {

                String sql = "INSERT INTO cuartel (nombre_cuartel, latitud," +
                        " longitud, actividad, localidad) VALUES (?,?,?,?,?)";
                try {
                    PreparedStatement sentence = conexion.prepareStatement(sql);
                    sentence.setString(1, nombre_cuartel);
                    sentence.setDouble(2, latitud);
                    sentence.setDouble(3, longitud);
                    if (actividad == true) {
                        sentence.setInt(4, 1);
                    } else {
                        if (actividad == false) {
                            sentence.setInt(4, 0);
                        } else sentence.setInt(4, 1);
                    }
                    sentence.setString(5, localidad);

                    sentence.executeUpdate();
                } catch (SQLException e) {
                    Utilities.mensajeError("Error al dar de alta cuartel");
                    e.printStackTrace();
                }

            } else {

                String sql = "call guardarCuartel(?,?,?,?,?)";
                try {
                    PreparedStatement sentence = conexion.prepareStatement(sql);
                    sentence.setString(1, nombre_cuartel);
                    sentence.setString(2, localidad);
                    sentence.setDouble(3, latitud);
                    sentence.setDouble(4, longitud);
                    if (actividad == true) {
                        sentence.setInt(5, 1);
                    } else {
                        if (actividad == false) {
                            sentence.setInt(5, 0);
                        } else sentence.setInt(5, 1);
                    }


                    sentence.executeUpdate();
                } catch (SQLException e) {
                    Utilities.mensajeError("Error al dar de alta cuartel");
                    e.printStackTrace();
                }
            }
            cambiarUsoCuartel(0, nombre_cuartel);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public void guardarUnidadSentencia(String nombre_unidad, String tipo,
                                       int no_tropas, Date fecha_creacion, String cuartel) {

        if (usoUnidad(nombre_unidad) == 0) {
            cambiarUsoUnidad(1, nombre_unidad);
            if (Values.driver.equalsIgnoreCase("org.postgresql.Driver")) {

                try {
                    conexion.setAutoCommit(false);
                    String sql = "INSERT INTO unidad (nombre_unidad, tipo, no_tropas," +
                            " fecha_creacion, id_cuartel) VALUES (?,?,?,?,?)";
                    int id_cuartel = 0;
                    if ((id_cuartel = consultaID("id", "cuartel", "nombre_cuartel", cuartel)) != 0) ;

                    PreparedStatement sentence = conexion.prepareStatement(sql);
                    sentence.setString(1, nombre_unidad);
                    sentence.setString(2, tipo);
                    sentence.setInt(3, no_tropas);
                    sentence.setDate(4, fecha_creacion);
                    sentence.setInt(5, id_cuartel);

                    sentence.executeUpdate();
                    conexion.commit();
                    conexion.setAutoCommit(true);
                } catch (SQLException sqle) {
                    Utilities.mensajeError("Error al dar de alta unidad");
                }
            } else {

                try {
                    String sql = "call guardarUnidad(?,?,?,?,?)";

                    PreparedStatement sentence = conexion.prepareStatement(sql);
                    sentence.setString(1, nombre_unidad);
                    sentence.setString(2, tipo);
                    sentence.setInt(3, no_tropas);
                    sentence.setDate(4, fecha_creacion);
                    sentence.setString(5, cuartel);

                    sentence.executeUpdate();
                } catch (SQLException sqle) {
                    Utilities.mensajeError("Error al dar de alta unidad");
                }
            }
            cambiarUsoUnidad(0, nombre_unidad);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public void guardarSoldadoSentencia(String nombre, String apellidos, Date fecha_nacimiento,
                                        String rango, String lugar_nacimiento, String unidad) {

        if (usoSoldado(nombre, apellidos) == 0) {
            cambiarUsoSoldado(1, nombre, apellidos);
            try {
                conexion.setAutoCommit(false);
                String sql = "INSERT INTO soldado (nombre, apellidos, fecha_nacimiento," +
                        " rango, lugar_nacimiento, id_unidad)" +
                        " VALUES (?,?,?,?,?,?)";
                int id_unidad = 0;
                if ((id_unidad = consultaID("id", "unidad", "nombre_unidad", unidad)) != 0) ;

                PreparedStatement sentence = conexion.prepareStatement(sql);
                sentence.setString(1, nombre);
                sentence.setString(2, apellidos);
                sentence.setDate(3, fecha_nacimiento);
                sentence.setString(4, rango);
                sentence.setString(5, lugar_nacimiento);
                sentence.setInt(6, id_unidad);

                sentence.executeUpdate();
                conexion.commit();
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                Utilities.mensajeError("Error al dar de alta soldado");
            }
            cambiarUsoSoldado(0, nombre, apellidos);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    // Metodos que eliminan el objeto en la posicion seleccionada

    public void borrarCuartelSentencia(int id) {

        if (usoCuartel(id) == 0) {
            cambiarUsoCuartel(1, id);
            String sql = "DELETE FROM cuartel WHERE id = ?";

            PreparedStatement sentencia = null;
            try {
                sentencia = conexion.prepareStatement(sql);
                sentencia.setInt(1, id);
                sentencia.executeUpdate();
            } catch (SQLException e) {
                Utilities.mensajeError("Error al borrar cuartel");
            }
            cambiarUsoCuartel(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public void borrarUnidadSentencia(int id) {

        if (usoUnidad(id) == 0) {
            cambiarUsoUnidad(1, id);
            String sql = "DELETE FROM unidad WHERE id = ?";

            PreparedStatement sentencia = null;
            try {
                sentencia = conexion.prepareStatement(sql);
                sentencia.setInt(1, id);
                sentencia.executeUpdate();
            } catch (SQLException e) {
                Utilities.mensajeError("Error al borrar unidad");
            }
            cambiarUsoUnidad(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }

    }

    public void borrarSoldadoSentencia(int id) {

        if (usoSoldado(id) == 0) {
            cambiarUsoSoldado(1, id);
            String sql = "DELETE FROM soldado WHERE id = ?";

            PreparedStatement sentencia = null;
            try {
                sentencia = conexion.prepareStatement(sql);
                sentencia.setInt(1, id);
                sentencia.executeUpdate();
            } catch (SQLException e) {
                Utilities.mensajeError("Error al borrar soldado");
            }
            cambiarUsoSoldado(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }

    }

    //Metodos de modificado

    public void modificarCuartelSentencia(int id, String nombre_cuartel, Double latitud,
                                 Double longitud, Boolean actividad, String localidad) {

        if (usoCuartel(id) == 0) {
            cambiarUsoSoldado(1, id);
            String sql = "UPDATE cuartel SET nombre_cuartel = ?," +
                    " latitud = ?, longitud = ?, actividad = ?, localidad = ? WHERE id = ?";

            PreparedStatement sentence = null;

            try {
                sentence = conexion.prepareStatement(sql);
                sentence.setString(1, nombre_cuartel);
                sentence.setDouble(2, latitud);
                sentence.setDouble(3, longitud);
                sentence.setBoolean(4, actividad);
                sentence.setString(5, localidad);
                sentence.setInt(6, id);
                sentence.executeUpdate();
            } catch (SQLException e) {
                Utilities.mensajeError("Error al modificar cuartel");
            }
            cambiarUsoSoldado(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public void modificarUnidadSentencia(int id, String nombre_unidad,
                                         String tipo, int no_tropas,
                                         Date fecha_creacion, String cuartel) {

        if (usoUnidad(id) == 0) {
            cambiarUsoUnidad(1, id);
            try {
                conexion.setAutoCommit(false);
                String sql = "UPDATE unidad SET nombre_unidad = ?, tipo = ?," +
                        " no_tropas = ?, fecha_creacion = ?, id_cuartel = ? WHERE id = ?";

                PreparedStatement sentence = null;

                int id_cuartel = 0;
                if ((id_cuartel = consultaID("id", "cuartel", "nombre_cuartel", cuartel)) != 0);


                sentence = conexion.prepareStatement(sql);
                sentence.setString(1, nombre_unidad);
                sentence.setString(2, tipo);
                sentence.setInt(3, no_tropas);
                sentence.setDate(4, fecha_creacion);
                sentence.setInt(5, id_cuartel);
                sentence.setInt(6, id);
                sentence.executeUpdate();
                conexion.commit();
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                Utilities.mensajeError("Error al modificar unidad");
                e.printStackTrace();
            }
            cambiarUsoUnidad(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public void modificarSoldadoSentencia(int id, String nombre, String apellidos,
                                         String rango, Date fecha_nacimiento,
                                         String lugar_nacimiento, String unidad) {

        if (usoSoldado(id) == 0) {
            cambiarUsoSoldado(1, id);
            try {
                conexion.setAutoCommit(false);
                String sql = "UPDATE soldado SET nombre = ?, apellidos = ?," +
                        " rango = ?, fecha_nacimiento = ?, lugar_nacimiento = ?, id_unidad = ? WHERE id = ?";

                PreparedStatement sentence = null;

                int id_unidad = 0;
                if ((id_unidad = consultaID("id", "unidad", "nombre_unidad", unidad)) != 0) ;


                sentence = conexion.prepareStatement(sql);
                sentence.setString(1, nombre);
                sentence.setString(2, apellidos);
                sentence.setString(3, rango);
                sentence.setDate(4, fecha_nacimiento);
                sentence.setString(5, lugar_nacimiento);
                sentence.setInt(6, id_unidad);
                sentence.setInt(7, id);
                sentence.executeUpdate();
                conexion.commit();
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                Utilities.mensajeError("Error al modificar soldado");
            }
            cambiarUsoSoldado(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede guardar.\nCampo siendo usado por otra persona.\nInténtelo en unos segundos.");
        }
    }

    public int consultaID(String select, String table, String campo, String condicion) throws SQLException{

        String sql = "SELECT " + select + " FROM " + table + " WHERE " + campo + " = ?";
        int id = 0;
        PreparedStatement sentencia = null;

            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, condicion);
            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next())id = resultado.getInt(select);

        return id;
    }

    public String consultaNombreCuartel_NombreUnidad(String tabla, int id) {

        if (tabla.equalsIgnoreCase("cuartel")) {

            String sql = "SELECT nombre_cuartel FROM cuartel WHERE id = ?";
            String nombre = "";
            PreparedStatement sentencia = null;
            try {

                sentencia = conexion.prepareStatement(sql);
                sentencia.setInt(1, id);
                ResultSet resultado = sentencia.executeQuery();

                if (resultado.next())nombre = resultado.getString("nombre_cuartel");
                return nombre;

            } catch (SQLException e) {
                Utilities.mensajeError("Error al cotejar datos en consulta Cuartel");
            }

        } else {

            if (tabla.equalsIgnoreCase("unidad")) {

                String sql = "SELECT nombre_unidad FROM unidad WHERE id = ?";
                String nombre = "";
                PreparedStatement sentencia = null;
                try {

                    sentencia = conexion.prepareStatement(sql);
                    sentencia.setInt(1, id);
                    ResultSet resultado = sentencia.executeQuery();

                    if (resultado.next())nombre = resultado.getString("nombre_unidad");
                    return nombre;

                } catch (SQLException e) {
                    Utilities.mensajeError("Error al cotejar datos en consulta Unidad");
                }
            }
        }

        return null;
    }

    public List<String> consultaActualizarComboBox(int op) {

        List<String> ret;

        if (op == 0) {

            ret = new ArrayList<>();

            String sql = "SELECT nombre_cuartel FROM cuartel";

            PreparedStatement sentence = null;

            try {
                sentence = conexion.prepareStatement(sql);
                ResultSet resultado = sentence.executeQuery();

                while (resultado.next()) {

                    ret.add(resultado.getString("nombre_cuartel"));
                }

                return ret;

            } catch (SQLException e) {
                Utilities.mensajeError("Error al actualizar combobox Unidad");
            }

        } else {

            if (op == 1) {

                ret = new ArrayList<>();

                String sql = "SELECT nombre_unidad FROM unidad";

                PreparedStatement sentence = null;

                try {
                    sentence = conexion.prepareStatement(sql);
                    ResultSet resultado = sentence.executeQuery();

                    while (resultado.next()) {

                        ret.add(resultado.getString("nombre_unidad"));
                    }

                    return ret;

                } catch (SQLException e) {
                    Utilities.mensajeError("Error al actualizar combobox Soldado");
                }
            }
        }

        return null;
    }

    public List<Object[]> listar(String tabla) {

        Object[] fila;
        List<Object[]> list;

        if (tabla.equalsIgnoreCase("cuartel")) {

            String sql = "SELECT * FROM cuartel";
            list = new ArrayList<>();
            try {
                PreparedStatement sentencia = null;

                sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery();
                while (resultado.next()) {

                    int id = resultado.getInt("id");
                    String nombre_cuartel = resultado.getString("nombre_cuartel");
                    Double latitud = resultado.getDouble("latitud");
                    Double longitud = resultado.getDouble("longitud");
                    String localidad = resultado.getString("localidad");
                    Boolean actividad = resultado.getBoolean("actividad");

                    fila = new Object[]{id, nombre_cuartel, localidad, latitud,
                            longitud, actividad};
                    list.add(fila);
                }
                return list;
            } catch (SQLException e) {
                Utilities.mensajeError("Error al listar Cuartel");
            }


        } else {

            if (tabla.equalsIgnoreCase("unidad")) {

                String sql = "SELECT * FROM unidad";
                list = new ArrayList<>();
                try {
                    PreparedStatement sentencia = null;

                    sentencia = conexion.prepareStatement(sql);
                    ResultSet resultado = sentencia.executeQuery();
                    while (resultado.next()) {

                        int id = resultado.getInt("id");
                        String nombre_unidad = resultado.getString("nombre_unidad");
                        String tipo = resultado.getString("tipo");
                        int no_tropas = resultado.getInt("no_tropas");
                        Date fecha_creacion = resultado.getDate("fecha_creacion");
                        int id_cuartel = resultado.getInt("id_cuartel");

                        fila = new Object[]{id, nombre_unidad, tipo, no_tropas, fecha_creacion,
                                consultaNombreCuartel_NombreUnidad("cuartel", id_cuartel)};
                        list.add(fila);
                    }
                    return list;
                } catch (SQLException e) {
                    Utilities.mensajeError("Error al listar Unidad");
                }

            } else {

                if (tabla.equalsIgnoreCase("soldado")) {

                    String sql = "SELECT * FROM soldado";
                    list = new ArrayList<>();
                    try {
                        PreparedStatement sentencia = null;
                        sentencia = conexion.prepareStatement(sql);
                        ResultSet resultado = sentencia.executeQuery();
                        while (resultado.next()) {

                            int id = resultado.getInt("id");
                            String nombre = resultado.getString("nombre");
                            String apellidos = resultado.getString("apellidos");
                            String rango = resultado.getString("rango");
                            String lugar_nacimiento = resultado.getString("lugar_nacimiento");
                            Date fecha_nacimiento = resultado.getDate("fecha_nacimiento");
                            int id_unidad = resultado.getInt("id_unidad");

                            fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento,
                                    consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                            list.add(fila);
                        }
                        return list;
                    } catch (SQLException e) {
                        Utilities.mensajeError("Error al listar Soldado");
                    }
                }
            }
        }

        return null;
    }

    public Cuartel cargarCuartelSeleccionado(int id) {

        Cuartel cuartel = null;

        String sql = "SELECT * FROM cuartel WHERE id = ?";
        PreparedStatement sentence;

        try {
            sentence = conexion.prepareStatement(sql);
            sentence.setInt(1, id);
            ResultSet resultado = sentence.executeQuery();

            cuartel = new Cuartel();

            if (resultado.next()) {

                cuartel.setnCuartel(resultado.getString("nombre_cuartel"));
                cuartel.setLocalidad(resultado.getString("localidad"));
                cuartel.setActividad(resultado.getBoolean("actividad"));
                cuartel.setLatitud(resultado.getDouble("latitud"));
                cuartel.setLongitud(resultado.getDouble("longitud"));
            }


        } catch (SQLException e) {
            Utilities.mensajeError("Error al cargar Cuartel seleccionado");
        }

        return cuartel;
    }

    public Unidad cargarUnidadSeleccionada(int id) {

        Unidad unidad = null;

        String sql = "SELECT * FROM unidad WHERE id = ?";
        PreparedStatement sentence;

        try {
            sentence = conexion.prepareStatement(sql);
            sentence.setInt(1, id);
            ResultSet resultado = sentence.executeQuery();

            unidad = new Unidad();
            if (resultado.next()) {

                unidad.setnUnidad(resultado.getString("nombre_unidad"));
                unidad.setNoTropas(resultado.getInt("no_tropas"));
                unidad.setFechaCreacion(resultado.getDate("fecha_creacion"));
                unidad.setTipo(resultado.getString("tipo"));
                unidad.setCuartel(consultaNombreCuartel_NombreUnidad("cuartel", resultado.getInt("id_cuartel")));
            }


            return unidad;
        } catch (SQLException e) {
            Utilities.mensajeError("Error al cargar Unidad seleccionada");
        }

        return unidad;
    }

    public Soldado cargarSoldadoSeleccionada(int id) {

        Soldado soldado = null;

        String sql = "SELECT * FROM soldado WHERE id = ?";
        PreparedStatement sentence;

        try {
            sentence = conexion.prepareStatement(sql);
            sentence.setInt(1, id);
            ResultSet resultado = sentence.executeQuery();

            soldado = new Soldado();

            if (resultado.next()) {

                soldado.setNombre(resultado.getString("nombre"));
                soldado.setApellidos(resultado.getString("apellidos"));
                soldado.setRango(resultado.getString("rango"));
                soldado.setFechaNacimiento(resultado.getDate("fecha_nacimiento"));
                soldado.setLugarNacimiento(resultado.getString("lugar_nacimiento"));
                soldado.setUnidad(consultaNombreCuartel_NombreUnidad("unidad", resultado.getInt("id_unidad")));
            }


            return soldado;
        } catch (SQLException e) {
            Utilities.mensajeError("Error al cargar Soldado seleccionado");
        }

        return soldado;
    }

    public List<Object[]> buscarCuartel(String busqueda, String campo) {

        List<Object[]> list;
        Object[] fila;
        String sql = "SELECT * FROM cuartel WHERE " + campo + " LIKE '%" + busqueda + "%'";
        list = new ArrayList<>();
        try {
            PreparedStatement sentencia = null;

            sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nombre_cuartel = resultado.getString("nombre_cuartel");
                Double latitud = resultado.getDouble("latitud");
                Double longitud = resultado.getDouble("longitud");
                String localidad = resultado.getString("localidad");
                Boolean actividad = resultado.getBoolean("actividad");

                fila = new Object[]{id, nombre_cuartel, localidad, latitud,
                        longitud, actividad};
                list.add(fila);
            }
            return list;
        } catch (SQLException e) {
            Utilities.mensajeError("Error al buscar Cuartel");
        }

        return list;
    }

    public List<Object[]> buscarUnidad(String busqueda, String campo) {

        List<Object[]> list;
        Object[] fila;
        list = new ArrayList<>();
        String sql;
        if (campo.equalsIgnoreCase("cuartel")) {

            List<Integer> ids = buscarCuartelUnidad(busqueda);

            for (int i = 0; i < ids.size(); i++) {

                try {
                    sql = "SELECT * FROM unidad WHERE id_cuartel = ?";
                    PreparedStatement sentencia = null;

                    sentencia = conexion.prepareStatement(sql);
                    sentencia.setInt(1, ids.get(i));
                    ResultSet resultado = sentencia.executeQuery();
                    while (resultado.next()) {

                        int id = resultado.getInt("id");
                        String nombre_unidad = resultado.getString("nombre_unidad");
                        String tipo = resultado.getString("tipo");
                        int no_tropas = resultado.getInt("no_tropas");
                        Date fecha_creacion = resultado.getDate("fecha_creacion");
                        int id_cuartel = resultado.getInt("id_cuartel");

                        fila = new Object[]{id, nombre_unidad, tipo, no_tropas, fecha_creacion,
                                consultaNombreCuartel_NombreUnidad("cuartel", id_cuartel)};
                        list.add(fila);
                    }

                } catch (SQLException e) {
                    Utilities.mensajeError("Error al buscar Unidad");
                }
            }

            return list;
        } else {

            sql = "SELECT * FROM unidad WHERE " + campo + " LIKE '%" + busqueda + "%'";

            try {
                PreparedStatement sentencia = null;

                sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery();
                while (resultado.next()) {

                    int id = resultado.getInt("id");
                    String nombre_unidad = resultado.getString("nombre_unidad");
                    String tipo = resultado.getString("tipo");
                    int no_tropas = resultado.getInt("no_tropas");
                    Date fecha_creacion = resultado.getDate("fecha_creacion");
                    int id_cuartel = resultado.getInt("id_cuartel");

                    fila = new Object[]{id, nombre_unidad, tipo, no_tropas, fecha_creacion,
                            consultaNombreCuartel_NombreUnidad("cuartel", id_cuartel)};
                    list.add(fila);
                }
                return list;
            } catch (SQLException e) {
                Utilities.mensajeError("Error al buscar Unidad");
            }
        }

        return list;
    }

    public List<Object[]> buscarSoldado(String busqueda, String campo) {

        List<Object[]> list = new ArrayList<>();;
        Object[] fila;
        String sql;

        if (campo.equalsIgnoreCase("unidad")) {
            List<Integer> ids = buscarUnidadSoldado(busqueda);

            for (int i = 0; i < ids.size(); i++) {

                try {
                    sql = "SELECT * FROM soldado WHERE id_unidad = ?";
                    PreparedStatement sentencia = null;

                    sentencia = conexion.prepareStatement(sql);
                    sentencia.setInt(1, ids.get(i));
                    ResultSet resultado = sentencia.executeQuery();
                    while (resultado.next()) {

                        int id = resultado.getInt("id");
                        String nombre = resultado.getString("nombre");
                        String apellidos = resultado.getString("apellidos");
                        String rango = resultado.getString("rango");
                        String lugar_nacimiento = resultado.getString("lugar_nacimiento");
                        Date fecha_nacimiento = resultado.getDate("fecha_nacimiento");
                        int id_unidad = resultado.getInt("id_unidad");

                        fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento,
                                consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                        list.add(fila);
                    }
                } catch (SQLException e) {
                    Utilities.mensajeError("Error al buscar Soldado");
                }
            }

            return list;
        } else {

            if (campo.equalsIgnoreCase("cuartel")) {

                List<Integer> ids = buscarCuartelSoldado(busqueda);

                for (int i = 0; i < ids.size(); i++) {

                    try {
                        sql = "SELECT * FROM soldado WHERE id_unidad = ?";
                        PreparedStatement sentencia = null;

                        sentencia = conexion.prepareStatement(sql);
                        sentencia.setInt(1, ids.get(i));
                        ResultSet resultado = sentencia.executeQuery();
                        while (resultado.next()) {

                            int id = resultado.getInt("id");
                            String nombre = resultado.getString("nombre");
                            String apellidos = resultado.getString("apellidos");
                            String rango = resultado.getString("rango");
                            String lugar_nacimiento = resultado.getString("lugar_nacimiento");
                            Date fecha_nacimiento = resultado.getDate("fecha_nacimiento");
                            int id_unidad = resultado.getInt("id_unidad");

                            fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento,
                                    consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                            list.add(fila);
                        }
                    } catch (SQLException e) {
                        Utilities.mensajeError("Error al buscar Soldado");
                    }
                }

                return list;
            } else {

                sql = "SELECT * FROM soldado WHERE " + campo + " LIKE '%" + busqueda + "%'";
                try {
                    PreparedStatement sentencia = null;

                    sentencia = conexion.prepareStatement(sql);
                    ResultSet resultado = sentencia.executeQuery();
                    while (resultado.next()) {

                        int id = resultado.getInt("id");
                        String nombre = resultado.getString("nombre");
                        String apellidos = resultado.getString("apellidos");
                        String rango = resultado.getString("rango");
                        String lugar_nacimiento = resultado.getString("lugar_nacimiento");
                        Date fecha_nacimiento = resultado.getDate("fecha_nacimiento");
                        int id_unidad = resultado.getInt("id_unidad");

                        fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento,
                                consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                        list.add(fila);
                    }
                    return list;
                } catch (SQLException e) {
                    Utilities.mensajeError("Error al buscar Soldado");
                }
            }
        }

        return list;
    }

    public List<Integer> buscarCuartelUnidad(String busqueda) {

        List<Integer> ids = new ArrayList<>();
        String sql;

        sql = "SELECT id FROM cuartel WHERE nombre_cuartel LIKE '%" + busqueda + "%'";

        try {
            PreparedStatement sentencia = null;

            sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {

                int id = resultado.getInt("id");
                ids.add(id);
            }

        } catch (SQLException e) {
            Utilities.mensajeError("Error al buscar Unidad");
        }

        return ids;
    }

    public List<Integer> buscarUnidadSoldado(String busqueda) {

        List<Integer> ids = new ArrayList<>();
        String sql;

        sql = "SELECT id FROM unidad WHERE nombre_unidad LIKE '%" + busqueda + "%'";

        try {
            PreparedStatement sentencia = null;

            sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {

                int id = resultado.getInt("id");
                ids.add(id);
            }

        } catch (SQLException e) {
            Utilities.mensajeError("Error al buscar Soldado");
        }

        return ids;
    }

    public List<Integer> buscarCuartelSoldado(String busqueda) {

        List<Integer> ids = new ArrayList<>();
        List<Integer> ids_unidad = new ArrayList<>();

        String sql;

        sql = "SELECT id FROM cuartel WHERE nombre_cuartel LIKE '%" + busqueda + "%'";

        try {
            PreparedStatement sentencia = null;

            sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {

                int id = resultado.getInt("id");
                ids.add(id);
            }

            for (int i = 0; i < ids.size(); i++) {
                sql = "SELECT id FROM unidad WHERE id_cuartel = ?";
                sentencia = null;

                sentencia = conexion.prepareStatement(sql);
                sentencia.setInt(1, ids.get(i));
                resultado = sentencia.executeQuery();

                while (resultado.next()) {

                    int id = resultado.getInt("id");
                    ids_unidad.add(id);
                }
            }

        } catch (SQLException e) {
            Utilities.mensajeError("Error al buscar Soldado");
        }

        return ids_unidad;
    }

    public int usoCuartel(int id) {
        int i = 0;
        String sql = "SELECT uso FROM cuartel WHERE id = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public int usoCuartel(String nombre_cuartel) {
        int i = 0;
        String sql = "SELECT uso FROM cuartel WHERE nombre_cuartel = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, nombre_cuartel);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public int usoUnidad(String nombre_unidad) {
        int i = 0;
        String sql = "SELECT uso FROM unidad WHERE nombre_unidad = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, nombre_unidad);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public int usoUnidad(int id) {
        int i = 0;
        String sql = "SELECT uso FROM unidad WHERE id = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public int usoSoldado(int id) {
        int i = 0;
        String sql = "SELECT uso FROM soldado WHERE id = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public int usoSoldado(String nombre, String apellidos) {
        int i = 0;
        String sql = "SELECT uso FROM soldado WHERE nombre = ? and apellidos = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) i = resultado.getInt("uso");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public void cambiarUsoCuartel(int i, int id) {

        String sql = "UPDATE cuartel SET uso = ? WHERE id = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setInt(2, id);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarUsoCuartel(int i, String nombre_cuartel) {

        String sql = "UPDATE cuartel SET uso = ? WHERE nombre_cuartel = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setString(2, nombre_cuartel);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarUsoUnidad(int i, int id) {

        String sql = "UPDATE unidad SET uso = ? WHERE id = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setInt(2, id);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarUsoUnidad(int i, String nombre_unidad) {

        String sql = "UPDATE unidad SET uso = ? WHERE nombre_unidad = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setString(2, nombre_unidad);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarUsoSoldado(int i, int id) {

        String sql = "UPDATE soldado SET uso = ? WHERE id = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setInt(2, id);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarUsoSoldado(int i, String nombre, String apellidos) {

        String sql = "UPDATE soldado SET uso = ? WHERE nombre = ? and apellidos = ?";

        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, i);
            sentencia.setString(2, nombre);
            sentencia.setString(3, apellidos);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Object> prepararExportar() {
        String sql1 = "SELECT * FROM cuartel";
        String sql2 = "SELECT * FROM unidad";
        String sql3 = "SELECT * FROM soldado";
        ArrayList<Object> lo = new ArrayList<>();
        ArrayList<Cuartel> lc;
        ArrayList<Unidad> lu;
        ArrayList<Soldado> ls;
        PreparedStatement sentence = null;

        try {
            lc = new ArrayList<>();
            sentence = conexion.prepareStatement(sql1);
            ResultSet resultado = sentence.executeQuery();

            while (resultado.next()) {
                Cuartel cuartel = new Cuartel();
                    cuartel.setnCuartel(resultado.getString("nombre_cuartel"));
                    cuartel.setLocalidad(resultado.getString("localidad"));
                    cuartel.setActividad(resultado.getBoolean("actividad"));
                    cuartel.setLatitud(resultado.getDouble("latitud"));
                    cuartel.setLongitud(resultado.getDouble("longitud"));
                lc.add(cuartel);
            }
            lo.add(lc);

            lu = new ArrayList<>();
            sentence = conexion.prepareStatement(sql2);
            resultado = sentence.executeQuery();

            while (resultado.next()) {
                Unidad unidad = new Unidad();

                unidad.setnUnidad(resultado.getString("nombre_unidad"));
                unidad.setNoTropas(resultado.getInt("no_tropas"));
                unidad.setFechaCreacion(resultado.getDate("fecha_creacion"));
                unidad.setTipo(resultado.getString("tipo"));
                unidad.setCuartel(consultaNombreCuartel_NombreUnidad("cuartel", resultado.getInt("id_cuartel")));

                lu.add(unidad);
            }
            lo.add(lu);

            ls = new ArrayList<>();
            sentence = conexion.prepareStatement(sql3);
            resultado = sentence.executeQuery();

            while (resultado.next()) {
                Soldado soldado = new Soldado();

                soldado.setNombre(resultado.getString("nombre"));
                soldado.setApellidos(resultado.getString("apellidos"));
                soldado.setRango(resultado.getString("rango"));
                soldado.setFechaNacimiento(resultado.getDate("fecha_nacimiento"));
                soldado.setLugarNacimiento(resultado.getString("lugar_nacimiento"));
                soldado.setUnidad(consultaNombreCuartel_NombreUnidad("unidad", resultado.getInt("id_unidad")));

                ls.add(soldado);
            }
            lo.add(ls);

        } catch (SQLException e) {
            Utilities.mensajeError("Error al preparar datos para documento xml");
        }

        return lo;
    }

    public void cargarImport(ArrayList<Object> pack) {
        ArrayList<Cuartel> lc = (ArrayList<Cuartel>) pack.get(0);
        ArrayList<Unidad> lu = (ArrayList<Unidad>) pack.get(1);
        ArrayList<Soldado> ls = (ArrayList<Soldado>) pack.get(2);

        for (int i = 0; i < lc.size(); i++) {
            guardarCuartelSentencia(lc.get(i).getnCuartel(), lc.get(i).getLocalidad(), lc.get(i).getLatitud(),
                    lc.get(i).getLongitud(), lc.get(i).getActividad());
        }

        for (int i = 0; i < lu.size(); i++) {
            guardarUnidadSentencia(lu.get(i).getnUnidad(), lu.get(i).getTipo(), lu.get(i).getNoTropas(),
                    new Date(lu.get(i).getFechaCreacion().getTime()), lu.get(i).getCuartel());
        }

        for (int i = 0; i < ls.size(); i++) {
            guardarSoldadoSentencia(ls.get(i).getNombre(), ls.get(i).getApellidos(),
                    new Date(ls.get(i).getFechaNacimiento().getTime()), ls.get(i).getRango(),
                    ls.get(i).getLugarNacimiento(), ls.get(i).getUnidad());
        }
    }

    // Metodo que exporta a XML los objetos, en una ruta determinada

    public void exportar(String path) throws ParserConfigurationException,
            TransformerConfigurationException, TransformerException{
        ArrayList<Object> pack = prepararExportar();
        ArrayList<Cuartel> lc = (ArrayList<Cuartel>) pack.get(0);
        ArrayList<Unidad> lu = (ArrayList<Unidad>) pack.get(1);
        ArrayList<Soldado> ls = (ArrayList<Soldado>) pack.get(2);

        DateFormat format = new SimpleDateFormat("dd MM yyyy");

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        Document doc;

        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        DOMImplementation dom = docBuilder.getDOMImplementation();
        doc = dom.createDocument(null, "xml", null);

        Element root = doc.createElement("archivo");
        Element cuarteles = doc.createElement("cuarteles");
        Element unidades = doc.createElement("unidades");
        Element soldados = doc.createElement("soldados");

        doc.getDocumentElement().appendChild(root);
        root.appendChild(cuarteles);
        root.appendChild(unidades);
        root.appendChild(soldados);

        Element nodoCuartel = null, nodoUnidad = null,
                nodoSoldado = null, nodoDatos = null;
        Text txt = null;

        for (Cuartel cuartel : lc) {

            nodoCuartel = doc.createElement("cuartel");
            cuarteles.appendChild(nodoCuartel);

            nodoDatos = doc.createElement("nombre_cuartel");
            nodoCuartel.appendChild(nodoDatos);
            txt = doc.createTextNode(cuartel.getnCuartel());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("localidad");
            nodoCuartel.appendChild(nodoDatos);
            txt = doc.createTextNode(cuartel.getLocalidad());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("latitud");
            nodoCuartel.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(cuartel.getLatitud()));
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("longitud");
            nodoCuartel.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(cuartel.getLongitud()));
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("activo");
            nodoCuartel.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(cuartel.getActividad()));
            nodoDatos.appendChild(txt);
        }

        for (Unidad unidad : lu) {

            nodoUnidad = doc.createElement("unidad");
            unidades.appendChild(nodoUnidad);

            nodoDatos = doc.createElement("nombre_unidad");
            nodoUnidad.appendChild(nodoDatos);
            txt = doc.createTextNode(unidad.getnUnidad());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("tipo");
            nodoUnidad.appendChild(nodoDatos);
            txt = doc.createTextNode(unidad.getTipo());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("n_cuartel");
            nodoUnidad.appendChild(nodoDatos);
            txt = doc.createTextNode(unidad.getCuartel());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("no_tropas");
            nodoUnidad.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(unidad.getNoTropas()));
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("fecha_creacion");
            nodoUnidad.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(format.format(unidad.getFechaCreacion())));
            nodoDatos.appendChild(txt);
        }

        for (Soldado soldado : ls) {

            nodoSoldado = doc.createElement("soldado");
            soldados.appendChild(nodoSoldado);

            nodoDatos = doc.createElement("nombre");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(soldado.getNombre());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("apellidos");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(soldado.getApellidos());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("lugar_nacimiento");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(soldado.getLugarNacimiento());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("fecha_nacimiento");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(String.valueOf(format.format(soldado.getFechaNacimiento())));
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("rango");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(soldado.getRango());
            nodoDatos.appendChild(txt);

            nodoDatos = doc.createElement("n_unidad");
            nodoSoldado.appendChild(nodoDatos);
            txt = doc.createTextNode(soldado.getUnidad());
            nodoDatos.appendChild(txt);
        }

        Source source = new DOMSource(doc);
        Result resultado = new StreamResult(new File(path));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, resultado);
    }

    // Metodo que importa un XML de una ruta determinada, y lo transforma a en un paquete
    // para poder cargarlo mas tarde

    public ArrayList<Object> importar(String path) throws ParserConfigurationException,
            SAXException, IOException, ParseException {

        ArrayList<Object> pack = new ArrayList();
        ArrayList<Cuartel> lcuartel = new ArrayList<>();
        ArrayList<Unidad> lunidad = new ArrayList<>();
        ArrayList<Soldado> lsoldado = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("dd MM yyyy");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;

        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(new File(path));

        // Extraccion de los datos Cuartel

        NodeList cuarteles = doc.getElementsByTagName("cuartel");
        for (int i = 0; i < cuarteles.getLength(); i++) {
            Node cuartel = cuarteles.item(i);
            Element elemento = (Element) cuartel;

            Cuartel c = new Cuartel();

            c.setnCuartel(elemento.getElementsByTagName("nombre_cuartel").item(0)
                    .getChildNodes().item(0).getNodeValue());
            c.setLocalidad(elemento.getElementsByTagName("localidad").item(0)
                    .getChildNodes().item(0).getNodeValue());
            c.setLatitud(Double.valueOf(elemento.getElementsByTagName("latitud").item(0)
                    .getChildNodes().item(0).getNodeValue()));
            c.setLongitud(Double.valueOf(elemento.getElementsByTagName("longitud").item(0)
                    .getChildNodes().item(0).getNodeValue()));
            c.setActividad(Boolean.valueOf(elemento.getElementsByTagName("activo").item(0)
                    .getChildNodes().item(0).getNodeValue()));

            lcuartel.add(c);
        }

        // Extraccion de los datos Unidad

        NodeList unidades = doc.getElementsByTagName("unidad");
        for (int i = 0; i < unidades.getLength(); i++) {
            Node unidad = unidades.item(i);
            Element elemento = (Element) unidad;

            Unidad u = new Unidad();

            u.setnUnidad(elemento.getElementsByTagName("nombre_unidad").item(0)
                    .getChildNodes().item(0).getNodeValue());
            u.setTipo(elemento.getElementsByTagName("tipo").item(0)
                    .getChildNodes().item(0).getNodeValue());
            u.setCuartel(elemento.getElementsByTagName("n_cuartel").item(0)
                    .getChildNodes().item(0).getNodeValue());
            u.setNoTropas(Integer.valueOf(elemento.getElementsByTagName("no_tropas").item(0)
                    .getChildNodes().item(0).getNodeValue()));
            u.setFechaCreacion(format.parse(elemento.getElementsByTagName("fecha_creacion").item(0)
                    .getChildNodes().item(0).getNodeValue()));

            lunidad.add(u);
        }

        // Extraccion de los datos Soldado

        NodeList soldados = doc.getElementsByTagName("soldado");
        for (int i = 0; i < soldados.getLength(); i++) {
            Node soldado = soldados.item(i);
            Element elemento = (Element) soldado;

            Soldado s = new Soldado();

            s.setNombre(elemento.getElementsByTagName("nombre").item(0)
                    .getChildNodes().item(0).getNodeValue());
            s.setApellidos(elemento.getElementsByTagName("apellidos").item(0)
                    .getChildNodes().item(0).getNodeValue());
            s.setUnidad(elemento.getElementsByTagName("n_unidad").item(0)
                    .getChildNodes().item(0).getNodeValue());
            s.setRango(elemento.getElementsByTagName("rango").item(0)
                    .getChildNodes().item(0).getNodeValue());
            s.setFechaNacimiento(format.parse(elemento.getElementsByTagName("fecha_nacimiento").item(0)
                    .getChildNodes().item(0).getNodeValue()));
            s.setLugarNacimiento(elemento.getElementsByTagName("lugar_nacimiento").item(0)
                    .getChildNodes().item(0).getNodeValue());

            lsoldado.add(s);
        }

        // Empaquetado

        pack.add(lcuartel);
        pack.add(lunidad);
        pack.add(lsoldado);

        return pack;
    }
}
