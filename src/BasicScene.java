import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
import org.yunghegel.gdx.gizmo.core.utility.CompassGizmo;
import org.yunghegel.gdx.scenegraph.scene3d.BaseScene;
import org.yunghegel.gdx.scenegraph.scene3d.SceneGraph;
import org.yunghegel.gdx.utils.graphics.CameraController;
import org.yunghegel.gdx.utils.graphics.Grid;
import org.yunghegel.gdx.utils.graphics.model.Primitive;
import org.yunghegel.gdx.utils.graphics.model.PrimitiveSupplier;
import org.yunghegel.gdx.utils.ui.LoggerWidget;
import org.yunghegel.gdx.utils.ui.ViewportWidget;
import org.yunghegel.gdx.utils.ui.widgets.SceneTree;

public class BasicScene extends BaseScene {



    protected CameraController camController;
    protected CompassGizmo compass;
    protected Grid grid;

    protected SpriteBatch spriteBatch;
    protected BitmapFont font;
    protected Stage stage;
    protected VisTable root;
    protected Skin skin;
    protected ViewportWidget viewportWidget;
    protected ModelInstance instance;
    protected Array<ModelInstance> instances = new Array<>();
    protected Viewport viewport;
    protected PerspectiveCamera camera;
    protected ModelBatch batch;
    protected Environment environment;
    protected InputMultiplexer inputs;
    protected SceneGraph sceneGraph;
    protected VisTable rightTable;
    protected VisTable leftTable;

    public BasicScene() {
        super();
        create();
        buildUI();
    }

    protected void buildUI() {


        environment=new Environment();



        stage=new Stage(new ScreenViewport());
        root=new VisTable();
        stage.addActor(root);
        root.setFillParent(true);
        inputs.addProcessor(stage);
        leftTable = new VisTable();
        rightTable = new VisTable();

        VisSplitPane upDown;
        VisSplitPane leftRight;


        leftTable.add(viewportWidget = new ViewportWidget(viewport, (f)-> {


            grid.render(camera);
            sceneGraph.render(Gdx.graphics.getDeltaTime());
            batch.begin(camera);
            batch.render(instances, environment);
            batch.end();


        },inputs,camController,stage)).grow();

        rightTable.add(new SceneTree(sceneGraph)).grow().fill().row();

//        compass.widget = viewportWidget;
        viewportWidget.enableCompassGizmo(compass);
        VisTable loggerTable = new VisTable();
        loggerTable.add(new LoggerWidget(skin)).grow().fill().align(Align.bottomLeft);
        loggerTable.align(Align.bottomLeft);
        leftRight = new VisSplitPane(leftTable,rightTable,false);
        leftRight.setSplitAmount(0.8f);


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
    public void render(float delta) {

        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(0.2f, .2f, .2f, 1);





        stage.act(delta);
        stage.draw();

        compass.render(batch);




        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight()-20);
        spriteBatch.end();


    }

    void create(){
            skin = new FreeTypeSkin(Gdx.files.internal("skin/salient/uiskin.json"));
            VisUI.load(skin);

            spriteBatch = new SpriteBatch();
            font = skin.getFont("default-font");
            sceneGraph=new SceneGraph(this);
            batch=new ModelBatch();
            camera=new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            viewport=new ScreenViewport(camera);



            inputs = new InputMultiplexer();
            batch = new ModelBatch();
            camController = new CameraController(camera);



            inputs.addProcessor(camController);

            Gdx.input.setInputProcessor(inputs);

            compass = new CompassGizmo(inputs,batch,camera,viewport,-1);
            float[] subdivisions = new float[]{0.25f, 0.125f};
            grid = new Grid(0.2f, 50, 0.5f, subdivisions);

             instance = PrimitiveSupplier.quickPrimitive(Primitive.Cube);
            instance.transform.setToTranslation(0, 0, 0);
            instances.add(instance);
        }


        public void render(Camera camera) {

        }

    @Override
        public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
        stage.getViewport().update(width, height, true);
        stage.getViewport().apply(true);

        }


    @Override
    public void dispose() {

    }
}
