package com.raider.principal.procesos;

import com.raider.principal.Util.Values;
import com.raider.principal.controller.Projectcontroller;
import com.raider.principal.model.Projectmodel;

import java.sql.PreparedStatement;

/**
 * Created by raider on 7/12/15.
 */
public class Listado extends Thread {

    Projectcontroller pc;

    public Listado(Projectcontroller pc) {
        this.pc = pc;
    }

    public void run() {

        while (isAlive()) {

            if(Values.tpConstant == 0) {
                pc.listarCuartel();
            }

            if(Values.tpConstant == 1) {
                pc.listarUnidad();
            }

            if(Values.tpConstant == 2) {
                pc.listarSoldado();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
