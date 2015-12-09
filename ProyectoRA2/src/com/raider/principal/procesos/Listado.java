package com.raider.principal.procesos;

import com.raider.principal.Gui.Ventana;
import com.raider.principal.util.Values;
import com.raider.principal.controller.Projectcontroller;

/**
 * Created by raider on 7/12/15.
 */
public class Listado extends Thread {

    private Projectcontroller pc;
    private Ventana v;

    public Listado(Projectcontroller pc, Ventana v) {
        this.pc = pc;
        this.v = v;
    }

    public void run() {

        while (isAlive()) {

            if (Values.warningBaseDatos == false) {

                if (v.txtBusquedacuartel.getText().isEmpty()) {

                    if (Values.tpConstant == 0) {
                        pc.listarCuartel();
                    }
                }

                if (v.txtBusquedaunidad.getText().isEmpty()) {

                    if (Values.tpConstant == 1) {
                        pc.listarUnidad();
                    }
                }

                if (v.txtBusquedasoldado.getText().isEmpty()) {

                    if(Values.tpConstant == 2) {
                        pc.listarSoldado();
                    }
                }
            }


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
