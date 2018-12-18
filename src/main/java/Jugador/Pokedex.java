package Jugador;

import Pokemon.*;
import Pokemon.Especial.Legendario.Legendario;
import Pokemon.Especial.Mistico.Mitico;
import Pokemon.Especial.Mistico.Quest;
import Pokemon.Especial.Mistico.SpecialResearch;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;
import utils.CheckType;


import java.util.ArrayList;
import java.util.List;

public class Pokedex {
    private ArrayList<Pokemon> pokedex = new ArrayList<Pokemon>();

    public void showEspecialRecerques() {
        System.out.println("Recerques Especials:\n");
        for (Pokemon i: pokedex) {
            if(i instanceof Mitico){
                Mitico mitico = (Mitico)i;
                if (mitico.getEnCurso()){
                     SpecialResearch sp =  mitico.getSpecial_Research();
                     ArrayList<Quest> quests = sp.getQuests();
                    for (Quest q: quests) {
                        int id = q.getTarget();
                        String name = getPokemonById(id).getName();
                        q.printQuest(name);
                    }
                }
            }
        }
    }

    public Pokemon getPokemonById(int id) {
        for (Pokemon i : this.pokedex) {
            if (i.getId() == id){
                return i;
            }
        }
        return null;
    }

    public ArrayList<Pokemon> getPokedex() {
        return pokedex;
    }


    public void setPokedex(ArrayList<Pokemon> pokedex2) {
        this.pokedex.addAll(pokedex2);
    }

    public Pokemon buscarPokemonSalvaje(String parametro){
        int idBuscado;
        Pokemon pokemonBuscado;
        CheckType ch = new CheckType();

        boolean numeric = ch.checkType(parametro);

        if (numeric){
            idBuscado = Integer.parseInt(parametro);
            pokemonBuscado = buscarPokemonPorId(idBuscado);
        } else {
            pokemonBuscado = buscarPokemonPorNombre(parametro);
        }

        //Controlamos que no sea un pokemon legenadario ni mitico
        try {
            if (pokemonBuscado.getClass() == Legendario.class){

                System.out.println("Ho sentim, però aquest Pokémon és Llegendari i no apareix salvatge.\n");
                return null;

            } else if (pokemonBuscado.getClass() == Mitico.class) {

                System.out.println("Ho sentim, però aquest Pokémon és mític i no apareix salvatge.\n");
                return null;
            }
        }catch (NullPointerException e){

            System.out.println("Ho sentim, però aquest Pokémon no existeix (encara).\n");
            return null;

        }

        return pokemonBuscado;
    }

    public Pokemon buscarPokemonPorId(int id){
        for (Pokemon pokemon : pokedex) {
            if (pokemon.getId() == id){
                return pokemon;
            }
        }
        return null;
    }

    public Pokemon buscarPokemonPorNombre(String nombre){
        nombre = nombre.toLowerCase();

        for (Pokemon pokemon : pokedex) {
            if (pokemon.getName().equals(nombre)){
                return pokemon;
            }
        }
        return null;
    }

    public Pokemon buscarPokemon(String idPokemon) {
        int idBuscado;
        CheckType ch = new CheckType();
        boolean numeric = ch.checkType(idPokemon);
        if (numeric){
            idBuscado = Integer.parseInt(idPokemon);
            return buscarPokemonPorId(idBuscado);

        } else {
            return buscarPokemonPorNombre(idPokemon);
        }
    }

    public double getCaptureRatePokemon(String name) {
        for (Pokemon pokemon: pokedex) {
            if (pokemon.getName().equals(name)){
                return pokemon.getCapture_rate();
            }
        }
        return 0;
    }

    public int checkSpecialResearchIsCompleted(int id) {
        boolean fight = false;
        for (Pokemon i: pokedex){
            if (i instanceof Mitico){
                Mitico mitico = (Mitico)i;
                fight = mitico.checkSpecialResearch(id);
            }
            if (fight){
                return i.getId();
            }
        }
        return -1;
    }
}
