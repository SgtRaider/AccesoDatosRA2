package com.raider.principal.controller;

import com.raider.principal.Gui.Login;
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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;;

/**
 * Created by raider on 5/11/15.
 * Con colaboracion de Daniel Cano y Diego Ordoñez
 */
public class Projectcontroller implements ChangeListener, ActionListener, KeyListener {

    //Objetos para cada clase usada

    Ventana v;
    Projectmodel pm;
    Cuartel c;
    Unidad u;
    Soldado s;

    public DateFormat format;
    // Constructor

    public Projectcontroller(Ventana ve) {

        this.v = ve;
        pm = new Projectmodel();

        try {
            pm.conexionMysql();
            Login log = new Login();
            log.setVisible(true);
            rol(pm.login(log.getUsuario(), log.getContrasena()));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");

        // Asignación de Listeners

        v.tabbedPane1.addChangeListener(this);

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

        v.txtBusquedacuartel.addKeyListener(this);
        v.txtBusquedaunidad.addKeyListener(this);
        v.txtBusquedasoldado.addKeyListener(this);

        //Carga de datos externos usando el path estandar o el path modificado,
        // en caso de haber guardado otro path, siempre y cuando exista el Archivo
        // TODO crear fichero de configuración con el path modificado

        iniciarComboBox();
    }

    // Agregamos datos fijos a las combo box (Fijos, que no varian con la introducción de datos)

    public void rol(String rol) {

        if (rol.equalsIgnoreCase("administrador")) {
            visibilidadAdministrador();
        } else {

            if (rol.equalsIgnoreCase("tecnico")) {
                visibilidadTecnico();
            } else {

                if (rol.equalsIgnoreCase("usuario")) {
                    visibilidadUsuario();
                }
            }
        }
    }

    public void visibilidadUsuario() {

        v.btEliminarcuartel.setEnabled(false);
        v.btEliminarsoldado.setEnabled(false);
        v.btEliminarunidad.setEnabled(false);
        v.btGuardarcuartel.setEnabled(false);
        v.btGuardarunidad.setEnabled(false);
        v.btGuardarsoldado.setEnabled(false);
        v.btModificarcuartel.setEnabled(false);
        v.btModificarunidad.setEnabled(false);
        v.btModificarsoldado.setEnabled(false);
        v.cbTipo.setEnabled(false);
        v.cbActividad.setEnabled(false);
        v.cbCuartel.setEnabled(false);
        v.cbRango.setEnabled(false);
        v.cbUnidad.setEnabled(false);
        v.txtApellidos.setEnabled(false);
        v.txtLatitud.setEnabled(false);
        v.txtLocalidad.setEnabled(false);
        v.txtLongitud.setEnabled(false);
        v.txtLugarNacimiento.setEnabled(false);
        v.txtNombre.setEnabled(false);
        v.txtNombrecuartel.setEnabled(false);
        v.txtNombreunidad.setEnabled(false);
        v.txtNoTropas.setEnabled(false);
        v.dcFechanacimiento.setEnabled(false);
        v.dcFechaUnidad.setEnabled(false);
    }

    public void visibilidadTecnico() {

        v.btEliminarcuartel.setEnabled(true);
        v.btEliminarsoldado.setEnabled(true);
        v.btEliminarunidad.setEnabled(true);
        v.btGuardarcuartel.setEnabled(true);
        v.btGuardarunidad.setEnabled(true);
        v.btGuardarsoldado.setEnabled(true);
        v.btModificarcuartel.setEnabled(true);
        v.btModificarunidad.setEnabled(true);
        v.btModificarsoldado.setEnabled(true);
        v.cbTipo.setEnabled(true);
        v.cbActividad.setEnabled(true);
        v.cbCuartel.setEnabled(true);
        v.cbRango.setEnabled(true);
        v.cbUnidad.setEnabled(true);
        v.txtApellidos.setEnabled(true);
        v.txtLatitud.setEnabled(true);
        v.txtLocalidad.setEnabled(true);
        v.txtLongitud.setEnabled(true);
        v.txtLugarNacimiento.setEnabled(true);
        v.txtNombre.setEnabled(true);
        v.txtNombrecuartel.setEnabled(true);
        v.txtNombreunidad.setEnabled(true);
        v.txtNoTropas.setEnabled(true);
    }

    public void visibilidadAdministrador() {

        v.btEliminarcuartel.setEnabled(true);
        v.btEliminarsoldado.setEnabled(true);
        v.btEliminarunidad.setEnabled(true);
        v.btGuardarcuartel.setEnabled(true);
        v.btGuardarunidad.setEnabled(true);
        v.btGuardarsoldado.setEnabled(true);
        v.btModificarcuartel.setEnabled(true);
        v.btModificarunidad.setEnabled(true);
        v.btModificarsoldado.setEnabled(true);
        v.cbTipo.setEnabled(true);
        v.cbActividad.setEnabled(true);
        v.cbCuartel.setEnabled(true);
        v.cbRango.setEnabled(true);
        v.cbUnidad.setEnabled(true);
        v.txtApellidos.setEnabled(true);
        v.txtLatitud.setEnabled(true);
        v.txtLocalidad.setEnabled(true);
        v.txtLongitud.setEnabled(true);
        v.txtLugarNacimiento.setEnabled(true);
        v.txtNombre.setEnabled(true);
        v.txtNombrecuartel.setEnabled(true);
        v.txtNombreunidad.setEnabled(true);
        v.txtNoTropas.setEnabled(true);
    }

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

                //TODO importar base de datos

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


                //TODO Exportar a base de datos

        }
    }

    // Metodo que actualiza la label con la ruta actual de guardado

    // Metodo que actualiza las ComboBox que tienen datos variables

    public void actualizarComboBox(int op) {

        //TODO actualizar label con base de datos
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

                } else {

                    // Modificado de la clase Cuartel
                    //TODO modificar si esta vacia la tabla == true
                    if (true) {
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



                } else {

                    // Modificado de la clase Unidad

                    if (true) {
                        Values.modifyConstant = false;

                        return;
                    }

                    u = new Unidad();

                    u.setnUnidad(v.txtNombreunidad.getText());
                    u.setTipo((String) v.cbTipo.getSelectedItem());
                    u.setFechaCreacion(v.dcFechaUnidad.getDate());
                    u.setCuartel((String) v.cbCuartel.getSelectedItem());
                    u.setNoTropas(Integer.valueOf(v.txtNoTropas.getText()));


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



                } else {

                    // Modificado de la clase Soldado

                    if (true) {
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

                break;

            // Borrado Unidades

            case 1:

                break;

            // Borrado Soldados

            case 2:

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

            //TODO consulas busqueda cuartel
        }

        // Metodo que busca por nombre de unidad y tipo, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarUnidad() {

            //TODO consultas busqueda unidad
        }

        // Metodo que busca por nombre, apellidos, rango, unidad y lugar de nacimiento, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarSoldado() {

            //TODO consultas busqueda soldado
        }

    // Metodo que recoge los cambios de las pestañas, y ejecuta algunos de los metodos anteriores

    @Override
    public void stateChanged(ChangeEvent e) {

        Values.tpConstant = v.tabbedPane1.getSelectedIndex();

        switch (Values.tpConstant) {

            case 0:
                vaciarCuartel();
                break;

            case 1:
                vaciarUnidad();
                break;

            case 2:
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

                    break;
                case "Modificar":
                    if (Utilities.mensajeConfirmacion("¿Esta seguro? \n Asegurese de modificar algun dato") == JOptionPane.NO_OPTION) return;
                    Values.modifyConstant = true;
                    guardarController();
                    break;
                case "Eliminar":
                    if (Utilities.mensajeConfirmacion("¿Esta seguro?") == JOptionPane.NO_OPTION) return;
                    borrarController();
                    break;
            }

        } else {

            String actionCommand = ((JMenuItem) e.getSource()).getActionCommand();

            switch (actionCommand) {

                case "Save":
                    break;
                case "Save as...":
                    break;
                case "Export":
                    exportar();
                    break;
                case "Import":
                    importar();
                    break;
                case "Change PATH":
                    break;
            }
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
