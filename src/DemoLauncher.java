import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DemoLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setWindowedMode(1200, 900);
        configuration.setResizable(true);
        configuration.useVsync(false);
        configuration.setForegroundFPS(0);
        configuration.setBackBufferConfig(8, 8, 8, 8, 8, 8, 16);
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2);
        new Lwjgl3Application(new Demo(), configuration);
    }
}
