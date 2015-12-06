package com.raider.principal.Gui;

import com.raider.principal.Util.Values;
import com.raider.principal.base.Cuartel;
import com.raider.principal.base.Soldado;
import com.raider.principal.base.Unidad;
import com.raider.principal.controller.Projectcontroller;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;

/**
 * Created by raider on 28/10/15.
 */
public class Ventana {

    // Variables de la GUI Ventana

    public JTabbedPane tabbedPane1;
    private JPanel p1;
    public JTextField txtLongitud;
    public JTextField txtLatitud;
    public JTextField txtLocalidad;
    public JTextField txtNombrecuartel;
    public JList<Cuartel> ltCuarteles;
    public JList<Unidad> ltUnidadescuartel;
    public JTextField txtNombreunidad;
    public JComboBox cbTipo;
    public JComboBox cbCuartel;
    public JTextField txtNoTropas;
    public JList<Unidad> ltUnidades;
    public JButton btGuardarunidad;
    public JButton btModificarunidad;
    public JButton btEliminarunidad;
    public JList<Soldado> ltTropasunidad;
    public JButton btGuardarcuartel;
    public JButton btModificarcuartel;
    public JButton btEliminarcuartel;
    public JTextField txtNombre;
    public JComboBox cbRango;
    public JTextField txtApellidos;
    public JTextField txtLugarNacimiento;
    public JList<Soldado> ltSoldados;
    public JComboBox cbUnidad;
    public JButton btGuardarsoldado;
    public JButton btModificarsoldado;
    public JButton btEliminarsoldado;
    public JTextField txtBusquedasoldado;
    public JDateChooser dcFechaUnidad;
    public JTextField txtBusquedaunidad;
    public JTextField txtBusquedacuartel;
    public JDateChooser dcFechanacimiento;
    public JComboBox cbActividad;
    public JCheckBox cbGuargarAutomatico;
    public JLabel lbRuta;

    public static JMenuBar mbVentana;
    public static JMenu mArchivo;
    public static JMenuItem miGuardar;
    public static JMenuItem miGuardarcomo;
    public static JMenu mOpciones;
    public static JMenuItem miExportar;
    public static JMenuItem miImportar;
    public static JMenuItem miCambiarPath;

    // Constructor de la ventana

    public Ventana() {

        MenuBar();
        lbRuta.setText("Ruta Guardado Fijo: " + Values.PATH);;
        Projectcontroller pc = new Projectcontroller(Ventana.this);
    }

    public static void main(String[] args) {

        // Creacion de la Ventana o Frame con sus elementos

        JFrame frame = new JFrame("Ventana");
        frame.setContentPane(new Ventana().p1);
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setJMenuBar(getMenuBar());
    }

    // Creacion de el JMenuBar

    public void MenuBar() {

        mbVentana = new JMenuBar();

        mArchivo = new JMenu("File");

        miGuardar = new JMenuItem("Save");
        miGuardarcomo = new JMenuItem("Save as...");
        mOpciones = new JMenu("Settings");
        miExportar = new JMenuItem("Export");
        miImportar = new JMenuItem("Import");
        miCambiarPath = new JMenuItem("Change PATH");

        mbVentana.add(mArchivo);
        mArchivo.add(miGuardar);
        mArchivo.add(miGuardarcomo);
        mArchivo.add(mOpciones);
        mOpciones.add(miExportar);
        mOpciones.add(miImportar);
        mOpciones.add(miCambiarPath);
    }

    // Getter del JMenuBar

    public static JMenuBar getMenuBar() {
        return mbVentana;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
