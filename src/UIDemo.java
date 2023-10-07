import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.RECT;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WINDOWPLACEMENT;
import org.yunghegel.gdx.ui.UI;
import org.yunghegel.gdx.ui.widgets.*;
import org.yunghegel.gdx.ui.widgets.viewport.SImageButton;
import org.yunghegel.gdx.ui.widgets.viewport.ViewportPanel;
import org.yunghegel.gdx.ui.widgets.viewport.ViewportWidget;
import org.yunghegel.gdx.ui.widgets.viewport.events.ViewportEvent;
import org.yunghegel.gdx.ui.widgets.viewport.events.ViewportEventListener;
import org.yunghegel.gdx.utils.system.NativesListener;
import org.yunghegel.gdx.utils.ui.LoggerWidget;

import java.nio.DoubleBuffer;

public class UIDemo extends ApplicationAdapter {

    Stage stage;
    Table table;

    ViewportWidget viewportWidget;
    SWindow window;

    GenericRender scene;
    ViewportPanel panel;
    MenuBar menuBar;
    NativesListener nativesListener;

    public UIDemo(NativesListener nativesListener) {
        this.nativesListener = nativesListener;
    }




    @Override
    public void create() {
        super.create();
        UI.load();
        UI.overwriteWindowProc2();
        VisUI.load(UI.getSkin());

        scene=new GenericRender();

        createUI();
        createViewportWidget();
        createWindowListener();


    }

    private void createWindowListener() {
        stage.addListener(new DragListener() {
            float dragStartX = getDragStartX();
            float dragStartY = getDragStartY();

            float deltaX = getDeltaX();
            float deltaY = getDeltaY();


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {



                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                nativesListener.setPosition((int) (Gdx.graphics.getWidth() - (dragStartX - getDragStartX())), (int) (Gdx.graphics.getHeight() - (dragStartY - getDragStartY())));
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
            }

            @Override
            public boolean handle(Event event) {
                return false;
            }
        });

    }

    void createUI(){
        stage=new Stage(new ScreenViewport());
        scene.inputs.addProcessor(stage);
        table = new Table(UI.getSkin());
        table.setFillParent(true);
        stage.addActor(table);
        STable buttons= new STable();

        buttons.add(new EditableTextButton("TestButton"));
        menuBar = new MenuBar();
        menuBar.addMenu(new Menu("File"));
        menuBar.addMenu(new Menu("Edit"));
        menuBar.addMenu(new Menu("View"));
        menuBar.addMenu(new Menu("Help"));
        table.add(menuBar.getTable()).growX().top().row();
        STable windowControls = new STable();
        var minimizeButton = new SImageButton("iconify-window");
        var maximizeButton = new SImageButton("maximize-window");
        var closeButton = new SImageButton("close-window");

        windowControls.add(minimizeButton).size(22,22).padRight(5).padBottom(0).center().width(22);
        minimizeButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                nativesListener.setIconified();
            }
        });
        windowControls.add(maximizeButton).size(22,22).padRight(5).center().width(22);
        maximizeButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                nativesListener.setFullscreen();
            }
        });
        windowControls.add(closeButton).size(22,22).padRight(5).padBottom(0).center().width(22);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        windowControls.align(Align.right);
        menuBar.getTable().add(windowControls).growX().right().padRight(5);
        menuBar.getTable().setTouchable(Touchable.enabled);
    }

    void createViewportWidget(){


        viewportWidget = new ViewportWidget((ScreenViewport) scene.viewport,stage);
        viewportWidget.setRenderer((f)->{
           scene.render(Gdx.graphics.getDeltaTime());
        });
        viewportWidget.addViewportListener(new ViewportEventListener() {

            @Override
            public boolean exited(ViewportEvent event) {
                System.out.println("exited");
                Gdx.input.setInputProcessor(stage);
                return false;
            }

            @Override
            public boolean entered(ViewportEvent event) {
                Gdx.input.setInputProcessor(scene.inputs);
                return super.entered(event);
            }

            @Override
            public boolean resized(ViewportEvent event, int width, int height) {
                return super.resized(event, width, height);
            }

            @Override
            public boolean gameViewportApplied(ViewportEvent event) {
                return super.gameViewportApplied(event);
            }

            @Override
            public boolean stageViewportApplied(ViewportEvent event) {
                return super.stageViewportApplied(event);
            }
        });

//        viewportWidget.addViewportInputProcessor(camController);
        window = new SWindow("ScenePreview");
        window.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {

                Gdx.input.setInputProcessor(scene.inputs);

                System.out.println("enter");

                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.input.setInputProcessor(stage);
                super.exit(event, x, y, pointer, toActor);
            }
        });
         panel = new ViewportPanel(viewportWidget);


        table.add(panel).grow().row();





    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void render() {
        super.render();

        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(0.1f, .1f, .1f, 1);


        stage.act();
        stage.draw();
    }


    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        NativesListener nativesListener = new NativesListener();
        configuration.setWindowedMode(1920, 1080);
        configuration.setResizable(true);
        configuration.useVsync(false);
        configuration.setForegroundFPS(0);
        configuration.setDecorated(false);
        configuration.setBackBufferConfig(8, 8, 8, 8, 8, 8, 16);
        configuration.setWindowListener(nativesListener);
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2);
        new Lwjgl3Application(new UIDemo(nativesListener), configuration);
    }
}
