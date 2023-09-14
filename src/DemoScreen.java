import com.badlogic.gdx.ScreenAdapter;
import org.yunghegel.gdx.scenegraph.scene3d.Scene;

public class DemoScreen extends ScreenAdapter {

    Scene scene;

    public DemoScreen(Scene scene){
        this.scene = scene;
    }

    @Override
    public void render(float delta) {
        scene.update(delta);
        scene.render(delta);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
