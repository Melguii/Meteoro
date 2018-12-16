package Principal;

import utils.DataManager;
import java.io.FileNotFoundException;


public class Main {
    public static void main (String[] args) throws FileNotFoundException {
        DataManager dataManager = new DataManager();
        Menu m = new Menu();
        dataManager.loadDataPokeballs();
        dataManager.loadDataPokedex();
        dataManager.loadUser();
        do{
            m.mostrarMenu();
            do {
                m.peticion();
            }while (!m.opcionValida());

            dataManager.seleccionarOpcio(m.getOpcion());

        }while(!m.salir());
    }
}
