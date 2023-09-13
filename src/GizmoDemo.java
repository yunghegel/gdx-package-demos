import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.yunghegel.gdx.gizmo.GizmoManager;
import org.yunghegel.gdx.gizmo.Util;
import org.yunghegel.gdx.gizmo.core.GizmoType;
import org.yunghegel.gdx.gizmo.core.transform.BasicTransformTarget;
import org.yunghegel.gdx.gizmo.core.transform.RotateGizmo;
import org.yunghegel.gdx.gizmo.core.transform.TransformGizmoTarget;
import org.yunghegel.gdx.gizmo.core.transform.TranslateGizmo;
import org.yunghegel.gdx.gizmo.core.utility.CompassGizmo;
import org.yunghegel.gdx.gizmo.core.utility.CompassTarget;
import org.yunghegel.gdx.scenegraph.scene3d.graph.Spatial;
import org.yunghegel.gdx.utils.graphics.CameraController;
import org.yunghegel.gdx.utils.graphics.Grid;
import org.yunghegel.gdx.utils.shaders.OutlineShader;
import org.yunghegel.gdx.utils.ui.BooleanUI;
import org.yunghegel.gdx.utils.ui.CollapsableUI;
import org.yunghegel.gdx.utils.ui.FloatUI;
import org.yunghegel.gdx.utils.ui.Vector4UI;

public class GizmoDemo extends ApplicationAdapter {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setWindowedMode(1920, 1080);
        configuration.setResizable(true);
        configuration.useVsync(false);
        configuration.setForegroundFPS(0);
        configuration.setBackBufferConfig(8, 8, 8, 8, 8, 8, 16);
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2);
        new Lwjgl3Application(new GizmoDemo(), configuration);
    }

    ModelBatch batch;
    Viewport viewport;
    PerspectiveCamera camera;
    InputMultiplexer inputs;
    CameraController camController;
    ModelInstance instance;
    Environment environment;
    BitmapFont font;
    SpriteBatch spriteBatch;

    TranslateGizmo translateGizmo;
    RotateGizmo rotateGizmo;
    OutlineShader outlineShader;
    Stage stage;
    Grid grid;

    Array<RenderableProvider> instances = new Array<>();
    ModelInstance axisInstance;
    PointLight pointLight;
    DirectionalLight dirLight;

    GizmoManager gizmoManager;

    CompassGizmo compassGizmo;



    @Override
    public void create() {
        super.create();
        batch = new ModelBatch();
        camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.01f;
        camera.far = 300f;

        stage=new Stage(new ScreenViewport());

        inputs = new InputMultiplexer();
        camController = new CameraController(camera);

        viewport = new ScreenViewport(camera);
        viewport.apply();

        inputs.addProcessor(camController);

        gizmoManager = new GizmoManager(inputs, camera, viewport);


        ModelBuilder modelBuilder = new ModelBuilder();
        Model sphere = modelBuilder.createBox(1f,1f,1f,new Material(ColorAttribute.createEmissive(Color.GRAY),ColorAttribute.createDiffuse(Color.SLATE),ColorAttribute.createSpecular(Color.SLATE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(sphere);
//        Model gridLines = modelBuilder.createLineGrid(100, 100, .5f, .5f,new Material(ColorAttribute.createEmissive(Color.GRAY), new BlendingAttribute(0.5f)),VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


        float[] subdivisions = new float[]{0.25f, 0.125f};
        grid = new Grid(0.2f, 50, 0.5f, subdivisions);


//        instances.add(grid);

        instances.add(instance);
        camera.position.set(2,1,2);
        camera.lookAt(0,0,0);

        environment = new Environment();

//        environment.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
//        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f,-0.2f));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, .1f));
        environment.add(dirLight = new DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f));
        environment.add(pointLight = new PointLight().set(0.2f, 0.8f, 0.2f, 0f, 2f, 0f, 10f));
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);


//        translateGizmo =new TranslateGizmo(inputs,batch,camera,viewport);
//        rotateGizmo=new RotateGizmo(inputs,batch,camera,viewport);

//        gizmo.enable(instance.transform);
        VisUI.load();

        VisWindow window=new VisWindow("Test");
        VisTextButton button=new VisTextButton("Translate");

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

//        Spatial root= new Spatial(0,new Matrix4());

//        root.addChild(spatial);
        TransformGizmoTarget target=new TransformGizmoTarget(new BasicTransformTarget(instance.transform));

        gizmoManager.setTransformGizmoTarget(target);

        VisCheckBox checkBox=new VisCheckBox("Translate");
        checkBox.setChecked(false);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(checkBox.isChecked()){
                    gizmoManager.enableGizmo(GizmoType.TRANSLATE, target);
                } else {
                    gizmoManager.disableGizmo(GizmoType.TRANSLATE);
                }
            }
        });

        VisCheckBox checkBox2=new VisCheckBox("Rotate");
        checkBox2.setChecked(false);
        checkBox2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(checkBox2.isChecked()){
                    gizmoManager.enableGizmo(GizmoType.ROTATE, target);
                } else {
                    gizmoManager.disableGizmo(GizmoType.ROTATE);
                }
            }
        });

        VisCheckBox checkBox3=new VisCheckBox("Scale");
        checkBox3.setChecked(false);
        checkBox3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(checkBox3.isChecked()){
                    gizmoManager.enableGizmo(GizmoType.SCALE, target);
                } else {
                    gizmoManager.disableGizmo(GizmoType.SCALE);
                }
            }
        });

        VisCheckBox checkBox4=new VisCheckBox("Compass");
        checkBox4.setChecked(false);
        checkBox4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(checkBox4.isChecked()){
                    gizmoManager.enableGizmo(GizmoType.COMPASS, new CompassTarget(camera,.9f,.9f));
                } else {
                    gizmoManager.disableGizmo(GizmoType.COMPASS);
                }
            }
        });

        window.add(button);
        window.add(checkBox);
        Skin skin =   VisUI.getSkin();
//        stage.addActor(window);
        Table table=new Table(skin);

        table.setFillParent(true);
        table.align(Align.topLeft);
        table.pad(10);


        table.add(checkBox);
        table.add(checkBox2).row();
        table.add(checkBox3);
        table.add(checkBox4).row();





        stage.addActor(table);


        inputs.addProcessor(stage);
//        inputs  .addProcessor(translateGizmo);


        spriteBatch=new SpriteBatch();
        font=new BitmapFont();

        Gdx.input.setInputProcessor(inputs);

        compassGizmo =new CompassGizmo(inputs,batch,camera,viewport,-1);
        compassGizmo.enable(new CompassTarget(camera,.9f,.9f));
        outlineShader=new OutlineShader(true);

    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(0.1f, .1f, .1f, 1);


//        translateGizmo.update();
//        rotateGizmo.update();

        gizmoManager.processInput();
        gizmoManager.update();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());



//        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        grid.render(camera);
        batch.begin(camera);

        batch.render(instances,environment);
//        batch.render(axisInstance,environment);
        batch.end();
        outlineShader.renderOutline(instances,camera);



        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(Gdx.gl.GL_DEPTH_BUFFER_BIT);
//        translateGizmo.render(batch);
//        rotateGizmo.render(batch);
        gizmoManager.render();

        stage.act();
        stage.draw();

        spriteBatch.begin();
//        font.draw(spriteBatch,"degree " + rotateGizmo.degree,50,200);
//        font.draw(spriteBatch, "rot "+rotateGizmo.rot,50,250);
//        font.draw(spriteBatch, "lastRot "+rotateGizmo.lastRot,50,300);
        spriteBatch.end();

    }

}
