package utils;

import Jugador.*;
import Pokemon.*;
import Pokemon.Especial.Legendario.Legendario;
import Pokemon.Especial.Mistico.Mitico;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

import static java.lang.Math.pow;

import java.util.Scanner;

public class DataManager {
    //Constantes
    private static final String PATH = "files/";
    private static final String FILE1 = "balls.json";
    private static final String FILE2 = "poke.json";
    private static final String FILE3 = "legends.json";

    //Atributos de la clase
    private Tienda tienda = new Tienda();
    private Usuario usuario;

    //Getters

    /**
     * Carga la infomacion del Json de pokebolls en la tienda
     */
    public void loadDataPokeballs() {
        Gson gson = new Gson();
        JsonReader reader;
        Pokeball[] pokeballs;
        try {
            reader = new JsonReader(new FileReader(PATH + FILE1));
            pokeballs = gson.fromJson(reader, Pokeball[].class);
            this.tienda.setPokeballs(pokeballs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la informacion del usuario
     */
    public void loadUser() {
        usuario = new Usuario(tienda.getFirstPokeball());
    }


    /**
     * Ahora cargamos la informacion de los todos los pokemons.
     */
    public void loadDataPokedex() {
        Pokemon[] pokemonsAux = loadPokemon(FILE2);
        ArrayList<Pokemon> pokedex = new ArrayList<>(Arrays.asList(pokemonsAux));
        pokemonsAux = loadPokemon(FILE3);
        int i = 0;
        for (Pokemon pokemonActual : pokedex) {
            for (Pokemon pokemonAuxActual : pokemonsAux) {
                if (pokemonActual.getId() == pokemonAuxActual.getId()) {
                    pokemonAuxActual.setName(pokemonActual.getName());
                    pokemonAuxActual.setCapture_rate(pokemonActual.getCapture_rate());
                    pokedex.set(i, pokemonAuxActual);
                }
            }
            i++;
        }
        usuario.setPokedex(pokedex);
    }


    //Metodos

    /**
     * Lee la informacion de los pokemons
     *
     * @param file: El archivo a leer
     * @return Array de pokemons
     */
    private Pokemon[] loadPokemon(String file) {
        Pokemon[] pokemons;
        JsonReader reader;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Pokemon.class, new IdDeserializer()).create();
            reader = new JsonReader(new FileReader(PATH + file));
            pokemons = gson.fromJson(reader, Pokemon[].class);
        } catch (FileNotFoundException e) {
            pokemons = null;
            e.printStackTrace();
        }
        return pokemons;
    }



    public void seleccionarOpcio(int opcion) {
        switch (opcion) {
            case 1:
                //Monedas
                usuario.comprarMonedas();
                break;

            case 2:
                //Comprar Objetos

                System.out.println("Teniu " + usuario.getMonedas() +  " monedes.\n");
                tienda.mostrarObjetos();

                char eleccion = usuario.pideObjeto();
                List<Pokeball> pokeball = tienda.getObjetos(eleccion);
                usuario.addItemsInventario(pokeball);

                break;

            case 3:

                //Consultar Inventario
                usuario.consultarInventario();

                break;

            case 4:
                boolean miticalIsOn = false;
                int miticalId = -1;
                boolean quedanPokeballs;
                do {
                    if (!miticalIsOn){
                        //Capturar pokemons salvajes
                        quedanPokeballs = usuario.pokeballsDisponibles();
                        if (quedanPokeballs){
                            Pokemon pokemon = usuario.peticionPokemon();
                            if (pokemon != null){
                                boolean capturado = sistemaCaptura(pokemon);
                                if (capturado){
                                    usuario.pokemonCapturado(pokemon);
                                    miticalId = usuario.checkSpecialResearchIsCompleted(pokemon.getId());
                                    if (miticalId != -1){
                                        miticalIsOn = true;
                                    }
                                }
                            }
                        }
                    }else{
                        Pokemon pokemon = usuario.getPokemonById(miticalId);
                        System.out.println("Recerca Especial completada: Se t'apareix el mític " + pokemon.getName() + "!");
                        boolean capturado = sistemaCaptura(pokemon);
                        if (capturado){
                            usuario.pokemonCapturado(pokemon);
                            usuario.setCompletedResearch(pokemon.getId());
                            miticalId = usuario.checkSpecialResearchIsCompleted(pokemon.getId());
                            if (miticalId != -1){
                                miticalIsOn = true;
                            }else{
                                miticalIsOn = false;
                            }
                        }else{
                            usuario.resetSpecialResearch(pokemon.getId());
                        }
                    }
                }while (miticalIsOn);
                break;

            case 5:

                break;

            case 6:
                usuario.getEspecialRecerques();
                break;

            case 7:
                List<Pokemon> pokemonsCapturados = usuario.getPokemonsCapturados();
                for (Pokemon i :pokemonsCapturados) {
                    setMoreInformactionOfPokemon(i);
                }
                try {
                    WriteFileCapturados(pokemonsCapturados);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 8:

                Pokemon pokemon = usuario.peticionInformacionPokemon();
                if(pokemon != null){

                    setMoreInformactionOfPokemon(pokemon);
                    try {

                        WriteFileInformation(pokemon);

                    } catch (IOException e) {

                        e.printStackTrace();

                    }
                }

                break;

            case 9:
                System.out.println("Ens veiem!");

                break;

            case 10:
                //Si queremos hacer el sistema de partidas:
                //      Deberíamos colocar un sitema que controle si el usuario quiere guardar o no la partida

                //En caso que hagamos partidas ya creadas:
                //System.out.println("Guardant Partida");
                //wait(1);
                //System.out.print(".");
                //wait(1);
                //System.out.print(".");
                //wait(1);
                //System.out.println(".");
                //wait(2);
                //System.out.println("Fet!\nTorna quan vulguis!");
                break;

        }
    }

    private void WriteFileCapturados(List<Pokemon> pokemonsCapturados) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter( "Pokemons_capturats.html"));
        int nPokemons = pokemonsCapturados.size();
        String backgorund = getBackgroundColor();
        writer.write("<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Pokemons capturats: "+nPokemons+"</title>\n" +
                "    <link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\">\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            "+backgorund+";\n" +
                "            font-family: 'Open Sans', sans-serif;\n" +
                "            max-width: 90%;\n" +
                "            margin: 0 auto;\n" +
                "            margin-top: 2%;\n" +
                "        }\n" +
                "\n" +
                "        .grid-container {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: auto auto auto auto auto auto;\n" +
                "            grid-column-gap: 50px;\n" +
                "            grid-row-gap: 50px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            border-radius: 25px;\n" +
                "            padding: 10px;\n" +
                "            animation-name: example;\n" +
                "            animation-duration: 4s;\n" +
                "            min-width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        @keyframes example {\n" +
                "            from {\n" +
                "                background-color : white;\n" +
                "            }\n" +
                "\n" +
                "            to {\n" +
                "                background-color: none;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "\n" +
                "    <div>\n" +
                "        <h1 style=\"text-align: center;color : black;\">Pokemons capturats: "+nPokemons+"</h1>\n" +
                "    </div>\n" +
                "    <div class=\"grid-container\">\n" +
                "\n");
        ArrayList<Integer> visitados = new ArrayList<>();
        for (Pokemon i : pokemonsCapturados) {
            if (!visitados.contains(i.getId())){
                visitados.add(i.getId());
                int total = 0;
                for (Pokemon j :pokemonsCapturados) {
                    if (i.getId() == j.getId()){
                        total++;
                    }
                }
                String backgroundiv = getBackgroundColor();
                writer.write("<div class=\"content\" style=\""+backgroundiv+"\">\n" +
                        "            <h2 style=\"text-align: center;\">"+i.getName() + " x"+ total+"</h2><img src=\""+ i.getFront_default() +"\"\n" +
                        "                style=\"display: block;width: 70%;margin: auto;\">\n" +
                        "        </div>");
            }
        }
        writer.write("        </div>\n" +
                "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>");
        writer.close();
    }

    private boolean sistemaCaptura(Pokemon pokemon){
        boolean capturado;

        if (pokemon.getClass() == Legendario.class){
            capturado = resultadoCaptura(pokemon);

        } else if (pokemon.getClass() == Mitico.class){
            capturado = resultadoCapturaMi(pokemon);

        } else {
            System.out.println("Un " + pokemon.getName() + " salvatge aparegué!");
            capturado = resultadoCaptura(pokemon);

        }

        return capturado;
    }

    private boolean resultadoCapturaMi(Pokemon pokemon){
        boolean pokeballExitente = false;
        boolean atrapado = false;
        int intents = 5;
        String tipoPokeball;
        Pokeball[] pokeballsExistentes = tienda.getPokeballs();
        do {
            System.out.println("Tienes Pokeballs ilimitadas, pero solo 5 intentos, que pokeball quieres?");
            do {
                Scanner scPokeball = new Scanner(System.in);
                tipoPokeball = scPokeball.nextLine();
                tipoPokeball = tipoPokeball.toLowerCase();
                for (Pokeball pk:pokeballsExistentes) {
                    if (pk.getName().equals(tipoPokeball)){
                        pokeballExitente = true;
                    }
                }
            }while (!pokeballExitente);
            double random = Math.random();
            int pb = pokeballCaptureRate(tipoPokeball);
            double pc = pokemon.captureEcuation(pb);


            if (pc >= random){
                atrapado = true;

            } else {
                System.out.println("La " + tipoPokeball + " ha fallat!");
                intents--;
            }

        }while (intents > 0 && !atrapado);
        return  atrapado;
    }

    private boolean resultadoCaptura(Pokemon pokemon){
        double pc;                                              //Probabilidad de Captura
        double pb;                                                  //Pokeball Capture Rate
        boolean atrapado = false;
        boolean tieneTipoPokeball;
        int intents = 5;
        String tipoPokeball;
        double random;
        Pokeball[] pokeballsExistentes = tienda.getPokeballs();     //Pokeballs que existen en el juego definidas en la tienda como posibles compras

        do{
            System.out.println("Queden "+ usuario.pokeballsTotales() + " Pokéballs i "+ intents +"/5 intents. Quin tipus de Pokéball vol fer servir?");

            do {
                Scanner scPokeball = new Scanner(System.in);
                tipoPokeball = scPokeball.nextLine();
                tipoPokeball = tipoPokeball.toLowerCase();
                tieneTipoPokeball = usuario.existeEnInventario(tipoPokeball, pokeballsExistentes);

            }while (!tieneTipoPokeball);

            random = Math.random();
            pb = pokeballCaptureRate(tipoPokeball);
            pc = pokemon.captureEcuation(pb);


            if (pc >= random){
                atrapado = true;

            } else {
                System.out.println("La " + tipoPokeball + " ha fallat!");
                intents--;
                usuario.setTotalPokeballs(usuario.pokeballsTotales() - 1);
            }

        }while (intents > 0 && usuario.pokeballsTotales() > 0 && !atrapado);

        if (intents == 0){
            System.out.println("El " + pokemon.getName() + " escapat...\n");

        } else  if (usuario.pokeballsTotales() == 0) {
            System.out.println("No queden Pokeballs...\n");

        }

        return atrapado;
    }

    private int pokeballCaptureRate(String nombrePokeball){
        for (Pokeball pokeball: tienda.getPokeballs()) {
            if (pokeball.getName().equals(nombrePokeball)){
                return pokeball.getCapture_rate();
            }
        }

        return 0;
    }


    private void WriteFileInformation(Pokemon pokemon) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(pokemon.getName() + ".html"));
        String title = pokemon.getName().toUpperCase();
        String h1 = title  + "(" + pokemon.getId() + ")";
        String description = pokemon.getFlavor_text();
        String img = pokemon.getFront_default();
        String background_div = getBackgroundColor();
        String background_body = getBackgroundColor();
        writer.write("<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>" + title +  "</title>\n" +
                "\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            "+background_body +";\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            border-radius: 25px;\n" +
                "            "+background_div +";\n" +
                "            padding: 10px;\n" +
                "            max-width: 800px;\n" +
                "            position: fixed;\n" +
                "            top: 50%;\n" +
                "            left: 50%;\n" +
                "            transform: translate(-50%, -50%);\n" +
                "        }\n" +
                "    </style>\n" +
                "\n" +
                "\n" +
                "    <div class=\"content\">\n" +
                "        <h1 style=\"text-align: center;\">" + h1 + "</h1><img src=\""+ img +"\"\n" +
                "            style=\"display: block;width: 50%;margin: auto;\">\n" +
                "        <div style=\"padding: 20px;\">\n" +
                "            <p>" + description + "</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        writer.close();
    }

    private String getBackgroundColor() {
        int color1 = (int) ((Math.random() * ((255 - 180) + 1)) + 180);
        int color2 = (int) ((Math.random() * ((255 - 180) + 1)) + 180);
        int color3 = (int) ((Math.random() * ((255 - 180) + 1)) + 180);
        return  "background-color:rgb(" + color1 + ", " +color2  + ", " + color3  +")";
    }

    private void setMoreInformactionOfPokemon(Pokemon pokemon) {
        if (pokemon.getFlavor_text() == null){
            PokeApi pokeApi = new PokeApi(pokemon.getId());
            pokemon.setHeight(pokeApi.getHeight());
            pokemon.setWeight(pokeApi.getWeight());
            pokemon.setFront_default(pokeApi.getDefaultSprite());
            pokemon.setFlavor_text(pokeApi.getFlavorTextEnglish());
            pokemon.setBase_experience(pokeApi.getBaseExperience());
        }
    }

}