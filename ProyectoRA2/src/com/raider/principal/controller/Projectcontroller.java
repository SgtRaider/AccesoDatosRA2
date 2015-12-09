package com.raider.principal.controller;

import com.raider.principal.Gui.Login;
import com.raider.principal.Gui.Preferencias;
import com.raider.principal.Gui.Ventana;
import com.raider.principal.util.Values;
import com.raider.principal.util.FolderFilter;
import com.raider.principal.procesos.Listado;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;;

/**
 * Created by raider on 5/11/15.
 * Con colaboracion de Daniel Cano y Diego Ordoñez
 */
public class Projectcontroller implements ListSelectionListener, ChangeListener, ActionListener, KeyListener {

    //Objetos para cada clase usada

    private Ventana v;
    private Projectmodel pm;
    private Cuartel c;
    private Unidad u;
    private Soldado s;

    private DefaultTableModel defmodelcuartel;
    private DefaultTableModel defmodelunidad;
    private DefaultTableModel defmodelsoldado;

    private Preferencias pref;
    private Login log;

    public DateFormat format;
    // Constructor

    public Projectcontroller(Ventana ve) {

        this.v = ve;
        pm = new Projectmodel();

        pref = new Preferencias();
        pref.setVisible(false);

        try {
            pm.conexion();
            log = new Login();
            log.setVisible(true);
            rol(pm.login(log.getUsuario(), log.getContrasena()));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        generarTablas();
        iniciarComboBox();

        format = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
        DefaultTableModel defmodelcuartel;
        DefaultTableModel defmodelunidad;
        DefaultTableModel defmodelsoldado;

        v.tCuartel.setRowSelectionAllowed(true);
        v.tUnidad.setRowSelectionAllowed(true);
        v.tSoldado.setRowSelectionAllowed(true);

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

        v.tCuartel.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (v.tCuartel.isRowSelected(v.tCuartel.getSelectedRow())) {
                    Values.idCuartel = Integer.valueOf(v.tCuartel.getValueAt(v.tCuartel.getSelectedRow(), 0).toString());
                    cargarController(pm.cargarCuartelSeleccionado(Values.idCuartel));
                }

            }
        });

        v.tUnidad.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (v.tUnidad.isRowSelected(v.tUnidad.getSelectedRow())) {
                    Values.idUnidad = Integer.valueOf(v.tUnidad.getValueAt(v.tUnidad.getSelectedRow(), 0).toString());
                    cargarController(pm.cargarUnidadSeleccionada(Values.idUnidad));
                }

            }
        });

        v.tSoldado.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (v.tSoldado.isRowSelected(v.tSoldado.getSelectedRow())) {
                    Values.idSoldado = Integer.valueOf(v.tSoldado.getValueAt(v.tSoldado.getSelectedRow(), 0).toString());
                    cargarController(pm.cargarSoldadoSeleccionada(Values.idSoldado));
                }

            }
        });

        v.miPreferencias.addActionListener(this);
        v.miExportar.addActionListener(this);
        v.miImportar.addActionListener(this);
        v.miLogin.addActionListener(this);

        v.txtBusquedacuartel.addKeyListener(this);
        v.txtBusquedaunidad.addKeyListener(this);
        v.txtBusquedasoldado.addKeyListener(this);

        listarCuartel();
        Listado proceso = new Listado(Projectcontroller.this, v);
        proceso.start();
    }

    // Agregamos datos fijos a las combo box (Fijos, que no varian con la introducción de datos)

    public void rol(String rol) {

        if (rol != null) {

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
        } else {

            sinVisibilidad();
        }
    }

    public void generarTablas() {

        defmodelcuartel = new DefaultTableModel();
        v.tCuartel.setModel(defmodelcuartel);
        defmodelcuartel.addColumn("ID");
        defmodelcuartel.addColumn("Nombre Cuartel");
        defmodelcuartel.addColumn("Localidad");
        defmodelcuartel.addColumn("Latitud");
        defmodelcuartel.addColumn("Longitud");
        defmodelcuartel.addColumn("Actividad");

        TableColumnModel cc = v.tCuartel.getColumnModel();
        cc.getColumn(0).setPreferredWidth(10);
        cc.getColumn(3).setPreferredWidth(35);
        cc.getColumn(4).setPreferredWidth(35);
        cc.getColumn(5).setPreferredWidth(35);

        defmodelunidad = new DefaultTableModel();
        v.tUnidad.setModel(defmodelunidad);
        defmodelunidad.addColumn("ID");
        defmodelunidad.addColumn("Nombre Unidad");
        defmodelunidad.addColumn("Tipo");
        defmodelunidad.addColumn("No Tropas");
        defmodelunidad.addColumn("Fecha Creacion");
        defmodelunidad.addColumn("Cuartel");

        TableColumnModel cu = v.tUnidad.getColumnModel();
        cu.getColumn(0).setPreferredWidth(5);
        cu.getColumn(2).setPreferredWidth(30);
        cu.getColumn(3).setPreferredWidth(40);
        cu.getColumn(5).setPreferredWidth(35);

        defmodelsoldado = new DefaultTableModel();
        v.tSoldado.setModel(defmodelsoldado);
        defmodelsoldado.addColumn("ID");
        defmodelsoldado.addColumn("Nombre");
        defmodelsoldado.addColumn("Apellidos");
        defmodelsoldado.addColumn("Rango");
        defmodelsoldado.addColumn("Fecha Nacimiento");
        defmodelsoldado.addColumn("Lugar Nacimiento");
        defmodelsoldado.addColumn("Unidad");

        TableColumnModel cs = v.tSoldado.getColumnModel();
        cs.getColumn(0).setPreferredWidth(15);
    }

    public void listarCuartel() {

        if (Values.warningBaseDatos == false) {
            List<Object[]> list = pm.listar("cuartel");

            if (list != null) {

                defmodelcuartel.setNumRows(0);
                for (int i = 0;i < list.size(); i++) {

                    defmodelcuartel.addRow(list.get(i));
                }
            }
        }
    }

    public void listarUnidad() {

        if (Values.warningBaseDatos == false) {
            List<Object[]> list = pm.listar("unidad");

            if (list != null) {
                defmodelunidad.setNumRows(0);
                for (int i = 0; i < list.size(); i++) {

                    defmodelunidad.addRow(list.get(i));
                }
            }
        }
    }

    public void listarSoldado() {

        if (Values.warningBaseDatos == false) {
            List<Object[]> list = pm.listar("soldado");

            if (list != null) {

                defmodelsoldado.setNumRows(0);
                for (int i = 0; i < list.size(); i++) {

                    defmodelsoldado.addRow(list.get(i));
                }
            }
        }
    }

    public void sinVisibilidad() {

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
        v.txtBusquedasoldado.setEnabled(false);
        v.txtBusquedaunidad.setEnabled(false);
        v.txtBusquedacuartel.setEnabled(false);
        v.tCuartel.setEnabled(false);
        v.tUnidad.setEnabled(false);
        v.tSoldado.setEnabled(false);
        v.cbTablaCuartel.setEnabled(false);
        v.cbTablaUnidad.setEnabled(false);
        v.cbTablaSoldado.setEnabled(false);
        v.miExportar.setEnabled(false);
        v.miImportar.setEnabled(false);
        pref.setEditable(false);
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
        v.txtBusquedasoldado.setEnabled(true);
        v.txtBusquedaunidad.setEnabled(true);
        v.txtBusquedacuartel.setEnabled(true);
        v.tCuartel.setEnabled(true);
        v.tUnidad.setEnabled(true);
        v.tSoldado.setEnabled(true);
        v.cbTablaCuartel.setEnabled(true);
        v.cbTablaUnidad.setEnabled(true);
        v.cbTablaSoldado.setEnabled(true);
        v.miExportar.setEnabled(false);
        v.miImportar.setEnabled(false);
        pref.setEditable(false);
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
        v.txtBusquedasoldado.setEnabled(true);
        v.txtBusquedaunidad.setEnabled(true);
        v.txtBusquedacuartel.setEnabled(true);
        v.tCuartel.setEnabled(true);
        v.tUnidad.setEnabled(true);
        v.tSoldado.setEnabled(true);
        v.cbTablaCuartel.setEnabled(true);
        v.cbTablaUnidad.setEnabled(true);
        v.cbTablaSoldado.setEnabled(true);
        v.miExportar.setEnabled(true);
        v.miImportar.setEnabled(false);
        pref.setEditable(false);
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
        v.txtBusquedasoldado.setEnabled(true);
        v.txtBusquedaunidad.setEnabled(true);
        v.txtBusquedacuartel.setEnabled(true);
        v.tCuartel.setEnabled(true);
        v.tUnidad.setEnabled(true);
        v.tSoldado.setEnabled(true);
        v.cbTablaCuartel.setEnabled(true);
        v.cbTablaUnidad.setEnabled(true);
        v.cbTablaSoldado.setEnabled(true);
        v.miExportar.setEnabled(true);
        v.miImportar.setEnabled(true);
        v.dcFechanacimiento.setEnabled(true);
        v.dcFechaUnidad.setEnabled(true);
        pref.setEditable(true);
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

        v.cbTablaCuartel.addItem("nombre_cuartel");
        v.cbTablaCuartel.addItem("localidad");

        if (!Values.driver.equalsIgnoreCase("org.postgresql.Driver")) {
            v.cbTablaCuartel.addItem("longitud");
            v.cbTablaCuartel.addItem("latitud");
        }

        v.cbTablaUnidad.addItem("nombre_unidad");
        v.cbTablaUnidad.addItem("tipo");
        if (!Values.driver.equalsIgnoreCase("org.postgresql.Driver")) {
            v.cbTablaUnidad.addItem("no_tropas");
        }
        v.cbTablaUnidad.addItem("cuartel");

        v.cbTablaSoldado.addItem("nombre");
        v.cbTablaSoldado.addItem("apellidos");
        v.cbTablaSoldado.addItem("rango");
        v.cbTablaSoldado.addItem("lugar_nacimiento");
        v.cbTablaSoldado.addItem("unidad");
        v.cbTablaSoldado.addItem("cuartel");
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
                pm.cargarImport(pm.importar(path));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
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
                pm.exportar(path);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }

        }
    }

    // Metodo que actualiza las ComboBox que tienen datos variables

    public void actualizarComboBox(int op) {

        if (Values.warningBaseDatos == false) {
            List<String> cb = pm.consultaActualizarComboBox(op);

            if (op == 0) {

                v.cbCuartel.removeAllItems();
                for (int i = 0; i < cb.size(); i++) {
                    v.cbCuartel.addItem(cb.get(i));
                }

            } else {

                if (op == 1) {

                    v.cbUnidad.removeAllItems();
                    for (int i = 0; i < cb.size(); i++) {
                        v.cbUnidad.addItem(cb.get(i));
                    }
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

                    if(v.cbActividad.getSelectedItem() == "true") {
                        pm.guardarCuartelSentencia(v.txtNombrecuartel.getText(), v.txtLocalidad.getText(),
                                Double.valueOf(v.txtLatitud.getText()), Double.valueOf(v.txtLongitud.getText()), true);
                    } else {
                        pm.guardarCuartelSentencia(v.txtNombrecuartel.getText(), v.txtLocalidad.getText(),
                                Double.valueOf(v.txtLatitud.getText()), Double.valueOf(v.txtLongitud.getText()), false);
                    }

                    actualizarComboBox(0);

                } else {

                    // Modificado de la clase Cuartel

                    if(v.cbActividad.getSelectedItem() == "true") {
                        pm.modificarCuartelSentencia(Values.idCuartel, v.txtNombrecuartel.getText(),
                                Double.valueOf(v.txtLatitud.getText()), Double.valueOf(v.txtLongitud.getText()),
                                true, v.txtLocalidad.getText());
                    } else {
                        pm.modificarCuartelSentencia(Values.idCuartel, v.txtNombrecuartel.getText(),
                                Double.valueOf(v.txtLatitud.getText()), Double.valueOf(v.txtLongitud.getText()),
                                false, v.txtLocalidad.getText());
                    }

                    actualizarComboBox(0);

                    Values.modifyConstant = false;
                }
                vaciarCuartel();
                listarCuartel();
                break;

            // Guardado de la clase Unidad

            case 1:

                if(v.txtNombreunidad.getText().equals("")) {
                    Utilities.mensajeError("Introduzca al menos nombre de unidad");
                    return;
                }

                if (v.txtNoTropas.getText().equals("")) v.txtNoTropas.setText("0");

                if(Values.modifyConstant == false) {

                    pm.guardarUnidadSentencia(v.txtNombreunidad.getText(), (String) v.cbTipo.getSelectedItem(),
                            Integer.valueOf(v.txtNoTropas.getText()), new Date(v.dcFechaUnidad.getDate().getTime()),
                            (String) v.cbCuartel.getSelectedItem());

                } else {

                    // Modificado de la clase Unidad

                    pm.modificarUnidadSentencia(Values.idUnidad, v.txtNombreunidad.getText(),
                            (String) v.cbTipo.getSelectedItem(), Integer.valueOf(v.txtNoTropas.getText()),
                            new Date(v.dcFechaUnidad.getDate().getTime()),
                            (String) v.cbCuartel.getSelectedItem());

                    actualizarComboBox(1);

                    Values.modifyConstant = false;
                }
                vaciarUnidad();
                listarUnidad();
                break;

            // Guardado de la clase Soldado

            case 2:

                if(v.txtNombre.getText().equals("") || v.txtApellidos.getText().equals("")) {
                    Utilities.mensajeError("Introduzca al menos nombre y apellidos");
                    return;
                }

                if(Values.modifyConstant == false) {

                    pm.guardarSoldadoSentencia(v.txtNombre.getText(), v.txtApellidos.getText(),
                            new Date(v.dcFechanacimiento.getDate().getTime()), (String) v.cbRango.getSelectedItem(),
                            v.txtLugarNacimiento.getText(), (String) v.cbUnidad.getSelectedItem());

                } else {

                    // Modificado de la clase Soldado

                    pm.modificarSoldadoSentencia(Values.idSoldado, v.txtNombre.getText(),
                            v.txtApellidos.getText(), (String) v.cbRango.getSelectedItem(),
                            new Date(v.dcFechanacimiento.getDate().getTime()), v.txtLugarNacimiento.getText(),
                            (String) v.cbUnidad.getSelectedItem());

                    Values.modifyConstant = false;
                }
                vaciarSoldado();
                listarSoldado();
                break;
        }
    }

    // Metodo que controla el borrado del objeto, dependiendo de la pestaña seleccionada

    public void borrarController() {

        switch (Values.tpConstant) {

            // Borrado Cuarteles

            case 0:
                pm.borrarCuartelSentencia(Values.idCuartel);
                vaciarCuartel();
                listarCuartel();
                break;

            // Borrado Unidades

            case 1:
                pm.borrarUnidadSentencia(Values.idUnidad);
                vaciarUnidad();
                listarUnidad();
                break;

            // Borrado Soldados

            case 2:
                pm.borrarSoldadoSentencia(Values.idSoldado);
                vaciarSoldado();
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

        public void controlBuscarCuartel(String busqueda) {

            List<Object[]> list = pm.buscarCuartel(busqueda, (String) v.cbTablaCuartel.getSelectedItem());

            if (list != null) {

                defmodelcuartel.setNumRows(0);
                for (int i = 0; i < list.size(); i++) {

                    defmodelcuartel.addRow(list.get(i));
                }
            }
        }

        // Metodo que busca por nombre de unidad y tipo, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarUnidad(String busqueda) {

            List<Object[]> list = pm.buscarUnidad(busqueda, (String) v.cbTablaUnidad.getSelectedItem());

            if (list != null) {

                defmodelunidad.setNumRows(0);
                for (int i = 0; i < list.size(); i++) {

                    defmodelunidad.addRow(list.get(i));
                }
            }
        }

        // Metodo que busca por nombre, apellidos, rango, unidad y lugar de nacimiento, y posteriormente devuelve los resultados
        // en caso de vaciarse el campo de busqueda se vuelven a listar todos los datos

        public void controlBuscarSoldado(String busqueda) {

            List<Object[]> list = pm.buscarSoldado(busqueda, (String) v.cbTablaSoldado.getSelectedItem());

            if (list != null) {

                defmodelsoldado.setNumRows(0);
                for (int i = 0; i < list.size(); i++) {

                    defmodelsoldado.addRow(list.get(i));
                }
            }
        }

    // Metodo que recoge los cambios de las pestañas, y ejecuta algunos de los metodos anteriores

    @Override
    public void stateChanged(ChangeEvent e) {

        Values.tpConstant = v.tabbedPane1.getSelectedIndex();

        switch (Values.tpConstant) {

            case 0:
                vaciarCuartel();
                listarCuartel();
                break;

            case 1:
                vaciarUnidad();
                listarUnidad();
                actualizarComboBox(0);
                break;

            case 2:
                vaciarSoldado();
                listarSoldado();
                actualizarComboBox(1);
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
                    if (Utilities.mensajeConfirmacion("¿Esta seguro? \n Asegurese de modificar algun dato")
                            == JOptionPane.NO_OPTION) return;
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

                case "Preferencias":
                    pref.setVisible(true);
                    pref.cargarPreferencias();
                    break;
                case "Exportar":
                    exportar();
                    break;
                case "Importar":
                    importar();
                    break;
                case "Login":
                    log = new Login();
                    log.setVisible(true);
                    rol(pm.login(log.getUsuario(), log.getContrasena()));
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
                controlBuscarCuartel(v.txtBusquedacuartel.getText());
                break;

            case 1:
                controlBuscarUnidad(v.txtBusquedaunidad.getText());
                break;

            case 2:
                controlBuscarSoldado(v.txtBusquedasoldado.getText());
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}
