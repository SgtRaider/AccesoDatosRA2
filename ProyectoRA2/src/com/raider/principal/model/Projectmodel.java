package com.raider.principal.model;

import com.raider.principal.Util.Values;
import com.raider.principal.base.Cuartel;
import com.raider.principal.base.Soldado;
import com.raider.principal.base.Unidad;
import com.raider.principal.controller.Projectcontroller;

import javax.lang.model.util.SimpleElementVisitor7;
import javax.swing.*;
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
            String protocolo = configuracion.getProperty("protocolo");
            String servidor = configuracion.getProperty("servidor");
            String puerto = configuracion.getProperty("puerto");
            String baseDatos = configuracion.getProperty("basedatos");
            String usuario = configuracion.getProperty("usuario");
            String contrasena = configuracion.getProperty("contrasena");

            Class.forName(driver).newInstance();
            conexion = DriverManager.getConnection(protocolo + servidor + ":" + puerto + "/" + baseDatos, usuario, contrasena);
            Utilities.mensajeInformacion("Conexion realizada con exito");


        } catch (FileNotFoundException fnfe) {

            Utilities.mensajeInformacion("Preferencias de conexion no encontradas.\n " +
                    "Base de datos cargada con preferencias por defecto.");
                Properties con = new Properties();

                con.setProperty("basedatos", "ejercito");
                con.setProperty("puerto", "3306");
                con.setProperty("servidor", "localhost");
                con.setProperty("contrasena", "pamaloyo18");
                con.setProperty("usuario", "root");
                con.setProperty("protocolo", "jdbc:mysql://");
                con.setProperty("driver", "com.mysql.jdbc.Driver");

                try {
                    con.store(new FileOutputStream("configuracion.props"), "|--- PREFERENCIAS ---|");
                } catch (FileNotFoundException fn) {
                    Utilities.mensajeError("Ruta de configuracion no encontrada");
                } catch (IOException io) {
                    io.printStackTrace();
                }

            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/ejercito", "root", "pamaloyo18");

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error al leer el fichero de configuraci�n");
        } catch (ClassNotFoundException cnfe) {
            JOptionPane.showMessageDialog(null, "No se ha podido cargar el driver de la Base de Datos");
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(null, "No se ha podido conectar con la Base de Datos");
            sqle.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String login(String usuario, String contrasena) {

        String sql = "SELECT usuario, rol FROM usuarios WHERE " +
                "usuario = ? AND password = SHA1(?)";

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
        } catch (SQLException sqle) {
            Utilities.mensajeError("Error Sql");
        }

        return rol;
    }

    // Metodos de guardado

    public void guardarCuartelSentencia(String nombre_cuartel, String localidad, Double latitud, Double longitud, Boolean actividad) {

        if (usoCuartel(nombre_cuartel) == 0) {
            cambiarUsoCuartel(1, nombre_cuartel);
            String sql = "INSERT INTO cuartel (nombre_cuartel, latitud, longitud, actividad, localidad) VALUES (?,?,?,?,?)";
            try {
                PreparedStatement sentence = conexion.prepareStatement(sql);
                sentence.setString(1, nombre_cuartel);
                sentence.setDouble(2, latitud);
                sentence.setDouble(3, longitud);
                sentence.setBoolean(4, actividad);
                sentence.setString(5, localidad);

                sentence.executeUpdate();
            } catch (SQLException e) {
                Utilities.mensajeError("Error al dar de alta cuartel");
            }
            cambiarUsoCuartel(0, nombre_cuartel);
        } else {
            Utilities.mensajeInformacion("No se puede guardar, campo siendo usado por otra persona");
        }
    }

    public void guardarUnidadSentencia(String nombre_unidad, String tipo, int no_tropas, Date fecha_creacion, String cuartel) {

        if (usoUnidad(nombre_unidad) == 0) {
            cambiarUsoUnidad(1, nombre_unidad);
            try {
                conexion.setAutoCommit(false);
                String sql = "INSERT INTO unidad (nombre_unidad, tipo, no_tropas, fecha_creacion, id_cuartel) VALUES (?,?,?,?,?)";
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
            } catch (SQLException sqle) {
                Utilities.mensajeError("Error al dar de alta unidad");
            }
            cambiarUsoUnidad(0, nombre_unidad);
        } else {
            Utilities.mensajeInformacion("No se puede guardar, campo siendo usado por otra persona");
        }
    }

    public void guardarSoldadoSentencia(String nombre, String apellidos, Date fecha_nacimiento, String rango, String lugar_nacimiento, String unidad) {

        if (usoSoldado(nombre, apellidos) == 0) {
            cambiarUsoSoldado(1, nombre, apellidos);
            try {
                conexion.setAutoCommit(false);
                String sql = "INSERT INTO soldado (nombre, apellidos, fecha_nacimiento, rango, lugar_nacimiento, id_unidad)" +
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
            } catch (SQLException e) {
                Utilities.mensajeError("Error al dar de alta soldado");
            }
            cambiarUsoSoldado(0, nombre, apellidos);
        } else {
            Utilities.mensajeInformacion("No se puede guardar, campo siendo usado por otra persona");
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
            Utilities.mensajeInformacion("No se puede borrar, campo siendo usado por otra persona");
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
            Utilities.mensajeInformacion("No se puede borrar, campo siendo usado por otra persona");
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
            Utilities.mensajeInformacion("No se puede borrar, campo siendo usado por otra persona");
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
            Utilities.mensajeInformacion("No se puede modificar, campo siendo usado por otra persona");
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
            } catch (SQLException e) {
                Utilities.mensajeError("Error al modificar unidad");
            }
            cambiarUsoUnidad(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede modificar, campo siendo usado por otra persona");
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
            } catch (SQLException e) {
                Utilities.mensajeError("Error al modificar soldado");
            }
            cambiarUsoSoldado(0, id);
        } else {
            Utilities.mensajeInformacion("No se puede modificar, campo siendo usado por otra persona");
        }
    }

    public int consultaID(String select, String table, String campo, String condicion) {

        String sql = "SELECT " + select + " FROM " + table + " WHERE " + campo + " = ?";
        int id = 0;
        PreparedStatement sentencia = null;
        try {

            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, condicion);
            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next())id = resultado.getInt(select);

        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.mensajeError("Error al cotejar ID");
        }

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
                e.printStackTrace();
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
                    e.printStackTrace();
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
                e.printStackTrace();
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
                    e.printStackTrace();
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
                e.printStackTrace();
            }


        } else {

            if (tabla.equalsIgnoreCase("unidad")) {

                String sql = "SELECT * FROM unidad";
                list = new ArrayList<>();
                try {
                    conexion.setAutoCommit(false);
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

                        fila = new Object[]{id, nombre_unidad, tipo, no_tropas, fecha_creacion, consultaNombreCuartel_NombreUnidad("cuartel", id_cuartel)};
                        list.add(fila);
                    }
                    conexion.commit();
                    return list;
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {

                if (tabla.equalsIgnoreCase("soldado")) {

                    String sql = "SELECT * FROM soldado";
                    list = new ArrayList<>();
                    try {
                        PreparedStatement sentencia = null;
                        conexion.setAutoCommit(false);
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

                            fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento, consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                            list.add(fila);
                        }
                        conexion.commit();
                        return list;
                    } catch (SQLException e) {
                        e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return soldado;
    }

    public List<Object[]> buscarSoldado(String busqueda, String campo) {

        List<Object[]> list;
        Object[] fila;
        String sql = "SELECT * FROM soldado WHERE " + campo + " LIKE '%" + busqueda + "%'";
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

                fila = new Object[]{id, nombre, apellidos, rango, fecha_nacimiento, lugar_nacimiento, consultaNombreCuartel_NombreUnidad("unidad", id_unidad)};
                list.add(fila);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> buscarUnidad(String busqueda, String campo) {

        List<Object[]> list;
        Object[] fila;
        String sql = "SELECT * FROM unidad WHERE " + campo + " LIKE '%" + busqueda + "%'";
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

                fila = new Object[]{id, nombre_unidad, tipo, no_tropas, fecha_creacion, consultaNombreCuartel_NombreUnidad("cuartel", id_cuartel)};
                list.add(fila);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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
            e.printStackTrace();
        }

        return list;
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
            sentencia.setString(1, apellidos);
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

    // Metodo que exporta a XML los objetos, en una ruta determinada

    public void exportar(ArrayList<Object> pack, String path) throws ParserConfigurationException,
            TransformerConfigurationException, TransformerException{

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

    public ArrayList importar(String path) throws ParserConfigurationException,
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
