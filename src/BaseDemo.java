import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ray3k.stripe.FreeTypeSkin;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import org.yunghegel.gdx.gizmo.core.utility.CompassGizmo;
import org.yunghegel.gdx.scenegraph.scene3d.scene.Scene;
import org.yunghegel.gdx.scenegraph.scene3d.scene.SceneGraph;
import org.yunghegel.gdx.utils.graphics.CameraController;
import org.yunghegel.gdx.utils.graphics.Grid;
import org.yunghegel.gdx.utils.graphics.model.Primitive;
import org.yunghegel.gdx.utils.graphics.model.PrimitiveSupplier;
import org.yunghegel.gdx.utils.ui.LoggerWidget;
import org.yunghegel.gdx.utils.ui.ViewportWidget;
import org.yunghegel.gdx.utils.ui.widgets.SceneTree;

public class BaseDemo extends Scene {



    protected CameraController camController;
    protected CompassGizmo compass;
    protected Grid grid;

    protected SpriteBatch spriteBatch;
    protected BitmapFont font;
    protected Stage stage;
    protected VisTable root;
    protected Skin skin;
    protected ViewportWidget viewportWidget;
    protected Array<ModelInstance> instances = new Array<>();

    public BaseDemo() {
        super();
        create();
        buildUI();
    }

    private void buildUI() {


        stage=new Stage(new ScreenViewport());
        root=new VisTable();
        stage.addActor(root);
        root.setFillParent(true);
        inputs.addProcessor(stage);

        VisSplitPane upDown;
        VisSplitPane leftRight;

        VisTable viewportTable = new VisTable();
        viewportTable.add(viewportWidget = new ViewportWidget(viewport, (f)-> {
            render(camera);

        },inputs,camController,stage)).grow();
        VisTable loggerTable = new VisTable();
        loggerTable.add(new LoggerWidget(skin)).grow().fill().align(Align.bottomLeft);
        loggerTable.align(Align.bottomLeft);
        leftRight = new VisSplitPane(viewportTable,new SceneTree(sceneGraph),false);


        upDown = new VisSplitPane(leftRight,loggerTable,true);
        upDown.setSplitAmount(0.8f);
        root.add(upDown).grow().fill();
    }

    @Override
    public void update(float delta) {

        sceneGraph.update(delta);
        camera.update();
        compass.update();

    }

    @Override
    public void render(Camera camera) {



        grid.render(camera);
        sceneGraph.render(Gdx.graphics.getDeltaTime());
        batch.begin(camera);
        batch.render(instances, environment);
        batch.end();
    }

    void create(){
            skin = new FreeTypeSkin(Gdx.files.internal("skin/salient/uiskin.json"));
            VisUI.load(skin);

            spriteBatch = new SpriteBatch();
            font = skin.getFont("default-font");




            inputs = new InputMultiplexer();
            camController = new CameraController(camera);



            inputs.addProcessor(camController);

            Gdx.input.setInputProcessor(inputs);

            compass = new CompassGizmo(inputs,batch,camera,viewport,-1);
            float[] subdivisions = new float[]{0.25f, 0.125f};
            grid = new Grid(0.2f, 50, 0.5f, subdivisions);

            ModelInstance cube = PrimitiveSupplier.quickPrimitive(Primitive.Cube);
            cube.transform.setToTranslation(0, 0, 0);
            instances.add(cube);
        }

        @Override
        public void render(float delta){
            Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
            ScreenUtils.clear(0.2f, .2f, .2f, 1);

            stage.act(delta);
            stage.draw();

            update(delta);
//            viewportWidget.updateViewport(true);

            compass.render(batch);


            spriteBatch.begin();
            spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight()-20);
            spriteBatch.end();
        }

    @Override
        public void resize(int width, int height) {
        super.resize(width, height);

        viewport.update(width, height);
        viewport.apply();
        stage.getViewport().update(width, height, true);
        stage.getViewport().apply(true);
//        stage.getViewport().apply();





        }



}
