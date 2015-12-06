package com.raider.principal.model;

import com.raider.principal.Util.Values;
import com.raider.principal.base.Cuartel;
import com.raider.principal.base.Soldado;
import com.raider.principal.base.Unidad;
import com.raider.principal.controller.Projectcontroller;

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
import java.util.Properties;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import raider.Util.Utilities;

/**
 * Created by raider on 5/11/15.
 */
public class Projectmodel {

    private Connection conexion;

    public void conexionMysql() throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/ejercito", "root", "pamaloyo18");
    }

    public String login(String usuario, String contrasena) {

        System.out.println(usuario + " " + contrasena);

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

    public void conexionPostgresql() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/ejercito";
        Properties prop = new Properties();
        prop.setProperty("user", "postgres");
        prop.setProperty("password", "pamaloyo18");
        prop.setProperty("ssl", "false");
        conexion = DriverManager.getConnection(url,prop);
    }

    public void guardarCuartel(String nombre_cuartel, String localidad, Double latitud, Double longitud, Boolean actividad) {

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
    }

    public void guardarUnidad(String nombre_unidad, String tipo, int no_tropas, Date fecha_creacion, String cuartel) {

        String sql = "INSERT INTO unidad (nombre_unidad, tipo, no_tropas, fecha_creacion, id_cuartel) VALUES (?,?,?,?,?)";
        int id_cuartel = 0;
        if ((id_cuartel = consultaID("id", "cuartel", "nombre_cuartel", cuartel)) != 0);
        try {
            PreparedStatement sentence = conexion.prepareStatement(sql);
            sentence.setString(1, nombre_unidad);
            sentence.setString(2, tipo);
            sentence.setInt(3, no_tropas);
            sentence.setDate(4, fecha_creacion);
            sentence.setInt(5, id_cuartel);
        } catch (SQLException sqle) {
            Utilities.mensajeError("Error al dar de alta unidad");
        }
    }

    public void guardarSoldado(String nombre, String apellidos, Date fecha_nacimiento, String rango, String lugar_nacimiento, String unidad) {

        String sql = "INSERT INTO soldado (nombre, apellidos, fecha_nacimiento, rango, lugar_nacimiento, id_unidad)" +
                " VALUES (?,?,?,?,?,?)";
        int id_unidad = 0;
        if ((id_unidad = consultaID("id", "unidad", "nombre_unidad", unidad)) != 0);

        try {
            PreparedStatement sentence = conexion.prepareStatement(sql);
            sentence.setString(1, nombre);
            sentence.setString(2, apellidos);
            sentence.setDate(3, fecha_nacimiento);
            

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int consultaID(String select, String table, String campo, String condicion) {

        String sql = "SELECT " + select + " FROM " + table + " WHERE " + campo + " = ?";
        System.out.println(sql);
        int id = 0;
        PreparedStatement sentencia = null;
        try {

            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, condicion);
            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next())id = resultado.getInt(select);

        } catch (SQLException e) {
        e.printStackTrace();}

        return id;
    }

    // Metodos que eliminan el objeto en la posicion seleccionada

    public Cuartel borrarCuartel(ArrayList<Cuartel> c,int n) {

        c.remove(n);
        if( c.size() != 0) {
            if (n == c.size()) {
                n--;
            }
            Cuartel cuartel = c.get(n);
            return cuartel;
        }

        return null;
    }

    public Unidad borrarUnidad(ArrayList<Unidad> u,int n) {

        u.remove(n);
        if( u.size() != 0) {
            if (n == u.size()) {
                n--;
            }
            Unidad unidad = u.get(n);
            return unidad;
        }

        return null;
    }

    public Soldado borrarSoldado(ArrayList<Soldado> s,int n) {

        s.remove(n);
        if( s.size() != 0) {
            if (n == s.size()) {
                n--;
            }
            Soldado soldado = s.get(n);
            return soldado;
        }

        return null;
    }

    public void buscarSoldado(ArrayList<Soldado> listaSoldado, String soldado, DefaultListModel<Soldado> def) {

        def.removeAllElements();
        for (int i = 0; i < listaSoldado.size(); i++) {

            if((listaSoldado.get(i).getNombre().toLowerCase()).contains(soldado)
                    || (listaSoldado.get(i).getRango().toLowerCase()).contains(soldado)
                    || (listaSoldado.get(i).getApellidos().toLowerCase()).contains(soldado)
                    || (listaSoldado.get(i).getUnidad().toLowerCase()).contains(soldado)
                    || (listaSoldado.get(i).getLugarNacimiento().toLowerCase()).contains(soldado)) {
                def.addElement(listaSoldado.get(i));
            }
        }
    }

    public void buscarUnidad(ArrayList<Unidad> listaUnidades, String unidad, DefaultListModel<Unidad> def) {

        def.removeAllElements();
        for (int i = 0; i < listaUnidades.size(); i++) {

            if((listaUnidades.get(i).getCuartel().toLowerCase()).contains(unidad) || (listaUnidades.get(i).getTipo().toLowerCase()).contains(unidad)) {
                def.addElement(listaUnidades.get(i));
            }
        }
    }

    public void buscarCuartel(ArrayList<Cuartel> listaCuarteles, String cuartel, DefaultListModel<Cuartel> def) {

        def.removeAllElements();
        for (int i = 0; i < listaCuarteles.size(); i++) {

            if((listaCuarteles.get(i).getnCuartel().toLowerCase()).contains(cuartel) || (listaCuarteles.get(i).getLocalidad().toLowerCase()).contains(cuartel)) {
                def.addElement(listaCuarteles.get(i));
            }
        }
    }

    // Metodo para guardar Archivo en una ruta fija

    public void guardarArchivo(Object objeto) {

        ObjectOutputStream serializador = null;

        String ruta = "";

        if(Values.PATHmod.isEmpty()) {
            ruta = Values.PATH;
        } else {

            if (Values.PATHmod.isEmpty() == false) {
                ruta = Values.PATHmod;
            }
        }


        try {

            serializador = new ObjectOutputStream(new FileOutputStream(ruta));
            serializador.writeObject(objeto);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        finally {

            if (serializador != null) {

                try {

                    serializador.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    // Metodo para guardar Archivo en una ruta seleccionada

    public void guardarArchivo(String path, Object objeto) {


        ObjectOutputStream serializador = null;

        try {

            serializador = new ObjectOutputStream(new FileOutputStream(path));
            serializador.writeObject(objeto);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        finally {

            if (serializador != null) {

                try {

                    serializador.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    // Metodo que carga el Archivo de la ruta por defecto

    public Object cargarArchivo(String path) throws FileNotFoundException,
            ClassNotFoundException, IOException {

        ArrayList lista = null;

        ObjectInputStream open = new ObjectInputStream(new FileInputStream(path));

        lista = (ArrayList) open.readObject();

        open.close();

        return lista;
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
