import com.badlogic.gdx.Game;
import org.yunghegel.gdx.scenegraph.scene3d.BaseScene;

public class Demo extends Game {


        @Override
        public void create() {
            setScreen(new DemoScreen(new GizmoScene() {
            }));
        }
}
