package sv.jertic.tecnico;

/**
 * Launcher externo — necesario para ejecutar la app como JAR ejecutable
 * sin que JavaFX cause problemas con el manifest.
 */
public class Launcher {
    public static void main(String[] args) {
        JerTicApp.main(args);
    }
}
