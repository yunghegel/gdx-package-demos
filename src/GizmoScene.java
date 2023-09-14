import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.yunghegel.gdx.gizmo.GizmoManager;
import org.yunghegel.gdx.gizmo.core.GizmoType;
import org.yunghegel.gdx.gizmo.core.transform.BasicTransformTarget;
import org.yunghegel.gdx.gizmo.core.transform.TransformGizmoTarget;
import org.yunghegel.gdx.gizmo.core.utility.CompassTarget;
import org.yunghegel.gdx.utils.ui.UI;
import org.yunghegel.gdx.utils.ui.widgets.Frame;

import java.util.function.Consumer;
import java.util.function.Function;

public class GizmoScene extends BasicScene {
    VisWindow window;

    @Override
    protected void buildUI() {
        super.buildUI();

        TransformGizmoTarget target=new TransformGizmoTarget(new BasicTransformTarget(instance.transform));

        gizmoManager.setTransformGizmoTarget(target);

        VisTextButton button=new VisTextButton("Translate");

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });
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
        window=new VisWindow("Gizmo");
        Skin skin =   VisUI.getSkin();
//        stage.addActor(window);
        Table table=new Table(skin);


        table.align(Align.topLeft);
        table.pad(10);

//
//        table.add(checkBox);
//        table.add(checkBox2).row();
//        table.add(checkBox3);
//        table.add(checkBox4).row();

        Array<GizmoType> gizmos = new Array<>();
        gizmos.add(GizmoType.TRANSLATE, GizmoType.ROTATE, GizmoType.SCALE);
        VisSelectBox<GizmoType> selectBox =  UI.selector(skin, gizmos, GizmoType.TRANSLATE, new Function<GizmoType, String>() {
                    @Override
                    public String apply(GizmoType gizmoType) {
                        return gizmoType.name();
                    }
                }, new Consumer<GizmoType>() {
                    @Override
                    public void accept(GizmoType gizmoType) {
                        gizmoManager.enableGizmo(gizmoType, target);
                    }
                });
        TextButton compassButton = UI.toggle(skin,"Compass",true,(enabled) -> {
            if(enabled){
                compass.enable();
            } else {
                compass.disable();
            }
                });
        Frame frame;

       frame = UI.frameToggle("Gizmo",skin,true,(b)-> {
            if(b){


            } else {

            }
        });

       frame.getContentTable().add(table).grow().fill().expandX();


        table.add(selectBox);
        table.add(compassButton).row();

//        window.add(table).grow().row();
//        window.pack();

                rightTable.add(frame).grow().row();




        viewportWidget.setRenderer((f)->{
            grid.render(camera);
            sceneGraph.render(Gdx.graphics.getDeltaTime());
            batch.begin(camera);
            batch.render(instances, environment);
            batch.end();
            gizmoManager.render(batch);
        }   );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        gizmoManager.update();
        gizmoManager.processInput();
    }

    GizmoManager gizmoManager;

    @Override
    public void create() {
        super.create();
        gizmoManager = new GizmoManager(inputs, camera, viewport);

    }


}
