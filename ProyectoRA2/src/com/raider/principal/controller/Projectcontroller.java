package com.raider.principal.controller;

import com.raider.principal.Gui.Ventana;
import com.raider.principal.Util.Values;
import com.raider.principal.Util.FolderFilter;
import raider.Util.Utilities;
import com.raider.principal.base.Cuartel;
import com.raider.principal.base.Soldado;
import com.raider.principal.base.Unidad;
import com.raider.principal.model.Projectmodel;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;;

/**
 * Created by raider on 5/11/15.
 * Con colaboracion de Daniel Cano y Diego Ordoñez
 */
public class Projectcontroller implements ChangeListener, ActionListener, ListSelectionListener, KeyListener {

    //Objetos para cada clase usada

    Ventana v;
    Projectmodel pm;
    Cuartel c;
    Unidad u;
    Soldado s;

    //ArrayList, DefaultListModel y DateFormat

    public ArrayList<Cuartel> listaCuarteles;
    public ArrayList<Unidad> listaUnidades;
    public ArrayList<Unidad> listaUnidadCuartel;
    public ArrayList<Soldado> listaSoldadoUnidad;
    public ArrayList<Soldado> listaSoldado;
    public ArrayList<Object> datos;
    public ArrayList<Object> pack;

    public DefaultListModel<Cuartel> modelolistacuartel;
    public DefaultListModel<Unidad> modelolistaunidades;
    public DefaultListModel<Soldado> modelolistasoldado;

    public DateFormat format;

    // Constructor

    public Projectcontroller(Ventana ve) {

        this.v = ve;
        pm = new Projectmodel();

        // Instanciado de Array List, DefaultListModel y DateFormat

        listaUnidadCuartel = new ArrayList<>();
        listaSoldadoUnidad = new ArrayList<>();
        listaSoldado = new ArrayList<>();
        listaCuarteles = new ArrayList<>();
        listaUnidades = new ArrayList<>();

        modelolistacuartel = new DefaultListModel<>();
        modelolistaunidades = new DefaultListModel<>();
        modelolistasoldado = new DefaultListModel<>();

        format = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");

        // Asignación de Listeners

        v.tabbedPane1.addChangeListener(this);

        v.ltCuarteles.addListSelectionListener(this);
        v.ltUnidades.addListSelectionListener(this);
        v.ltSoldados.addListSelectionListener(this);

        v.btGuardarsoldado.addActionListener(this);
        v.btGuardarunidad.addActionListener(this);
        v.btGuardarcuartel.addActionListener(this);
        v.btModificarcuartel.addActionListener(this);
        v.btModificarunidad.addActionListener(this);
        v.btModificarsoldado.addActionListener(this);
        v.btEliminarcuartel.addActionListener(this);
        v.btEliminarsoldado.addActionListener(this);
        v.btEliminarunidad.addActionListener(this);

        v.miGuardar.addActionListener(this);
        v.miCambiarPath.addActionListener(this);
        v.miGuardarcomo.addActionListener(this);
        v.miExportar.addActionListener(this);
        v.miImportar.addActionListener(this);

        v.txtBusquedacuartel.addKeyListener(this);
        v.txtBusquedaunidad.addKeyListener(this);
        v.txtBusquedasoldado.addKeyListener(this);

        // Asignación de modelos

        v.ltCuarteles.setModel(modelolistacuartel);
        v.ltUnidades.setModel(modelolistaunidades);
        v.ltSoldados.setModel(modelolistasoldado);
        v.ltTropasunidad.setModel(modelolistasoldado);
        v.ltUnidadescuartel.setModel(modelolistaunidades);

        //Carga de datos externos usando el path estandar o el path modificado,
        // en caso de haber guardado otro path, siempre y cuando exista el Archivo
        // TODO crear fichero de configuración con el path modificado

        File fichero = new File(Values.PATH);

        if (fichero.exists()) {

            try {

                if(Values.PATHmod.isEmpty()) {
                    datos = (ArrayList<Object>) pm.cargarArchivo(Values.PATH);
                } else {

                    if (Values.PATHmod.isEmpty() == false) {
                        datos = (ArrayList<Object>) pm.cargarArchivo(Values.PATHmod);
                    }
                }

                listaCuarteles = (ArrayList<Cuartel>) datos.get(0);
                listaUnidades = (ArrayList<Unidad>) datos.get(1);
                listaSoldado = (ArrayList<Soldado>) datos.get(2);

                listarController();

            } catch (FileNotFoundException fnfe) {
                Utilities.mensajeError("Archivo no encontrado");
            } catch (ClassNotFoundException cnfe) {
                Utilities.mensajeError("Clase no encontrada");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        iniciarComboBox();
    }

    // Agregamos datos fijos a las combo box (Fijos, que no varian con la introducción de datos)

    public void iniciarComboBox() {

        v.cbActividad.addItem("true");
        v.cbActividad.addItem("false");

        v.cbTipo.addItem("Compañia");
        v.cbTipo.addItem("Batallon");
        v.cbTipo.addItem("Regimiento");
        v.cbTipo.addItem("Brigada");
        v.cbTipo.addItem("Division");

        v.cbRango.addItem("Soldado");
        v.cbRango.addItem("Soldado de Primera");
        v.cbRango.addItem("Cabo");
        v.cbRango.addItem("Cabo Primero");
        v.cbRango.addItem("Cabo Mayor");
        v.cbRango.addItem("Sargento");
        v.cbRango.addItem("Sargento Primero");
        v.cbRango.addItem("Brigada");
        v.cbRango.addItem("Subteniente");
        v.cbRango.addItem("Suboficial Mayor");
        v.cbRango.addItem("Alférez");
        v.cbRango.addItem("Teniente");
        v.cbRango.addItem("Capitán");
        v.cbRango.addItem("Comandante");
        v.cbRango.addItem("Teniente Coronel");
        v.cbRango.addItem("Coronel");
        v.cbRango.addItem("General de Brigada");
        v.cbRango.addItem("General de Division");
        v.cbRango.addItem("Teniente General");
        v.cbRango.addItem("General de Ejército");
    }

    // Metodo que cambia la ruta de guardado, por una a elegir

    public void cambiarPath() {

        JFileChooser jfc = new JFileChooser();

        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        jfc.setDialogTitle("Cambiar Path");
        int val = jfc.showSaveDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            Values.PATHmod = selectedFile.getAbsolutePath() + File.separator + "ArchivoEjercito.dat";
            actualizarLabel();
        }
    }

    // Metodo para guardar en la ruta seleccionada

    public void guardarComo() {

        JFileChooser jfc = new JFileChooser();

        jfc.setFileFilter(new FolderFilter());
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        jfc.setDialogTitle("Save as...");
        int val = jfc.showSaveDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String path = selectedFile.getAbsolutePath() + ".dat";
            pm.guardarArchivo(path, packInfo());
        }
    }

    // Metodo que carga los datos de un archivo XML en los ARRAYLIST y posteriormente los lista

    public void importar() {

        JFileChooser jfc = new JFileChooser();

        jfc.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        jfc.setDialogTitle("Importar XML");
        int val = jfc.showSaveDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            try {

                datos = pm.importar(path);

                listaCuarteles.clear();
                listaUnidades.clear();
                listaSoldado.clear();

                listaCuarteles = (ArrayList<Cuartel>) datos.get(0);
                listaUnidades = (ArrayList<Unidad>) datos.get(1);
                listaSoldado = (ArrayList<Soldado>) datos.get(2);

                System.out.println(listaCuarteles.get(0).getnCuartel());

                listarController();

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (SAXException saxe) {
                saxe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }
    }

    // Metodo que convierte los datos empaquetados en un archivo XML que se guarda en la ruta seleccionada

    public void exportar() {

        JFileChooser jfc = new JFileChooser();

        jfc.setFileFilter(new FolderFilter());
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        jfc.setDialogTitle("Exportar XML");
        int val = jfc.showSaveDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String path = selectedFile.getAbsolutePath() + ".xml";

            try {

                pm.exportar(packInfo(), path);

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (TransformerException te) {
                te.printStackTrace();
            }
        }
    }

    // Metodo que actualiza la label con la ruta actual de guardado

    public void actualizarLabel() {
        v.lbRuta.setText("Ruta Guardado: " + Values.PATHmod);
    }

    // Metodo que actualiza las ComboBox que tienen datos variables

    public void actualizarComboBox(int op) {

        if (op == 0) {

            v.cbCuartel.removeAllItems();

            for (int i = 0; i < listaCuarteles.size(); i++) {
                v.cbCuartel.addItem(listaCuarteles.get(i).getnCuartel());
            }
        } else {

            if (op == 1) {

                v.cbUnidad.removeAllItems();

                for (int i = 0; i < listaUnidades.size(); i++) {
                    v.cbUnidad.addItem(listaUnidades.get(i).getnUnidad());
                }
            }
        }
    }

    // Metodo que controla el guardado o modificado del objeto, dependiendo de la pestaña seleccionada

    public void guardarController() {

        switch (Values.tpConstant) {

            // Guardado de la clase Cuartel

            case 0:

                if(v.txtNombrecuartel.getText().equals("") || v.txtLocalidad.getText().equals("")) {
                    Utilities.mensajeError("Introduzca al menos nombre de cuartel y localidad");
                    return;
                }

                if (v.txtLatitud.getText().equals("")) v.txtLatitud.setText("0");
                if (v.txtLongitud.getText().equals("")) v.txtLongitud.setText("0");

                if(Values.modifyConstant == false) {

                    c = new Cuartel();

                    c.setnCuartel(v.txtNombrecuartel.getText());
                    c.setLocalidad(v.txtLocalidad.getText());
                    c.setLatitud(Double.valueOf(v.txtLatitud.getText()));
                    c.setLongitud(Double.valueOf(v.txtLongitud.getText()));

                    if(v.cbActividad.getSelectedItem() == "true") {
                        c.setActividad(true);
                    } else {
                        c.setActividad(false);
                    }

                    listaCuarteles.add(c);
                } else {

                    // Modificado de la clase Cuartel

                    if (listaCuarteles.isEmpty()) {
                        Values.modifyConstant = false;
                        return;
                    }

                    c = new Cuartel();

                    c.setnCuartel(v.txtNombrecuartel.getText());
                    c.setLocalidad(v.txtLocalidad.getText());
                    c.setLatitud(Double.valueOf(v.txtLatitud.getText()));
                    c.setLongitud(Double.valueOf(v.txtLongitud.getText()));

                    if(v.cbActividad.getSelectedItem() == "true") {
                        c.setActividad(true);
                    } else {
                        c.setActividad(false);
                    }

                    String cuartel = listaCuarteles.get(Values.posCuarteles).getnCuartel();

                    modunidadCuartel(cuartel, v.txtNombrecuartel.getText());

                    listaCuarteles.set(Values.posCuarteles, c);

                    actualizarComboBox(0);

                    Values.modifyConstant = false;
                }
                break;

            // Guardado de la clase Unidad

            case 1:

                if(v.txtNombreunidad.getText().equals("")) {
                    Utilities.mensajeError("Introduzca al menos nombre de unidad");
                    return;
                }

                if (v.txtNoTropas.getText().equals("")) v.txtNoTropas.setText("0");

                if(Values.modifyConstant == false) {

                    u = new Unidad();

                    u.setnUnidad(v.txtNombreunidad.getText());
                    u.setTipo((String) v.cbTipo.getSelectedItem());
                    u.setFechaCreacion(v.dcFechaUnidad.getDate());
                    u.setCuartel((String) v.cbCuartel.getSelectedItem());
                    u.setNoTropas(Integer.valueOf(v.txtNoTropas.getText()));

                    listaUnidades.add(u);

                } else {

                    // Modificado de la clase Unidad

                    if (listaUnidades.isEmpty()) {
                        Values.modifyConstant = false;

                        return;
                    }

                    u = new Unidad();

                    u.setnUnidad(v.txtNombreunidad.getText());
                    u.setTipo((String) v.cbTipo.getSelectedItem());
                    u.setFechaCreacion(v.dcFechaUnidad.getDate());
                    u.setCuartel((String) v.cbCuartel.getSelectedItem());
                    u.setNoTropas(Integer.valueOf(v.txtNoTropas.getText()));

                    String unidad = listaUnidades.get(Values.posUnidades).getnUnidad();

                    listaUnidades.set(Values.posUnidades, u);

                    modsoldadoUnidad(unidad, v.txtNombreunidad.getText());

                    actualizarComboBox(1);

                    Values.modifyConstant = false;
                }
                break;

            // Guardado de la clase Soldado

            case 2:

                if(v.txtNombre.getText().equals("") || v.txtApellidos.getText().equals("")) {
                    Utilities.mensajeError("Introduzca al menos nombre y apellidos");
                    return;
                }

                if(Values.modifyConstant == false) {

                    s = new Soldado();

                    s.setNombre(v.txtNombre.getText());
                    s.setApellidos(v.txtApellidos.getText());
                    s.setFechaNacimiento(v.dcFechanacimiento.getDate());
                    s.setLugarNacimiento(v.txtLugarNacimiento.getText());
                    s.setUnidad((String) v.cbUnidad.getSelectedItem());
                    s.setRango((String) v.cbRango.getSelectedItem());

                    listaSoldado.add(s);

                } else {

                    // Modificado de la clase Soldado

                    if (listaSoldado.isEmpty()) {
                        Values.modifyConstant = false;

                        return;
                    }

                    s = new Soldado();

                    s.setNombre(v.txtNombre.getText());
                    s.setApellidos(v.txtApellidos.getText());
                    s.setFechaNacimiento(v.dcFechanacimiento.getDate());
                    s.setLugarNacimiento(v.txtLugarNacimiento.getText());
                    s.setUnidad((String) v.cbUnidad.getSelectedItem());
                    s.setRango((String) v.cbRango.getSelectedItem());

                    listaSoldado.set(Values.posSoldados, s);

                    Values.modifyConstant = false;
                }
                break;
        }
    }

    // Metodo que controla el borrado del objeto, dependiendo de la pestaña seleccionada

    public void borrarController() {

        switch (Values.tpConstant) {

            // Borrado Cuarteles

            case 0:
                if (listaCuarteles.isEmpty()) return;
                cargarController(pm.borrarCuartel(listaCuarteles, v.ltCuarteles.getSelectedIndex()));
                break;

            // Borrado Unidades

            case 1:
                if (listaUnidades.isEmpty()) return;
                cargarController(pm.borrarUnidad(listaUnidades, v.ltUnidades.getSelectedIndex()));
                break;

            // Borrado Soldados

            case 2:
                if (listaSoldado.isEmpty()) return;
                cargarController(pm.borrarSoldado(listaSoldado, v.ltSoldados.getSelectedIndex()));
                break;
        }
    }

    // Metodo que controla el listado de los objetos, dependiendo de la pestaña seleccionada

    public void listarController() {

        switch (Values.tpConstant) {

            case 0:
                listarCuartel();
                actualizarComboBox(0);
                break;
            case 1:
                listarUnidad();
                actualizarComboBox(1);
                break;
            case 2:
                listarSoldado();
                break;
        }
    }

    // Metodos que cargan los campos del objeto seleccionado

    public void cargarController(Cuartel cuartel) {

        v.txtNombrecuartel.setText(cuartel.getnCuartel());
        v.txtLocalidad.setText(cuartel.getLocalidad());
        v.txtLatitud.setText(String.valueOf(cuartel.getLatitud()));
        v.txtLongitud.setText(String.valueOf(cuartel.getLongitud()));

        if (cuartel.getActividad() == true) {

            v.cbActividad.setSelectedItem("true");
        } else {

            v.cbActividad.setSelectedItem("false");
        }
    }

    public void cargarController(Unidad unidad) {

        v.txtNombreunidad.setText(unidad.getnUnidad());
        v.cbTipo.setSelectedItem(unidad.getTipo());
        v.cbCuartel.setSelectedItem(unidad.getCuartel());
        v.txtNoTropas.setText(String.valueOf(unidad.getNoTropas()));
        v.dcFechaUnidad.setDate(unidad.getFechaCreacion());
    }

    public void cargarController(Soldado soldado) {

        v.txtNombre.setText(soldado.getNombre());
        v.txtApellidos.setText(soldado.getApellidos());
        v.dcFechanacimiento.setDate(soldado.getFechaNacimiento());
        v.cbRango.setSelectedItem(soldado.getRango());
        v.txtLugarNacimiento.setText(soldado.getLugarNacimiento());
        v.cbUnidad.setSelectedItem(soldado.getUnidad());
    }

    // Metodo para listar en JList de Cuarteles

    public void listarCuartel() {

        System.out.println("hola");
        modelolistacuartel.removeAllElements();
        for (Cuartel c: listaCuarteles) {

            modelolistacuartel.addElement(c);
        }
    }

    // Metodo para listar en JList de Unidades

    public void listarUnidad() {

        modelolistaunidades.removeAllElements();
        for (Unidad u: listaUnidades) {
            modelolistaunidades.addElement(u);
        }
    }

    // Metodo para listar en JList de Soldado

    public void listarSoldado() {

        modelolistasoldado.removeAllElements();
        for (Soldado s: listaSoldado) {
            modelolistasoldado.addElement(s);
        }
    }

    // Metodo para listar Unidades por Cuartel en la pestaña de Cuarteles

    public void listarUnidadCuartel() {

        unidadCuartel(Values.posCuarteles);

        modelolistaunidades.removeAllElements();
        for (Unidad u: listaUnidadCuartel) {
            modelolistaunidades.addElement(u);
        }
    }

    // Metodo que busca las Unidades que tiene cada Cuartel y las añade en el ARRAYLIST listaUnidadCuartel

    public void unidadCuartel(int pos) {

        listaUnidadCuartel.removeAll(listaUnidadCuartel);

        for (int i = 0; i < listaUnidades.size(); i++) {

            if(listaUnidades.get(i).getCuartel().equalsIgnoreCase(listaCuarteles.get(pos).getnCuartel())) {
                listaUnidadCuartel.add(listaUnidades.get(i));
            }
        }
    }

    // Metodo que cambia el Cuartel de las Unidades en caso de que el Cuartel sea modificado

    public void modunidadCuartel(String cuartel , String cuartelCambiado) {

        listaUnidadCuartel.removeAll(listaUnidadCuartel);

        for (int i = 0; i < listaUnidades.size(); i++) {

            if(listaUnidades.get(i).getCuartel().equalsIgnoreCase(cuartel)) {
                listaUnidades.get(i).setCuartel(cuartelCambiado);
            }
        }
    }

    // Metodo para listar Soldados por Unidad en la pestaña de Unidades

    public void listarSoldadoUnidad() {

        soldadoUnidad(Values.posUnidades);

        modelolistasoldado.removeAllElements();
        for (Soldado s: listaSoldadoUnidad) {
            modelolistasoldado.addElement(s);
        }
    }

    // Metodo que busca los Soldados que tiene cada Unidad y los añade en el ARRAYLIST listaSoldadoUnidad

    public void soldadoUnidad(int pos) {

        listaSoldadoUnidad.removeAll(listaSoldadoUnidad);

        for (int i = 0; i < listaSoldado.size(); i++) {

            if (listaSoldado.get(i).getUnidad().equalsIgnoreCase(listaUnidades.get(pos).getnUnidad())) {
                listaSoldadoUnidad.add(listaSoldado.get(i));
            }
        }
    }

    // Metodo que cambia la Unidad de los Soldados en caso de que la Unidad sea modificada

    public void modsoldadoUnidad(String unidad, String unidadCambiada) {

        listaSoldadoUnidad.removeAll(listaSoldadoUnidad);

        for (int i = 0; i < listaSoldado.size(); i++) {

            if (listaSoldado.get(i).getUnidad().equalsIgnoreCase(unidad)) {
                listaSoldado.get(i).setUnidad(unidadCambiada);
            }
        }
    }

    // Metodos para vaciar/limpiar los campos

    public void vaciarCuartel() {

        v.txtNombrecuartel.setText("");
        v.txtLocalidad.setText("");
        v.txtLatitud.setText("");
        v.txtLongitud.setText("");
        v.cbActividad.setSelectedItem(null);
    }

    public void vaciarUnidad() {

        v.txtNombreunidad.setText("");
        v.cbTipo.setSelectedItem(null);
        v.cbCuartel.setSelectedItem(null);
        v.txtNoTropas.setText("");
        v.dcFechaUnidad.setDate(null);
    }

    public void vaciarSoldado() {

        v.txtNombre.setText("");
        v.txtApellidos.setText("");
        v.dcFechanacimiento.setDate(null);
        v.cbRango.setSelectedItem(null);
        v.txtLugarNacimiento.setText("");
        v.cbUnidad.setSelectedItem(null);
    }

    // Metodos de busqueda

        // Metodo que busca por nombre de cuartel y localidad, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarCuartel() {

            if (v.txtBusquedacuartel.getText().toString().isEmpty() == false) {

                pm.buscarCuartel(listaCuarteles, v.txtBusquedacuartel.getText().toLowerCase(), modelolistacuartel);

            } else {

                if (v.txtBusquedacuartel.getText().isEmpty()) {
                    listarController();
                }
            }
        }

        // Metodo que busca por nombre de unidad y tipo, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarUnidad() {

            if (v.txtBusquedaunidad.getText().toString().isEmpty() == false) {

                pm.buscarUnidad(listaUnidades, v.txtBusquedaunidad.getText().toLowerCase(), modelolistaunidades);

            } else {

                if (v.txtBusquedaunidad.getText().isEmpty()) {
                    listarController();
                }
            }
        }

        // Metodo que busca por nombre, apellidos, rango, unidad y lugar de nacimiento, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarSoldado() {

            if (v.txtBusquedasoldado.getText().toString().isEmpty() == false) {

                pm.buscarSoldado(listaSoldado, v.txtBusquedasoldado.getText().toLowerCase(), modelolistasoldado);

            } else {

                if (v.txtBusquedasoldado.getText().isEmpty()) {
                    listarController();
                }
            }
        }

    // Metodo que empaqueta los ARRAYLIST para su posterior exportado u guardado

    public ArrayList packInfo() {

        pack = new ArrayList();

        pack.add(listaCuarteles);
        pack.add(listaUnidades);
        pack.add(listaSoldado);

        return pack;
    }

    // Metodo que recoge los cambios de las pestañas, y ejecuta algunos de los metodos anteriores

    @Override
    public void stateChanged(ChangeEvent e) {

        Values.tpConstant = v.tabbedPane1.getSelectedIndex();

        switch (Values.tpConstant) {

            case 0:
                listarController();
                modelolistaunidades.removeAllElements();
                vaciarCuartel();
                break;

            case 1:
                listarController();
                modelolistasoldado.removeAllElements();
                vaciarUnidad();
                break;

            case 2:
                listarController();
                vaciarSoldado();
                break;
        }
    }

    // Metodo que escucha los eventos de JButton y JMenuItem, y ejecuta los metodos correspondientes

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source.getClass() == JButton.class) {

            String actionCommand = ((JButton) e.getSource()).getActionCommand();

            switch (actionCommand) {

                case "Guardar":
                    guardarController();
                    if(v.cbGuargarAutomatico.isSelected()) {
                        pm.guardarArchivo(packInfo());
                    } else {
                        System.out.println(listaCuarteles.get(0).toString());
                    }
                    listarController();
                    break;
                case "Modificar":
                    if (Utilities.mensajeConfirmacion("¿Esta seguro? \n Asegurese de modificar algun dato") == JOptionPane.NO_OPTION) return;
                    Values.modifyConstant = true;
                    guardarController();
                    listarController();
                    break;
                case "Eliminar":
                    if (Utilities.mensajeConfirmacion("¿Esta seguro?") == JOptionPane.NO_OPTION) return;
                    borrarController();
                    listarController();
                    break;
            }

        } else {

            String actionCommand = ((JMenuItem) e.getSource()).getActionCommand();

            switch (actionCommand) {

                case "Save":
                    pm.guardarArchivo(packInfo());
                    break;
                case "Save as...":
                    guardarComo();
                    break;
                case "Export":
                    exportar();
                    break;
                case "Import":
                    importar();
                    break;
                case "Change PATH":
                    cambiarPath();
                    break;
            }
        }
    }

    // Metodo que escucha los eventos de selección en los JLIST, recoge la posicion seleccionada en un Value y
    // ejecuta los metodos correspondientes

    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (e.getSource() == v.ltCuarteles) {

            Values.posCuarteles = v.ltCuarteles.getSelectedIndex();

            if (Values.posCuarteles == -1) return;

            cargarController(listaCuarteles.get(Values.posCuarteles));
            listarUnidadCuartel();
        }

        if (e.getSource() == v.ltUnidades) {

            Values.posUnidades = v.ltUnidades.getSelectedIndex();

            if (Values.posUnidades == -1) return;

            cargarController(listaUnidades.get(Values.posUnidades));
            listarSoldadoUnidad();
        }

        if (e.getSource() == v.ltSoldados) {
            Values.posSoldados = v.ltSoldados.getSelectedIndex();

            if (Values.posSoldados == -1) return;

            cargarController(listaSoldado.get(Values.posSoldados));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    // Metodo que escucha la accion de dejar de pulsar una tecla y aplica los metodos de buscar
    // dependiendo la pestaña

    @Override
    public void keyReleased(KeyEvent e) {

        switch (Values.tpConstant) {

            case 0:
                controlBuscarCuartel();
                break;

            case 1:
                controlBuscarUnidad();
                break;

            case 2:
                controlBuscarSoldado();
                break;
        }
    }
}
