package com.raider.principal.Gui;

import raider.Util.Utilities;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Preferencias extends JDialog implements ActionListener {
    private JPanel contentPane;
    private JButton btOk;
    private JButton btCancel;
    private JRadioButton rbMysql;
    private JRadioButton rbPostgre;
    private JTextField txtServidor;
    private JTextField txtPuerto;
    private JTextField txtUsuario;
    private JTextField txtContraseña;

    public Preferencias() {
        super();
        setTitle("Login");
        getContentPane().add(contentPane);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setModal(true);

        btOk.addActionListener(this);
        btCancel.addActionListener(this);
        rbPostgre.addActionListener(this);
        rbMysql.addActionListener(this);

        cargarPreferencias();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btOk) {
            aceptar();
        }
        else if (e.getSource() == btCancel) {
            cancelar();
        }

        if (e.getSource() == rbMysql) {

            rbPostgre.setSelected(false);
        } else if (e.getSource() == rbPostgre) {

            rbMysql.setSelected(false);
        }
    }

    private void aceptar() {

        Properties configuracion = new Properties();
        if (rbMysql.isSelected()) {
            configuracion.setProperty("driver", "com.mysql.jdbc.Driver");
            configuracion.setProperty("protocolo", "jdbc:mysql://");
        }
        else {
            configuracion.setProperty("driver", "org.postgresql.Driver");
            configuracion.setProperty("protocolo", "jdbc:postgresql://");
        }

        configuracion.setProperty("servidor", txtServidor.getText());
        configuracion.setProperty("basedatos", "ejercito");
        configuracion.setProperty("puerto", txtPuerto.getText());
        configuracion.setProperty("usuario", txtUsuario.getText());
        configuracion.setProperty("contrasena", txtContraseña.getText());

        try {
            configuracion.store(new FileOutputStream("configuracion.props"), "|--- PREFERENCIAS ---|");
            setVisible(false);
        } catch (FileNotFoundException fnfe) {
            Utilities.mensajeError("Ruta de configuracion no encontrada");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void cargarPreferencias() {

        Properties configuracion = new Properties();
        try {
            configuracion.load(new FileInputStream("configuracion.props"));
            String driver = configuracion.getProperty("driver");
            if (driver.equals("com.mysql.jdbc.Driver"))
                rbMysql.setSelected(true);
            else
                rbPostgre.setSelected(true);

            txtServidor.setText(configuracion.getProperty("servidor"));
            txtPuerto.setText(configuracion.getProperty("puerto"));
            txtUsuario.setText(configuracion.getProperty("usuario"));
            txtContraseña.setText(configuracion.getProperty("contrasena"));

        } catch (FileNotFoundException e) {
            // TODO Mostrar mensaje de error
        } catch (IOException e) {
            // TODO Mostrar mensaje de error
        }
    }

    private void cancelar() { setVisible(false); }
}
