import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.utils.graphics.CameraController;
import org.yunghegel.gdx.utils.graphics.Grid;

public class GenericRender {

    public ModelBatch batch;
    public Viewport viewport;
    public PerspectiveCamera camera;
    public InputMultiplexer inputs;
    public CameraController camController;
    public ModelInstance instance;
    public Environment environment;
    public BitmapFont font;
    public SpriteBatch spriteBatch;
    public Grid grid;
    public PointLight pointLight;
    public DirectionalLight dirLight;
    public ShapeRenderer shapeRenderer;

    public GenericRender() {

        inputs = new InputMultiplexer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch = new ModelBatch();
        camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.01f;
        camera.far = 300f;

        camera.update();


        camController = new CameraController(camera);
        inputs.addProcessor(camController);
        viewport = new ScreenViewport(camera);
        viewport.apply();



        ModelBuilder modelBuilder = new ModelBuilder();
        Model sphere = modelBuilder.createBox(1f,1f,1f,new Material(ColorAttribute.createEmissive(Color.GRAY),ColorAttribute.createDiffuse(Color.SLATE),ColorAttribute.createSpecular(Color.SLATE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(sphere);

        float[] subdivisions = new float[]{0.25f, 0.125f};
        grid = new Grid(0.2f, 50, 0.5f, subdivisions);


//        instances.add(grid);

        camera.position.set(2,1,2);
        camera.lookAt(0,0,0);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, .1f));
        environment.add(dirLight = new DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f));
        environment.add(pointLight = new PointLight().set(0.2f, 0.8f, 0.2f, 0f, 2f, 0f, 10f));
    }

    public void render(float delta){
        grid.render(camera);
        batch.begin(camera);
        batch.render(instance, environment);
        batch.end();
    }
}
