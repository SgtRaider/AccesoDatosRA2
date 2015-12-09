package com.raider.principal.procesos;

import com.raider.principal.Gui.Preferencias;
import com.raider.principal.util.Values;

/**
 * Created by raider on 9/12/15.
 */
public class ComprobacionClaveSeguridad extends Thread{

    private Preferencias pref;

    public ComprobacionClaveSeguridad(Preferencias pref) {

        this.pref = pref;
    }

    @Override
    public void run() {

        while (isAlive()) {

            if (pref.isVisible()) {

                while (!String.valueOf(pref.securityPw.getPassword()).isEmpty()) {

                    if (String.valueOf(pref.securityPw.getPassword()).equalsIgnoreCase(Values.claveSeguridad)) {

                        pref.setEditable(true);
                    } else {

                        pref.setEditable(false);
                    }
                    if (String.valueOf(pref.securityPw.getPassword()).isEmpty()) {
                        pref.setEditable(false);
                    }
                }
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
